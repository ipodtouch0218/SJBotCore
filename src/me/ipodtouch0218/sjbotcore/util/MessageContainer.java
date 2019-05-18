package me.ipodtouch0218.sjbotcore.util;

import java.util.Optional;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * A simple message container, holding the Guild ID, Channel ID, and Message ID of
 * a JDA {@link Message}. Can be used to serialize the required IDs to request a Message instance.
 */
public class MessageContainer {

	private long guildId = -1;
	private long channelId;
	private long messageId;
	
	public MessageContainer() {}
	public MessageContainer(long guildId, long channelId, long messageId) {
		this.guildId = guildId;
		this.channelId = channelId;
		this.messageId = messageId;
	}
	public MessageContainer(Message msg) {
		messageId = msg.getIdLong();
		channelId = msg.getChannel().getIdLong();
		if (msg.getGuild() != null) { guildId = msg.getGuild().getIdLong(); }
	}
	
	//--Guild Getters--//
	/**
	 * Returns the guild ID of the wrapped message inside this instance.
	 * Will return -1 if the message is from a DM or Group Channel.
	 * @return The guild ID of the given message. 
	 */
	public long getGuildId() { return guildId; }
	/**
	 * Returns an optional containing the Guild this message was sent within. Can be empty if the
	 * message was sent within a DM or Group Channel.
	 * @param jda - {@link JDA} to retrieve the {@link Guild} instance.
	 * @return Optional {@link Guild} the message was sent within.
	 */
	public Optional<Guild> getGuild(JDA jda) { return Optional.ofNullable(jda.getGuildById(guildId)); }
	/**
	 * Returns an optional containing the Guild this message was sent within. Can be empty if the
	 * message was sent within a DM or Group Channel.
	 * @param sm - {@link ShardManager} to retrieve the {@link Guild} instance.
	 * @return Optional {@link Guild} the message was sent within.
	 */
	public Optional<Guild> getGuild(ShardManager sm) { return Optional.ofNullable(sm.getGuildById(guildId)); }
	
	//--Channel Getters--//
	/**
	 * Returns the ID of the channel containing the message wrapped in this container.
	 * @return The long ID of the channel.
	 */
	public long getChannelId() { return channelId; }
	/**
	 * Returns the channel the message was sent within.
	 * @param jda - {@link JDA} to retrieve the channel.
	 * @return the {@link MessageChannel} the message was sent within.
	 */
	public MessageChannel getChannel(JDA jda) { 
		if (guildId <= -1) {
			//no guild, return private channel.
			return jda.getPrivateChannelById(channelId);
		}
		return jda.getTextChannelById(channelId);
	}
	/**
	 * Returns the channel the message was sent within.
	 * @param sm - {@link ShardManager} to retrieve the channel.
	 * @return the {@link MessageChannel} the message was sent within.
	 */
	public MessageChannel getChannel(ShardManager sm) {
		if (guildId <= -1) {
			//no guild, return private channel.
			return sm.getPrivateChannelById(channelId);
		}
		return sm.getTextChannelById(channelId);
	}
	
	//--Message Getters--//
	/**
	 * Returns the id of the message wrapped within this continer.
	 * @return The long ID of the message.
	 */
	public long getMessageId() { return messageId; }
	/**
	 * Returns a RestAction containing the {@link Message} instance wrapped within this wrapper.
	 * @param jda - {@link JDA} instance to create the RestAction
	 * @return A {@link RestAction} containing the Message.
	 */
	public RestAction<Message> getMessage(JDA jda) {
		MessageChannel ch = getChannel(jda);
		if (ch == null) { 
			throw new IllegalArgumentException("Unable to get message with id " + messageId + " - invalid channel.");
		}
		return getChannel(jda).retrieveMessageById(messageId);
	}
	/**
	 * Returns a RestAction containing the {@link Message} instance wrapped within this wrapper.
	 * @param sm - {@link ShardManager} instance to create the RestAction
	 * @return A {@link RestAction} containing the Message.
	 */
	public RestAction<Message> getMessage(ShardManager sm) {
		MessageChannel ch = getChannel(sm);
		if (ch == null) { 
			throw new IllegalArgumentException("Unable to get message with id " + messageId + " - invalid channel.");
		}
		return getChannel(sm).retrieveMessageById(messageId);
	}
}
