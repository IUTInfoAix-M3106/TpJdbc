package fr.univ_amu.iut.DAO;

import fr.univ_amu.iut.beans.Module;
import fr.univ_amu.iut.beans.Prof;

import java.util.List;


public interface DAOModule extends DAO<Module> {
    List<Module> findByLibelle(String libelle);

    List<Module> findByDiscipline(String discipline);

    List<Module> findByResponsable(Prof Responsable);
}
