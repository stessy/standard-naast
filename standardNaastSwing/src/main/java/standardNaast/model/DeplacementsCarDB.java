// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   DeplacementsCarDB.java
package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection, GlobalQueries
public class DeplacementsCarDB {

	public DeplacementsCarDB() {
	}

	public Vector<Vector<Vector<Object>>> getDeplacementsCar() throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		Vector<Vector<Object>> allDeplacementsCar;
		Vector<Vector<Vector<Object>>> data;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		rs = null;
		Vector<Object> deplacementCar = new Vector<Object>();
		allDeplacementsCar = new Vector<Vector<Object>>();
		// Vector columnNames = new Vector();
		data = new Vector<Vector<Vector<Object>>>();
		try {
			selectStatement = connection
					.prepareStatement("select PRIX_LOCOMOTION_ID as ID,\n\t ANNEE,\n\t MONTANT,\n         AGE_MINIMUM as \"AGE MINIMUM\",\n         AGE_MAXIMUM as \"AGE MAXIMUM\",\n         LIEU,\n         MEMBRE  \n from\t PRIX_LOCOMOTION order by annee desc, age_minimum asc");
			rs = selectStatement.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector<Vector<Object>> columnNames = gq.getColumnNames(rs);

			for (; rs.next(); allDeplacementsCar.add(deplacementCar)) {
				deplacementCar = new Vector<Object>();
				deplacementCar.add(Integer.valueOf(rs.getInt(1)));
				deplacementCar.add(rs.getString(2));
				deplacementCar.add(Integer.valueOf(rs.getInt(3)));
				deplacementCar.add(Integer.valueOf(rs.getInt(4)));
				deplacementCar.add(Integer.valueOf(rs.getInt(5)));
				deplacementCar.add(rs.getString(6));
				deplacementCar.add(rs.getInt(7) != 0 ? ((Object) (new Boolean(
						true))) : ((Object) (new Boolean(false))));
			}

			rs.close();
			selectStatement.close();
			data.add(allDeplacementsCar);
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

	public boolean addDeplacementCar(Vector<?> data) throws Exception {
		PreparedStatement insertStatement = null;

		insertStatement = null;

		try {
			if (this.deplacementExistsOnAdd(data)) {
				JOptionPane
						.showMessageDialog(
								null,
								"Information d�j� existante dans la base de donn�es.",
								"Deplacement car", 0);
				return false;
			} else {

				Connection connection = JDBCConnection.getInstance()
						.getJDBCConnection();
				insertStatement = connection
						.prepareStatement("insert into prix_locomotion (PRIX_LOCOMOTION_ID,ANNEE,MONTANT,LIEU,MEMBRE,AGE_MINIMUM,AGE_MAXIMUM)\nvalues(prix_locomotion_seq.nextval,?,?,?,?,?,?)");
				insertStatement.setString(1, (String) data.get(0));
				insertStatement.setInt(2,
						Integer.parseInt((String) data.get(1)));
				insertStatement.setString(3, (String) data.get(2));
				insertStatement.setInt(4,
						Integer.parseInt((String) data.get(3)));
				insertStatement.setInt(5,
						Integer.parseInt((String) data.get(4)));
				insertStatement.setInt(6,
						Integer.parseInt((String) data.get(5)));
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
			} catch (Exception exception1) {
			}
		}
	}

	public boolean updateDeplacementCar(Vector<?> data) throws Exception {
		PreparedStatement updateStatement = null;
		Connection connection = null;
		updateStatement = null;
		try {

			if (this.deplacementExistsOnUpdate(data)) {
				JOptionPane
						.showMessageDialog(
								null,
								"Information d�j� existante dans la base de donn�es.",
								"Deplacement car", 0);
				return false;
			} else {
				connection = JDBCConnection.getInstance().getJDBCConnection();
				updateStatement = connection
						.prepareStatement("update prix_locomotion set annee = ?,montant = ?,lieu = ?,membre = ?,age_minimum = ?,age_maximum = ? where prix_locomotion_id = ?");
				updateStatement.setInt(1,
						Integer.parseInt((String) data.get(0)));
				updateStatement.setInt(2,
						Integer.parseInt((String) data.get(1)));
				updateStatement.setString(3, (String) data.get(2));
				updateStatement.setInt(4,
						Integer.parseInt((String) data.get(3)));
				updateStatement.setInt(5,
						Integer.parseInt((String) data.get(4)));
				updateStatement.setInt(6,
						Integer.parseInt((String) data.get(5)));
				updateStatement.setInt(7,
						Integer.parseInt((String) data.get(6)));
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
				updateStatement.close();
			} catch (Exception exception1) {
			}
		}
	}

	public boolean deplacementExistsOnAdd(Vector<?> data) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		boolean exists;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		rs = null;
		exists = false;
		try {
			selectStatement = connection
					.prepareStatement("select * from prix_locomotion where ANNEE = ?\nand MONTANT = ?\nand LIEU = ?\nand MEMBRE = ?\nand AGE_MINIMUM = ?\nand AGE_MAXIMUM = ?");
			selectStatement.setString(1, (String) data.get(0));
			System.out.println((String) data.get(0));
			selectStatement.setInt(2, Integer.parseInt((String) data.get(1)));
			System.out.println((String) data.get(1));
			selectStatement.setString(3, (String) data.get(2));
			System.out.println((String) data.get(2));
			selectStatement.setInt(4, Integer.parseInt((String) data.get(3)));
			System.out.println((String) data.get(3));
			selectStatement.setInt(5, Integer.parseInt((String) data.get(4)));
			System.out.println((String) data.get(4));
			selectStatement.setInt(6, Integer.parseInt((String) data.get(5)));
			System.out.println((String) data.get(5));
			rs = selectStatement.executeQuery();
			// System.out.println(""+rs.next());
			if (rs.next()) {
				exists = true;
			} else {
				exists = false;
			}
			rs.close();
			selectStatement.close();
			return exists;
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

	public boolean deplacementExistsOnUpdate(Vector<?> data) throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		boolean exists;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		rs = null;
		exists = false;
		try {
			selectStatement = connection
					.prepareStatement("select * from prix_locomotion where ANNEE = ?\nand MONTANT = ?\nand LIEU = ?\nand MEMBRE = ?\nand AGE_MINIMUM = ?\nand AGE_MAXIMUM = ? and prix_locomotion_id <> ?");
			selectStatement.setString(1, (String) data.get(0));
			selectStatement.setInt(2, Integer.parseInt((String) data.get(1)));
			selectStatement.setString(3, (String) data.get(2));
			selectStatement.setInt(4, Integer.parseInt((String) data.get(3)));
			selectStatement.setInt(5, Integer.parseInt((String) data.get(4)));
			selectStatement.setInt(6, Integer.parseInt((String) data.get(5)));
			selectStatement.setInt(7, Integer.parseInt((String) data.get(6)));
			rs = selectStatement.executeQuery();
			if (rs.next()) {
				exists = true;
			} else {
				exists = false;
			}
			rs.close();
			selectStatement.close();
			return exists;
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

	public int[] getPrixDeplacementParDateNaissance(int personneID, int matchID)
			throws Exception {
		Connection connection;
		PreparedStatement selectStatement = null;
		int membre;
		int info[];
		connection = JDBCConnection.getInstance().getJDBCConnection();
		selectStatement = null;
		ResultSet rs = null;
		String dateNaissance = null;
		int anneesDifference = 0;
		String lieu = null;
		String dateMatch = null;
		String annee = null;
		membre = 0;
		info = new int[2];
		try {
			System.out.println((new StringBuilder()).append("Personne ID: ")
					.append(personneID).toString());
			System.out.println((new StringBuilder()).append("Match ID: ")
					.append(matchID).toString());

			selectStatement = connection
					.prepareStatement("select to_char(sysdate,'YYYY') from dual");
			rs = selectStatement.executeQuery();
			rs.next();
			annee = rs.getString(1);
			rs.close();
			selectStatement.close();
			System.out.println((new StringBuilder()).append("Ann�e: ")
					.append(annee).toString());
			selectStatement = connection
					.prepareStatement("select count(*) from personnes_cotisations where personne_id = ? and annee_cotisation2 = to_number(to_char(sysdate,'YYYY'))");
			selectStatement.setInt(1, personneID);
			rs = selectStatement.executeQuery();
			rs.next();
			membre = rs.getInt(1);
			rs.close();
			selectStatement.close();
			if (membre == 0) {
				selectStatement = connection
						.prepareStatement("select sysdate from dual where to_date(?,'DD-MM-YYYY') > sysdate");
				selectStatement.setString(1,
						(new StringBuilder()).append("31-03-").append(annee)
								.toString());
				rs = selectStatement.executeQuery();
				if (rs.next()) {
					PreparedStatement selectStatement2 = connection
							.prepareStatement("select count(*) from personnes_cotisations where personne_id = ? and annee_cotisation2 = to_number(to_char(sysdate,'YYYY')) - 1");
					selectStatement2.setInt(1, personneID);
					ResultSet rs2 = selectStatement2.executeQuery();
					rs2.next();
					membre = rs2.getInt(1);
					rs2.close();
					selectStatement2.close();
				}
				rs.close();
				selectStatement.close();
			}
			System.out.println((new StringBuilder()).append("Membre: ")
					.append(membre).toString());
			selectStatement = connection
					.prepareStatement("select to_char(date_naissance,'DD-MM-YYYY')  from personnes where personne_id = ?");
			selectStatement.setInt(1, personneID);
			rs = selectStatement.executeQuery();
			rs.next();
			dateNaissance = rs.getString(1);
			rs.close();
			selectStatement.close();
			System.out.println((new StringBuilder())
					.append("Date de naissance: ").append(dateNaissance)
					.toString());

			selectStatement = connection
					.prepareStatement("select to_char(date_match,'DD-MM-YYYY') from match where match_id = ?");
			selectStatement.setInt(1, matchID);
			rs = selectStatement.executeQuery();
			rs.next();
			dateMatch = rs.getString(1);
			rs.close();
			selectStatement.close();
			System.out.println((new StringBuilder()).append("Date de match: ")
					.append(dateMatch).toString());
			selectStatement = connection.prepareStatement((new StringBuilder())
					.append("select trunc(((((86400*(to_date('")
					.append(dateMatch).append("','DD-MM-YYYY') - to_date('")
					.append(dateNaissance)
					.append("','DD-MM-YYYY')))/60)/60)/24)/365) from dual")
					.toString());
			rs = selectStatement.executeQuery();
			rs.next();
			anneesDifference = rs.getInt(1);
			rs.close();
			selectStatement.close();
			System.out.println((new StringBuilder())
					.append("Ann�es diff�rence: ").append(anneesDifference)
					.toString());
			selectStatement = connection
					.prepareStatement("select lieu from match where match_id = ?");
			selectStatement.setInt(1, matchID);
			rs = selectStatement.executeQuery();
			rs.next();
			lieu = rs.getString(1);
			rs.close();
			selectStatement.close();
			System.out.println((new StringBuilder()).append("Lieu: ")
					.append(lieu).toString());
			selectStatement = connection
					.prepareStatement("select montant, prix_locomotion_id from prix_locomotion where annee = to_number(to_char(sysdate,'YYYY')) and lieu = ? and membre = ? and age_minimum <= ? and age_maximum > ?");
			selectStatement.setString(1, lieu);
			selectStatement.setInt(2, membre);
			selectStatement.setInt(3, anneesDifference);
			selectStatement.setInt(4, anneesDifference);
			rs = selectStatement.executeQuery();
			rs.next();
			info[0] = rs.getInt(1);
			info[1] = rs.getInt(2);
			rs.close();
			selectStatement.close();
			System.out.println((new StringBuilder()).append("Montant: ")
					.append(info[0]).toString());
			System.out.println((new StringBuilder())
					.append("Prix locomotion ID: ").append(info[1]).toString());
			return info;
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

	public void ajouterDeplacementCarMembre(int personneID, int matchID,
			int prixLocomotionID) throws Exception {
		PreparedStatement insertStatement = null;
		Connection connection = null;
		insertStatement = null;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			insertStatement = connection
					.prepareStatement("insert into personnes_match (MATCH_ID,PERSONNE_ID,PRIX_LOCOMOTION_ID)\nvalues(?,?,?)");
			insertStatement.setInt(1, matchID);
			insertStatement.setInt(2, personneID);
			insertStatement.setInt(3, prixLocomotionID);
			insertStatement.execute();
			insertStatement.close();
			connection.commit();
			System.out.println("Personne ajout�e");
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

	public void effacerDeplacementCarMembre(int personneID, int matchID,
			int prixLocomotionID) throws Exception {
		PreparedStatement deleteStatement;
		Connection connection = null;
		deleteStatement = null;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			deleteStatement = connection
					.prepareStatement("delete from personnes_match where match_id = ? and personne_id = ? and prix_locomotion_id = ?");
			deleteStatement.setInt(1, matchID);
			deleteStatement.setInt(2, personneID);
			deleteStatement.setInt(3, prixLocomotionID);
			deleteStatement.execute();
			deleteStatement.close();
			connection.commit();
			System.out.println("Personne effac�e");
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

	public int calculerTotalDeplacementcar(int matchID) throws Exception {
		PreparedStatement selectStatement = null;
		int total;
		Connection connection = null;
		selectStatement = null;
		ResultSet rs = null;
		total = 0;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = connection
					.prepareStatement("select sum(montant) from prix_locomotion pl,personnes_match pm where pl.PRIX_LOCOMOTION_ID = pm.PRIX_LOCOMOTION_ID\nand match_id = ?");
			selectStatement.setInt(1, matchID);
			rs = selectStatement.executeQuery();
			rs.next();
			total = rs.getInt(1);
			rs.close();
			selectStatement.close();
			return total;
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

	public int calculerTotalPersonnesDeplacementcar(int matchID)
			throws Exception {
		PreparedStatement selectStatement = null;
		int total;
		Connection connection = null;
		selectStatement = null;
		ResultSet rs = null;
		total = 0;
		try {
			connection = JDBCConnection.getInstance().getJDBCConnection();
			selectStatement = connection
					.prepareStatement("select count(*) from personnes_match pm where match_id = ?");
			selectStatement.setInt(1, matchID);
			rs = selectStatement.executeQuery();
			rs.next();
			total = rs.getInt(1);
			rs.close();
			selectStatement.close();
			return total;
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

	public static void main(String args[]) {
		DeplacementsCarDB deplacementCarDB = new DeplacementsCarDB();
		try {
			int i[] = deplacementCarDB.getPrixDeplacementParDateNaissance(457,
					2342);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
