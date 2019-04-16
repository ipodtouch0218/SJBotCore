package me.ipodtouch0218.sjbotcore.command;

/**
 * Representation of a Command Flag after being parsed from a command's arguments.
 */
public class CommandFlag {

	private String flagTag; //Tag of the flag, ex. -<tag> in the command itself.
	private String[] parameters; //Arguments after the flag that were consumed as parameters.
	
	public CommandFlag(String tag, String[] parameters) {
		this.flagTag = tag;
		this.parameters = parameters;
	}
	
	//--Getters--//
	/**
	 * Returns the parameters the flag consumed from the command arguments. Should be empty,
	 * rather than null.
	 * @return The parameters of the flag.
	 */
	public String[] getParameters() { return parameters; }
	/**
	 * Returns the tag "name" of the flag.
	 * @return The tag of the flag.
	 */
	public String getTag() { return flagTag; }
	
	//--Other--//
	public static class FlagParameterException extends Exception {
		private static final long serialVersionUID = 1L;
		private String tag;
		private int expectedParameters;
		private int gottenParameters;
		public FlagParameterException(String tag, int expected, int gotten) {
			this.tag = tag;
			this.expectedParameters = expected;
			this.gottenParameters = gotten;
		}
		public String getFlagTag() { return tag; }
		public int getExpectedParameterCount() { return expectedParameters; }
		public int getGottenParameterCount() { return gottenParameters; }
	}
}
