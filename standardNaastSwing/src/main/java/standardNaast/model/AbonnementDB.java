// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:20
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AbonnementDB.java
package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries
public class AbonnementDB {

	public AbonnementDB() {
		AbonnementDB.logger.log(Level.INFO, "Starting AbonnementDB");
	}

	public Vector getAbonnements(long personneID) throws Exception {
		Vector<Vector<Object>> allAbonnements;
		Vector<Vector> data;
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		AbonnementDB.logger.log(Level.INFO, "Starting getAbonnements");
		Vector<Object> abonnement = new Vector<Object>();
		allAbonnements = new Vector<Vector<Object>>();
		data = new Vector();
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = null;
			rs = null;
			Vector vector;
			selectStatement = connection
					.prepareStatement("select ABONNEMENT_ID,PERSONNE_ID, SAISON, (select bloc_value from bloc where bloc.bloc_id = abonnement.bloc_id) BLOC, RANG, PLACE, REDUCTION, PAYE, ACOMPTE, PLACE_COMMANDEE \"PLACE COMMANDEE\" \nfrom abonnement where personne_id = ?");
			selectStatement.setLong(1, personneID);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);
			// Vector abonnement;
			while (rs.next()) {
				abonnement = new Vector<Object>();
				abonnement.add(new Integer(rs.getInt(1)));
				abonnement.add(new Integer(rs.getInt(2)));
				abonnement.add(rs.getString(3));
				abonnement.add(rs.getString(4));
				abonnement.add(rs.getString(5));
				abonnement.add(rs.getString(6));
				abonnement.add(new Integer(rs.getInt(7)));
				abonnement.add(new Integer(rs.getInt(8)));
				abonnement.add(new Integer(rs.getInt(9)));
				abonnement.add(new Integer(rs.getInt(10)));
				allAbonnements.add(abonnement);
			}

			rs.close();
			selectStatement.close();
			data.add(allAbonnements);
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
			AbonnementDB.logger.log(Level.INFO, "Ending getAbonnements");

		}

	}

	public Vector getAbonnement(long abonnementID) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		AbonnementDB.logger.log(Level.INFO, "Starting getAbonnement");
		// Vector abonnement = new Vector();
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = null;
			rs = null;
			Vector vector;
			selectStatement = connection
					.prepareStatement("select ABONNEMENT_ID,(select nom || ' ' || prenom from personnes where personnes.personne_id = abonnement.personne_id),PLACE,SAISON,REDUCTION,PAYE,(select bloc_value from bloc where bloc.bloc_id = abonnement.bloc_id), BLOC_id,RANG,ACOMPTE,PLACE_COMMANDEE,personne_id\nfrom abonnement where abonnement_id = ?");
			selectStatement.setLong(1, abonnementID);
			rs = selectStatement.executeQuery();
			rs.next();
			Vector abonnement = new Vector();
			abonnement.add(new Integer(rs.getInt(1)));
			abonnement.add(rs.getString(2));
			abonnement.add(rs.getString(3));
			abonnement.add(rs.getString(4));
			abonnement.add(new Integer(rs.getInt(5)));
			abonnement.add(new Integer(rs.getInt(6)));
			abonnement.add(rs.getString(7));
			abonnement.add(new Integer(rs.getInt(8)));
			abonnement.add(rs.getString(9));
			abonnement.add(new Integer(rs.getInt(10)));
			abonnement.add(new Integer(rs.getInt(11)));
			abonnement.add(new Integer(rs.getInt(12)));
			rs.close();
			selectStatement.close();
			return abonnement;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				rs.close();
				selectStatement.close();
			} catch (Exception exception) {
			}
			AbonnementDB.logger.log(Level.INFO, "Ending getAbonnement");
		}

	}

	public boolean addAbonnement(Vector data) throws Exception {
		Connection connection;
		PreparedStatement insertStatement = null;
		AbonnementDB.logger.log(Level.INFO, "Starting addAbonnement");
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			insertStatement = null;
			boolean success = false;
			// boolean flag;
			insertStatement = connection
					.prepareStatement("insert into abonnement (ABONNEMENT_ID,PERSONNE_ID,PLACE,SAISON,REDUCTION,PAYE,BLOC_ID,RANG,ACOMPTE,PLACE_COMMANDEE)\nvalues(abonnement_seq.nextval,?,?,?,?,?,?,?,?,0)");
			insertStatement.setLong(1, ((Long) data.get(0)).longValue());
			insertStatement.setString(2, (String) data.get(1));
			insertStatement.setString(3, (String) data.get(2));
			insertStatement.setInt(4, ((Integer) data.get(3)).intValue());
			insertStatement.setInt(5, ((Integer) data.get(4)).intValue());
			insertStatement.setInt(6, ((Integer) data.get(5)).intValue());
			insertStatement.setString(7, (String) data.get(6));
			insertStatement.setInt(8, ((Integer) data.get(7)).intValue());
			insertStatement.execute();
			insertStatement.close();
			connection.commit();
			success = true;
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());

		} finally {
			try {
				insertStatement.close();
			} catch (Exception exception) {
			}
			AbonnementDB.logger.log(Level.INFO, "Ending addAbonnement");
		}

	}

	public void deleteAbonnement(int abonnementID) throws Exception {
		Connection connection;
		PreparedStatement deleteStatement;
		AbonnementDB.logger.log(Level.INFO, "Starting deleteAbonnement");
		connection = JDBCConnection.getInstance().getJDBCConnection();
		deleteStatement = null;
		try {
			deleteStatement = connection
					.prepareStatement("delete from abonnement where abonnement_id = ?");
			deleteStatement.setInt(1, abonnementID);
			deleteStatement.execute();
			deleteStatement.close();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				deleteStatement.close();
			} catch (Exception exception) {
			}
		}
		AbonnementDB.logger.log(Level.INFO, "Ending deleteAbonnement");

	}

	public void updateStatusAbonnement(String saison) throws Exception {
		Connection connection;
		PreparedStatement updateStatement = null;
		AbonnementDB.logger.log(Level.INFO, "Starting updateStatusAbonnement");
		connection = JDBCConnection.getInstance().getJDBCConnection();
		updateStatement = null;
		try {
			updateStatement = connection
					.prepareStatement("update abonnement set place_commandee = 1 where place_commandee = 0 and saison = ?");
			updateStatement.setString(1, saison);

			updateStatement.execute();
			updateStatement.close();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				updateStatement.close();
			} catch (Exception exception) {
			}
		}
		AbonnementDB.logger.log(Level.INFO, "Ending updateStatusAbonnement");

	}

	public void updateAbonnement(Vector data) throws Exception {
		Connection connection;
		PreparedStatement updateStatement = null;
		AbonnementDB.logger.log(Level.INFO, "Starting updateAbonnement");
		connection = JDBCConnection.getInstance().getJDBCConnection();
		updateStatement = null;
		try {
			updateStatement = connection
					.prepareStatement("update abonnement set place = ?,\nsaison = ?,\nreduction = ?,\npaye = ?,\nbloc_id = ?,\nrang = ?,\nacompte = ? \nwhere abonnement_id = ?");
			updateStatement.setString(1, (String) data.get(0));
			updateStatement.setString(2, (String) data.get(1));
			updateStatement.setInt(3, ((Integer) data.get(2)).intValue());
			updateStatement.setInt(4, ((Integer) data.get(3)).intValue());
			updateStatement.setInt(5, ((Integer) data.get(4)).intValue());
			updateStatement.setString(6, (String) data.get(5));
			updateStatement.setInt(7, ((Integer) data.get(6)).intValue());
			updateStatement.setInt(8, ((Integer) data.get(7)).intValue());
			updateStatement.execute();
			updateStatement.close();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				updateStatement.close();
			} catch (Exception exception) {
			}
		}
		AbonnementDB.logger.log(Level.INFO, "Ending updateAbonnement");

	}

	public int getPrixAbonnement(long personneID, String saison, int blocID)
			throws Exception {
		Connection connection;
		ResultSet rs = null;
		PreparedStatement selectStatement = null;
		int montant;
		AbonnementDB.logger.log(Level.INFO, "Starting getPrixAbonnement");
		connection = JDBCConnection.getInstance().getJDBCConnection();
		rs = null;
		selectStatement = null;
		montant = 0;
		AbonnementDB.logger.log(Level.INFO,
				(new StringBuilder()).append("Personne ID: ")
						.append(personneID).toString());
		AbonnementDB.logger.log(Level.INFO,
				(new StringBuilder()).append("Saison: ").append(saison)
						.toString());
		AbonnementDB.logger.log(Level.INFO,
				(new StringBuilder()).append("bloc ID: ").append(blocID)
						.toString());
		String dateNaissance = null;
		try {
			String query = "select to_char(date_naissance,'DD-MM-YYYY') from personnes where personne_id = ?";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setLong(1, personneID);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				dateNaissance = rs.getString(1);
			}
			if (rs.wasNull()) {
				JOptionPane
						.showMessageDialog(
								null,
								"Date de naissance obligatoire pour connaitre le prix de l'abonnement.\nVeuillez remplir le champs date de naissance avant d'ajouter un abonnement � ce membre",
								"Erreur", 0);
			}
			AbonnementDB.logger.log(Level.INFO, dateNaissance);
			rs.close();
			selectStatement.close();

			String dateMatch = null;

			AbonnementDB.logger.log(
					Level.INFO,
					(new StringBuilder()).append("personneID: ")
							.append(personneID).append(" date de naissance: ")
							.append(dateNaissance).toString());
			query = "select to_char(DATE_PREMIER_MATCH_CHAMPIONNAT,'DD-MM-YYYY') from saison where saison_id = ?";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setString(1, saison);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				dateMatch = rs.getString(1);
			} else {
				JOptionPane
						.showMessageDialog(
								null,
								(new StringBuilder())
										.append("Date du premier match de championnat absente.\nVeuillez aller dans l'onglet 'Configuration g�n�rale' et ajouter la date de premier match de championnat pour la saison ")
										.append(saison).toString(), "Erreur", 0);
				return 0;
			}
			rs.close();
			selectStatement.close();

			int i;
			AbonnementDB.logger.log(Level.INFO,
					(new StringBuilder()).append("Saison: ").append(saison)
							.append(" date de match: ").append(dateMatch)
							.toString());
			query = (new StringBuilder())
					.append("select trunc(((((86400*(to_date('")
					.append(dateMatch).append("','DD-MM-YYYY') - to_date('")
					.append(dateNaissance)
					.append("','DD-MM-YYYY')))/60)/60)/24)/365) from dual")
					.toString();
			selectStatement = connection.prepareStatement(query);
			rs = selectStatement.executeQuery();
			rs.next();
			int anneesDifference = rs.getInt(1);
			rs.close();
			selectStatement.close();
			AbonnementDB.logger.log(Level.INFO,
					(new StringBuilder()).append("Ann�es de diff�rence: ")
							.append(anneesDifference).toString());
			query = "select numero_membre from personnes where personne_id = ?";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setLong(1, personneID);
			rs = selectStatement.executeQuery();
			rs.next();
			int membre = rs.getInt(1);
			if (membre < 10000) {
				membre = 1;
			} else {
				membre = 0;
			}
			rs.close();
			selectStatement.close();
			AbonnementDB.logger.log(
					Level.INFO,
					(new StringBuilder()).append("Membre?: ")
							.append(membre != 1 ? "non" : "oui").toString());
			query = "select type_personne_id from type_personne where age_minimum <= ? and age_maximum > ? and etudiant = (select etudiant from personnes where personne_id = ?) and membre = ?";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setInt(1, anneesDifference);
			selectStatement.setInt(2, anneesDifference);
			selectStatement.setLong(3, personneID);
			selectStatement.setInt(4, membre);
			rs = selectStatement.executeQuery();
			rs.next();
			int typePersonne = rs.getInt(1);
			rs.close();
			selectStatement.close();
			AbonnementDB.logger.log(
					Level.INFO,
					(new StringBuilder()).append("type de personne: ")
							.append(typePersonne).toString());
			query = "select montant from prix_place \n              where type_match_id = \n                  (select type_match_id from type_match \n                   where upper(denomination_match) = upper('abonnement')) \n                   and type_personne_id = ? \n                   and bloc_id = ?                   and saison = ?";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setInt(1, typePersonne);
			selectStatement.setInt(2, blocID);
			selectStatement.setString(3, saison);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				montant = rs.getInt(1);
			}
			rs.close();
			selectStatement.close();
			AbonnementDB.logger.log(Level.INFO,
					(new StringBuilder()).append("Prix de l'abonnement: ")
							.append(montant).toString());
			return montant;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}

			AbonnementDB.logger.log(Level.INFO, "Ending getPrixAbonnement");
		}

	}

	public int getRistourneDeplacements(long personneID, String saison)
			throws Exception {
		int reduction;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		AbonnementDB.logger.log(Level.INFO, "Starting getRistourne");
		reduction = 0;
		selectStatement = null;
		rs = null;
		int i;

		int deplacementDomicile = this.getNombreDeplacementsDomicile(
				personneID, saison);
		int deplacementExterieur = this.getNombreDeplacementsExterieur(
				personneID, saison);
		if ((deplacementDomicile > 11) || (deplacementExterieur > 11)) {
			int totalDeplacements = deplacementDomicile + deplacementExterieur;
			reduction = totalDeplacements - 2;
		}
		AbonnementDB.logger.log(Level.INFO,
				(new StringBuilder()).append("Montant de la r�duction: ")
						.append(reduction).toString());
		return reduction;

	}

	public String getRang(long personneID, String saison) throws Exception {
		Connection connection;
		ResultSet rs = null;
		PreparedStatement selectStatement = null;
		String rang;
		AbonnementDB.logger.log(Level.INFO, "Starting getRang");
		connection = JDBCConnection.getInstance().getJDBCConnection();
		rs = null;
		selectStatement = null;
		rang = "";
		String s;
		try {
			selectStatement = connection
					.prepareStatement("select rang from abonnement where personne_id = ? and saison = (select saison_id from saison \n                    where to_number(to_char(date_fin,'YYYY')) = \n                      (select to_number(to_char(date_fin,'YYYY')) - 1 \n                        from saison where saison_id = ?))");
			selectStatement.setLong(1, personneID);
			selectStatement.setString(2, saison);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				rang = rs.getString(1);
			}
			rs.close();
			selectStatement.close();
			s = rang;
			return rang;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}

			AbonnementDB.logger.log(Level.INFO, "Ending getRang");
		}

	}

	public String getPlace(long personneID, String saison) throws Exception {
		Connection connection;
		ResultSet rs = null;
		PreparedStatement selectStatement = null;
		String place;
		AbonnementDB.logger.log(Level.INFO, "Starting getPlace");
		connection = JDBCConnection.getInstance().getJDBCConnection();
		rs = null;
		selectStatement = null;
		place = "";
		String s;
		try {
			selectStatement = connection
					.prepareStatement("select place from abonnement where personne_id = ? and saison = (select saison_id from saison \n                    where to_number(to_char(date_fin,'YYYY')) = \n                      (select to_number(to_char(date_fin,'YYYY')) - 1 \n                        from saison where saison_id = ?))");
			selectStatement.setLong(1, personneID);
			selectStatement.setString(2, saison);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				place = rs.getString(1);
			}
			rs.close();
			selectStatement.close();
			return place;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}
			AbonnementDB.logger.log(Level.INFO, "Ending getPlace");
		}

	}

	public int getRistourneBenevolat(long personneID, String saison)
			throws Exception {
		int reduction;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Connection connection;
		AbonnementDB.logger.log(Level.INFO, "Starting getRistourneBenevolat");
		reduction = 0;
		selectStatement = null;
		rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		try {
			selectStatement = connection
					.prepareStatement("select sum(montant) \nfrom benevolat \nwhere personne_id = ? \nand date_benevolat \n  between\n  (select date_debut \n   from saison \n   where saison_id = \n    (select saison_id \n     from saison \n     where to_number(to_char(date_fin,'YYYY')) = \n      (select to_number(to_char(date_fin,'YYYY')) - 1 \n       from saison where saison_id = ?)\n    )\n  )                                               \n  and \n    (select date_fin \n     from saison \n     where saison_id = \n      (select saison_id \n       from saison             \n       where to_number(to_char(date_fin,'YYYY')) = \n        (select to_number(to_char(date_fin,'YYYY')) - 1 \n         from saison \n         where saison_id = ?\n         )\n      )\n    )");
			selectStatement.setLong(1, personneID);
			selectStatement.setString(2, saison);
			selectStatement.setString(3, saison);
			rs = selectStatement.executeQuery();
			rs.next();
			rs.getInt(1);
			if (!rs.wasNull()) {
				reduction = rs.getInt(1);
			}
			rs.close();
			selectStatement.close();
			return reduction;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}
			AbonnementDB.logger.log(Level.INFO, "Ending getRistourneBenevolat");
		}

	}

	public int getNombreDeplacementsDomicile(long personneID, String saison)
			throws Exception {
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Connection connection;
		AbonnementDB.logger.log(Level.INFO,
				"Starting getNombreDeplacementsDomicile");
		selectStatement = null;
		rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		try {
			String oldSaison = this.getSaisonPrecedente(saison);
			String query = "select count(*) from personnes_match where match_id in (select match_id from match where upper(lieu) = upper('domicile') and saison_equipe_id in (select saison_equipe_id from saison_equipe where saison_id = ?)) and personne_id = ?";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setString(1, oldSaison);
			selectStatement.setLong(2, personneID);
			rs = selectStatement.executeQuery();
			rs.next();
			int deplacementDomicile = rs.getInt(1);
			rs.close();
			selectStatement.close();
			AbonnementDB.logger.log(
					Level.INFO,
					(new StringBuilder())
							.append("Nombre de d�placement � domicile: ")
							.append(deplacementDomicile).toString());
			return deplacementDomicile;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}
			AbonnementDB.logger.log(Level.INFO,
					"Ending getNombreDeplacementsDomicile");
		}

	}

	public String getSaisonPrecedente(String saison) throws Exception {
		String oldSaison;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Connection connection;
		AbonnementDB.logger.log(Level.INFO, "Starting getSaisonPrecedente");
		oldSaison = null;
		selectStatement = null;
		rs = null;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			String query = "select saison_id from saison where to_number(to_char(date_fin,'YYYY')) = (select to_number(to_char(date_fin,'YYYY')) - 1 from saison where saison_id = ?)";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setString(1, saison);
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				oldSaison = rs.getString(1);
			} else {
				rs.close();
				selectStatement.close();
			}
			rs.close();
			selectStatement.close();
			AbonnementDB.logger.log(
					Level.INFO,
					(new StringBuilder()).append("Old saison: ")
							.append(oldSaison).toString());
			return oldSaison;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}
			AbonnementDB.logger.log(Level.INFO, "Ending getSaisonPrecedente");
		}

	}

	public int getNombreDeplacementsExterieur(long personneID, String saison)
			throws Exception {
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Connection connection;
		AbonnementDB.logger.log(Level.INFO,
				"Starting getNombreDeplacementsExterieur");
		selectStatement = null;
		rs = null;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		try {
			String oldSaison = this.getSaisonPrecedente(saison);
			String query = "select count(*) \n    from personnes_match \n    where match_id in\n      (select match_id \n      from match \n      where upper(lieu) = upper('deplacement')\n      and saison_equipe_id in \n          (select saison_equipe_id \n          from saison_equipe \n          where saison_id = ?)\n      )\n    and personne_id = ?";
			selectStatement = connection.prepareStatement(query);
			selectStatement.setString(1, oldSaison);
			selectStatement.setLong(2, personneID);
			rs = selectStatement.executeQuery();
			rs.next();
			int deplacementExterieur = rs.getInt(1);
			rs.close();
			selectStatement.close();
			AbonnementDB.logger
					.log(Level.INFO,
							(new StringBuilder())
									.append("Nombre de d�placement � l'ext�rieur: ")
									.append(deplacementExterieur).toString());
			return deplacementExterieur;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}
			AbonnementDB.logger.log(Level.INFO,
					"Ending getNombreDeplacementsExterieur");
		}

	}

	public Vector getAllAbonnements(String saison) throws Exception {
		Vector allAbonnements;
		Vector data;
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		AbonnementDB.logger.log(Level.INFO, "Starting getAllAbonnements");
		AbonnementDB.logger.log(Level.INFO, "Saison: " + saison);
		Vector abonnement = new Vector();
		allAbonnements = new Vector();
		data = new Vector();
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		rs = null;
		Vector vector;
		try {
			selectStatement = connection
					.prepareStatement("select ABONNEMENT_ID Abonnement,"
							+ "(select nom || ' '||prenom Membre from personnes "
							+ "where personnes.personne_id = abonnement.personne_id) Membre, "
							+ "SAISON, (select bloc_value from bloc where bloc.bloc_id = abonnement.bloc_id) "
							+ "BLOC, RANG, PLACE, REDUCTION, decode(PAYE,1,'OUI',0,'NON',PAYE) PAYE, ACOMPTE, "
							+ "decode(PLACE_COMMANDEE,1,'Command�e',0,'Non command�e',2,'Re�ue',PLACE_COMMANDEE) "
							+ "\"PLACE COMMANDEE\"  from abonnement where saison = ?");
			selectStatement.setString(1, saison);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);
			// Vector abonnement;
			while (rs.next()) {
				abonnement = new Vector();
				abonnement.add(new Integer(rs.getInt(1)));
				abonnement.add(rs.getString(2));
				abonnement.add(rs.getString(3));
				abonnement.add(rs.getString(4));
				abonnement.add(rs.getString(5));
				abonnement.add(rs.getString(6));
				abonnement.add(new Integer(rs.getInt(7)));
				abonnement.add(rs.getString(8));
				abonnement.add(new Integer(rs.getInt(9)));
				abonnement.add(rs.getString(10));
				allAbonnements.add(abonnement);
			}

			rs.close();
			selectStatement.close();
			data.add(allAbonnements);
			data.add(columnNames);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}
			AbonnementDB.logger.log(Level.INFO, "Ending getAbonnements");
		}

	}

	public Vector getAllAbonnementsNonCommandes(String saison) throws Exception {
		Vector<Vector<Object>> allAbonnements = new Vector<Vector<Object>>();
		Connection connection;
		PreparedStatement selectStatement = null;
		PreparedStatement tarifStatement = null;
		PreparedStatement montantStatement = null;
		ResultSet tarifRs = null;
		ResultSet rs = null;
		AbonnementDB.logger.log(Level.INFO,
				"Starting getAllAbonnementsNonCommandes");
		AbonnementDB.logger.log(Level.INFO, "Saison: " + saison);
		Vector<Object> abonnement = new Vector<Object>();
		connection = JDBCConnection.getInstance().getJDBCConnection();
		try {
			selectStatement = connection
					.prepareStatement("SELECT P.PERSONNE_ID, NOM, PRENOM , TO_CHAR(DATE_NAISSANCE,'DD-MM-YYYY') AS DATE_NAISSANCE, "
							+ " ADRESSE || ' \n'|| CODE_POSTAL || ' ' || VILLE AS ADRESSE, CARTE_IDENTITE, BLOC_VALUE, RANG, PLACE, AB.BLOC_ID"
							+ "  FROM PERSONNES P "
							+ "INNER JOIN ABONNEMENT AB "
							+ "     ON ( P.PERSONNE_ID = AB.PERSONNE_ID) "
							+ "INNER JOIN BLOC BL "
							+ "     ON (AB.BLOC_ID = BL.BLOC_ID) "
							+ "  WHERE AB.SAISON   = ? AND PLACE_COMMANDEE = '0' "
							+ "ORDER BY 2");
			selectStatement.setString(1, saison);
			rs = selectStatement.executeQuery();

			while (rs.next()) {
				abonnement = new Vector<Object>();
				int personneID = rs.getInt(1);
				String nom = rs.getString(2);
				String prenom = rs.getString(3);
				String dateNaissance = rs.getString(4);
				String adresse = rs.getString(5);
				String carteIdentite = rs.getString(6);
				String bloc = rs.getString(7);
				String rang = rs.getString(8);
				String place = rs.getString(9);
				int blocID = rs.getInt(10);
				System.out.println("Bloc ID: " + blocID);
				System.out.println("Personne ID: " + personneID);
				System.out.println("Saison: " + saison);
				tarifStatement = connection
						.prepareStatement("SELECT TARIF, MONTANT "
								+ " FROM PRIX_PLACE "
								+ " WHERE TYPE_MATCH_ID = "
								+ " (SELECT TYPE_MATCH_ID "
								+ " FROM TYPE_MATCH "
								+ " WHERE UPPER(DENOMINATION_MATCH) = UPPER('abonnement') "
								+ " ) AND TYPE_PERSONNE_ID            = "
								+ " (SELECT TYPE_PERSONNE_ID "
								+ " FROM TYPE_PERSONNE "
								+ " WHERE AGE_MINIMUM <= "
								+ " (SELECT TRUNC(((((86400*( "
								+ " (SELECT TRUNC(DATE_PREMIER_MATCH_CHAMPIONNAT) "
								+ " FROM SAISON "
								+ " WHERE SAISON_ID = ? "
								+ " ) - "
								+ " (SELECT TRUNC(DATE_NAISSANCE) FROM PERSONNES WHERE PERSONNE_ID = ? "
								+ " )))/60)/60)/24)/365) "
								+ " FROM DUAL "
								+ " ) AND AGE_MAXIMUM > "
								+ " (SELECT TRUNC(((((86400*( "
								+ " (SELECT TRUNC(DATE_PREMIER_MATCH_CHAMPIONNAT) "
								+ " FROM SAISON "
								+ " WHERE SAISON_ID = ? "
								+ " ) - "
								+ " (SELECT TRUNC(DATE_NAISSANCE) FROM PERSONNES WHERE PERSONNE_ID = ? "
								+ " )))/60)/60)/24)/365) "
								+ " FROM DUAL "
								+ " ) AND ETUDIANT = "
								+ " (SELECT ETUDIANT FROM PERSONNES WHERE PERSONNE_ID = ? "
								+ " ) AND MEMBRE = 1 "
								+ " ) AND BLOC_ID    = ? AND SAISON = ?");
				tarifStatement.setString(1, saison);
				tarifStatement.setInt(2, personneID);
				tarifStatement.setString(3, saison);
				tarifStatement.setInt(4, personneID);
				tarifStatement.setInt(5, personneID);
				tarifStatement.setInt(6, blocID);
				tarifStatement.setString(7, saison);
				tarifRs = tarifStatement.executeQuery();
				int tarif = 0;
				int montant = 0;
				if (tarifRs.next()) {
					tarif = tarifRs.getInt(1);
					montant = tarifRs.getInt(2);
					System.out.println("Tarif: " + tarif);
					System.out.println("Montant: " + montant);
				}
				tarifRs.close();
				tarifStatement.close();

				abonnement.add(nom);
				abonnement.add(prenom);
				abonnement.add(dateNaissance);
				abonnement.add(adresse);
				abonnement.add(carteIdentite);
				abonnement.add(tarif);
				abonnement.add(bloc);
				abonnement.add(rang);
				abonnement.add(place);
				abonnement.add(montant);
				allAbonnements.add(abonnement);
			}

			rs.close();
			selectStatement.close();
			return allAbonnements;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				selectStatement.close();
				rs.close();

			} catch (Exception e) {
			}
			AbonnementDB.logger.log(Level.INFO,
					"Ending getAllAbonnementsNonCommandes");
		}

	}

	private static Logger logger = Logger
			.getLogger("standardNaast.model.AbonnementDB");
}
