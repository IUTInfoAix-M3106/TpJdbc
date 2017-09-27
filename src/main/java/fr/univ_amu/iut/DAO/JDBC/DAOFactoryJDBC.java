package fr.univ_amu.iut.DAO.JDBC;

import fr.univ_amu.iut.DAO.DAOEtudiant;
import fr.univ_amu.iut.DAO.DAOFactory;
import fr.univ_amu.iut.DAO.DAOModule;
import fr.univ_amu.iut.DAO.DAOProf;

public final class DAOFactoryJDBC implements DAOFactory {
    @Override
    public DAOEtudiant createDAOEtudiant() {
        return new DAOEtudiantJDBC();
    }

    @Override
    public DAOProf createDAOProf() {
        return null;
    }

    @Override
    public DAOModule createDAOModule() {
        return null;
    }
}
