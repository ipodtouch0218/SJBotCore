package me.ipodtouch0218.sjbotcore.command;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A simple {@link HashSet} wrapper based around {@link CommandFlag}s.
 */
public class FlagSet {

	private HashSet<CommandFlag> flagSet = new HashSet<>();
	
	public FlagSet() {} 
	public FlagSet(HashSet<CommandFlag> existingList) {
		flagSet.addAll(existingList);
	}
	
	/**
	 * Adds a flag to the flagset.
	 * @param flag - The flag to add
	 * @return If the flag was successfully added, i.e. {@link Set#remove(Object)}
	 */
	public boolean addFlag(CommandFlag flag) {
		return flagSet.add(flag);
	}
	/**
	 * Removes a flag from the flagset.
	 * @param flag - The flag to remove
	 * @return If the flag was removed, i.e. {@link Set#remove(Object)}
	 */
	public boolean removeFlag(CommandFlag flag) {
		return flagSet.remove(flag);
	}
	
	//---Getters---//
	/**
	 * Reutrns if the FlagSet contains a {@link CommandFlag} with the given tag.
	 * @param tag - The tag to search for.
	 * @return If a flag with the given tag is present.
	 */
	public boolean containsFlag(String tag) {
		return flagSet.stream().anyMatch(f -> f.getTag().equalsIgnoreCase(tag));
	}
	/**
	 * Returns only one Optional {@link CommandFlag} with the specified tag.
	 * @param tag - Tag of the flag to return
	 * @return The Optional CommandFlag with the given tag
	 */
	public Optional<CommandFlag> getFlag(String tag) {
		return flagSet.stream().filter(f -> f.getTag().equalsIgnoreCase(tag)).findFirst();
	}
}
