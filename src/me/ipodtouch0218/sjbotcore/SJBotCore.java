package me.ipodtouch0218.sjbotcore;

import java.io.File;

import javax.security.auth.login.LoginException;

import me.ipodtouch0218.sjbotcore.files.BotSettings;
import me.ipodtouch0218.sjbotcore.files.YamlConfig;
import me.ipodtouch0218.sjbotcore.handler.CommandHandler;
import me.ipodtouch0218.sjbotcore.handler.MessageListener;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;

public class SJBotCore {

	/*
	 * FEATURES TODO:
	 * 
	 * Set token from string, not only file.
	 * Voice capibility from URL (streams?)
	 * Configuration files
	 * Some type of permission system (or use discord permissions? maybe.)
	 */
	
	private static ShardManager shardManager;
	private static MessageListener messageListener;
	private static CommandHandler commandHandler;
	private static BotSettings settings;
	
	public SJBotCore(MessageListener listener) {
		settings = YamlConfig.loadConfig(new File("config.yml"), BotSettings.class);
		messageListener = listener;
		commandHandler = new CommandHandler();
		
		try {
			shardManager = new DefaultShardManagerBuilder()
					.setToken(settings.token)
					.addEventListeners(listener)
					.build();
		} catch (LoginException | IllegalArgumentException e) {
			System.err.println("Unable to start the bot!");
			e.printStackTrace();
		}
	}
	
	public static ShardManager getShardManager() { return shardManager; }
	public static MessageListener getMessageListener() { return messageListener; }
	public static CommandHandler getCommandHandler() { return commandHandler; }
	public static BotSettings getBotSettings() { return settings; }
}
