package fr.univ_amu.iut.JDBC.RowMappers;

import fr.univ_amu.iut.beans.Etudiant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtudiantMapper implements RowMapper<Etudiant> {
    @Override
    public Etudiant mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Etudiant etudiant = new Etudiant();
        etudiant.setNumEt(resultSet.getInt("NUM_ET"));
        etudiant.setNomEt(resultSet.getString("NOM_ET"));
        etudiant.setPrenomEt(resultSet.getString("PRENOM_ET"));
        etudiant.setCpEt(resultSet.getString("CP_ET"));
        etudiant.setVilleEt(resultSet.getString("VILLE_ET"));
        etudiant.setAnnee(resultSet.getInt("ANNEE"));
        etudiant.setGroupe(resultSet.getInt("GROUPE"));
        return etudiant;
    }
}
