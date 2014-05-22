// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PersonneCotisationDB.java
package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries
public class PersonneCotisationDB {

	public PersonneCotisationDB() {
	}

	public int insertCotisation(final Vector data) throws Exception {
		Connection connection;
		PreparedStatement insertStatement = null;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		long personneID;
		String annee;
		String datePaiement;
		int numeroMembre;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		insertStatement = null;
		selectStatement = null;
		PreparedStatement updateStatement = null;
		rs = null;
		boolean success = false;
		personneID = ((Long) data.get(0)).longValue();
		annee = (String) data.get(1);
		datePaiement = (String) data.get(2);
		numeroMembre = 0;
		int i;
		try {
			selectStatement = connection
					.prepareStatement("select * from personnes_cotisations where personne_id = ? and annee_cotisation2 = ?");
			selectStatement.setLong(1, personneID);
			selectStatement.setString(2, annee);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				JOptionPane
						.showMessageDialog(
								null,
								(new StringBuilder())
										.append("Ce membre a dï¿½jï¿½ une cotisation payï¿½e pour l'annï¿½e ")
										.append(annee).toString());
				success = false;
				rs.close();
				selectStatement.close();
			} else {
				insertStatement = connection
						.prepareStatement("insert into personnes_cotisations (PERSONNE_ID,ANNEE_COTISATION2,DATE_PAIEMENT,CARTE_MEMBRE_ENVOYEE)\nvalues(?,?,to_date(?,'DD-MM-YYYY'),0)");
				insertStatement.setLong(1, personneID);
				insertStatement.setString(2, annee);
				insertStatement.setString(3, datePaiement);
				insertStatement.execute();
				insertStatement.close();
				selectStatement = connection
						.prepareStatement("select numero_membre from personnes where personne_id = ?");
				selectStatement.setLong(1, personneID);
				rs = selectStatement.executeQuery();
				rs.next();
				numeroMembre = rs.getInt(1);
				rs.close();
				selectStatement.close();
				if (numeroMembre == 10000) {
					selectStatement = connection
							.prepareStatement("select max(numero_membre) + 1 from personnes where numero_membre < 10000");
					rs = selectStatement.executeQuery();
					rs.next();
					numeroMembre = rs.getInt(1);
					rs.close();
					selectStatement.close();
					updateStatement = connection
							.prepareStatement("update personnes set numero_membre = ? where personne_id = ?");
					updateStatement.setInt(1, numeroMembre);
					updateStatement.setLong(2, personneID);
					updateStatement.executeUpdate();
					updateStatement.close();
				}
				connection.commit();
				success = true;

			}
			return numeroMembre;
		} catch (Exception e) {
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
	}

	public boolean deleteCotisation(final long personneID, final String annee)
			throws Exception {
		Connection connection;
		PreparedStatement deleteStatement;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		deleteStatement = null;
		boolean success = false;
		boolean flag;
		try {
			deleteStatement = connection
					.prepareStatement("delete from personnes_cotisations where personne_id = ? and annee_cotisation = ?");
			deleteStatement.setLong(1, personneID);
			deleteStatement.setString(2, annee);
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

	public Vector getCotisations(final long personneID) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allCotisations;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector cotisation = new Vector();
		allCotisations = new Vector();
		data = new Vector();
		boolean success = false;
		Vector vector;
		try {
			selectStatement = connection
					.prepareStatement("select annee_cotisation2 \"Année Cotisation\", to_char(date_paiement,'DD-MM-YYYY') \"Date de paiement\", carte_membre_envoyee \"Carte de membre envoyée\"  from personnes_cotisations\nwhere personne_id = ? order by annee_cotisation2");
			selectStatement.setInt(1, (int) personneID);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allCotisations.add(cotisation)) {
				cotisation = new Vector();
				cotisation.add(rs.getString(1));
				cotisation.add(rs.getString(2));
				cotisation.add(new Integer(rs.getInt(3)));
			}

			rs.close();
			selectStatement.close();
			data.add(allCotisations);
			data.add(columnNames);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				selectStatement.close();
			} catch (Exception exception) {
			}
		}
	}

	public Vector getCotisation(final long personneID) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector cotisation;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		cotisation = new Vector();
		String annee = null;
		Vector vector;
		try {
			selectStatement = connection
					.prepareStatement("select to_char(sysdate,'YYYY') from dual");
			rs = selectStatement.executeQuery();
			rs.next();
			annee = rs.getString(1);
			rs.close();
			selectStatement.close();
			selectStatement = connection
					.prepareStatement("select annee_cotisation2 \"Année Cotisation\", to_char(date_paiement,'DD-MM-YYYY') \"Date de paiement\", carte_membre_envoyee \"Carte de membre envoyée\"  from personnes_cotisations\nwhere personne_id = ? and annee_cotisation2 = ?");
			selectStatement.setLong(1, personneID);
			selectStatement.setString(2, annee);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				cotisation = new Vector();
				cotisation.add(rs.getString(1));
				cotisation.add(rs.getString(2));
				cotisation.add(new Integer(rs.getInt(3)));
			}
			rs.close();
			selectStatement.close();
			return cotisation;
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

	public Vector getCotisationsPayeesParAnnee(final String annee)
			throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allCotisations;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector cotisation = new Vector();
		allCotisations = new Vector();
		data = new Vector();
		Vector vector;
		try {
			selectStatement = connection
					.prepareStatement("select \nnumero_membre \"Numï¿½ro de membre\", \nnom \"Nom\", prenom \"Prï¿½nom\", \nto_char(date_paiement,'DD-MM-YYYY') \"Date de paiement\", \ndecode(carte_membre_envoyee,0,'NON',1,'OUI',carte_membre_envoyee) \"Carte de membre envoyï¿½e\" \nfrom personnes,personnes_cotisations\nwhere personnes.personne_id = personnes_cotisations.personne_id \nand annee_cotisation = ? \norder by numero_membre");
			selectStatement.setString(1, annee);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allCotisations.add(cotisation)) {
				cotisation = new Vector();
				cotisation.add(Integer.valueOf(rs.getInt(1)));
				cotisation.add(rs.getString(2));
				cotisation.add(rs.getString(3));
				cotisation.add(rs.getString(4));
				cotisation.add(rs.getString(5));

			}

			rs.close();
			selectStatement.close();
			data.add(allCotisations);
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

	public Vector getCotisationsNonPayeesParAnnee(final String annee)
			throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allCotisations;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector cotisation = new Vector();
		allCotisations = new Vector();
		data = new Vector();
		Vector vector;
		try {
			selectStatement = connection
					.prepareStatement("select \nnumero_membre \"Numï¿½ro de membre\", \nnom \"Nom\", \nprenom \"Prï¿½nom\" \nfrom personnes \nwhere numero_membre < 10000 \nand personne_id not in \n  (select personne_id from personnes_cotisations where annee_cotisation = ?)\norder by numero_membre");
			selectStatement.setString(1, annee);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allCotisations.add(cotisation)) {
				cotisation = new Vector();
				cotisation.add(Integer.valueOf(rs.getInt(1)));
				cotisation.add(rs.getString(2));
				cotisation.add(rs.getString(3));
			}

			rs.close();
			selectStatement.close();
			data.add(allCotisations);
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

	public void insertCotisation(final int numeroMembre, final String annee,
			final String datePaiement) throws Exception {
		Connection connection;
		PreparedStatement insertStatement = null;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		insertStatement = null;
		selectStatement = null;
		rs = null;
		boolean success = false;
		int personneID = 0;
		try {
			selectStatement = connection
					.prepareStatement("select personne_id from personnes where numero_membre = ?");
			selectStatement.setInt(1, numeroMembre);
			rs = selectStatement.executeQuery();
			rs.next();
			personneID = rs.getInt(1);
			rs.close();
			selectStatement.close();
			insertStatement = connection
					.prepareStatement("insert into personnes_cotisations (PERSONNE_ID,ANNEE_COTISATION,DATE_PAIEMENT,CARTE_MEMBRE_ENVOYEE)\nvalues(?,?,to_date(?,'DD-MM-YYYY'),0)");
			insertStatement.setInt(1, personneID);
			insertStatement.setString(2, annee);
			insertStatement.setString(3, datePaiement);
			insertStatement.execute();
			insertStatement.close();
			connection.commit();
			success = true;
		} catch (SQLException e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				rs.close();
				insertStatement.close();
				selectStatement.close();
			} catch (Exception exception) {
			}
		}
	}

	public Vector getPrintCarteMembreInfo() throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector<Vector<Object>> allCotisations;
		Vector data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector cotisation = new Vector();
		allCotisations = new Vector();
		data = new Vector();
		try {
			selectStatement = connection
					.prepareStatement("select numero_membre, nom, prenom, to_char(sysdate,'YYYY') from personnes, personnes_cotisations where personnes.personne_id = personnes_cotisations.personne_id and annee_cotisation = to_char(sysdate,'YYYY') and carte_membre_envoyee = 0 order by numero_membre");
			// selectStatement =
			// connection.prepareStatement("select numero_membre, nom, prenom, to_char(sysdate,'YYYY') from personnes, personnes_cotisations where personnes.personne_id = personnes_cotisations.personne_id and to_number(annee_cotisation) = to_number(to_char(sysdate,'YYYY')) -1 and carte_membre_envoyee = 0 order by numero_membre");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allCotisations.add(cotisation)) {
				cotisation = new Vector();
				cotisation.add(Integer.valueOf(rs.getInt(1)));
				cotisation.add(rs.getString(2));
				cotisation.add(rs.getString(3));
				cotisation.add(rs.getString(4));
			}

			rs.close();
			selectStatement.close();
			data.add(allCotisations);
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

	public void updateCarteMembreInfo(final int carteMembreInfo)
			throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;

		try {
			selectStatement = connection
					.prepareStatement(" update personnes_cotisations set carte_membre_envoyee = ? where carte_membre_envoyee = 0 and annee_cotisation = to_char(sysdate,'YYYY')");
			selectStatement.setInt(1, carteMembreInfo);
			selectStatement.executeUpdate();
			selectStatement.close();
			connection.commit();

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
}