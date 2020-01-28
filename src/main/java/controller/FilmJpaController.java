/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Film;
import model.Language;

/**
 *
 * @author Lukyril
 */
public class FilmJpaController implements Serializable {

    public FilmJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Film film) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Language languageId = film.getLanguageId();
            if (languageId != null) {
                languageId = em.getReference(languageId.getClass(), languageId.getLanguageId());
                film.setLanguageId(languageId);
            }
            Language originalLanguageId = film.getOriginalLanguageId();
            if (originalLanguageId != null) {
                originalLanguageId = em.getReference(originalLanguageId.getClass(), originalLanguageId.getLanguageId());
                film.setOriginalLanguageId(originalLanguageId);
            }
            em.persist(film);
            if (languageId != null) {
                languageId.getFilmCollection().add(film);
                languageId = em.merge(languageId);
            }
            if (originalLanguageId != null) {
                originalLanguageId.getFilmCollection().add(film);
                originalLanguageId = em.merge(originalLanguageId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Film film) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Film persistentFilm = em.find(Film.class, film.getFilmId());
            Language languageIdOld = persistentFilm.getLanguageId();
            Language languageIdNew = film.getLanguageId();
            Language originalLanguageIdOld = persistentFilm.getOriginalLanguageId();
            Language originalLanguageIdNew = film.getOriginalLanguageId();
            if (languageIdNew != null) {
                languageIdNew = em.getReference(languageIdNew.getClass(), languageIdNew.getLanguageId());
                film.setLanguageId(languageIdNew);
            }
            if (originalLanguageIdNew != null) {
                originalLanguageIdNew = em.getReference(originalLanguageIdNew.getClass(), originalLanguageIdNew.getLanguageId());
                film.setOriginalLanguageId(originalLanguageIdNew);
            }
            film = em.merge(film);
            if (languageIdOld != null && !languageIdOld.equals(languageIdNew)) {
                languageIdOld.getFilmCollection().remove(film);
                languageIdOld = em.merge(languageIdOld);
            }
            if (languageIdNew != null && !languageIdNew.equals(languageIdOld)) {
                languageIdNew.getFilmCollection().add(film);
                languageIdNew = em.merge(languageIdNew);
            }
            if (originalLanguageIdOld != null && !originalLanguageIdOld.equals(originalLanguageIdNew)) {
                originalLanguageIdOld.getFilmCollection().remove(film);
                originalLanguageIdOld = em.merge(originalLanguageIdOld);
            }
            if (originalLanguageIdNew != null && !originalLanguageIdNew.equals(originalLanguageIdOld)) {
                originalLanguageIdNew.getFilmCollection().add(film);
                originalLanguageIdNew = em.merge(originalLanguageIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = film.getFilmId();
                if (findFilm(id) == null) {
                    throw new NonexistentEntityException("The film with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Short id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Film film;
            try {
                film = em.getReference(Film.class, id);
                film.getFilmId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The film with id " + id + " no longer exists.", enfe);
            }
            Language languageId = film.getLanguageId();
            if (languageId != null) {
                languageId.getFilmCollection().remove(film);
                languageId = em.merge(languageId);
            }
            Language originalLanguageId = film.getOriginalLanguageId();
            if (originalLanguageId != null) {
                originalLanguageId.getFilmCollection().remove(film);
                originalLanguageId = em.merge(originalLanguageId);
            }
            em.remove(film);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Film> findFilmEntities() {
        return findFilmEntities(true, -1, -1);
    }

    public List<Film> findFilmEntities(int maxResults, int firstResult) {
        return findFilmEntities(false, maxResults, firstResult);
    }

    private List<Film> findFilmEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Film.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Film findFilm(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Film.class, id);
        } finally {
            em.close();
        }
    }

    public int getFilmCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Film> rt = cq.from(Film.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
