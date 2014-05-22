// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EditionMembreDB.java
package standardNaast.model;

import java.io.PrintStream;
import java.sql.*;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;

// Referenced classes of package standardNaast.model:
//            JDBCConnection
public class EditionMembreDB {

    public EditionMembreDB() {
    }

    public int getNumDeplacement(String saison, int personneID, String lieu)
            throws Exception {
        int numDeplacement = 0;
        Connection connection = JDBCConnection.getInstance().getJDBCConnection();
        PreparedStatement pstmt = connection.prepareStatement("select count(*) from personnes p ,personnes_match pm ,match m, saison_equipe se\nwhere p.personne_id = pm.personne_id and pm.match_id = m.match_id\nand p.personne_id = ? and upper(m.lieu)=upper(?)\nand se.saison_equipe_id = m.saison_equipe_id\nand saison_id = ?");
        pstmt.setInt(1, personneID);
        pstmt.setString(2, lieu);
        pstmt.setString(3, saison);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        numDeplacement = rs.getInt(1);
        rs.close();
        pstmt.close();
        return numDeplacement;
    }

    public Vector getCotisations(int personneID)
            throws Exception {
        Vector allCotisations = new Vector();
        Vector cotisation = null;
        Vector v = new Vector();
        Connection connection = JDBCConnection.getInstance().getJDBCConnection();
        PreparedStatement pstmt = connection.prepareStatement("select p.personne_id,c.annee_cotisation \"Annï¿½e cotisation\" ,c.montant_cotisation \"Montant Cotisation\", to_char(pc.date_paiement,'DD-MM-YYYY' \"Date de paiement\" from personnes p, cotisations c, personnes_cotisations pc where p.personne_id = pc.personne_id and c.annee_cotisation = pc.annee_cotisation and paye = 1 and p.personne_id = ? order by annee_cotisation");
        pstmt.setInt(1, personneID);
        ResultSet rs = pstmt.executeQuery();
        String columnNames[] = getColumnNames(rs);
        for (; rs.next(); allCotisations.add(cotisation)) {
            cotisation = new Vector();
            cotisation.add(new Integer(rs.getInt(1)));
            cotisation.add(rs.getString(2));
            cotisation.add(new Integer(rs.getInt(3)));
            cotisation.add(rs.getString(4));
        }

        rs.close();
        pstmt.close();
        v.add(allCotisations);
        v.add(columnNames);
        return v;
    }

    public String[] getColumnNames(ResultSet rs)
            throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        String columnNames[] = new String[rsmd.getColumnCount()];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = rsmd.getColumnName(i + 1);
        }

        return columnNames;
    }

    public Vector getAbonnements(int personneID)
            throws Exception {
        Vector allAbonnements = new Vector();
        Vector abonnement = null;
        Vector v = new Vector();
        Connection connection = JDBCConnection.getInstance().getJDBCConnection();
        PreparedStatement pstmt = connection.prepareStatement("select \nABONNEMENT_ID,\nBLOC_VALUE as BLOC,\nRANG,\nPLACE,\nSAISON,\nRetourner_prix_abonnement(PERSONNE_ID,SAISON,BLOC.BLOC_ID),\nREDUCTION,\nACOMPTE,\nDECODE(PAYE,0,'NON',1,'OUI',PAYE),\nfrom ABONNEMENT, BLOC\nwhere ABONNEMENT.BLOC_ID = BLOC.BLOC_ID\nand PERSONNE_ID = ?");
        pstmt.setInt(1, personneID);
        ResultSet rs = pstmt.executeQuery();
        String columnNames[] = getColumnNames(rs);
        for (; rs.next(); allAbonnements.add(abonnement)) {
            abonnement = new Vector();
            abonnement.add(new Integer(rs.getInt(1)));
            abonnement.add(rs.getString(2));
            abonnement.add(rs.getString(3));
            abonnement.add(rs.getString(4));
            abonnement.add(rs.getString(5));
            abonnement.add(new Integer(rs.getInt(6)));
            abonnement.add(new Integer(rs.getInt(7)));
            abonnement.add(new Integer(rs.getInt(8)));
            abonnement.add(rs.getString(9));
        }

        rs.close();
        pstmt.close();
        v.add(allAbonnements);
        v.add(columnNames);
        return v;
    }

    public int addMembre(List<String> data) throws Exception {
        Connection connection;
        PreparedStatement insertStatement = null;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        int personneID;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        insertStatement = null;
        selectStatement = null;
        rs = null;
        personneID = -1;
        boolean success = false;
        try {
            selectStatement = connection.prepareStatement("SELECT PERSONNES_SEQ.NEXTVAL FROM DUAL");
            rs = selectStatement.executeQuery();
            rs.next();
            personneID = rs.getInt(1);
            rs.close();
            selectStatement.close();
            insertStatement = connection.prepareStatement("insert into personnes (PERSONNE_ID,NOM,PRENOM,ADRESSE,CODE_POSTAL,VILLE,TELEPHONE,GSM,EMAIL,DATE_NAISSANCE,NUMERO_MEMBRE,CARTE_IDENTITE,ETUDIANT, validite_carte_identite)\nvalues(?,?,?,?,?,?,?,?,?,to_date(?,'DD-MM-YYYY'),?,?,?,to_date(?,'DD-MM-YYYY'))");
            insertStatement.setInt(1, personneID);
            insertStatement.setString(2, data.get(0));
            insertStatement.setString(3, data.get(1));
            insertStatement.setString(4, data.get(2));
            insertStatement.setString(5, data.get(3));
            insertStatement.setString(6, data.get(4));
            insertStatement.setString(7, data.get(5));
            insertStatement.setString(8, data.get(6));
            insertStatement.setString(9, data.get(7));
            if ((data.get(8)).equals("----------")) {
                insertStatement.setString(10, "01-01-1900");
            } else {
                insertStatement.setString(10, data.get(8));
            }
            insertStatement.setInt(11, Integer.parseInt(data.get(9)));
            insertStatement.setString(12, data.get(10));
            insertStatement.setInt(13, Integer.parseInt(data.get(11)));
            insertStatement.setString(14, data.get(12));
            insertStatement.execute();
            connection.commit();
            insertStatement.close();
            success = true;
            JOptionPane.showMessageDialog(null, "Membre ajouté", "Ajout Membre", 1);
            return personneID;
        } catch (SQLException e) {
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

    public boolean updateMembre(List<String> data)
            throws Exception {
        Connection connection;
        PreparedStatement updateStatement = null;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        updateStatement = null;
        boolean success = false;
        System.out.println(Integer.parseInt(data.get(13)));
        boolean flag;
        try {
            updateStatement = connection.prepareStatement("UPDATE PERSONNES SET NOM = ?,\nPRENOM = ?,\nADRESSE = ?,\nCODE_POSTAL = ?,\nVILLE = ?,\nTELEPHONE = ?,\nGSM = ?,\nEMAIL = ?,\nDATE_NAISSANCE = to_date(?,'DD-MM-YYYY'),\nNUMERO_MEMBRE = ?,\nCARTE_IDENTITE = ?,\nETUDIANT = ?, validite_carte_identite =to_date(?,'DD-MM-YYYY') \nWHERE PERSONNE_ID = ?");
            updateStatement.setString(1, data.get(0));
            updateStatement.setString(2, data.get(1));
            updateStatement.setString(3, data.get(2));
            updateStatement.setString(4, data.get(3));
            updateStatement.setString(5, data.get(4));
            updateStatement.setString(6, data.get(5));
            updateStatement.setString(7, data.get(6));
            updateStatement.setString(8, data.get(7));
            if ((data.get(8)).equals("----------")) {
                updateStatement.setString(9, "01-01-1900");
            } else {
                updateStatement.setString(9, data.get(8));
            }
            updateStatement.setInt(10, Integer.parseInt(data.get(9)));
            updateStatement.setString(11, data.get(10));
            updateStatement.setInt(12, Integer.parseInt(data.get(11)));
            updateStatement.setInt(14, Integer.parseInt(data.get(13)));
            if ((data.get(12)).equals("----------")) {
                updateStatement.setString(13, "01-01-1900");
            } else {
                updateStatement.setString(13, data.get(12));
            }
            updateStatement.executeUpdate();
            connection.commit();
            updateStatement.close();
            success = true;
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                updateStatement.close();
            } catch (Exception exception) {
            }
        }
    }

    public boolean deleteMembre(long personneID)
            throws Exception {
        Connection connection;
        PreparedStatement deleteStatement;
        boolean success;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        deleteStatement = null;
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        success = false;
        int numeroMembre = 0;
        boolean flag;
        try {
            selectStatement = connection.prepareStatement("select numero_membre from personnes where personne_id = ?");
            selectStatement.setLong(1, personneID);
            rs = selectStatement.executeQuery();
            rs.next();
            numeroMembre = rs.getInt(1);
            rs.close();
            selectStatement.close();
            if (numeroMembre < 10000) {
                deleteStatement = connection.prepareStatement("update personnes set numero_membre = 10000 WHERE PERSONNE_ID = ?");
                deleteStatement.setLong(1, personneID);
                deleteStatement.execute();
                deleteStatement.close();
                reorderNumeroMembre();
                connection.commit();

            }
            return true;
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

    public int getAnneesDifferences(String dateNaissance)
            throws Exception {
        PreparedStatement selectStatement = null;
        ResultSet rs = null;
        Connection connection = null;
        selectStatement = null;
        rs = null;
        int anneeDifferences = 0;
        int i;
        try {
            connection = JDBCConnection.getInstance().getJDBCConnection();
            selectStatement = connection.prepareStatement((new StringBuilder()).append("select trunc(((((86400*( sysdate - to_date('").append(dateNaissance).append("','DD-MM-YYYY')))/60)/60)/24)/365) from").append(" dual").toString());
            rs = selectStatement.executeQuery();
            rs.next();
            anneeDifferences = rs.getInt(1);
            rs.close();
            selectStatement.close();
            return anneeDifferences;
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

    public void reorderNumeroMembre()
            throws Exception {
        Connection connection;
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet rs = null;
        Vector personneID;
        connection = JDBCConnection.getInstance().getJDBCConnection();
        selectStatement = null;
        updateStatement = null;
        rs = null;
        personneID = new Vector();
        try {
            selectStatement = connection.prepareStatement("select personne_id from personnes where numero_membre < 10000 order by numero_membre");
            for (rs = selectStatement.executeQuery(); rs.next(); personneID.add(new Integer(rs.getInt(1))));
            rs.close();
            selectStatement.close();
            updateStatement = connection.prepareStatement("update personnes set numero_membre = ? where personne_id = ?");
            for (int i = 0; i < personneID.size(); i++) {
                updateStatement.setInt(1, i + 1);
                updateStatement.setInt(2, ((Integer) personneID.get(i)).intValue());
                updateStatement.addBatch();
            }

            updateStatement.executeBatch();
            updateStatement.close();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                rs.close();
                selectStatement.close();
                updateStatement.close();
            } catch (Exception exception) {
            }
        }
    }
}