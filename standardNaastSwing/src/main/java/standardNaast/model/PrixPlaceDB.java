// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PrixPlaceDB.java

package standardNaast.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection

public class PrixPlaceDB {

	public PrixPlaceDB() {
	}


	public void ajouterPrixPlace(final Vector data) throws Exception {
		Connection connection;
		int typeMatch;
		int montant;
		String saison;
		int tarif;
		int abonne;
		String blocs[];
		String equipe;
		Vector typePersonne;
		connection = JDBCConnection.getInstance().getJDBCConnection();
		PreparedStatement selectStatement = null;
		PreparedStatement insertStatement = null;
		ResultSet rs = null;
		typeMatch = ((Integer) data.get(0)).intValue();
		montant = ((Integer) data.get(1)).intValue();
		saison = (String) data.get(2);
		tarif = ((Integer) data.get(3)).intValue();
		abonne = ((Integer) data.get(4)).intValue();
		blocs = (String[]) data.get(5);
		equipe = (String) data.get(6);
		typePersonne = new Vector();
		try {
			selectStatement = connection.prepareStatement("select type_personne_id from type_personne where tarif = ?");
			selectStatement.setInt(1, tarif);

			for (rs = selectStatement.executeQuery(); rs.next(); typePersonne.add(Integer.valueOf(rs.getInt(1)))) {
				;
			}
			rs.close();
			selectStatement.close();
			for (int i = 0; i < blocs.length; i++) {
				selectStatement =
						connection
								.prepareStatement("select bloc_id from bloc where bloc_value = ? and equipe_id = (select equipe_id from equipe where nom_equipe = ?)");
				selectStatement.setString(1, blocs[i]);
				selectStatement.setString(2, equipe);
				rs = selectStatement.executeQuery();
				rs.next();
				int blocID = rs.getInt(1);
				for (int j = 0; j < typePersonne.size(); j++) {
					insertStatement =
							connection
									.prepareStatement("insert into prix_place (PRIX_PLACE_ID,BLOC_ID,TYPE_PERSONNE_ID,TYPE_MATCH_ID,MONTANT,SAISON,TARIF,ABONNE)\nvalues(prix_place_seq.NEXTVAL,?,?,?,?,?,?,?)");
					insertStatement.setInt(1, blocID);
					insertStatement.setInt(2, ((Integer) typePersonne.get(j)).intValue());
					insertStatement.setInt(3, typeMatch);
					insertStatement.setInt(4, montant);
					insertStatement.setString(5, saison);
					insertStatement.setInt(6, tarif);
					insertStatement.setInt(7, abonne);
					insertStatement.execute();
					insertStatement.close();
				}

			}

			rs.close();
			selectStatement.close();
			connection.commit();
			JOptionPane.showMessageDialog(null, "Prix des places ajoutés", "", 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
				selectStatement.close();

			} catch (Exception e) {

			}
		}
	}
}