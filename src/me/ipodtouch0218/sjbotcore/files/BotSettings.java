package me.ipodtouch0218.sjbotcore.files;

public class BotSettings extends YamlConfig {

	public String token = "<PASTE TOKEN HERE>"; //Bot token used for logging into discord without using email/password
	public String botPlayingMessage = "Use ;help for help~"; //Bot game message, used as a short message displayed in the user profile

	public String defaultCommandPrefix = ";"; //Command prefix used in dm's or servers without a custom cmd prefix.
	public boolean deleteIssuedCommand = false; //If a command issued by a user should be deleted automatically
	
}
