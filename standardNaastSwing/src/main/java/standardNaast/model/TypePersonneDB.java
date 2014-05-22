// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TypePersonneDB.java

package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries

public class TypePersonneDB {

	public TypePersonneDB() {
	}


	public Vector getTypePersonnes() throws Exception {
		Vector allTypePersonnes;
		Vector data;
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Vector typePersonne = new Vector();
		allTypePersonnes = new Vector();
		data = new Vector();
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		rs = null;
		Vector vector;
		try {
			selectStatement =
					connection
							.prepareStatement("select TYPE_PERSONNE_ID id,DENOMINATION_TYPE_PERSONNE \"dénomination\",AGE_MINIMUM \"Ã¢ge minimum\",AGE_MAXIMUM \"Ã¢ge maximum\",ETUDIANT \"étudiant\" ,MEMBRE \"membre\", TARIF \"tarif\" from type_personne order by age_minimum");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector colunmNames = gq.getColumnNames(rs);

			for (; rs.next(); allTypePersonnes.add(typePersonne)) {
				typePersonne = new Vector();
				typePersonne.add(Integer.valueOf(rs.getInt(1)));
				typePersonne.add(rs.getString(2));
				typePersonne.add(Integer.valueOf(rs.getInt(3)));
				typePersonne.add(Integer.valueOf(rs.getInt(4)));
				typePersonne.add(new Boolean(rs.getInt(5) != 0 ? "true" : "false"));
				typePersonne.add(new Boolean(rs.getInt(6) != 0 ? "true" : "false"));
				typePersonne.add(Integer.valueOf(rs.getInt(7)));
			}

			rs.close();
			selectStatement.close();
			data.add(allTypePersonnes);
			data.add(colunmNames);
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


	public boolean addTypePersonne(final Vector data) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		PreparedStatement insertStatement = null;
		ResultSet rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		insertStatement = null;
		rs = null;
		boolean flag;
		try {
			selectStatement =
					connection
							.prepareStatement("select * from type_personne where age_minimum = ? and age_maximum = ? and etudiant = ? and membre = ?");
			selectStatement.setInt(1, Integer.parseInt((String) data.get(0)));
			selectStatement.setInt(2, Integer.parseInt((String) data.get(1)));
			selectStatement.setInt(3, Integer.parseInt((String) data.get(2)));
			selectStatement.setInt(4, Integer.parseInt((String) data.get(3)));
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				JOptionPane.showMessageDialog(null, "Type de personne dï¿½jï¿½ existant.");
				rs.close();
				selectStatement.close();
				return false;
			} else {
				insertStatement =
						connection
								.prepareStatement("insert into type_personne (TYPE_PERSONNE_ID,DENOMINATION_TYPE_PERSONNE,AGE_MINIMUM,AGE_MAXIMUM,ETUDIANT,MEMBRE,TARIF)\nvalues (type_personne_seq.nextval,?,?,?,?,?,?)");
				insertStatement.setString(1, (String) data.get(4));
				insertStatement.setInt(2, Integer.parseInt((String) data.get(0)));
				insertStatement.setInt(3, Integer.parseInt((String) data.get(1)));
				insertStatement.setInt(4, Integer.parseInt((String) data.get(2)));
				insertStatement.setInt(5, Integer.parseInt((String) data.get(3)));
				insertStatement.setInt(6, Integer.parseInt((String) data.get(5)));
				insertStatement.execute();
				insertStatement.close();
				connection.commit();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
				selectStatement.close();
				insertStatement.close();
			} catch (Exception exception1) {
			}
		}
	}


	public boolean updateTypePersonne(final Vector data) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		PreparedStatement updateStatement = null;
		ResultSet rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		updateStatement = null;
		rs = null;
		boolean flag;
		try {
			selectStatement =
					connection
							.prepareStatement("select * from type_personne where age_minimum = ? and age_maximum = ? and etudiant = ? and membre = ? and type_personne_id <> ?");
			selectStatement.setInt(1, Integer.parseInt((String) data.get(0)));
			selectStatement.setInt(2, Integer.parseInt((String) data.get(1)));
			selectStatement.setInt(3, Integer.parseInt((String) data.get(2)));
			selectStatement.setInt(4, Integer.parseInt((String) data.get(3)));
			selectStatement.setInt(5, Integer.parseInt((String) data.get(6)));
			rs = selectStatement.executeQuery();
			if (!rs.next()) {
				JOptionPane.showMessageDialog(null, "Type de personne dï¿½jï¿½ existant.");
				rs.close();
				selectStatement.close();
				return false;
			} else {
				updateStatement =
						connection
								.prepareStatement("update type_personne \nset denomination_type_personne = ?,\nage_minimum = ?,\nage_maximum = ?,\netudiant = ?,\nmembre = ?,\n tarif = ? \nwhere type_personne_id = ?");
				updateStatement.setString(1, (String) data.get(4));
				updateStatement.setInt(2, Integer.parseInt((String) data.get(0)));
				updateStatement.setInt(3, Integer.parseInt((String) data.get(1)));
				updateStatement.setInt(4, Integer.parseInt((String) data.get(2)));
				updateStatement.setInt(5, Integer.parseInt((String) data.get(3)));
				updateStatement.setInt(6, Integer.parseInt((String) data.get(5)));
				updateStatement.setInt(7, Integer.parseInt((String) data.get(6)));
				updateStatement.executeUpdate();
				updateStatement.close();
				connection.commit();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
				selectStatement.close();
				updateStatement.close();
			} catch (Exception exception1) {
			}
		}
	}
}