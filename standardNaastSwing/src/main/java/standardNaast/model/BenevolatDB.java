// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:20
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BenevolatDB.java
package standardNaast.model;

import java.io.PrintStream;
import java.sql.*;
import java.util.Vector;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries
public class BenevolatDB {

    public BenevolatDB() {
    }

    public boolean insertBenevolat(Vector data) throws Exception {
        Connection connection;
        PreparedStatement insertStatement = null;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        boolean success;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        insertStatement = null;
        selectStatement = null;
        rs = null;
        success = false;
        try {
            selectStatement = connection.prepareStatement("SELECT BENEVOLAT_SEQ.NEXTVAL FROM DUAL");
            rs = selectStatement.executeQuery();
            rs.next();
            int benevolatID = rs.getInt(1);
            rs.close();
            selectStatement.close();
            insertStatement = connection.prepareStatement("insert into benevolat (BENEVOLAT_ID,PERSONNE_ID,MONTANT,TYPE_BENEVOLAT,DATE_BENEVOLAT)\nvalues (?,?,?,?,to_date(?,'DD-MM-YYYY'))");
            insertStatement.setInt(1, benevolatID);
            insertStatement.setLong(2, ((Long) data.get(0)).longValue());
            insertStatement.setInt(3, ((Integer) data.get(1)).intValue());
            insertStatement.setString(4, (String) data.get(2));
            insertStatement.setString(5, (String) data.get(3));
            insertStatement.execute();
            connection.commit();
            insertStatement.close();
            success = true;
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                rs.close();
                insertStatement.close();
                selectStatement.close();
            } catch (Exception exception) {
            }
        }
        //return success;
    }

    public boolean updateBenevolat(Vector data) throws Exception {
        Connection connection;
        PreparedStatement updateStatement = null;
        boolean success;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        updateStatement = null;
        success = false;
        System.out.println(((Integer) data.get(1)).intValue());
        System.out.println((String) data.get(2));
        System.out.println((String) data.get(3));
        System.out.println(((Integer) data.get(0)).intValue());
        try {
            updateStatement = connection.prepareStatement("update benevolat set MONTANT = ?,\nTYPE_BENEVOLAT = ?,\nDATE_BENEVOLAT = to_date(?,'DD-MM-YYYY') \nwhere benevolat_id = ?");
            updateStatement.setInt(1, ((Integer) data.get(1)).intValue());
            updateStatement.setString(2, (String) data.get(2));
            updateStatement.setString(3, (String) data.get(3));
            updateStatement.setInt(4, ((Integer) data.get(0)).intValue());
            updateStatement.execute();
            connection.commit();
            updateStatement.close();
            success = true;
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                updateStatement.close();
            } catch (Exception exception) {
            }
        }
    }

    public boolean deleteBenevolat(int benevolatID) throws Exception {
        Connection connection;
        PreparedStatement deleteStatement;
        boolean success;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        deleteStatement = null;
        success = false;
        try {
            deleteStatement = connection.prepareStatement("delete from benevolat where benevolat_id = ?");
            deleteStatement.setInt(1, benevolatID);
            deleteStatement.execute();
            connection.commit();
            deleteStatement.close();
            success = true;
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                deleteStatement.close();
            } catch (Exception exception) {
            }
        }
    }

    public Vector getBenevolats(long personneID)
            throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        Vector allBenevolats;
        Vector data;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        ResultSet rs = null;
        Vector benevolat = new Vector();
        allBenevolats = new Vector();
        data = new Vector();
        Vector vector;
        selectStatement = connection.prepareStatement("select benevolat_id,to_char(date_benevolat,'DD-MM-YYYY') \"Date\" , type_benevolat \"B�n�volat\", montant \"Montant\" from benevolat\nwhere personne_id = ? order by date_benevolat");
        selectStatement.setLong(1, personneID);
        rs = selectStatement.executeQuery();
        GlobalQueries gq = new GlobalQueries();
        Vector columnNames = gq.getColumnNames(rs);
        //Vector benevolat;
        while (rs.next()) {
            benevolat = new Vector();
            benevolat.add(new Integer(rs.getInt(1)));
            benevolat.add(rs.getString(2));
            benevolat.add(rs.getString(3));
            benevolat.add(new Integer(rs.getInt(4)));
            allBenevolats.add(benevolat);
        }

        rs.close();
        selectStatement.close();
        data.add(allBenevolats);
        data.add(columnNames);
        return data;

    }
}