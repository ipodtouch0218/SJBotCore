package me.ipodtouch0218.sjbotcore;

import java.io.File;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import me.ipodtouch0218.sjbotcore.command.BotCommand;
import me.ipodtouch0218.sjbotcore.files.BotSettings;
import me.ipodtouch0218.sjbotcore.files.MessageSettings;
import me.ipodtouch0218.sjbotcore.files.YamlConfig;
import me.ipodtouch0218.sjbotcore.handler.MessageHandler;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Simple JDA Bot Core. Proves fundamental bot function like command handling
 * and message listening, along with saving and loading bot configurations. Requires
 * a {@link BotSettings} instance to start.
 */
public class SJBotCore extends ListenerAdapter {

	/*
	 * FEATURES TODO:
	 * Voice capibility from URL (streams? lavaplayer... maybe)
	 * Some type of permission system (or just stick to the discord permissions?)
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
	
	/**
	 * Starts the bot with the token from the current {@link BotSettings} instance.
	 * @see SJBotCore#startBot(String)
	 */
	public void startBot() throws IllegalArgumentException, LoginException {
		startBot(settings.token);
	}
	/**
	 * Starts the bot with the given token. Settings will be loaded from the currently
	 * loaded {@link BotSettings} instance.
	 * @param token - String token the bot will use to login to the account.
	 */
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
	
	/**
	 * Shuts down the currently running shard manager.
	 * @see ShardManager#shutdown()
	 */
	public void stopBot() {
		if (!running || shardManager == null) { return; }
		shardManager.shutdown();
		running = false;
		shardManager = null;
	}
	
	
	//--Configuration Saving & Loading--//
	
	/**
	 * Loads a BotSettings config from the given file. If the file is blank, or reading the file
	 * somehow fails, a default settings file will be loaded in its place.
	 * @param file - File to load a {@link BotSettings} instance from.
	 */
	public void loadConfigFromFile(File file) {
		settings = YamlConfig.loadConfig(file, BotSettings.class);
	}
	/**
	 * Saves the current Botsettings to a file.
	 * @param file - File to save the current {@link BotSettings} configuration to.
	 * @see BotSettings#saveConfig(File)
	 */
	public void saveConfigToFile(File file) {
		if (settings == null) { return; }
		settings.saveConfig(file);
	}
	
	//--Misc--//
	/**
	 * Registers a command to the currently used CommandHandler. Shorthand for
	 * the {@link MessageHandler} register method.
	 * @param cmd - Command to be registered
	 * @return If the command was successfully added, i.e. {@link ArrayList#add(Object)}
	 * @see MessageHandler#registerCommand(BotCommand)
	 */
	public boolean registerCommand(BotCommand cmd) {
		return messageHandler.registerCommand(cmd);
	}
	/**
	 * Unregisters a command from the currently used CommandHandler. Shorthand for
	 * the {@link MessageHandler} unregister method.
	 * @param cmd - The command to be unregistered
	 * @return If the command was successfully removed, i.e. {@link ArrayList#remove(Object)}
	 * @see MessageHandler#unregisterCommand(BotCommand)
	 */
	public boolean unregisterCommand(BotCommand cmd) {
		return messageHandler.unregisterCommand(cmd);
	}
	
	//--Setters--//
	/**
	 * Replaces the current {@link MessageSettings} instance being used.
	 * @param newsettings - MessageSettings instance to use.
	 */
	public void setMessages(MessageSettings newsettings) {
		messages = newsettings;
	}
	
	//--Getters--//
	/**
	 * Returns the {@link ShardManager} instance the bot is running on. ShardManager replaces using
	 * separate JDA instances for each shard, and allows management of each shard together.
	 * @return The ShardManager instance, the bot itself.
	 */
	public ShardManager getShardManager() { return shardManager; }
	/**
	 * Returns the {@link MessageHandler} instance currently being used to parse commands.
	 * @return Current MessageHandler instance.
	 */
	public MessageHandler getCommandHandler() { return messageHandler; }
	/**
	 * Returns the {@link BotSettings} that are currently being used to start the bot.
	 * @return The currently used BotSettings instance. 
	 */
	public BotSettings getBotSettings() { return settings; }
	/**
	 * Returns an {@link MessageSettings} instance containing the currently used messages.
	 * @return Currently used MessageSettings instance
	 */
	public MessageSettings getMessages() { return messages; }
	/**
	 * Returns if the bot is currently running. Changes when the {@link SJBotCore#startBot()}
	 * and {@link SJBotCore#stopBot()} functions are called, not necessarily when the JDA itself has shut down.
	 * @return If the bot is running.
	 */
	public boolean isBotRunning() { return running; }
}
