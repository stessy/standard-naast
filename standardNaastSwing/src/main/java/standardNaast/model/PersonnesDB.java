// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PersonnesDB.java

package standardNaast.model;

import java.sql.*;
import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection

public class PersonnesDB
{

    public PersonnesDB()
    {
    }

    public void ajouterPersonne(String nom, String prenom, String dateNaissance)
        throws Exception
    {
        Connection connection;
        PreparedStatement insertStatement = null;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        insertStatement = null;
        selectStatement = null;
        rs = null;
        int personneID = -1;
        
        try
        {
            selectStatement = connection.prepareStatement("SELECT PERSONNES_SEQ.NEXTVAL FROM DUAL");
            rs = selectStatement.executeQuery();
            rs.next();
            personneID = rs.getInt(1);
            rs.close();
            selectStatement.close();
            insertStatement = connection.prepareStatement("insert into personnes (PERSONNE_ID,NOM,PRENOM,DATE_NAISSANCE,NUMERO_MEMBRE)\nvalues(?,?,?,to_date(?,'DD_MM_YYYY'),?)");
            insertStatement.setInt(1, personneID);
            insertStatement.setString(2, nom);
            insertStatement.setString(3, prenom);
            insertStatement.setString(4, dateNaissance);
            insertStatement.setInt(5, 10000);
            insertStatement.execute();
            connection.commit();
            insertStatement.close();            
            JOptionPane.showMessageDialog(null, "Membre ajouté", "Ajout Membre", 1);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
        try
        {
            rs.close();
            insertStatement.close();
            selectStatement.close();
        }
        catch(Exception exception) { }
        }
    }
}