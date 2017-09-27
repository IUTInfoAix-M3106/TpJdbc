package fr.univ_amu.iut.DAO;

import java.util.List;

public interface DAO<T> {

    /**
     * Permet la suppression d'un tuple de la base
     *
     * @param obj Objet à supprimer dans la base
     */
    boolean delete(T obj);

    /**
     * Permet de récupérer tous les objets d'une table
     *
     * @return liste de tous les objets contenus dans la base
     */
    List<T> findAll();

    /**
     * Permet de récupérer un objet via son ID
     *
     * @param id identifiant du tuple recherché
     * @return le tuple recherché s'il existe
     */
    T getById(int id);

    /**
     * Permet de créer une entrée dans la base de données par rapport à un objet
     *
     * @param obj Objet à insérer dans la base
     */
    T insert(T obj);

    /**
     * Permet de mettre à jour les données d'un tuple dans la base à partir d'un
     * objet passé en paramètre
     *
     * @param obj Objet à mettre à jour dans la base
     */
    boolean update(T obj);
}