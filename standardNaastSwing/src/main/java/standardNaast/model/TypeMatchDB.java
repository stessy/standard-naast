// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TypeMatchDB.java

package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries

public class TypeMatchDB {

	public TypeMatchDB() {
	}


	public Vector getTypeMatch(final int typeCompetition) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allTypesMatchs;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector typeMatch = new Vector();
		allTypesMatchs = new Vector();
		data = new Vector();
		Vector vector;
		try {
			selectStatement =
					connection
							.prepareStatement("select type_match_id,denomination_match,type_competition_value from type_match,type_competition  where type_competition.TYPE_COMPETITION_ID = type_match.TYPE_COMPETITION_ID  and type_match.TYPE_COMPETITION_ID = ?");
			selectStatement.setInt(1, typeCompetition);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allTypesMatchs.add(typeMatch)) {
				typeMatch = new Vector();
				typeMatch.add(new Integer(rs.getInt(1)));
				typeMatch.add(rs.getString(2));
				typeMatch.add(rs.getString(3));
			}

			rs.close();
			selectStatement.close();
			data.add(allTypesMatchs);
			data.add(columnNames);
			return data;
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				selectStatement.close();
			} catch (Exception e) {
			}
		}
	}


	public Vector getTypeMatch(final String typeCompetition) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allTypesMatchs;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector typeMatch = new Vector();
		allTypesMatchs = new Vector();
		data = new Vector();
		Vector vector;
		try {
			selectStatement =
					connection
							.prepareStatement("select type_match_id,denomination_match,type_competition_value from type_match,type_competition  where type_competition.TYPE_COMPETITION_ID = type_match.TYPE_COMPETITION_ID  and type_competition.TYPE_COMPETITION_VALUE = ?");
			selectStatement.setString(1, typeCompetition);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allTypesMatchs.add(typeMatch)) {
				typeMatch = new Vector();
				typeMatch.add(new Integer(rs.getInt(1)));
				typeMatch.add(rs.getString(2));
				typeMatch.add(rs.getString(3));
			}

			rs.close();
			selectStatement.close();
			data.add(allTypesMatchs);
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


	public void addTypeMatch(final String typeMatch, final int typeCompetitionID) throws Exception {
		Connection connection;
		PreparedStatement insertStatement = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		insertStatement = null;
		try {
			insertStatement =
					connection
							.prepareStatement("insert into type_match(type_match_id,type_competition_id,denomination_match) values(type_match_seq.nextval,?,?)");
			insertStatement.setInt(1, typeCompetitionID);
			insertStatement.setString(2, typeMatch);
			insertStatement.execute();
			insertStatement.close();
			connection.commit();
			JOptionPane.showMessageDialog(null, "Type de match ajoutï¿½", "Type match", 1);
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				insertStatement.close();
			} catch (Exception exception) {
			}
		}
	}


	public Vector getAllTypeMatch() throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allTypesMatchs;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector typeMatch = new Vector();
		allTypesMatchs = new Vector();
		data = new Vector();
		Vector vector;
		try {
			selectStatement =
					connection
							.prepareStatement("select type_match_id ID,denomination_match \"Type de match\",type_competition_value \"Type de compétition\" from type_match tm,type_competition tc where tc.TYPE_COMPETITION_ID = tm.TYPE_COMPETITION_ID\norder by type_competition_value,denomination_match");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allTypesMatchs.add(typeMatch)) {
				typeMatch = new Vector();
				typeMatch.add(new Integer(rs.getInt(1)));
				typeMatch.add(rs.getString(2));
				typeMatch.add(rs.getString(3));
			}

			rs.close();
			selectStatement.close();
			data.add(allTypesMatchs);
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


	public void updateTypeMatch(final int typeMatchID, final String typeMatch, final String typeCompetitionValue)
			throws Exception {
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
							.prepareStatement("select * from type_match where denomination_match = ? and type_competition_id = \n(select type_competition_id from type_competition where upper(type_competition_value)=upper(?)) and type_match_id <> ?");
			selectStatement.setString(1, typeMatch);
			selectStatement.setString(2, typeCompetitionValue);
			selectStatement.setInt(3, typeMatchID);
			rs = selectStatement.executeQuery();
			if (!rs.next()) {
				updateStatement =
						connection
								.prepareStatement("update type_match set denomination_match = ? where type_match_id = ?");
				updateStatement.setString(1, typeMatch);
				updateStatement.setInt(2, typeMatchID);
				updateStatement.executeUpdate();
				updateStatement.close();
				JOptionPane.showMessageDialog(null, "Type de match modifiï¿½", "Type de match", 1);
			} else {
				JOptionPane.showMessageDialog(null, "Type de match dï¿½jï¿½ existant pour ce type de compï¿½tition",
						"Type de match", 0);
			}
			selectStatement.close();
			rs.close();
			connection.commit();
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