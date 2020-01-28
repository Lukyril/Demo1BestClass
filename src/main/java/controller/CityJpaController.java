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
import model.Country;
import model.Address;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.City;

/**
 *
 * @author Lukyril
 */
public class CityJpaController implements Serializable {

    public CityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(City city) {
        if (city.getAddressCollection() == null) {
            city.setAddressCollection(new ArrayList<Address>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country countryId = city.getCountryId();
            if (countryId != null) {
                countryId = em.getReference(countryId.getClass(), countryId.getCountryId());
                city.setCountryId(countryId);
            }
            Collection<Address> attachedAddressCollection = new ArrayList<Address>();
            for (Address addressCollectionAddressToAttach : city.getAddressCollection()) {
                addressCollectionAddressToAttach = em.getReference(addressCollectionAddressToAttach.getClass(), addressCollectionAddressToAttach.getAddressId());
                attachedAddressCollection.add(addressCollectionAddressToAttach);
            }
            city.setAddressCollection(attachedAddressCollection);
            em.persist(city);
            if (countryId != null) {
                countryId.getCityCollection().add(city);
                countryId = em.merge(countryId);
            }
            for (Address addressCollectionAddress : city.getAddressCollection()) {
                City oldCityIdOfAddressCollectionAddress = addressCollectionAddress.getCityId();
                addressCollectionAddress.setCityId(city);
                addressCollectionAddress = em.merge(addressCollectionAddress);
                if (oldCityIdOfAddressCollectionAddress != null) {
                    oldCityIdOfAddressCollectionAddress.getAddressCollection().remove(addressCollectionAddress);
                    oldCityIdOfAddressCollectionAddress = em.merge(oldCityIdOfAddressCollectionAddress);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(City city) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City persistentCity = em.find(City.class, city.getCityId());
            Country countryIdOld = persistentCity.getCountryId();
            Country countryIdNew = city.getCountryId();
            Collection<Address> addressCollectionOld = persistentCity.getAddressCollection();
            Collection<Address> addressCollectionNew = city.getAddressCollection();
            List<String> illegalOrphanMessages = null;
            for (Address addressCollectionOldAddress : addressCollectionOld) {
                if (!addressCollectionNew.contains(addressCollectionOldAddress)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Address " + addressCollectionOldAddress + " since its cityId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (countryIdNew != null) {
                countryIdNew = em.getReference(countryIdNew.getClass(), countryIdNew.getCountryId());
                city.setCountryId(countryIdNew);
            }
            Collection<Address> attachedAddressCollectionNew = new ArrayList<Address>();
            for (Address addressCollectionNewAddressToAttach : addressCollectionNew) {
                addressCollectionNewAddressToAttach = em.getReference(addressCollectionNewAddressToAttach.getClass(), addressCollectionNewAddressToAttach.getAddressId());
                attachedAddressCollectionNew.add(addressCollectionNewAddressToAttach);
            }
            addressCollectionNew = attachedAddressCollectionNew;
            city.setAddressCollection(addressCollectionNew);
            city = em.merge(city);
            if (countryIdOld != null && !countryIdOld.equals(countryIdNew)) {
                countryIdOld.getCityCollection().remove(city);
                countryIdOld = em.merge(countryIdOld);
            }
            if (countryIdNew != null && !countryIdNew.equals(countryIdOld)) {
                countryIdNew.getCityCollection().add(city);
                countryIdNew = em.merge(countryIdNew);
            }
            for (Address addressCollectionNewAddress : addressCollectionNew) {
                if (!addressCollectionOld.contains(addressCollectionNewAddress)) {
                    City oldCityIdOfAddressCollectionNewAddress = addressCollectionNewAddress.getCityId();
                    addressCollectionNewAddress.setCityId(city);
                    addressCollectionNewAddress = em.merge(addressCollectionNewAddress);
                    if (oldCityIdOfAddressCollectionNewAddress != null && !oldCityIdOfAddressCollectionNewAddress.equals(city)) {
                        oldCityIdOfAddressCollectionNewAddress.getAddressCollection().remove(addressCollectionNewAddress);
                        oldCityIdOfAddressCollectionNewAddress = em.merge(oldCityIdOfAddressCollectionNewAddress);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = city.getCityId();
                if (findCity(id) == null) {
                    throw new NonexistentEntityException("The city with id " + id + " no longer exists.");
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
            City city;
            try {
                city = em.getReference(City.class, id);
                city.getCityId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The city with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Address> addressCollectionOrphanCheck = city.getAddressCollection();
            for (Address addressCollectionOrphanCheckAddress : addressCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This City (" + city + ") cannot be destroyed since the Address " + addressCollectionOrphanCheckAddress + " in its addressCollection field has a non-nullable cityId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Country countryId = city.getCountryId();
            if (countryId != null) {
                countryId.getCityCollection().remove(city);
                countryId = em.merge(countryId);
            }
            em.remove(city);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<City> findCityEntities() {
        return findCityEntities(true, -1, -1);
    }

    public List<City> findCityEntities(int maxResults, int firstResult) {
        return findCityEntities(false, maxResults, firstResult);
    }

    private List<City> findCityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(City.class));
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

    public City findCity(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(City.class, id);
        } finally {
            em.close();
        }
    }

    public int getCityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<City> rt = cq.from(City.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
