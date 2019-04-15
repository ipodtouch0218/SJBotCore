package me.ipodtouch0218.sjbotcore.command;

public class FlagInfo {

	private String flagTag;
	private int parameterCount;
	private String description = "N/A";
	private String usage;
	
	public FlagInfo(String tag, int parameters) {
		this(tag, parameters, "N/A");
	}
	public FlagInfo(String tag, int parameters, String description) {
		this(tag, parameters, description, null);
	}
	public FlagInfo(String tag, int parameters, String description, String usage) {
		flagTag = tag;
		parameterCount = parameters;
		this.description = description;
		this.usage = usage;
	}
	
	public String getTag() { return flagTag; }
	public int getParameterCount() { return parameterCount; }
	public String getDescription() { return description; }
	public String getUsage() { return (usage == null ? flagTag : usage); }
}
