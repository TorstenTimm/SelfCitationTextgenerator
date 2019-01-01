package de.voynich.text.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class provides access to conf.properties
 */
public class PropertiesLoader {

	private static final String replaceBy = "replace.by", include = "include.conf";

	private static PropertiesLoader singleton = new PropertiesLoader();

	/**
	 * provides singleton instance of PropertiesLoader
	 */
	public static PropertiesLoader getInstance() {
		return singleton;
	}

	/**
	 * reads a configuration files
	 */
	public Properties readPropertyFile(String fileName) {
		Properties p = new Properties();
		return readPropertyFile(fileName, p);
	}

	/**
	 * reads a configuration files
	 */
	public Properties readPropertyFile(String fileName, Properties p) {
		try {
			// if part of a JAR
			InputStream is = getClass().getResourceAsStream(fileName);
			if (is == null) {
				is = getClass().getClassLoader().getResourceAsStream(fileName);
				if (is == null) {
					throw new RuntimeException("Could not find property file = " + fileName);
				}
			}
			p.load(is);
			is.close();

			/*
			 * to allow conf.properties in any location
			 */
			while (p.getProperty(replaceBy) != null) {
				fileName = p.getProperty(replaceBy);
				p = new Properties();
				loadFile(fileName, p);
			}

			/*
			 * to allow splitting up and including
			 */
			while (p.getProperty(include) != null) {
				fileName = p.getProperty(include);
				p.remove(include);
				loadFile(fileName, p);
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return p;
	}

	/**
	 * loads file as resource or uses class loader to load the class from jar(s)
	 */
	@SuppressWarnings("ConstantConditions")
	private void loadFile(String fileName, Properties p) throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			p.load(new FileInputStream(file));
		} else {
			InputStream is = getClass().getResourceAsStream(fileName);
			if (is == null) {
				is = getClass().getClassLoader().getResourceAsStream(fileName);
			}
			if (is != null) {
				p.load(is);
			} else {
				throw new RuntimeException("COULD NOT FIND FILE = " + fileName);
			}
			if (is != null) {
				is.close();
			}
		}
	}
}
