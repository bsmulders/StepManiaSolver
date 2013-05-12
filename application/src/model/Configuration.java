/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static Properties properties;
	private static String filename;

	static {
		setFilename("configuration.properties");
		loadConfiguration();
	}

	// Getters and setters
	private static Properties getProperties() {
		return properties;
	}

	private static void setProperties(Properties properties) {
		Configuration.properties = properties;
	}

	private static String getFilename() {
		return filename;
	}

	private static void setFilename(String filename) {
		Configuration.filename = filename;
	}

	public static String getStringSetting(String key) {
		return getProperties().getProperty(key);
	}

	public static int getIntSetting(String key) {
		return Integer.parseInt(getProperties().getProperty(key));
	}

	public static double getDoubleSetting(String key) {
		return Double.parseDouble(getProperties().getProperty(key));
	}

	public static void setSetting(String key, String value) {
		getProperties().setProperty(key, value);
		saveConfiguration();
	}

	public static void setSetting(String key, int value) {
		getProperties().setProperty(key, Integer.toString(value));
		saveConfiguration();
	}

	public static void setSetting(String key, double value) {
		getProperties().setProperty(key, Double.toString(value));
		saveConfiguration();
	}

	public static void removeSetting(String key) {
		getProperties().remove(key);
		saveConfiguration();
	}

	// File IO
	private static void loadConfiguration() {
		try {
			setProperties(new Properties());
			FileInputStream in = new FileInputStream(getFilename());
			getProperties().load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveConfiguration() {
		try {
			FileOutputStream out = new FileOutputStream(getFilename());
			getProperties().store(out, "StepManiaSolver configuration");
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
