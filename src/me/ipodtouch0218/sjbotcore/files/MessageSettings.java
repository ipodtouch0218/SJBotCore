package me.ipodtouch0218.sjbotcore.files;

public class MessageSettings extends YamlConfig {

	public String unknownCommand  = "**Unknown Command:** `%s`.";
	public String unknownCommandSuggestion = "**Unknown Command:** `%s`. Did you mean to type `%s`?";
	public String noPermission = "**Permissions Error:** You must have the `%s` permission to execute this command!";
	public String invalidChannelGuild = "**Invalid Channel**: You cannot execute this command within a Guild text channel!";
	public String invalidChannelDM = "**Invalid Channel**: You cannot execute this command within a DM or Group channel!";
	public String commandError = "**Command Error:** Stacktrace output: ```%s```";
	public String flagError = "**Command Error:** Ran out of parameters for the `%s` flag. Got %d, expected %d"; 
	
}
