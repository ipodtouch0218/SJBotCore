package me.ipodtouch0218.sjbotcore.command;

import java.util.HashSet;
import java.util.Optional;

public class FlagSet {

	private HashSet<CommandFlag> flagSet = new HashSet<>();
	
	public FlagSet() {} 
	public FlagSet(HashSet<CommandFlag> existingList) {
		flagSet.addAll(existingList);
	}
	
	
	public void addFlag(CommandFlag flag) {
		flagSet.add(flag);
	}
	public void removeFlag(CommandFlag flag) {
		flagSet.remove(flag);
	}
	
	//---Getters---//
	public boolean containsFlag(String tag) {
		return flagSet.stream().anyMatch(f -> f.getTag().equalsIgnoreCase(tag));
	}
	public Optional<CommandFlag> getFlag(String tag) {
		return flagSet.stream().filter(f -> f.getTag().equalsIgnoreCase(tag)).findFirst();
	}
}
