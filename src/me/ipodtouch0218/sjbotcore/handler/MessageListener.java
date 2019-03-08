package me.ipodtouch0218.sjbotcore.handler;

import java.util.HashMap;

import me.ipodtouch0218.sjbotcore.SJBotCore;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

	private static HashMap<Long, ReactionHandler> reactionHandlers = new HashMap<>();
	
	//---Event Handling---//
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message msg = e.getMessage();
		User author = e.getAuthor();
		
		if (e.getAuthor().getIdLong() == e.getJDA().getSelfUser().getIdLong()) { return; }
		if (CommandHandler.isCommand(msg)) {
			//the message is a command, send to the commandhandler
			SJBotCore.getCommandHandler().executeCommand(msg, author); //result is if the command was successful.
		}
	}

	@Override
	public void onGenericMessageReaction(GenericMessageReactionEvent e) {
		if (e.getUser().getIdLong() == e.getJDA().getSelfUser().getIdLong()) { return; }
		
		long messageId = e.getMessageIdLong();
		if (reactionHandlers.containsKey(messageId)) {
			//this message has a reactionhandler for it. time to check.
			ReactionHandler handler = reactionHandlers.get(messageId);
			handleReaction(handler, e);
		}
	}
	
	private void handleReaction(ReactionHandler handler, GenericMessageReactionEvent e) {
		boolean isOwner = false;
		boolean add = e instanceof MessageReactionAddEvent;
		if (handler.getOwnerId() > -1) {
			isOwner = (e.getUser().getIdLong() == handler.getOwnerId());
		}
		handler.handleReaction(e, add, isOwner);
	}
	
	@Override
	public void onMessageDelete(MessageDeleteEvent e) {
		if (reactionHandlers.containsKey(e.getMessageIdLong())) {
			reactionHandlers.remove(e.getMessageIdLong());
		}
	}
	
	//---//
	public static void addReactionHandler(long messageid, ReactionHandler handler) {
		reactionHandlers.put(messageid, handler);
	}

	public static void removeReactionHandler(long messageId) {
		reactionHandlers.remove(messageId);
	}
}
