package fr.univ_amu.iut;

// Importer les classes jdbc
import java.sql.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExempleJDBC {
	// Chaine de connexion
	static final String CONNECT_URL = "jdbc:mysql://localhost:3306/GestionPedaBD";
	static final String LOGIN = "monUser";
	static final String PASSWORD = "monPassword";
    
    // La requete de test
    private static final String reqEtudiantsAixois =
            "SELECT NUM_ET, NOM_ET, PRENOM_ET, CP_ET, VILLE_ET, ANNEE, GROUPE  " +
                    "FROM ETUDIANT " +
                    "WHERE VILLE_ET = 'AIX-EN-PROVENCE'";

    public static void main(String[] args) throws SQLException {
        // Connexion a la base de données
        System.out.println("Connexion");
        try (Connection conn = DriverManager.getConnection(CONNECT_URL, LOGIN, PASSWORD)) {
            System.out.println("Connecte\n");
            
            // Creation d'une instruction SQL
            Statement statement = conn.createStatement();
            
            // Execution de la requete
            System.out.println("Execution de la requete : " + reqEtudiantsAixois);
            ResultSet resultSet = statement.executeQuery(reqEtudiantsAixois);
            
            // Affichage du resultat
            while (resultSet.next()) {

                Object[] tupleCour = {
                        resultSet.getInt("NUM_ET"),
                        resultSet.getString("NOM_ET"),
                        resultSet.getString("PRENOM_ET"),
                        resultSet.getString("CP_ET"),
                        resultSet.getString("VILLE_ET"),
                        resultSet.getInt("ANNEE"),
                        resultSet.getInt("GROUPE")
                };

                System.out.println(Arrays.stream(tupleCour)
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
            }

            // Fermeture de l'instruction (liberation des ressources)
            statement.close();
            System.out.println("\nOk.\n");
        } catch (SQLException e) {
            e.printStackTrace();// Arggg!!!
            System.out.println(e.getMessage() + "\n");
        }
    }
}
