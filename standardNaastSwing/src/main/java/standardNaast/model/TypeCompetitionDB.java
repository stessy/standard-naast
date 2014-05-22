// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TypeCompetitionDB.java

package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries

public class TypeCompetitionDB {

	public TypeCompetitionDB() {
	}


	public Vector getTypeCompetition() throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allTypesCompetitions;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector typeCompetition = new Vector();
		allTypesCompetitions = new Vector();
		data = new Vector();
		Vector vector;
		try {
			selectStatement =
					connection
							.prepareStatement("select type_competition_id  ID, type_competition_value  \"Type de compétition\" from type_competition order by type_competition_value");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);
			for (int i = 0; i < columnNames.size(); i++) {
				System.out.println((String) columnNames.get(i));
			}

			for (; rs.next(); allTypesCompetitions.add(typeCompetition)) {
				typeCompetition = new Vector();
				typeCompetition.add(new Integer(rs.getInt(1)));
				typeCompetition.add(rs.getString(2));
			}

			rs.close();
			selectStatement.close();
			data.add(allTypesCompetitions);
			data.add(columnNames);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
			} catch (Exception exception) {
			}
		}
	}


	public void addTypeCompetition(final String competitionValue) throws Exception {
		Connection connection;
		PreparedStatement insertStatement = null;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		insertStatement = null;
		selectStatement = null;
		rs = null;
		try {
			selectStatement =
					connection.prepareStatement("select * from type_competition where type_competition_value = ?");
			selectStatement.setString(1, competitionValue);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				JOptionPane.showMessageDialog(null, "Type de compï¿½tition dï¿½jï¿½ dï¿½fini", "type de compï¿½tition", 0);
			} else {
				insertStatement =
						connection
								.prepareStatement("insert into type_competition(type_competition_id,type_competition_value) values(type_competition_seq.nextval,?)");
				insertStatement.setString(1, competitionValue);
				insertStatement.execute();
				insertStatement.close();
				connection.commit();
				JOptionPane.showMessageDialog(null, "Type de compï¿½tition ajoutï¿½", "Type de compï¿½tition", 1);
			}
			rs.close();
			selectStatement.close();
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				rs.close();
				selectStatement.close();
				insertStatement.close();
			} catch (Exception exception) {
			}
		}
	}


	public void updateTypeCompetition(final String typeCompetition, final int typeCompetitionID) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		PreparedStatement updateStatement = null;
		ResultSet rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		updateStatement = null;
		rs = null;
		try {
			selectStatement =
					connection
							.prepareStatement("select * from type_competition where type_competition_value=? and type_competition_id <> ?");
			selectStatement.setString(1, typeCompetition);
			selectStatement.setInt(2, typeCompetitionID);
			rs = selectStatement.executeQuery();
			if (!rs.next()) {
				updateStatement =
						connection
								.prepareStatement("update type_competition set type_competition_value = ? where type_competition_id = ?");
				updateStatement.setString(1, typeCompetition);
				updateStatement.setInt(2, typeCompetitionID);
				updateStatement.executeUpdate();
				updateStatement.close();
			}
			selectStatement.close();
			rs.close();
			connection.commit();
			JOptionPane.showMessageDialog(null, "Type de compï¿½tition modifiï¿½", "Type de compï¿½tition", 1);
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				selectStatement.close();
				updateStatement.close();
				rs.close();
			} catch (Exception exception) {
			}
		}
	}
}