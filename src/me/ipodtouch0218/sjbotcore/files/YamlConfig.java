package me.ipodtouch0218.sjbotcore.files;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlConfig {

	@JsonIgnore
	public static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	
	
	public void saveConfig(File file) {
		try {
			mapper.writeValue(file, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends YamlConfig> T loadConfig(File file, Class<T> classType) {
		try {
			//Try to read the file
			return mapper.readValue(file, classType);
		} catch (IOException e) {
			//e.printStackTrace();
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
