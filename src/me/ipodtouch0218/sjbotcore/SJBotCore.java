package me.ipodtouch0218.sjbotcore;

import java.io.File;

import javax.security.auth.login.LoginException;

import me.ipodtouch0218.sjbotcore.command.BotCommand;
import me.ipodtouch0218.sjbotcore.files.BotSettings;
import me.ipodtouch0218.sjbotcore.files.MessageSettings;
import me.ipodtouch0218.sjbotcore.files.YamlConfig;
import me.ipodtouch0218.sjbotcore.handler.MessageHandler;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SJBotCore extends ListenerAdapter {

	/*
	 * FEATURES TODO:
	 * 
	 * Voice capibility from URL (streams? lavaplayer... maybe)
	 * Some type of permission system (or use discord permissions? maybe.)
	 */
	
	private boolean running;
	
	private ShardManager shardManager;
	private MessageHandler messageHandler;
	
	private BotSettings settings;
	private MessageSettings messages;
	
	public SJBotCore(File settingsFile) {
		this(YamlConfig.loadConfig(settingsFile, BotSettings.class));
	}
	public SJBotCore(BotSettings settings) {
		this.settings = settings;
		messageHandler = new MessageHandler(this);
		messages = new MessageSettings();
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
			.addEventListeners(messageHandler, this)
			.setAudioEnabled(settings.enableAudio)
			.build();
	}
	
	public void stopBot() {
		if (!running || shardManager == null) { return; }
		shardManager.shutdown();
		running = false;
		shardManager = null;
	}
	
	
	//--Configuration Saving & Loading--//
	public void loadConfigFromFile(File file) {
		settings = YamlConfig.loadConfig(file, BotSettings.class);
	}
	public void saveConfigToFile(File file) {
		if (settings == null) { return; }
		settings.saveConfig(file);
	}
	
	//--Misc--//
	public void registerCommand(BotCommand cmd) {
		messageHandler.registerCommand(cmd);
	}
	public void unregisterCommand(BotCommand cmd) {
		messageHandler.unregisterCommand(cmd);
	}
	
	//--Setters--//
	public void setMessages(MessageSettings newsettings) {
		messages = newsettings;
	}
	
	//--Getters--//
	public ShardManager getShardManager() { return shardManager; }
	public MessageHandler getCommandHandler() { return messageHandler; }
	public BotSettings getBotSettings() { return settings; }
	public MessageSettings getMessages() { return messages; }
	public boolean isBotRunning() { return running; }
}
