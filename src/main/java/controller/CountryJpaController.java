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
import model.City;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Country;

/**
 *
 * @author Lukyril
 */
public class CountryJpaController implements Serializable {

    public CountryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Country country) {
        if (country.getCityCollection() == null) {
            country.setCityCollection(new ArrayList<City>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<City> attachedCityCollection = new ArrayList<City>();
            for (City cityCollectionCityToAttach : country.getCityCollection()) {
                cityCollectionCityToAttach = em.getReference(cityCollectionCityToAttach.getClass(), cityCollectionCityToAttach.getCityId());
                attachedCityCollection.add(cityCollectionCityToAttach);
            }
            country.setCityCollection(attachedCityCollection);
            em.persist(country);
            for (City cityCollectionCity : country.getCityCollection()) {
                Country oldCountryIdOfCityCollectionCity = cityCollectionCity.getCountryId();
                cityCollectionCity.setCountryId(country);
                cityCollectionCity = em.merge(cityCollectionCity);
                if (oldCountryIdOfCityCollectionCity != null) {
                    oldCountryIdOfCityCollectionCity.getCityCollection().remove(cityCollectionCity);
                    oldCountryIdOfCityCollectionCity = em.merge(oldCountryIdOfCityCollectionCity);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Country country) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country persistentCountry = em.find(Country.class, country.getCountryId());
            Collection<City> cityCollectionOld = persistentCountry.getCityCollection();
            Collection<City> cityCollectionNew = country.getCityCollection();
            List<String> illegalOrphanMessages = null;
            for (City cityCollectionOldCity : cityCollectionOld) {
                if (!cityCollectionNew.contains(cityCollectionOldCity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain City " + cityCollectionOldCity + " since its countryId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<City> attachedCityCollectionNew = new ArrayList<City>();
            for (City cityCollectionNewCityToAttach : cityCollectionNew) {
                cityCollectionNewCityToAttach = em.getReference(cityCollectionNewCityToAttach.getClass(), cityCollectionNewCityToAttach.getCityId());
                attachedCityCollectionNew.add(cityCollectionNewCityToAttach);
            }
            cityCollectionNew = attachedCityCollectionNew;
            country.setCityCollection(cityCollectionNew);
            country = em.merge(country);
            for (City cityCollectionNewCity : cityCollectionNew) {
                if (!cityCollectionOld.contains(cityCollectionNewCity)) {
                    Country oldCountryIdOfCityCollectionNewCity = cityCollectionNewCity.getCountryId();
                    cityCollectionNewCity.setCountryId(country);
                    cityCollectionNewCity = em.merge(cityCollectionNewCity);
                    if (oldCountryIdOfCityCollectionNewCity != null && !oldCountryIdOfCityCollectionNewCity.equals(country)) {
                        oldCountryIdOfCityCollectionNewCity.getCityCollection().remove(cityCollectionNewCity);
                        oldCountryIdOfCityCollectionNewCity = em.merge(oldCountryIdOfCityCollectionNewCity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = country.getCountryId();
                if (findCountry(id) == null) {
                    throw new NonexistentEntityException("The country with id " + id + " no longer exists.");
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
            Country country;
            try {
                country = em.getReference(Country.class, id);
                country.getCountryId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The country with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<City> cityCollectionOrphanCheck = country.getCityCollection();
            for (City cityCollectionOrphanCheckCity : cityCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Country (" + country + ") cannot be destroyed since the City " + cityCollectionOrphanCheckCity + " in its cityCollection field has a non-nullable countryId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(country);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Country> findCountryEntities() {
        return findCountryEntities(true, -1, -1);
    }

    public List<Country> findCountryEntities(int maxResults, int firstResult) {
        return findCountryEntities(false, maxResults, firstResult);
    }

    private List<Country> findCountryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Country.class));
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

    public Country findCountry(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Country.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Country> rt = cq.from(Country.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
