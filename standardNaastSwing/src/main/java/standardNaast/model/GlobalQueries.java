// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   GlobalQueries.java
package standardNaast.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// Referenced classes of package standardNaast.model:
//            JDBCConnection
public class GlobalQueries {

    public GlobalQueries() {
    }

    public String getPersonneNameAndSurname(int personneID)
            throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        try {
            connection = JDBCConnection.getInstance().getJDBCConnection();
            selectStatement = null;
            rs = null;
            String s;
            selectStatement = connection.prepareStatement("select nom || ' ' || prenom from personnes where personne_id = ?");
            selectStatement.setInt(1, personneID);
            rs = selectStatement.executeQuery();
            rs.next();
            String nom = rs.getString(1);
            rs.close();
            selectStatement.close();
            return nom;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {


            try {
                rs.close();
                selectStatement.close();
            } catch (Exception exception) {
            }
        }
    }

    public List<String> getListeSaison()
            throws Exception {
        List<String> saison = new ArrayList<String>();
        Connection connection = JDBCConnection.getInstance().getJDBCConnection();
        PreparedStatement pstmt = connection.prepareStatement("select saison_id from saison order by date_fin desc");
        ResultSet rs = null;
        for (rs = pstmt.executeQuery(); rs.next(); saison.add(rs.getString(1)));
        rs.close();
        pstmt.close();
        return saison;
    }

    public Vector getColumnNames(ResultSet rs)
            throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Vector columnNames = new Vector();
        for (int i = 0; i < columnCount; i++) {
            columnNames.add(rsmd.getColumnName(i + 1));
        }

        return columnNames;
    }

    public Vector getBlocsForStandard()
            throws Exception {
        Vector<Integer> blocID;
        Vector<String> blocValue;
        Connection connection;
        blocID = new Vector<Integer>();
        blocValue = new Vector<String>();
        connection = JDBCConnection.getInstance().getJDBCConnection();
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        try {
            selectStatement = connection.prepareStatement("select bloc_id,bloc_value \nfrom bloc \nwhere upper(club) = upper('standard') \nand bloc_id in \n  (select distinct(bloc_id) \n    from prix_place \n    where type_match_id = \n      (select type_match_id \n       from type_match \n       where upper(denomination_match) = upper('abonnement')\n      )\n  )  \norder by bloc_value");

            for (rs = selectStatement.executeQuery(); rs.next(); blocValue.add(rs.getString(2))) {
                blocID.add(new Integer(rs.getInt(1)));
            }

            rs.close();
            selectStatement.close();
            Vector<Vector> data = new Vector();
            data.add(blocID);
            data.add(blocValue);
            return data;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                rs.close();
                selectStatement.close();
            } catch (Exception e) {
            }
        }
    }

    public Vector getAnnees()
            throws Exception {
        Vector annees;
        Connection connection;
        annees = new Vector();
        connection = JDBCConnection.getInstance().getJDBCConnection();
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        try {
            selectStatement = connection.prepareStatement("select annee_cotisation from cotisations order by annee_cotisation desc");

            for (rs = selectStatement.executeQuery(); rs.next(); annees.add(rs.getString(1)));
            rs.close();
            selectStatement.close();
            return annees;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                rs.close();
                selectStatement.close();
            } catch (Exception e) {
            }
        }
    }
}