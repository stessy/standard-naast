// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:20
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EnvoiEmail.java
package standardNaast;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import oracle.jdbc.OracleDriver;
import standardNaast.utils.Email;
import standardNaast.utils.ProgressBarDlg;

public class EnvoiEmail extends JFrame {

    private void jbInit() {
        props = new Properties();
        username = "";
        smtpHost = "";
        password = "";
        fileToSend = "";
        mailFrom = "";
        nameFrom = "";
        documentTitle = "";
    }

    public EnvoiEmail(String titreEmail, String corpEmail) {
        jbInit();
        try {
            StringBuilder buf = new StringBuilder();
            File f = new File("config.txt");
            props.load(new FileInputStream(f.getAbsolutePath()));
            username = props.getProperty("mail_username");
            password = props.getProperty("mail_password");
            smtpHost = props.getProperty("smtpMailServer");
            mailFrom = props.getProperty("mail_from");
            nameFrom = props.getProperty("name_from");
            List<List<String>> allEmails = getEmailFromDB();
            ProgressBarDlg bar = new ProgressBarDlg(allEmails.size());
            for (List<String> email : allEmails) {
                String nom = (String) email.get(0);
                String emailAddress = (String) email.get(1);
                Email mail = new Email(smtpHost, username, password);
                mail.addReceipientTo(nom, emailAddress);
                mail.addAttachment("Lettre d'information.doc", readCompleteFile(new File("Lettre_Information.doc")));
                mail.sendWithAuthentification(nameFrom, mailFrom, titreEmail, corpEmail);
            }
            JOptionPane.showMessageDialog(null, (new StringBuilder()).append("Tous les emails (Total de ").append(allEmails.size()).append(") ont été envoyé").toString());
            bar.close();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] readCompleteFile(File aFile) {
        RandomAccessFile raf = null;
        byte buffer[] = null;
        if (!aFile.exists()) {
            System.out.println((new StringBuilder()).append("readFile failure:").append(aFile.getAbsolutePath()).append(" does not exist or cannot read!").toString());
        } else {
            try {
                raf = new RandomAccessFile(aFile, "r");
                int len = (int) raf.length();
                buffer = new byte[len];
                raf.readFully(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception exception1) {
                }
            }
        }
        return buffer;
    }

    private List<List<String>> getEmailFromDB()
            throws Exception {
        List<List<String>> allEmails = new ArrayList<List<String>>();
        try {
            Connection con = openConnectionToDB();
            PreparedStatement pstmt = con.prepareStatement("select nom ||' '|| prenom, email from personnes where email is not null and numero_membre < 10000");
            ResultSet rs = null;
            List<String> email;
            rs = pstmt.executeQuery();
            while (rs.next()) {
                email = new ArrayList<String>();
                email.add(rs.getString(1));
                email.add(rs.getString(2));
                allEmails.add(email);
            }

            rs.close();
            pstmt.close();
            con.close();
            return allEmails;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new Exception();
    }

    private Connection openConnectionToDB()
            throws Exception {
        try {
            String database = props.getProperty("db_connection_url");
            String userName = props.getProperty("db_username");
            String password = props.getProperty("db_password");
            DriverManager.registerDriver(new OracleDriver());
            Connection connection = DriverManager.getConnection(database, userName, password);
            connection.setAutoCommit(false);
            Connection connection1 = connection;
            return connection1;
        } catch (Exception e) {
            System.out.println("Exception in openConnectionToDB");
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new Exception();
        }
    }
    Properties props;
    String username;
    String smtpHost;
    String password;
    String fileToSend;
    String mailFrom;
    String nameFrom;
    String documentTitle;
}