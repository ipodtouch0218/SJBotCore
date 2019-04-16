package me.ipodtouch0218.sjbotcore.command;

/**
 * An info class containing parameters for creating a {@link CommandTag}.
 */
public class FlagInfo {

	private String flagTag = "default";
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
	
	/**
	 * Returns the flag of the tag. The tag is the "name" of the flag, and
	 * will be used when parsing flags in the MessageHandler.
	 * @return The tag of the flag.
	 */
	public String getTag() { return flagTag; }
	/**
	 * Returns the amount of parameters the flag will consume from the specified command arguments after it.
	 * @return The amount of parameters.
	 */
	public int getParameterCount() { return parameterCount; }
	/**
	 * Returns the specified description of the flag, shouldn't be null.
	 * @return The description of the flag.
	 */
	public String getDescription() { return description; }
	/**
	 * Returns the specified usage of the flag, shouldn't be null.
	 * @return The usage information of the flag.
	 */
	public String getUsage() { return (usage == null ? flagTag : usage); }
}
