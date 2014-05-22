// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MatchDB.java

package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, DeplacementsCarDB, GlobalQueries

public class MatchDB {

	public MatchDB() {
	}


	public Vector getMatches(final String saison) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Vector allMatches;
		Vector data;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = null;
			rs = null;
			Vector match = new Vector();
			allMatches = new Vector();
			data = new Vector();

			Vector vector;
			selectStatement =
					connection
							.prepareStatement("select\t     \"MATCH\".\"MATCH_ID\" as \"MATCH ID\",    \"SAISON_EQUIPE\".\"SAISON_ID\" as \"SAISON\",   \"EQUIPE\".\"NOM_EQUIPE\" as \"Adversaire\", \"MATCH\".\"LIEU\" as \"LIEU\", to_char(DATE_MATCH,'DD-MM-YYYY HH24:MI:SS') as \"DATE de MATCH\",\t \"TYPE_COMPETITION\".\"TYPE_COMPETITION_VALUE\" as \"TYPE de compétition\",      type_match.denomination_match as \" Type de match\"  from\t \"TYPE_MATCH\" \"TYPE_MATCH\",\t \"TYPE_COMPETITION\" \"TYPE_COMPETITION\" ,\t \"MATCH\" \"MATCH\",\t \"SAISON_EQUIPE\" \"SAISON_EQUIPE\",\t \"EQUIPE\" \"EQUIPE\" WHERE TYPE_COMPETITION.TYPE_COMPETITION_ID = TYPE_MATCH.TYPE_COMPETITION_ID AND   MATCH.SAISON_EQUIPE_ID = SAISON_EQUIPE.SAISON_EQUIPE_ID AND MATCH.TYPE_MATCH = TYPE_MATCH.TYPE_MATCH_ID AND EQUIPE.EQUIPE_ID = SAISON_EQUIPE.EQUIPE_ID AND SAISON_EQUIPE.SAISON_ID = ? ORDER BY SAISON_EQUIPE.SAISON_ID DESC, MATCH.DATE_MATCH DESC");
			selectStatement.setString(1, saison);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allMatches.add(match)) {
				match = new Vector();
				match.add(Integer.valueOf(rs.getInt(1)));
				match.add(rs.getString(2));
				match.add(rs.getString(3));
				match.add(rs.getString(4));
				match.add(rs.getString(5));
				match.add(rs.getString(6));
				match.add(rs.getString(7));
			}

			rs.close();
			selectStatement.close();
			data.add(allMatches);
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


	public Vector getMatchsForDeplacementCar() throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Vector allMatches;
		Vector data;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = null;
			rs = null;
			Vector match = new Vector();
			allMatches = new Vector();
			data = new Vector();

			Vector vector;
			selectStatement =
					connection
							.prepareStatement("select match_id, nom_equipe || ' ' || lieu || ' '|| type_competition_value, to_char(date_match,'DD-MM-YYYY') from match m, saison_equipe se, equipe e,type_competition tc, type_match tm where m.SAISON_EQUIPE_ID = se.SAISON_EQUIPE_ID and e.equipe_id = se.equipe_id and m.type_match = tm.type_match_id and tm.type_competition_id = tc.type_competition_id and trunc(date_match) >= trunc(sysdate) order by date_match");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allMatches.add(match)) {
				match = new Vector();
				match.add(Integer.valueOf(rs.getInt(1)));
				match.add(rs.getString(2));
				match.add(rs.getString(3));
			}

			rs.close();
			selectStatement.close();
			data.add(allMatches);
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


	public Vector getMatchsForDeplacementCarArchive() throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Vector allMatches;
		Vector data;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = null;
			rs = null;
			Vector match = new Vector();
			allMatches = new Vector();
			data = new Vector();

			Vector vector;
			// selectStatement =
			// connection.prepareStatement("select match_id, nom_equipe || ' ' || lieu || ' '|| type_competition_value, to_char(date_match,'DD-MM-YYYY') from match m, saison_equipe se, equipe e,type_competition tc, type_match tmwhere m.SAISON_EQUIPE_ID = se.SAISON_EQUIPE_IDand e.equipe_id = se.equipe_idand m.type_match = tm.type_match_idand tm.type_competition_id = tc.type_competition_idand date_match >= sysdateorder by date_match");
			selectStatement =
					connection
							.prepareStatement("select "
									+ "match_id, "
									+ "nom_equipe || ' ' || lieu || ' '|| type_competition_value, "
									+ "to_char(date_match,'DD-MM-YYYY') "
									+ "from match m, "
									+ "	saison_equipe se, "
									+ "equipe e,"
									+ "type_competition tc, "
									+ "type_match tm where m.SAISON_EQUIPE_ID = se.SAISON_EQUIPE_ID and e.equipe_id = se.equipe_id and m.type_match = tm.type_match_id and tm.type_competition_id = tc.type_competition_id and trunc(date_match) < trunc(sysdate) order by date_match desc");

			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allMatches.add(match)) {
				match = new Vector();
				match.add(Integer.valueOf(rs.getInt(1)));
				match.add(rs.getString(2));
				match.add(rs.getString(3));
			}

			rs.close();
			selectStatement.close();
			data.add(allMatches);
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


	public Vector getDeplacementsCarParMatch(final int matchID, final boolean isMembre) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		Vector allDeplacementsCar;
		Vector prixLocomotionID;
		Vector data;
		DeplacementsCarDB deplacementCarDB;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		Vector deplacementsCar = new Vector();
		allDeplacementsCar = new Vector();
		prixLocomotionID = new Vector();
		data = new Vector();
		Vector columnNames = new Vector();
		deplacementCarDB = new DeplacementsCarDB();
		Vector vector;
		try {
			selectStatement =
					connection
							.prepareStatement((new StringBuilder())
									.append("select p.personne_id, nom || ' ' || prenom \"Nom et Prénom\", 'true' payé from personnes p inner join personnes_match pmon p.personne_id = pm.personne_idwhere match_id = ? and numero_membre ")
									.append(isMembre ? "<" : ">=")
									.append(" 10000 ")
									.append("union ")
									.append("select p.personne_id, ")
									.append("nom || ' ' || prenom \"Nom et Prénom\", ")
									.append("'false' payé ")
									.append("from personnes p")
									.append("where numero_membre ")
									.append(isMembre ? "<" : ">=")
									.append(" 10000 and personne_id not in (select p.personne_id from personnes p inner join personnes_match pm")
									.append(" on p.personne_id = pm.personne_id").append(" where match_id = ?)")
									.append(" order by 2").toString());

			// selectStatement = connection.prepareStatement((new
			// StringBuilder()).append(" select p.personne_id, nom || ' ' || prenom \"Nom et Prï¿½nom\", 'true' payï¿½ from personnes p inner join personnes_match pmon p.personne_id = pm.personne_idwhere match_id = ?and numero_membre ").append(isMembre
			// ? "<" :
			// ">=").append(" 10000 ").append("union ").append("select p.personne_id, ").append("nom || ' ' || prenom \"Nom et Prï¿½nom\", ").append("'false' payï¿½ ").append("from personnes p").append("where numero_membre ").append(isMembre
			// ? "<" : ">=").append(" 10000 ").append("order by 2").toString());
			selectStatement.setInt(1, matchID);
			selectStatement.setInt(2, matchID);
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			columnNames = gq.getColumnNames(rs);
			int montantDeplacement[];
			for (; rs.next(); prixLocomotionID.add(new Integer(montantDeplacement[1]))) {
				deplacementsCar = new Vector();
				deplacementsCar.add(Integer.valueOf(rs.getInt(1)));
				deplacementsCar.add(rs.getString(2));
				montantDeplacement = deplacementCarDB.getPrixDeplacementParDateNaissance(rs.getInt(1), matchID);
				deplacementsCar.add(Integer.valueOf(montantDeplacement[0]));
				deplacementsCar.add(Boolean.valueOf(Boolean.parseBoolean(rs.getString(3))));
				allDeplacementsCar.add(deplacementsCar);
			}

			rs.close();
			selectStatement.close();
			columnNames.add(columnNames.get(2));
			columnNames.setElementAt("Montant", 2);
			for (int i = 0; i < columnNames.size(); i++) {
				System.out.println((new StringBuilder()).append("ColumnName: ").append(columnNames.get(i)).toString());
			}

			data.add(allDeplacementsCar);
			data.add(columnNames);
			data.add(prixLocomotionID);
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


	public void ajouterMatch(final Vector data) throws Exception {
		String dateMatch;
		String typeMatch;
		String lieuMatch;
		String equipeMatch;
		String typeCompetition;
		String saison;
		Connection connection;
		dateMatch = (String) data.get(0);
		typeMatch = (String) data.get(1);
		int typeMatchID = 0;
		lieuMatch = (String) data.get(2);
		equipeMatch = (String) data.get(3);
		typeCompetition = (String) data.get(4);
		saison = (String) data.get(5);
		int saisonEquipeID = 0;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		PreparedStatement selectStatement = null;
		PreparedStatement insertStatement = null;
		ResultSet rs = null;
		try {
			selectStatement =
					connection
							.prepareStatement("select type_match_id from type_match where denomination_match = ? and type_competition_id = (select type_competition_id from type_competition where type_competition_value  = ?)");
			selectStatement.setString(1, typeMatch);
			selectStatement.setString(2, typeCompetition);
			rs = selectStatement.executeQuery();
			rs.next();
			typeMatchID = rs.getInt(1);
			rs.close();
			selectStatement.close();
			selectStatement =
					connection
							.prepareStatement("select saison_equipe_id from saison_equipe where saison_id = ? and equipe_id = (select equipe_id from equipe where nom_equipe = ?)");
			selectStatement.setString(1, saison);
			selectStatement.setString(2, equipeMatch);
			rs = selectStatement.executeQuery();
			rs.next();
			saisonEquipeID = rs.getInt(1);
			rs.close();
			selectStatement.close();
			insertStatement =
					connection
							.prepareStatement("insert into match(match_id,lieu,date_match,type_match,saison_equipe_id) values(match_seq.nextval,?,?,?,?)");
			insertStatement.setString(1, lieuMatch);
			insertStatement.setString(2, dateMatch);
			insertStatement.setInt(3, typeMatchID);
			insertStatement.setInt(4, saisonEquipeID);
			insertStatement.execute();
			insertStatement.close();
			connection.commit();
			JOptionPane.showMessageDialog(null, "Match ajoutï¿½", "Ajout match", 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
				selectStatement.close();
				insertStatement.close();
			} catch (Exception e) {

			}
		}
	}
}