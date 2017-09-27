package fr.univ_amu.iut.JDBC.RowMappers;

import fr.univ_amu.iut.beans.Module;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModuleMapper implements RowMapper<Module> {
    @Override
    public Module mapRow(ResultSet rset, int rowNum) throws SQLException {
        Module module = new Module();
        module.setCode(rset.getString("CODE"));
        module.setCoefCc(rset.getInt("COEFF_CC"));
        module.setCoefTest(rset.getInt("COEFF_TEST"));
        module.setDiscipline(rset.getString("DISCIPLINE"));
        module.sethCoursPrev(rset.getInt("H_COURS_PREV"));
        module.sethCoursRea(rset.getInt("H_COURS_REA"));
        module.sethTpPrev(rset.getInt("H_TP_PREV"));
        module.sethTpRea(rset.getInt("H_TP_REA"));
        module.setLibelle(rset.getString("LIBELLE"));
        return module;
    }
}
