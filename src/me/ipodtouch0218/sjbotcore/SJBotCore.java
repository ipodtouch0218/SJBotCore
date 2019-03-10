package me.ipodtouch0218.sjbotcore;

import java.io.File;

import javax.security.auth.login.LoginException;

import me.ipodtouch0218.sjbotcore.files.BotSettings;
import me.ipodtouch0218.sjbotcore.files.YamlConfig;
import me.ipodtouch0218.sjbotcore.handler.MessageHandler;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;

public class SJBotCore {

	/*
	 * FEATURES TODO:
	 * 
	 * Voice capibility from URL (streams?)
	 * Use the configuration file for more.
	 * Some type of permission system (or use discord permissions? maybe.)
	 * Custom message outputs from MessageHandler
	 * - instead of just extending the class & replacing it completely... somehow...
	 * Fix "closest command" function
	 */
	
	private boolean running;
	
	private ShardManager shardManager;
	private MessageHandler messageHandler;
	private BotSettings settings;
	
	public SJBotCore(BotSettings settings) {
		this.settings = settings;
		messageHandler = new MessageHandler(settings);
	}
	
	//--Startup and Shutdown--//
	public void startBot() throws IllegalArgumentException, LoginException {
		startBot(settings.token);
	}
	public void startBot(String token) throws IllegalArgumentException, LoginException {
		if (running && shardManager != null) {
			stopBot();
		}
		shardManager = new DefaultShardManagerBuilder()
			.setToken(token)
			.addEventListeners(messageHandler)
			.build();
	}
	public void stopBot() {
		if (!running || shardManager == null) { return; }
		shardManager.shutdown();
		running = false;
	}
	
	//--Configuration Saving & Loading--//
	public void loadConfigFromFile(File file) {
		settings = YamlConfig.loadConfig(file, BotSettings.class);
	}
	public void saveConfigToFile(File file) {
		if (settings == null) { return; }
		settings.saveConfig(file);
	}
	
	//--Getters--//
	public ShardManager getShardManager() { return shardManager; }
	public MessageHandler getCommandHandler() { return messageHandler; }
	public BotSettings getBotSettings() { return settings; }
	public boolean isBotRunning() { return running; }
}
