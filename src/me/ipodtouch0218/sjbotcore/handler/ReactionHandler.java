package me.ipodtouch0218.sjbotcore.handler;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;

/**
 * A basic {@link MessageReaction} handler. Can be added to a {@link MessageHandler} to be passed control
 * to handle reaction additions or removals. An owner can be specified to limit the function of the
 * reaction handler to one particular user.
 */
public abstract class ReactionHandler {

	protected long ownerId = -1; //only the owner can trigger the reactions, -1 if null.
	
	/**
	 * Abstract method to handle the addition or removal of a {@link MessageReaction} on a certain message.
	 * @param e - {@link GenericMessageReactionEvent} passed by the event handler.
	 * @param add - If the given was added to the message. If not, the reaction was removed.
	 * @param isOwner - If the {@link ReactionHandler#getOwnerId()} matches the user which added the reaction.
	 */
	public abstract void handleReaction(GenericMessageReactionEvent e, boolean add, boolean isOwner);
	
	/**
	 * Sets the ownerID field, allows the reactionHandler method to limit function to only the user with the given id passed.
	 * @param id - Sets the ID of the owner.
	 */
	public void setOwnerId(long id) { ownerId = id; }
	public long getOwnerId() { return ownerId; }
	
	//---//
	/**
	 * Removes {@link MessageReaction}s from a given {@link Message} added by the
	 * given user.
	 * @param user - {@link User} to remove reactions by.
	 * @param m - {@link Message} to remove reactions from.
	 */
	public void clearUserReactions(User user, Message m) {
		for (MessageReaction r : m.getReactions()) {
			r.removeReaction(user).queue();
		}
	}
	//---//
	
	/**
	 * Removes all {@link MessageReaction}s from a given message.
	 * @param m - Message instance to remove {@link MessageReaction}s from.
	 * @see Message#clearReactions()
	 */
	public static void clearAllReactions(Message m) {
		if (m.getChannel().getType() != ChannelType.TEXT) { return; }
		m.clearReactions().queue();
	}
	
	/**
	 * Adds all {@link MessageReaction}s specified to the given message. Useful for settings default options the user can choose,
	 * without having the user manually search and add the required reactions.
	 * @param m - {@link Message} instance to add reactions to.
	 * @param options - Adds all given String reactions to the message.
	 */
	public static void setReactions(Message m, String... options) {
		if (m.getChannel().getType() != ChannelType.TEXT) { return; }
		if (!m.getReactions().isEmpty()) { 
			m.clearReactions().complete();
		}
		for (String option : options) {
			m.addReaction(option).queue();
		}
	}
}
