package fr.univ_amu.iut.DAO;

public interface DAOFactory {
    DAOEtudiant createDAOEtudiant();

    DAOProf createDAOProf();

    DAOModule createDAOModule();
}
