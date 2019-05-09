package me.ipodtouch0218.sjbotcore.files;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * A simple configuration wrapper which uses Jackson to serialize objects to file. Classes 
 * extending YamlConfig can be saved using {@link YamlConfig#saveConfig(File)} or loaded using
 * {@link YamlConfig#loadConfig(File, Class)} from file. All fields within the class will be
 * automatically serialized and saved, so developers don't have to handle serializing all the
 * fields within their custom class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlConfig {

	@JsonIgnore
	public static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	
	/**
	 * Saves this YamlConfig instance to the given file.
	 * @param file - File to save the configuration to.
	 */
	public void saveConfig(File file) {
		try {
			mapper.writeValue(file, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads a particular YamlConfig-extending class from file. Will return a new instance of the
	 * class if an IOException occurred when loading the file.
	 * @param file - File to load the config from.
	 * @param classType - Class extending YamlConfig to load from the file.
	 * @return An instance of the specified YamlConfig class.
	 */
	public static <T extends YamlConfig> T loadConfig(File file, Class<T> classType) {
		try {
			//Try to read the file
			return mapper.readValue(file, classType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//Try to return a default instance
			T newConfig = classType.newInstance();
			newConfig.saveConfig(file);
			return newConfig;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		//Well.. can't do much now.
		return null;
	}
}
