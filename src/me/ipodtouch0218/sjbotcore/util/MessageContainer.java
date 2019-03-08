package me.ipodtouch0218.sjbotcore.util;

import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.requests.RestAction;

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
	
	////
	public long getGuildId() { return guildId; }
	public Guild getGuild(JDA jda) { return jda.getGuildById(guildId); }
	public Guild getGuild(ShardManager sm) { return sm.getGuildById(guildId); }
	
	////
	public long getChannelId() { return channelId; }
	public MessageChannel getChannel(JDA jda) { 
		if (guildId <= -1) {
			//no guild, return private channel.
			return jda.getPrivateChannelById(channelId);
		}
		return jda.getTextChannelById(channelId);
	}
	public MessageChannel getChannel(ShardManager sm) {
		if (guildId <= -1) {
			//no guild, return private channel.
			return sm.getPrivateChannelById(channelId);
		}
		return sm.getTextChannelById(channelId);
	}
	
	////
	public long getMessageId() { return messageId; }
	public RestAction<Message> getMessage(JDA jda) {
		return getChannel(jda).getMessageById(messageId);
	}
	public RestAction<Message> getMessage(ShardManager sm) {
		return getChannel(sm).getMessageById(messageId);
	}
}
