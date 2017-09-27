package fr.univ_amu.iut.JDBC.RowMappers;

import fr.univ_amu.iut.beans.Prof;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfMapper implements RowMapper<Prof> {
    @Override
    public Prof mapRow(ResultSet rset, int rowNum) throws SQLException {
        Prof prof = new Prof();
        prof.setNumProf(rset.getInt("NUM_PROF"));
        prof.setNomProf(rset.getString("NOM_PROF"));
        prof.setPrenomProf(rset.getString("PRENOM_PROF"));
        prof.setAdrProf(rset.getString("ADR_PROF"));
        prof.setCpProf(rset.getString("CP_PROF"));
        prof.setVilleProf(rset.getString("VILLE_PROF"));
        prof.setMatSpec((new ModuleMapper()).mapRow(rset, rowNum));
        return prof;
    }
}
