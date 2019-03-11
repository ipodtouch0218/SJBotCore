package me.ipodtouch0218.sjbotcore.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ipodtouch0218.sjbotcore.command.BotCommand;
import me.ipodtouch0218.sjbotcore.command.CommandFlag;
import me.ipodtouch0218.sjbotcore.command.FlagSet;
import me.ipodtouch0218.sjbotcore.files.BotSettings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageHandler extends ListenerAdapter {

	private static final Pattern ARGS_WITH_QUOTES = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
	
	///INSTANCE STUFFS
	//--Variables & Constructor--//
	public MessageHandler(BotSettings config) {
		this.configuration = config;
	}
	protected BotSettings configuration;
	protected HashSet<BotCommand> commands = new HashSet<>(); //list of all registered commands
	protected HashMap<Long, ReactionHandler> reactionHandlers = new HashMap<>();
	
	//--EVENTS--//
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message msg = e.getMessage();
		User author = e.getAuthor();
		
		if (e.getAuthor().getIdLong() == e.getJDA().getSelfUser().getIdLong()) { return; }
		if (isCommand(msg)) {
			executeCommand(msg, author);
		}
	}
	
	@Override
	public void onGenericMessageReaction(GenericMessageReactionEvent e) {
		if (e.getUser().getIdLong() == e.getJDA().getSelfUser().getIdLong()) { return; }
		long messageId = e.getMessageIdLong();
		
		if (reactionHandlers.containsKey(messageId)) {
			//this message has a reactionhandler for it. run it.
			ReactionHandler handler = reactionHandlers.get(messageId);
			handleReaction(handler, e);
		}
	}
	
	@Override
	public void onMessageDelete(MessageDeleteEvent e) {
		if (reactionHandlers.containsKey(e.getMessageIdLong())) {
			reactionHandlers.remove(e.getMessageIdLong());
		}
	}
	
	//--Reaction Handling--//
	protected void handleReaction(ReactionHandler handler, GenericMessageReactionEvent e) {
		boolean isOwner = false;
		boolean add = e instanceof MessageReactionAddEvent;
		if (handler.getOwnerId() > -1) {
			isOwner = (e.getUser().getIdLong() == handler.getOwnerId());
		}
		handler.handleReaction(e, add, isOwner);
	}
	
	//--Command Execution--//
	protected void executeCommand(Message msg, User sender) {
		if (!isCommand(msg)) { return; } //not a command, but somehow got passed as one? huh.
		MessageChannel channel = msg.getChannel();
		
		//TODO:
		String prefix = configuration.defaultCommandPrefix;
//		if (msg.getChannelType() == ChannelType.TEXT) {
//			prefix = SJBotCore.getGuildSettings(msg.getGuild()).getCommandPrefix();
//		}
		
		String prefixRegex = Matcher.quoteReplacement(prefix); //regex to remove the command prefix from start of message
		String strippedMessage = msg.getContentRaw().replaceFirst(prefixRegex, "");	//removed the command prefix from the message
		
		ArrayList<String> arguments = parseArguments(strippedMessage);
		String cmdName = arguments.get(0);
		Optional<BotCommand> optCommand = getCommandByName(cmdName); //first argument is the command itself.
		arguments.remove(0); //remove the command itself
		if (!optCommand.isPresent()) {	
			//invalid command, send error and return.
			BotCommand closest = closestCommand(cmdName);
			if (closest == null) { return; }
			
			channel.sendMessage(":pancakes: **Unknown Command:** `" + cmdName + "`. Did you mean to type `" + closest.getName() + "`?").queue();
			return;
		}
		
		BotCommand command = optCommand.get();
		if (!command.canExecute(msg)) {
			//command cannot be ran through this channel type
			channel.sendMessage(":pancakes: **Error:** You cannot run this command in a " + (msg.getChannelType() == ChannelType.TEXT ? "Guild" : "DM") + "!").queue();
			return;
		}
		if (msg.getChannelType() == ChannelType.TEXT) {	
			//guild text channel, can check for permissions
			//TODO:
			if (command.getPermission() != null && !msg.getMember().hasPermission(command.getPermission()) /*&& !BotMain.getGuildSettings(msg.getGuild()).isBotAdmin(sender.getIdLong())*/) {
				channel.sendMessage(":pancakes: **Error:** You must have the `" + command.getPermission().name() + "` permission to use this command!").queue();
				return;
			}
		}
		
		try {
			//separate flags from arguments
			FlagSet flags = parseFlagsFromArguments(command, arguments);
			//finally, execute the command.
			command.execute(msg, cmdName, arguments, flags);
			
			if (configuration.deleteIssuedCommand) {
				msg.delete().queue();
			}
		} catch (Exception e) {
			//some error occured? output error message to discord
			StringWriter stacktrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stacktrace));
			channel.sendMessage(":pancakes: **Command Error Caught:** " + e.getMessage() + " ```" + stacktrace.toString() + "```").queue();
		}
	}
	
	protected ArrayList<String> parseArguments(String inputMessage) {
		ArrayList<String> args = new ArrayList<>();
		
		Matcher matcher = ARGS_WITH_QUOTES.matcher(inputMessage);
		while (matcher.find()) {
			String match = matcher.group();
			if (match.matches("\"([^\"]*)\"|'([^']*)'")) {
				//remove the initial quotes.
				match = match.substring(1, match.length()-1);
			}
			args.add(match);
		}
		return args;
	}
	protected FlagSet parseFlagsFromArguments(BotCommand command, ArrayList<String> arguments) {
		HashSet<CommandFlag> flags = new HashSet<CommandFlag>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean contains(Object obj) {
				if (obj == null) { return false; }
				if (obj instanceof String) {
					return stream().anyMatch(f -> f.getTag().equalsIgnoreCase(obj.toString()));
				}
				return super.contains(obj);
			}
		};
		
		//populate flag list
		Iterator<String> it = arguments.iterator();
		while (it.hasNext()) {
			String argument = it.next();
			if (!argument.matches("-[^\\d].*")) { continue; }
			//not a flag, doesnt start with a dash -char
			String dashRemoved = argument.substring(1, argument.length());
			if (!command.isFlagRegistered(dashRemoved)) { continue; }
			//this argument is a flag, remove it from args and retrieve parameters
			int parametercount = command.getFlags().get(dashRemoved);
			it.remove();
			
			String[] parameters = new String[parametercount];
			for (int i = 0; i < parametercount; i++) {
				if (!it.hasNext()) {
					//no parameters left for this flag? err error?....
					throw new IllegalArgumentException("Ran out of parameters for flag " + dashRemoved + ": Expected " + parametercount + ", got " + i);
				}
				String nextArg = it.next();
				parameters[i] = nextArg;
				it.remove();
			}
			flags.add(new CommandFlag(dashRemoved, parameters));
		}
		return new FlagSet(flags);
	}
	
	//--Misc Functions--//
	/**
	 * Returns if a given message can be parsed into a {@link BotCommand}. Automatically
	 * checks for {@link GuildSettings} and recognizes the proper Command Prefix. 
	 * @param msg - Discord {@link Message} instance to check. 
	 * @return If the specified message is parseable as a command. 
	 */
	public boolean isCommand(Message msg) {
		String prefix = configuration.defaultCommandPrefix; 
		if (msg.getChannelType() == ChannelType.TEXT) {
			//TODO: guild prefix
//			prefix = BotMain.getGuildSettings(msg.getGuild()).getCommandPrefix();
		}
		return msg.getContentDisplay().startsWith(prefix);
	}
	
	/**
	 * Uses the Levenshtein distance formula to obtain a registered {@link BotCommand} with the given
	 * name or alias. Possibly null if no command matches at least half of the given input.
	 * @param input - Input string to check for similar {@link BotCommand}s.
	 * @return A possibly-null {@link BotCommand} with a similar name to the input.
	 * @see <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein Distance</a>
	 */
	protected BotCommand closestCommand(String input) {

		BotCommand closest = null;
		float closestDistance = 1;
		for (BotCommand other : commands) {
			int distance = calcLevenshteinDistance(input, other.getName());
			float smartDistance = (float) distance / (float) other.getName().length();
			if (smartDistance < closestDistance) {
				closestDistance = smartDistance;
				closest = other;
			}
		}
		
		if (closestDistance <= 0.5) { //at least half the characters have to match for a suggestion
			return closest;
		}
		return null;
	}
	
	private static int calcLevenshteinDistance(String x, String y) {
		//don't ask i just-... um..
	    int[][] dp = new int[x.length() + 1][y.length() + 1];
	 
	    for (int i = 0; i <= x.length(); i++) {
	        for (int j = 0; j <= y.length(); j++) {
	            if (i == 0) {
	                dp[i][j] = j;
	            }
	            else if (j == 0) {
	                dp[i][j] = i;
	            }
	            else {
	                dp[i][j] = Math.min(dp[i - 1][j - 1] 
	                + (x.charAt(i-1) == y.charAt(j-1) ? 0 : 1), 
	                  Math.min(dp[i - 1][j] + 1, 
	                  dp[i][j - 1] + 1));
	            }
	        }
	    }
	 
	    return dp[x.length()][y.length()];
	}
	
	
	//--Register--//
	/**
	 * Registers a command to this CommandHandler. Commands must be registered before they will
	 * be able to be exectued by users and detected by help commands.
	 * @param newCmd - Command to be registered
	 * @return If the command was successfully added, i.e. {@link ArrayList#add()}
	 */
	public boolean registerCommand(BotCommand newCmd) {
		return commands.add(newCmd);
	}
	public boolean unregisterCommand(BotCommand newCmd) {
		return commands.remove(newCmd);
	}
	public void addReactionHandler(long messageid, ReactionHandler handler) {
		reactionHandlers.put(messageid, handler);
	}
	public void removeReactionHandler(long messageId) {
		reactionHandlers.remove(messageId);
	}
	
	//--Getters--//
	public HashSet<BotCommand> getAllCommands() { return commands; }
	public Optional<BotCommand> getCommandByName(String name) {
		BotCommand cmd = null;
		for (BotCommand cmds : commands) {
			if (name.equalsIgnoreCase(cmds.getName())) { 
				//matches name, don't check aliases
				cmd = cmds; 
				break; 
			}
			if (cmd != null || cmds.getAliases() == null) { continue; } //already found another command (through alias), continue looping.
			aliasLoop:
			for (String aliases : cmds.getAliases()) {
				if (name.equalsIgnoreCase(aliases)) {
					//matches an alias, but keep checking for other matches to the name.
					cmd = cmds;
					break aliasLoop;
				}
			}
		}
		return Optional.ofNullable(cmd);
	}

}
