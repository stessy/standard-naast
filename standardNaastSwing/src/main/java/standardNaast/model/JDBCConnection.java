// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   JDBCConnection.java
package standardNaast.model;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import oracle.jdbc.driver.OracleDriver;

public class JDBCConnection {

    private void jbInit() {
        props = new Properties();
    }

    private JDBCConnection() {
        jbInit();
        try {
            if (!connectionCreated) {
                propertyFile = new File("config.txt");
                props.load(new FileInputStream(propertyFile.getAbsolutePath()));
                String urlConnection = props.getProperty("db_connection_url");
                String username = props.getProperty("db_username");
                String password = props.getProperty("db_password");
                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                connection = DriverManager.getConnection(urlConnection, username, password);
                connectionCreated = true;
                System.out.println("Connection created");
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT VALUE FROM NLS_SESSION_PARAMETERS WHERE PARAMETER = 'NLS_DATE_FORMAT'");
                rs.next();
                String dateFormat = rs.getString(1);
                System.out.println("NLS_DATE_FORMAT:" + dateFormat);
                rs.close();
                rs = stmt.executeQuery("SELECT SYSDATE FROM DUAL");
                rs.next();
                String sysdate = rs.getString(1);
                System.out.println("SYSDATE:" + sysdate);
                rs.close();
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static JDBCConnection getInstance() {
        if (jdbcConnection == null) {
            synchronized (synchronized__) {
                if (jdbcConnection == null) {
                    jdbcConnection = new JDBCConnection();
                }
            }
        }
        return jdbcConnection;
    }

    public Connection getJDBCConnection() {
        return connection;
    }
    public static Object synchronized__;
    private Connection connection;
    private boolean connectionCreated;
    private Properties props;
    private static JDBCConnection jdbcConnection = new JDBCConnection();
    private File propertyFile;
}