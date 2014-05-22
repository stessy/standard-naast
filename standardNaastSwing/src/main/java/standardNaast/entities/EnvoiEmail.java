// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:20
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EnvoiEmail.java
package standardNaast.entities;

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

    /*public static void main(String args[])
     {
     new EnvoiEmail();
     }*/
    public EnvoiEmail(String titreEmail, String corpEmail) {
        jbInit();
        try {
            StringBuffer buf = new StringBuffer();
            File f = new File("config.txt");
            String extension = "";
            props.load(new FileInputStream(f.getAbsolutePath()));
            username = props.getProperty("mail_username");
            password = props.getProperty("mail_password");
            smtpHost = props.getProperty("smtpMailServer");
            mailFrom = props.getProperty("mail_from");
            nameFrom = props.getProperty("name_from");
            /*JFileChooser chooser = new JFileChooser();
             chooser.setDialogTitle("Choisir le fichier � envoyer");
             JOptionPane.showMessageDialog(null, "Veuillez d'abord s�lectionner le fichier qui contient le corps du message", "Information", 1);
             int returnVal = chooser.showOpenDialog(new JFrame());
             if(returnVal == 0)
             {
             fileToSend = chooser.getSelectedFile().getAbsolutePath();
             RandomAccessFile raf = new RandomAccessFile(fileToSend, "r");
             long filePointer = 0L;
             for(long longueur = raf.length(); filePointer < longueur; filePointer = raf.getFilePointer())
             {
             buf.append(raf.readLine());
             buf.append("\n");
             }

             raf.close();
             }
             JOptionPane.showMessageDialog(null, "Veuillez maintenant s�lectionner le fichier � joindre � l'email", "Information", 1);
             returnVal = chooser.showOpenDialog(new JFrame());
             if(returnVal == 0)
             {
             fileToSend = chooser.getSelectedFile().getAbsolutePath();
             extension = fileToSend.substring(fileToSend.lastIndexOf("."));
             }
             String titreEmail = JOptionPane.showInputDialog(null, "Veuillez donner le titre de l'email");
             String titreDocument = JOptionPane.showInputDialog(null, "Veuillez donner le titre du document en pi�ce jointe");*/
            Vector allEmails = getEmailFromDB();
            ProgressBarDlg bar = new ProgressBarDlg(allEmails.size());
            for (Enumeration allEnumeration = allEmails.elements(); allEnumeration.hasMoreElements(); bar.doneOne()) {
                Vector emailInfo = (Vector) allEnumeration.nextElement();
                String nom = (String) emailInfo.get(0);
                String email = (String) emailInfo.get(1);
                Email mail = new Email(smtpHost, username, password);
                mail.addReceipientTo(nom, email);
                mail.addAttachment("Lettre d'information.doc", readCompleteFile(new File("Lettre_Information.doc")));
                mail.sendWithAuthentification(nameFrom, mailFrom, titreEmail, corpEmail);

                /* mail.addAttachment((new StringBuilder()).append(titreDocument).append(extension).toString(), readCompleteFile(new File(fileToSend)));
                 mail.sendWithAuthentification(nameFrom, mailFrom, titreEmail, buf.toString());*/
            }

            JOptionPane.showMessageDialog(null, (new StringBuilder()).append("Tous les emails (Total de ").append(allEmails.size()).append(") ont �t� envoy�").toString());
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

    private Vector getEmailFromDB()
            throws Exception {
        Vector allEmails = new Vector();
        try {
            Connection con = openConnectionToDB();
            PreparedStatement pstmt = con.prepareStatement("select nom ||' '|| prenom, email from personnes where email is not null and numero_membre < 10000");
            ResultSet rs = null;
            Vector email;
            for (rs = pstmt.executeQuery(); rs.next(); allEmails.add(email)) {
                email = new Vector();
                email.add(rs.getString(1));
                email.add(rs.getString(2));
            }

            rs.close();
            pstmt.close();
            con.close();
            Vector vector = allEmails;
            return vector;
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