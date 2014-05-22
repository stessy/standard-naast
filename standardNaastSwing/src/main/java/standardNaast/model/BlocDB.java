// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:20
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BlocDB.java
package standardNaast.model;

import java.sql.*;
import java.util.Vector;

// Referenced classes of package standardNaast.model:
//            JDBCConnection
public class BlocDB {

    public BlocDB() {
    }

    public boolean blocExists(String bloc, String equipe)
            throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        boolean blocExists;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        rs = null;
        blocExists = false;
        try {
            selectStatement = connection.prepareStatement("select * from bloc where bloc_value = ? and equipe_id = (select equipe_id from equipe where nom_equipe = ?)");
            selectStatement.setString(1, bloc);
            selectStatement.setString(2, equipe);
            rs = selectStatement.executeQuery();
            if (rs.next()) {
                blocExists = true;
            }
            rs.close();
            selectStatement.close();
            return blocExists;
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

    public void insertPrixAbonnementBloc(String blocs[], int montant, String saison, int tarif) {
        Connection connection;
        PreparedStatement insertStatement = null;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        int benevolatID;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        insertStatement = null;
        selectStatement = null;
        rs = null;
        benevolatID = 0;
        int typePersonneID = 0;
        boolean success = false;
        try {
            selectStatement = connection.prepareStatement("select type_personne_id from type_personne where tarif = ?");
            selectStatement.setInt(1, tarif);
            for (rs = selectStatement.executeQuery(); rs.next(); insertStatement.close()) {
                typePersonneID = rs.getInt(1);
                insertStatement = connection.prepareStatement("insert into prix_place (prix_place_id,bloc_id,type_personne,type_match_id,montant,saison,tarif,abonne)\nvalues (prix_place_seq.NEXTVAL,(select bloc_id from bloc where bloc_value = ? and upper(club) = upper ('standard')),?,(select type_match_id from type_match");
                insertStatement.setInt(1, benevolatID);
                insertStatement.execute();
                connection.commit();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                insertStatement.close();
                selectStatement.close();
            } catch (Exception exception) {
            }
        }

    }

    public int getBlocId(String blocValue) throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        int blocID;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        rs = null;
        blocID = 0;
        try {
            selectStatement = connection.prepareStatement("select BLOC_ID from bloc where bloc_value = ? and upper(club) = upper(standard)");
            selectStatement.setString(1, blocValue);
            rs = selectStatement.executeQuery();
            rs.next();
            blocID = rs.getInt(1);
            rs.close();
            selectStatement.close();
            return blocID;
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

    public Vector getBlocsFromEquipe(String equipe) throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        Vector blocValues;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        rs = null;
        blocValues = new Vector();
        try {
            selectStatement = connection.prepareStatement("select bloc_value from bloc where equipe_id = (select equipe_id from equipe where nom_equipe = ?)");
            selectStatement.setString(1, equipe);
            rs = selectStatement.executeQuery();
            while (rs.next()) {
                blocValues.add(rs.getString(1));
            }

            rs.close();
            selectStatement.close();
            return blocValues;
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

    public void insertBloc(String blocValue, String equipe)
            throws Exception {
        Connection connection;
        PreparedStatement insertStatement = null;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        insertStatement = null;
        if (!blocExists(blocValue, equipe)) {

            try {
                insertStatement = connection.prepareStatement("insert into bloc(bloc_id,bloc_value,equipe_id) values (bloc_seq.nextval,?,(select equipe_id from equipe where nom_equipe = ?)");
                insertStatement.setString(1, blocValue);
                insertStatement.setString(2, equipe);
                insertStatement.execute();
                insertStatement.close();
                connection.commit();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(e.getMessage());
            } finally {
                try {
                    insertStatement.close();
                } catch (Exception exception) {
                }
            }
        }
    }
}