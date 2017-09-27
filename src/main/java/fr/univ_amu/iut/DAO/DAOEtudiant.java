package fr.univ_amu.iut.DAO;


import fr.univ_amu.iut.beans.Etudiant;

import java.util.List;

public interface DAOEtudiant extends DAO<Etudiant> {
    int computeNbEtudiant();

    List<Etudiant> findByAnnee(int annee);

    List<Etudiant> findByGroupe(int groupe);

    List<Etudiant> findByNom(String nomEt);
}