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

import model.Address;
import model.Customer;
import model.Store;

/**
 * @author Lukyril
 */
public class CustomerJpaController implements Serializable {

    public CustomerJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Customer> getAllCustomer() {
        EntityManager em = getEntityManager();
        return (List<Customer>) em
                .createNamedQuery("Customer.findAllNameSorted")
                .getResultList();
    }

    public void create(Customer customer) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address addressId = customer.getAddressId();
            if (addressId != null) {
                addressId = em.getReference(addressId.getClass(), addressId.getAddressId());
                customer.setAddressId(addressId);
            }
            Store storeId = customer.getStoreId();
            if (storeId != null) {
                storeId = em.getReference(storeId.getClass(), storeId.getStoreId());
                customer.setStoreId(storeId);
            }
            em.persist(customer);
            if (addressId != null) {
                addressId.getCustomerCollection().add(customer);
                addressId = em.merge(addressId);
            }
            if (storeId != null) {
                storeId.getCustomerCollection().add(customer);
                storeId = em.merge(storeId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Customer customer) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Customer persistentCustomer = em.find(Customer.class, customer.getCustomerId());
            Address addressIdOld = persistentCustomer.getAddressId();
            Address addressIdNew = customer.getAddressId();
            Store storeIdOld = persistentCustomer.getStoreId();
            Store storeIdNew = customer.getStoreId();
            if (addressIdNew != null) {
                addressIdNew = em.getReference(addressIdNew.getClass(), addressIdNew.getAddressId());
                customer.setAddressId(addressIdNew);
            }
            if (storeIdNew != null) {
                storeIdNew = em.getReference(storeIdNew.getClass(), storeIdNew.getStoreId());
                customer.setStoreId(storeIdNew);
            }
            customer = em.merge(customer);
            if (addressIdOld != null && !addressIdOld.equals(addressIdNew)) {
                addressIdOld.getCustomerCollection().remove(customer);
                addressIdOld = em.merge(addressIdOld);
            }
            if (addressIdNew != null && !addressIdNew.equals(addressIdOld)) {
                addressIdNew.getCustomerCollection().add(customer);
                addressIdNew = em.merge(addressIdNew);
            }
            if (storeIdOld != null && !storeIdOld.equals(storeIdNew)) {
                storeIdOld.getCustomerCollection().remove(customer);
                storeIdOld = em.merge(storeIdOld);
            }
            if (storeIdNew != null && !storeIdNew.equals(storeIdOld)) {
                storeIdNew.getCustomerCollection().add(customer);
                storeIdNew = em.merge(storeIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = customer.getCustomerId();
                if (findCustomer(id) == null) {
                    throw new NonexistentEntityException("The customer with id " + id + " no longer exists.");
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
            Customer customer;
            try {
                customer = em.getReference(Customer.class, id);
                customer.getCustomerId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The customer with id " + id + " no longer exists.", enfe);
            }
            Address addressId = customer.getAddressId();
            if (addressId != null) {
                addressId.getCustomerCollection().remove(customer);
                addressId = em.merge(addressId);
            }
            Store storeId = customer.getStoreId();
            if (storeId != null) {
                storeId.getCustomerCollection().remove(customer);
                storeId = em.merge(storeId);
            }
            em.remove(customer);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Customer> findCustomerEntities() {
        return findCustomerEntities(true, -1, -1);
    }

    public List<Customer> findCustomerEntities(int maxResults, int firstResult) {
        return findCustomerEntities(false, maxResults, firstResult);
    }

    private List<Customer> findCustomerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Customer.class));
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

    public Customer findCustomer(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    public int getCustomerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Customer> rt = cq.from(Customer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
