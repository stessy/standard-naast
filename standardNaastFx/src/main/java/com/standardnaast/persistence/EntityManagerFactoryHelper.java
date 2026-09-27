package com.standardnaast.persistence;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EntityManagerFactoryHelper {

    private static EntityManagerFactory factory;

    private static final Map<String, String> PERSISTENCE_MAP = new HashMap<>();

    static {
        try {
            initFactory();
        } catch (Exception e) {
            System.err.println("Error initializing EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unable to load database", e);
        }
    }

    private static void initFactory() {
        Properties props = loadProperties();
        String databaseName = props.getProperty("database", "standardnaast");
        String urls = props.getProperty("url", "./data/;../data/");
        String user = props.getProperty("user", "sa");
        String password = props.getProperty("password", "standard");

        String dbPath = findDatabasePath(urls, databaseName);
        if (dbPath != null) {
            final String databaseUrl = "jdbc:h2:file:" + dbPath
                    + ";DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;NON_KEYWORDS=VALUE";
            System.out.println("Connecting to database at: " + databaseUrl);
            EntityManagerFactoryHelper.PERSISTENCE_MAP.put("javax.persistence.jdbc.url", databaseUrl);
            EntityManagerFactoryHelper.PERSISTENCE_MAP.put("javax.persistence.jdbc.user", user);
            EntityManagerFactoryHelper.PERSISTENCE_MAP.put("javax.persistence.jdbc.password", password);
            EntityManagerFactoryHelper.factory = Persistence.createEntityManagerFactory("StandardNaastPeristenceUnit",
                    EntityManagerFactoryHelper.PERSISTENCE_MAP);
        } else {
            System.out.println("Database file standardnaast not found via search paths, attempting fallback default");
            EntityManagerFactoryHelper.PERSISTENCE_MAP.put("javax.persistence.jdbc.user", user);
            EntityManagerFactoryHelper.PERSISTENCE_MAP.put("javax.persistence.jdbc.password", password);
            EntityManagerFactoryHelper.factory = Persistence.createEntityManagerFactory("StandardNaastPeristenceUnit",
                    EntityManagerFactoryHelper.PERSISTENCE_MAP);
        }
    }

    private static Properties loadProperties() {
        final Properties props = new Properties();
        final String[] possibleFiles = {"init.xml", "../init.xml", "standardNaastFx/init.xml"};
        for (String filePath : possibleFiles) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.loadFromXML(fis);
                    return props;
                } catch (Exception ignored) {
                }
            }
        }
        return props;
    }

    private static String findDatabasePath(final String urls, final String databaseName) {
        if (urls != null) {
            String[] candidateDirs = urls.split(";");
            for (String dir : candidateDirs) {
                if (StringUtils.isBlank(dir)) {
                    continue;
                }
                File dirFile = new File(dir.trim());
                if (dirFile.exists() && dirFile.isDirectory()) {
                    File mvDbFile = new File(dirFile, databaseName + ".mv.db");
                    if (mvDbFile.exists()) {
                        return new File(dirFile, databaseName).getAbsolutePath();
                    }
                    File dbFile = new File(dirFile, databaseName);
                    if (dbFile.exists()) {
                        return dbFile.getAbsolutePath();
                    }
                }
            }
        }
        File[] defaultDirs = {new File("data"), new File("../data"), new File("standardNaastFx/data")};
        for (File dirFile : defaultDirs) {
            if (dirFile.exists() && dirFile.isDirectory()) {
                File mvDbFile = new File(dirFile, databaseName + ".mv.db");
                if (mvDbFile.exists()) {
                    return new File(dirFile, databaseName).getAbsolutePath();
                }
            }
        }
        return null;
    }

    public static EntityManagerFactory getFactory() {
        return EntityManagerFactoryHelper.factory;
    }

}
