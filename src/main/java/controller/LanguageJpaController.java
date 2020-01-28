/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Film;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Language;

/**
 *
 * @author Lukyril
 */
public class LanguageJpaController implements Serializable {

    public LanguageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Language language) {
        if (language.getFilmCollection() == null) {
            language.setFilmCollection(new ArrayList<Film>());
        }
        if (language.getFilmCollection1() == null) {
            language.setFilmCollection1(new ArrayList<Film>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Film> attachedFilmCollection = new ArrayList<Film>();
            for (Film filmCollectionFilmToAttach : language.getFilmCollection()) {
                filmCollectionFilmToAttach = em.getReference(filmCollectionFilmToAttach.getClass(), filmCollectionFilmToAttach.getFilmId());
                attachedFilmCollection.add(filmCollectionFilmToAttach);
            }
            language.setFilmCollection(attachedFilmCollection);
            Collection<Film> attachedFilmCollection1 = new ArrayList<Film>();
            for (Film filmCollection1FilmToAttach : language.getFilmCollection1()) {
                filmCollection1FilmToAttach = em.getReference(filmCollection1FilmToAttach.getClass(), filmCollection1FilmToAttach.getFilmId());
                attachedFilmCollection1.add(filmCollection1FilmToAttach);
            }
            language.setFilmCollection1(attachedFilmCollection1);
            em.persist(language);
            for (Film filmCollectionFilm : language.getFilmCollection()) {
                Language oldLanguageIdOfFilmCollectionFilm = filmCollectionFilm.getLanguageId();
                filmCollectionFilm.setLanguageId(language);
                filmCollectionFilm = em.merge(filmCollectionFilm);
                if (oldLanguageIdOfFilmCollectionFilm != null) {
                    oldLanguageIdOfFilmCollectionFilm.getFilmCollection().remove(filmCollectionFilm);
                    oldLanguageIdOfFilmCollectionFilm = em.merge(oldLanguageIdOfFilmCollectionFilm);
                }
            }
            for (Film filmCollection1Film : language.getFilmCollection1()) {
                Language oldOriginalLanguageIdOfFilmCollection1Film = filmCollection1Film.getOriginalLanguageId();
                filmCollection1Film.setOriginalLanguageId(language);
                filmCollection1Film = em.merge(filmCollection1Film);
                if (oldOriginalLanguageIdOfFilmCollection1Film != null) {
                    oldOriginalLanguageIdOfFilmCollection1Film.getFilmCollection1().remove(filmCollection1Film);
                    oldOriginalLanguageIdOfFilmCollection1Film = em.merge(oldOriginalLanguageIdOfFilmCollection1Film);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Language language) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Language persistentLanguage = em.find(Language.class, language.getLanguageId());
            Collection<Film> filmCollectionOld = persistentLanguage.getFilmCollection();
            Collection<Film> filmCollectionNew = language.getFilmCollection();
            Collection<Film> filmCollection1Old = persistentLanguage.getFilmCollection1();
            Collection<Film> filmCollection1New = language.getFilmCollection1();
            List<String> illegalOrphanMessages = null;
            for (Film filmCollectionOldFilm : filmCollectionOld) {
                if (!filmCollectionNew.contains(filmCollectionOldFilm)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Film " + filmCollectionOldFilm + " since its languageId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Film> attachedFilmCollectionNew = new ArrayList<Film>();
            for (Film filmCollectionNewFilmToAttach : filmCollectionNew) {
                filmCollectionNewFilmToAttach = em.getReference(filmCollectionNewFilmToAttach.getClass(), filmCollectionNewFilmToAttach.getFilmId());
                attachedFilmCollectionNew.add(filmCollectionNewFilmToAttach);
            }
            filmCollectionNew = attachedFilmCollectionNew;
            language.setFilmCollection(filmCollectionNew);
            Collection<Film> attachedFilmCollection1New = new ArrayList<Film>();
            for (Film filmCollection1NewFilmToAttach : filmCollection1New) {
                filmCollection1NewFilmToAttach = em.getReference(filmCollection1NewFilmToAttach.getClass(), filmCollection1NewFilmToAttach.getFilmId());
                attachedFilmCollection1New.add(filmCollection1NewFilmToAttach);
            }
            filmCollection1New = attachedFilmCollection1New;
            language.setFilmCollection1(filmCollection1New);
            language = em.merge(language);
            for (Film filmCollectionNewFilm : filmCollectionNew) {
                if (!filmCollectionOld.contains(filmCollectionNewFilm)) {
                    Language oldLanguageIdOfFilmCollectionNewFilm = filmCollectionNewFilm.getLanguageId();
                    filmCollectionNewFilm.setLanguageId(language);
                    filmCollectionNewFilm = em.merge(filmCollectionNewFilm);
                    if (oldLanguageIdOfFilmCollectionNewFilm != null && !oldLanguageIdOfFilmCollectionNewFilm.equals(language)) {
                        oldLanguageIdOfFilmCollectionNewFilm.getFilmCollection().remove(filmCollectionNewFilm);
                        oldLanguageIdOfFilmCollectionNewFilm = em.merge(oldLanguageIdOfFilmCollectionNewFilm);
                    }
                }
            }
            for (Film filmCollection1OldFilm : filmCollection1Old) {
                if (!filmCollection1New.contains(filmCollection1OldFilm)) {
                    filmCollection1OldFilm.setOriginalLanguageId(null);
                    filmCollection1OldFilm = em.merge(filmCollection1OldFilm);
                }
            }
            for (Film filmCollection1NewFilm : filmCollection1New) {
                if (!filmCollection1Old.contains(filmCollection1NewFilm)) {
                    Language oldOriginalLanguageIdOfFilmCollection1NewFilm = filmCollection1NewFilm.getOriginalLanguageId();
                    filmCollection1NewFilm.setOriginalLanguageId(language);
                    filmCollection1NewFilm = em.merge(filmCollection1NewFilm);
                    if (oldOriginalLanguageIdOfFilmCollection1NewFilm != null && !oldOriginalLanguageIdOfFilmCollection1NewFilm.equals(language)) {
                        oldOriginalLanguageIdOfFilmCollection1NewFilm.getFilmCollection1().remove(filmCollection1NewFilm);
                        oldOriginalLanguageIdOfFilmCollection1NewFilm = em.merge(oldOriginalLanguageIdOfFilmCollection1NewFilm);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = language.getLanguageId();
                if (findLanguage(id) == null) {
                    throw new NonexistentEntityException("The language with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Short id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Language language;
            try {
                language = em.getReference(Language.class, id);
                language.getLanguageId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The language with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Film> filmCollectionOrphanCheck = language.getFilmCollection();
            for (Film filmCollectionOrphanCheckFilm : filmCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Language (" + language + ") cannot be destroyed since the Film " + filmCollectionOrphanCheckFilm + " in its filmCollection field has a non-nullable languageId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Film> filmCollection1 = language.getFilmCollection1();
            for (Film filmCollection1Film : filmCollection1) {
                filmCollection1Film.setOriginalLanguageId(null);
                filmCollection1Film = em.merge(filmCollection1Film);
            }
            em.remove(language);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Language> findLanguageEntities() {
        return findLanguageEntities(true, -1, -1);
    }

    public List<Language> findLanguageEntities(int maxResults, int firstResult) {
        return findLanguageEntities(false, maxResults, firstResult);
    }

    private List<Language> findLanguageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Language.class));
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

    public Language findLanguage(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Language.class, id);
        } finally {
            em.close();
        }
    }

    public int getLanguageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Language> rt = cq.from(Language.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
