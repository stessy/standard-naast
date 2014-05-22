// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CommandePlaceDB.java
package standardNaast.model;

import java.sql.*;
import java.util.Vector;

// Referenced classes of package standardNaast.model:
//            JDBCConnection
public class CommandePlaceDB {

    public CommandePlaceDB() {
    }

    public Vector getMatchs()
            throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        Vector match;
        Vector matchID;
        Vector data;
        try {
            connection = JDBCConnection.getInstance().getJDBCConnection();
            selectStatement = null;
            rs = null;
            match = new Vector();
            matchID = new Vector();
            data = new Vector();
            Vector vector;
            selectStatement = connection.prepareStatement("select nom_equipe || ' '|| lieu , MATCH_ID\nfrom MATCH, saison_equipe, equipe\nwhere equipe.equipe_id = saison_equipe.equipe_id\nand match.saison_equipe_id = saison_equipe.saison_equipe_id\nand date_match >= sysdate\norder by date_match");
            rs = selectStatement.executeQuery();
            while (rs.next()) {
                matchID.add(new Integer(rs.getInt(2)));
                match.add(rs.getString(1));

            }


            rs.close();
            selectStatement.close();
            data.add(match);
            data.add(matchID);
            return data;
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

    public Vector getBlocs(int matchID)
            throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        Vector blocs;
        Vector blocID;
        Vector data;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        rs = null;
        blocs = new Vector();
        blocID = new Vector();
        data = new Vector();
        Vector vector;
        try {
            selectStatement = connection.prepareStatement("select lieu from match where match_id = ?");
            selectStatement.setInt(1, matchID);
            rs = selectStatement.executeQuery();
            rs.next();
            String lieu = rs.getString(1);
            rs.close();
            selectStatement.close();
            if (lieu.equalsIgnoreCase("domicile")) {
                selectStatement = connection.prepareStatement("select bloc_value, bloc_id from bloc where upper(club) = upper('standard')");
            } else {
                selectStatement = connection.prepareStatement("select bloc_value, bloc_id from bloc where club = (select nom_equipe\nfrom MATCH, saison_equipe, equipe\nwhere equipe.equipe_id = saison_equipe.equipe_id\nand match.saison_equipe_id = saison_equipe.saison_equipe_id\nand date_match >= sysdate\nand match_id = ?)");
                selectStatement.setInt(1, matchID);
            }

            rs = selectStatement.executeQuery();
            while (rs.next()) {
                blocID.add(new Integer(rs.getInt(2)));
                blocs.add(rs.getString(1));
            }

            rs.close();
            selectStatement.close();
            data.add(blocs);
            data.add(blocID);
            return data;
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
}