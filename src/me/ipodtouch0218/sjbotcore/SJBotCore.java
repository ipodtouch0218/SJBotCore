package me.ipodtouch0218.sjbotcore;

import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import me.ipodtouch0218.sjbotcore.command.BotCommand;
import me.ipodtouch0218.sjbotcore.command.CommandFlag;
import me.ipodtouch0218.sjbotcore.handler.CommandHandler;
import me.ipodtouch0218.sjbotcore.handler.MessageListener;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Message;

public class SJBotCore {

	/*
	 * FEATURES TODO:
	 * 
	 * Set token from string or file
	 * YamlFile reader/manager (jackson)
	 * Voice capibility from URL (streams?)
	 * Configuration files
	 * - General bot instance 
	 * - Guild-specific 
	 * Some type of permission system (or use discord permissions? maybe.)
	 */

	//TODO: temporary
	public static ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
	
	private static ShardManager shardManager;
	private static MessageListener messageListener;
	private static CommandHandler commandHandler;
	
	//TODO: dhauoiwhdaowhnd temporary
	public static void main(String[] args) {
		new SJBotCore();
	}
	
	public SJBotCore() {
		messageListener = new MessageListener();
		commandHandler = new CommandHandler();
		
		try {
			shardManager = new DefaultShardManagerBuilder()
					.setToken("redacted for testing")
					.addEventListeners(messageListener)
					.build();
		} catch (LoginException | IllegalArgumentException e) {
			System.err.println("Unable to start the bot!");
			e.printStackTrace();
		}
		
		new BotCommand("ping", true, true) {
			
			@Override
			public void execute(Message msg, String alias, ArrayList<String> args, HashMap<String, CommandFlag> flags) {
				msg.getChannel().sendMessage(":ping_pong: Test Pong! " + shardManager.getShardsTotal() + " shard(s), with an average ping of `" + shardManager.getAveragePing() + "ms`.").queue();
			}
		}.register(commandHandler);
	}
	
	public static ShardManager getShardManager() { return shardManager; }
	public static MessageListener getMessageListener() { return messageListener; }
	public static CommandHandler getCommandHandler() { return commandHandler; }
}
