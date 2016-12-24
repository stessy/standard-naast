package com.standardnaast.persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerFactoryHelper {

	private static EntityManagerFactory factory;

	private static final Map<String, String> PERSISTENCE_MAP = new HashMap<>();

	static {
		try {
			final Properties props = new Properties();
			final FileInputStream fis = new FileInputStream("init.properties");
			props.load(fis);
			final String url = "jdbc:h2:mem:standard_naast;INIT=runscript from '" + props.getProperty("url")
					+ props.getProperty("database") + "'\\;";
			EntityManagerFactoryHelper.PERSISTENCE_MAP.put("javax.persistence.jdbc.url",
					url);
			EntityManagerFactoryHelper.factory = Persistence
					.createEntityManagerFactory("StandardNaastPeristenceUnit",
							EntityManagerFactoryHelper.PERSISTENCE_MAP);
		} catch (ExceptionInInitializerError | IOException e) {
			throw new RuntimeException("Unable to load database");
		}
	}

	public static EntityManagerFactory getFactory() {
		return EntityManagerFactoryHelper.factory;
	}

}
