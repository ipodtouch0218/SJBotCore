package me.ipodtouch0218.sjbotcore.command;

public class FlagInfo {

	private String flagTag;
	private int parameterCount;
	private String description;
	
	public FlagInfo(String tag, int parameters) {
		this(tag, parameters, "N/A");
	}
	public FlagInfo(String tag, int parameters, String description) {
		flagTag = tag;
		parameterCount = parameters;
		this.description = description;
	}
	
	public String getTag() { return flagTag; }
	public int getParameterCount() { return parameterCount; }
	public String getDescription() { return description; }
}
