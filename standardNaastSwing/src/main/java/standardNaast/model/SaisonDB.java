// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   SaisonDB.java

package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries

public class SaisonDB {

	public SaisonDB() {
	}


	public boolean addSaison(final Vector data) throws Exception {
		PreparedStatement insertStatement = null;
		Connection connection = null;
		insertStatement = null;
		boolean flag;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			insertStatement =
					connection
							.prepareStatement("insert into saison (saison_id,date_debut,date_fin,date_premier_match_championnat) values(?, to_date(?,'DD-MM-YYYY'), to_date(?,'DD-MM-YYYY'), to_date(?,'DD-MM-YYYY'))");
			insertStatement.setString(1, (String) data.get(0));
			insertStatement.setString(2, (String) data.get(1));
			insertStatement.setString(3, (String) data.get(2));
			insertStatement.setString(4, (String) data.get(3));
			insertStatement.execute();
			insertStatement.close();
			connection.commit();
			return true;
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


	public boolean updateSaison(final Vector data) throws Exception {
		PreparedStatement insertStatement = null;
		Connection connection = null;
		insertStatement = null;
		boolean flag;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			insertStatement =
					connection
							.prepareStatement("update saison set \nDATE_DEBUT = to_date(?,'DD-MM-YYYY'), \nDATE_FIN = to_date(?,'DD-MM-YYYY'), \nDATE_PREMIER_MATCH_CHAMPIONNAT = to_date(?,'DD-MM-YYYY')\nwhere SAISON_ID = ?");
			insertStatement.setString(1, (String) data.get(1));
			insertStatement.setString(2, (String) data.get(2));
			insertStatement.setString(3, (String) data.get(3));
			insertStatement.setString(4, (String) data.get(0));
			insertStatement.executeUpdate();
			insertStatement.close();
			connection.commit();
			return true;
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


	public boolean saisonExists(final String saison) throws Exception {
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		boolean success;
		Connection connection = null;
		selectStatement = null;
		rs = null;
		success = false;
		boolean flag;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = connection.prepareStatement("select * from saison  where SAISON_ID = ?");
			selectStatement.setString(1, saison);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				success = true;
			}
			rs.close();
			selectStatement.close();
			return success;
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


	public Vector getSaisons() throws Exception {
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Vector allSaisons;
		Vector data;
		Connection connection = null;
		selectStatement = null;
		rs = null;
		Vector saisonData = new Vector();
		allSaisons = new Vector();
		data = new Vector();
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement =
					connection
							.prepareStatement("select SAISON_ID saison, to_char(DATE_DEBUT,'DD-MM-YYYY') \"Date de début\", to_char(DATE_FIN,'DD-MM-YYYY') \"Date de fin\", to_char(DATE_PREMIER_MATCH_CHAMPIONNAT,'DD-MM-YYYY') \"Date premier match\"  from saison order by saison_id desc");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allSaisons.add(saisonData)) {
				saisonData = new Vector();
				saisonData.add(rs.getString(1));
				saisonData.add(rs.getString(2));
				saisonData.add(rs.getString(3));
				saisonData.add(rs.getString(4));
			}

			rs.close();
			selectStatement.close();
			data.add(allSaisons);
			data.add(columnNames);
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