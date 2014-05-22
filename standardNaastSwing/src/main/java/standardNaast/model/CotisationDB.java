// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CotisationDB.java

package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries

public class CotisationDB {

	public CotisationDB() {
	}


	public boolean insertCotisation(final String annee, final int montant) throws Exception {
		Connection connection;
		PreparedStatement insertStatement = null;
		PreparedStatement selectStatement = null;
		connection = null;
		insertStatement = null;
		selectStatement = null;
		ResultSet rs = null;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = connection.prepareStatement("select * from cotisations where annee_cotisation = ?");
			selectStatement.setString(1, annee);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				JOptionPane.showMessageDialog(null, "Annï¿½e de la cotisation dï¿½jï¿½ existante. Insertion impossible",
						"Cotisation", 0);
				return false;
			} else {

				insertStatement =
						connection
								.prepareStatement("insert into cotisations (annee_cotisation,montant_cotisation) values (?,?)");
				insertStatement.setString(1, annee);
				insertStatement.setInt(2, montant);
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
				insertStatement.close();
				selectStatement.close();
			} catch (Exception exception1) {
			}
		}
	}


	public boolean updateCotisation(final String annee, final int montant) throws Exception {
		PreparedStatement updateStatement = null;
		Connection connection = null;
		updateStatement = null;
		boolean flag;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			updateStatement =
					connection
							.prepareStatement("update cotisations set montant_cotisation = ? where annee_cotisation = ?");
			updateStatement.setInt(1, montant);
			updateStatement.setString(2, annee);
			updateStatement.executeUpdate();
			updateStatement.close();
			connection.commit();
			return true;
		} catch (Exception sqle) {
			sqle.printStackTrace();
			JOptionPane.showMessageDialog(
					null,
					(new StringBuilder()).append("Erreur lors de l'ajout de la cotisation. Erreur: ")
							.append(sqle.getMessage()).toString(), "Cotisation", 64);
			throw new Exception(sqle.getMessage());
		} finally {
			try {
				updateStatement.close();
			} catch (Exception exception) {
			}
		}

	}


	public Vector getCotisations() throws Exception {
		PreparedStatement selectStatement = null;
		Vector allCotisations;
		Vector data;
		Connection connection = null;
		selectStatement = null;
		ResultSet rs = null;
		Vector cotisations = new Vector();
		allCotisations = new Vector();
		data = new Vector();
		Vector vector;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement =
					connection
							.prepareStatement("select annee_cotisation Année, montant_cotisation Montant from cotisations");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allCotisations.add(cotisations)) {
				cotisations = new Vector();
				cotisations.add(rs.getString(1));
				cotisations.add(Integer.valueOf(rs.getInt(2)));
			}

			rs.close();
			selectStatement.close();
			data.add(allCotisations);
			data.add(columnNames);
			return data;
		} catch (Exception sqle) {
			JOptionPane.showMessageDialog(null, (new StringBuilder()).append("Erreur: ").append(sqle.getMessage())
					.toString(), "Cotisation", JOptionPane.ERROR_MESSAGE);
			throw new Exception(sqle.getMessage());

		} finally {
			try {
				selectStatement.close();
			} catch (Exception exception) {
			}
		}
	}


	public int getMontantCotisation(final String annee) throws Exception {
		PreparedStatement selectStatement = null;
		Connection connection = null;
		selectStatement = null;
		ResultSet rs = null;
		int montantCotisation = 0;
		int i;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement =
					connection
							.prepareStatement("select montant_cotisation from cotisations where annee_cotisation = ?");
			selectStatement.setString(1, annee);
			rs = selectStatement.executeQuery();
			rs.next();
			montantCotisation = rs.getInt(1);
			rs.close();
			selectStatement.close();
			return montantCotisation;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, (new StringBuilder()).append("Erreur: ").append(e.getMessage())
					.toString(), "Cotisation", 64);
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
			} catch (Exception exception) {
			}
		}
	}
}
