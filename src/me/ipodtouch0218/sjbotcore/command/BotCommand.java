package me.ipodtouch0218.sjbotcore.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import me.ipodtouch0218.sjbotcore.handler.MessageHandler;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * A command that can be executed by the bot. It will automatically be parsed
 * by the {@link MessageHandler} and the {@link BotCommand#execute(Message, String, ArrayList, FlagSet)} method
 * will be called. Must be registered to the MessageHandler using {@link MessageHandler#registerCommand(BotCommand)}
 * before the command can be used.
 */
public abstract class BotCommand {

	//--Variables & Constructor--//
	private String name; //Command name. Used to run the command itself, [prefix][name]
	private String[] aliases; //The command can also be ran through these names. Command names take priority over aliases (if there's overlap)
	@Deprecated
	private Permission permission; //Permission the user needs to use this command.
	
	private String usage = ""; //Command usage, <> = required parameters, [] = optional parameters 
	private String description = "No Info Provided."; //Command description, used in outputting the help page.
	
	private HashSet<FlagInfo> registeredFlags = new HashSet<>(); //List of all flags. Flags must be registered before they will be parsed as flags.
	
	private boolean useInGuilds; //Command can be used within Guilds
	private boolean useInDMs; //Command can be used within DMs
	
	public BotCommand(String name, boolean guilds, boolean dms) {
		this(name, guilds, dms, null);
	}
	@Deprecated
	public BotCommand(String name, boolean guilds, boolean dms, Permission perm) {
		this.name = name;
		this.useInGuilds = guilds;
		this.useInDMs = dms;
		this.permission = perm;
		
		this.usage = name;
	}
	
	//--//
	/**
	 * Method that gets called when a message comes from a user that both 
	 * starts with the command prefix + {@link BotCommand#name} OR any {@link BotCommand#aliases}
	 * and The sender has the correct {@link BotCommand#permission} to use this command.
	 * 
	 * @param msg - Discord message instance.
	 * @param alias - Alias used to execute the command.
	 * @param args - List of arguments separated by spaces (Excluding flags and their parameters).
	 * @param flags - List of {@link CommandFlag} which were parsed out of the command.
	 */
	public abstract void execute(Message msg, String alias, ArrayList<String> args, FlagSet flags);
	
	//--Setters--//
	/**
	 * Sets the Discord {@link Permission} a sender of a command must have to execute this command
	 * @param permission - New {@link Permission} to require.
	 */
	@Deprecated
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	/**
	 * Populates the description and usage. Can be used by help commands.
	 * @param description - Description of the command.
	 * @param usage - Usage of the command including possible arguments.
	 */
	public void setHelpInfo(String description, String usage) {
		this.usage = usage;
		this.description = description; 
	}
	/**
	 * Sets aliases that the command can use in substitution of the {@link BotCommand#name}. The name of the command
	 * has higher priority than an alias.
	 * @param alises - An array of aliases to use.
	 */
	public void setAliases(String... alises) {
		this.aliases = alises;
	}
	/**
	 * Adds a {@link CommandFlag} to this command. A flag must be registered in order to be parsed
	 * properly by the {@link MessageHandler}.
	 * 
	 * @param tag - Name of the flag to add.
	 * @param parameters - Required number of parameters for this flag.
	 */
	public void registerFlag(String tag, int parameters) {
		registeredFlags.add(new FlagInfo(tag, parameters));
	}
	/**
	 * Adds a {@link CommandFlag} to this command. A flag must be registered in order to be parsed
	 * properly by the {@link MessageHandler}.
	 * 
	 * @param tag - Name of the flag to add.
	 * @param parameters - Required number of parameters for this flag.
	 * @param description - Description to be listed in a help command.
	 */
	public void registerFlag(String tag, int parameters, String description) {
		registeredFlags.add(new FlagInfo(tag, parameters, description));
	}
	/**
	 * Adds a {@link CommandFlag} to this command. A flag must be registered in order to be parsed
	 * properly by the {@link MessageHandler}.
	 * 
	 * @param tag - Name of the flag to add.
	 * @param parameters - Required number of parameters for this flag.
	 * @param description - Description to be listed in a help command.
	 * @param usage - Usage information to be listed in a help command.
	 */
	public void registerFlag(String tag, int parameters, String description, String usage) {
		registeredFlags.add(new FlagInfo(tag, parameters, description, usage));
	}
	
	//--Getters--//
	/**
	 * Returns the name of the command, used at the start of a command after the prefix to sepcify the command.
	 * @return The name of the command
	 */
	public String getName() { return name; }
	/**
	 * @return All applicable aliases
	 */
	public Optional<String[]> getAliases() { return Optional.ofNullable(aliases); }
	public String getUsage() { return usage; }
	public String getDescription() { return description; }
	public boolean isFlagRegistered(String tag) { return registeredFlags.stream().anyMatch(f -> tag.equalsIgnoreCase(f.getTag())); }
	public Optional<FlagInfo> getFlag(String tag) { return registeredFlags.stream().filter(f -> tag.equalsIgnoreCase(f.getTag())).findAny(); }
	/**
	 * Returns the permission the {@link User} sender must use to execute this command. Is ignored outside of Guilds.
	 * @return The permission the user must have to execute this command
	 */
	@Deprecated
	public Permission getPermission() { return permission; }
	/**
	 * Returns a set of {@link FlagInfo} instances, representing all registered flags.
	 * @return A set of all registered flags
	 */
	public HashSet<FlagInfo> getFlags() { return registeredFlags; }
	
	/**
`	 * Returns if the message can be ran in the channel the message was requested within.
	 * @param msg - The message command request
	 * @return If the command can be ran in the channel.
	 */
	public boolean canExecute(Message msg) {
		switch (msg.getChannelType()) {
		case PRIVATE:
		case GROUP: { return useInDMs; }
		
		case TEXT: { return useInGuilds; }
		default: { return false; }
		}
	}
}
