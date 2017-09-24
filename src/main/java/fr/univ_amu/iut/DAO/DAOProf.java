package fr.univ_amu.iut.DAO;

import fr.univ_amu.iut.beans.Module;
import fr.univ_amu.iut.beans.Prof;

import java.util.List;

public interface DAOProf extends DAO<Prof> {
    List<Prof> findByNom(String nom);

    List<Prof> findMatSpec(Module matSpec);

    int computeNbProf();
}
