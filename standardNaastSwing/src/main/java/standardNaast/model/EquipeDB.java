// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EquipeDB.java

package standardNaast.model;

import java.sql.*;
import java.util.Vector;
import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection

public class EquipeDB
{

    public EquipeDB()
    {
    }

    public Vector getAllEquipes()
        throws Exception
    {
        Connection connection;
        PreparedStatement selectStatement = null;
        Vector allEquipes;
        try
        {
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        ResultSet rs = null;
        allEquipes = new Vector();
        Vector vector;
        selectStatement = connection.prepareStatement("select distinct nom_equipe from equipe order by nom_equipe");
        rs = selectStatement.executeQuery();
        while(rs.next()) {
            allEquipes.add(rs.getString(1)); 
        }
        //for(rs = selectStatement.executeQuery(); rs.next(); allEquipes.add(rs.getString(1)));
        rs.close();
        selectStatement.close();
        return allEquipes;
        }
            catch(Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
                
            }
        finally
        {
        try
        {
            selectStatement.close();
        }
        catch(Exception exception) { }
        }
        
    }

    public void ajouterEquipe(String equipe)
        throws Exception
    {
        Connection connection;
        PreparedStatement insertStatement = null;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        insertStatement = null;
        ResultSet rs = null;
        try
        {
            insertStatement = connection.prepareStatement("insert into equipe(equipe_id,nom_equipe) values(equipe_seq.nextval,?)");
            insertStatement.setString(1, equipe);
            insertStatement.execute();
            insertStatement.close();
            connection.commit();
            JOptionPane.showMessageDialog(null, "Equipe ajoutée", "Ajout Equipe", 1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new Exception();
        }
        finally{
        try
        {
            insertStatement.close();
        }
        catch(Exception exception) { }
    }
    }

    public void ajouterEquipeSaison(String saison, String equipe)
        throws Exception
    {
        Connection connection;
        PreparedStatement insertStatement = null;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        insertStatement = null;
        try
        {
            insertStatement = connection.prepareStatement("insert into saison_equipe (saison_equipe_id,saison_id,equipe_id) values(saison_equipe_seq.nextval,?,(select equipe_id from equipe where nom_equipe=?))");
            insertStatement.setString(1, saison);
            insertStatement.setString(2, equipe);
            insertStatement.execute();
            insertStatement.close();
            connection.commit();
            JOptionPane.showMessageDialog(null, "Equipe ajoutée pour une saison", "Ajout Equipe", 1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new Exception();
        }
        finally{
        try
        {
            insertStatement.close();
        }
        catch(Exception exception) { }
        }
    }

    public Vector getEquipesParSaison(String saison)
        throws Exception
    {
        Connection connection;
        PreparedStatement selectStatement = null;
        Vector allEquipes;
        try
        {
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        ResultSet rs = null;
        allEquipes = new Vector();
        Vector vector;
        selectStatement = connection.prepareStatement("select nom_equipe from equipe,saison_equipe where equipe.equipe_id = saison_equipe.equipe_id and saison_equipe.saison_id = ? order by nom_equipe");
        selectStatement.setString(1, saison);
        
        for(rs = selectStatement.executeQuery(); rs.next(); allEquipes.add(rs.getString(1)));
        rs.close();
        selectStatement.close();
        return allEquipes;
        }
        
        catch(Exception e) {
        e.printStackTrace();
        throw new Exception(e.getMessage());
            
        }
        finally
        {
        try
        {
        selectStatement.close();
        }
        catch(Exception exception) { }
        }
    }

    public Vector getEquipesPasParSaison(String saison)
        throws Exception
    {
        Connection connection;
        PreparedStatement selectStatement = null;
        Vector allEquipes;
        try
        {
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        ResultSet rs = null;
        allEquipes = new Vector();
        Vector vector;
        selectStatement = connection.prepareStatement("select nom_equipe from equipe where equipe_id not in (select saison_equipe.equipe_id from equipe,saison_equipe where equipe.equipe_id = saison_equipe.equipe_id and saison_equipe.saison_id = ?) order by nom_equipe");
        selectStatement.setString(1, saison);
        
        for(rs = selectStatement.executeQuery(); rs.next(); allEquipes.add(rs.getString(1)));
        rs.close();
        selectStatement.close();
        return allEquipes;
        }
        catch(Exception e) {
        e.printStackTrace();
        throw new Exception(e.getMessage());
            
        }
        finally
        {
        try
        {
        selectStatement.close();
        }
        catch(Exception exception) { }
        }
    }
}