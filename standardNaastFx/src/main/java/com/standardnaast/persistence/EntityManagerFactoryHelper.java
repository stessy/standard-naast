package com.standardnaast.persistence;

import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EntityManagerFactoryHelper {


    private static EntityManagerFactory factory;

    private static final Map<String, String> PERSISTENCE_MAP = new HashMap<>();

    static {
        try {
            final Properties props = new Properties();
            final FileInputStream fis = new FileInputStream("init.xml");
            props.loadFromXML(fis);
            String databaseName = props.getProperty("database");
            System.out.println("Database name: " + databaseName);
            String urls = props.getProperty("url");
            System.out.println("URLs: " + urls);
            String[] splittesUrls = urls.split(";");
            for (String url : splittesUrls) {
                if (isDatabaseFileAvailable(url, databaseName)) {
                    final String databaseUrl = "jdbc:h2:mem:standard_naast;INIT=runscript from '" + url
                            + databaseName + "'\\;";
                    EntityManagerFactoryHelper.PERSISTENCE_MAP.put("javax.persistence.jdbc.url",
                            databaseUrl);
                    EntityManagerFactoryHelper.factory = Persistence
                            .createEntityManagerFactory("StandardNaastPeristenceUnit",
                                    EntityManagerFactoryHelper.PERSISTENCE_MAP);
                }
            }
        } catch (ExceptionInInitializerError | IOException e) {
            throw new RuntimeException("Unable to load database", e);
        }
    }

    private static boolean isDatabaseFileAvailable(final String path, final String databaseName) {
        File urlPath = new File(path);
        if (urlPath.exists() && urlPath.isDirectory()) {
            String[] databaseFiles = urlPath.list((dir, name) -> name.equalsIgnoreCase(databaseName));
            return CollectionUtils.isNotEmpty(Arrays.asList(databaseFiles));
        } else {
            return false;
        }

    }

    public static EntityManagerFactory getFactory() {
        return EntityManagerFactoryHelper.factory;
    }

}
