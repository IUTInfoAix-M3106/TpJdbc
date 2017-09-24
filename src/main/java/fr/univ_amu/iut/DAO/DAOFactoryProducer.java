package fr.univ_amu.iut.DAO;

import fr.univ_amu.iut.DAO.JDBC.DAOFactoryJDBC;

public class DAOFactoryProducer {
    public static DAOFactory getFactory(String choice) {
        if (choice.equalsIgnoreCase("JDBC")) {
            return new DAOFactoryJDBC();
        }
        return null;
    }
}