package me.ipodtouch0218.sjbotcore.command;

public class CommandFlag {

	private String flagTag; //Tag of the flag, ex. -<tag> in the command itself.
	private String[] parameters; //Arguments after the flag that were consumed as parameters.
	
	public CommandFlag(String tag, String[] parameters) {
		this.flagTag = tag;
		this.parameters = parameters;
	}
	
	//--Getters--//
	public String[] getParameters() { return parameters; }
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
