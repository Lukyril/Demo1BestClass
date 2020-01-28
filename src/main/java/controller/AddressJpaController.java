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
import model.Staff;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Address;
import model.Store;
import model.Customer;

/**
 *
 * @author Lukyril
 */
public class AddressJpaController implements Serializable {

    public AddressJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Address address) {
        if (address.getStaffCollection() == null) {
            address.setStaffCollection(new ArrayList<Staff>());
        }
        if (address.getStoreCollection() == null) {
            address.setStoreCollection(new ArrayList<Store>());
        }
        if (address.getCustomerCollection() == null) {
            address.setCustomerCollection(new ArrayList<Customer>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City cityId = address.getCityId();
            if (cityId != null) {
                cityId = em.getReference(cityId.getClass(), cityId.getCityId());
                address.setCityId(cityId);
            }
            Collection<Staff> attachedStaffCollection = new ArrayList<Staff>();
            for (Staff staffCollectionStaffToAttach : address.getStaffCollection()) {
                staffCollectionStaffToAttach = em.getReference(staffCollectionStaffToAttach.getClass(), staffCollectionStaffToAttach.getStaffId());
                attachedStaffCollection.add(staffCollectionStaffToAttach);
            }
            address.setStaffCollection(attachedStaffCollection);
            Collection<Store> attachedStoreCollection = new ArrayList<Store>();
            for (Store storeCollectionStoreToAttach : address.getStoreCollection()) {
                storeCollectionStoreToAttach = em.getReference(storeCollectionStoreToAttach.getClass(), storeCollectionStoreToAttach.getStoreId());
                attachedStoreCollection.add(storeCollectionStoreToAttach);
            }
            address.setStoreCollection(attachedStoreCollection);
            Collection<Customer> attachedCustomerCollection = new ArrayList<Customer>();
            for (Customer customerCollectionCustomerToAttach : address.getCustomerCollection()) {
                customerCollectionCustomerToAttach = em.getReference(customerCollectionCustomerToAttach.getClass(), customerCollectionCustomerToAttach.getCustomerId());
                attachedCustomerCollection.add(customerCollectionCustomerToAttach);
            }
            address.setCustomerCollection(attachedCustomerCollection);
            em.persist(address);
            if (cityId != null) {
                cityId.getAddressCollection().add(address);
                cityId = em.merge(cityId);
            }
            for (Staff staffCollectionStaff : address.getStaffCollection()) {
                Address oldAddressIdOfStaffCollectionStaff = staffCollectionStaff.getAddressId();
                staffCollectionStaff.setAddressId(address);
                staffCollectionStaff = em.merge(staffCollectionStaff);
                if (oldAddressIdOfStaffCollectionStaff != null) {
                    oldAddressIdOfStaffCollectionStaff.getStaffCollection().remove(staffCollectionStaff);
                    oldAddressIdOfStaffCollectionStaff = em.merge(oldAddressIdOfStaffCollectionStaff);
                }
            }
            for (Store storeCollectionStore : address.getStoreCollection()) {
                Address oldAddressIdOfStoreCollectionStore = storeCollectionStore.getAddressId();
                storeCollectionStore.setAddressId(address);
                storeCollectionStore = em.merge(storeCollectionStore);
                if (oldAddressIdOfStoreCollectionStore != null) {
                    oldAddressIdOfStoreCollectionStore.getStoreCollection().remove(storeCollectionStore);
                    oldAddressIdOfStoreCollectionStore = em.merge(oldAddressIdOfStoreCollectionStore);
                }
            }
            for (Customer customerCollectionCustomer : address.getCustomerCollection()) {
                Address oldAddressIdOfCustomerCollectionCustomer = customerCollectionCustomer.getAddressId();
                customerCollectionCustomer.setAddressId(address);
                customerCollectionCustomer = em.merge(customerCollectionCustomer);
                if (oldAddressIdOfCustomerCollectionCustomer != null) {
                    oldAddressIdOfCustomerCollectionCustomer.getCustomerCollection().remove(customerCollectionCustomer);
                    oldAddressIdOfCustomerCollectionCustomer = em.merge(oldAddressIdOfCustomerCollectionCustomer);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Address address) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address persistentAddress = em.find(Address.class, address.getAddressId());
            City cityIdOld = persistentAddress.getCityId();
            City cityIdNew = address.getCityId();
            Collection<Staff> staffCollectionOld = persistentAddress.getStaffCollection();
            Collection<Staff> staffCollectionNew = address.getStaffCollection();
            Collection<Store> storeCollectionOld = persistentAddress.getStoreCollection();
            Collection<Store> storeCollectionNew = address.getStoreCollection();
            Collection<Customer> customerCollectionOld = persistentAddress.getCustomerCollection();
            Collection<Customer> customerCollectionNew = address.getCustomerCollection();
            List<String> illegalOrphanMessages = null;
            for (Staff staffCollectionOldStaff : staffCollectionOld) {
                if (!staffCollectionNew.contains(staffCollectionOldStaff)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Staff " + staffCollectionOldStaff + " since its addressId field is not nullable.");
                }
            }
            for (Store storeCollectionOldStore : storeCollectionOld) {
                if (!storeCollectionNew.contains(storeCollectionOldStore)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Store " + storeCollectionOldStore + " since its addressId field is not nullable.");
                }
            }
            for (Customer customerCollectionOldCustomer : customerCollectionOld) {
                if (!customerCollectionNew.contains(customerCollectionOldCustomer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Customer " + customerCollectionOldCustomer + " since its addressId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cityIdNew != null) {
                cityIdNew = em.getReference(cityIdNew.getClass(), cityIdNew.getCityId());
                address.setCityId(cityIdNew);
            }
            Collection<Staff> attachedStaffCollectionNew = new ArrayList<Staff>();
            for (Staff staffCollectionNewStaffToAttach : staffCollectionNew) {
                staffCollectionNewStaffToAttach = em.getReference(staffCollectionNewStaffToAttach.getClass(), staffCollectionNewStaffToAttach.getStaffId());
                attachedStaffCollectionNew.add(staffCollectionNewStaffToAttach);
            }
            staffCollectionNew = attachedStaffCollectionNew;
            address.setStaffCollection(staffCollectionNew);
            Collection<Store> attachedStoreCollectionNew = new ArrayList<Store>();
            for (Store storeCollectionNewStoreToAttach : storeCollectionNew) {
                storeCollectionNewStoreToAttach = em.getReference(storeCollectionNewStoreToAttach.getClass(), storeCollectionNewStoreToAttach.getStoreId());
                attachedStoreCollectionNew.add(storeCollectionNewStoreToAttach);
            }
            storeCollectionNew = attachedStoreCollectionNew;
            address.setStoreCollection(storeCollectionNew);
            Collection<Customer> attachedCustomerCollectionNew = new ArrayList<Customer>();
            for (Customer customerCollectionNewCustomerToAttach : customerCollectionNew) {
                customerCollectionNewCustomerToAttach = em.getReference(customerCollectionNewCustomerToAttach.getClass(), customerCollectionNewCustomerToAttach.getCustomerId());
                attachedCustomerCollectionNew.add(customerCollectionNewCustomerToAttach);
            }
            customerCollectionNew = attachedCustomerCollectionNew;
            address.setCustomerCollection(customerCollectionNew);
            address = em.merge(address);
            if (cityIdOld != null && !cityIdOld.equals(cityIdNew)) {
                cityIdOld.getAddressCollection().remove(address);
                cityIdOld = em.merge(cityIdOld);
            }
            if (cityIdNew != null && !cityIdNew.equals(cityIdOld)) {
                cityIdNew.getAddressCollection().add(address);
                cityIdNew = em.merge(cityIdNew);
            }
            for (Staff staffCollectionNewStaff : staffCollectionNew) {
                if (!staffCollectionOld.contains(staffCollectionNewStaff)) {
                    Address oldAddressIdOfStaffCollectionNewStaff = staffCollectionNewStaff.getAddressId();
                    staffCollectionNewStaff.setAddressId(address);
                    staffCollectionNewStaff = em.merge(staffCollectionNewStaff);
                    if (oldAddressIdOfStaffCollectionNewStaff != null && !oldAddressIdOfStaffCollectionNewStaff.equals(address)) {
                        oldAddressIdOfStaffCollectionNewStaff.getStaffCollection().remove(staffCollectionNewStaff);
                        oldAddressIdOfStaffCollectionNewStaff = em.merge(oldAddressIdOfStaffCollectionNewStaff);
                    }
                }
            }
            for (Store storeCollectionNewStore : storeCollectionNew) {
                if (!storeCollectionOld.contains(storeCollectionNewStore)) {
                    Address oldAddressIdOfStoreCollectionNewStore = storeCollectionNewStore.getAddressId();
                    storeCollectionNewStore.setAddressId(address);
                    storeCollectionNewStore = em.merge(storeCollectionNewStore);
                    if (oldAddressIdOfStoreCollectionNewStore != null && !oldAddressIdOfStoreCollectionNewStore.equals(address)) {
                        oldAddressIdOfStoreCollectionNewStore.getStoreCollection().remove(storeCollectionNewStore);
                        oldAddressIdOfStoreCollectionNewStore = em.merge(oldAddressIdOfStoreCollectionNewStore);
                    }
                }
            }
            for (Customer customerCollectionNewCustomer : customerCollectionNew) {
                if (!customerCollectionOld.contains(customerCollectionNewCustomer)) {
                    Address oldAddressIdOfCustomerCollectionNewCustomer = customerCollectionNewCustomer.getAddressId();
                    customerCollectionNewCustomer.setAddressId(address);
                    customerCollectionNewCustomer = em.merge(customerCollectionNewCustomer);
                    if (oldAddressIdOfCustomerCollectionNewCustomer != null && !oldAddressIdOfCustomerCollectionNewCustomer.equals(address)) {
                        oldAddressIdOfCustomerCollectionNewCustomer.getCustomerCollection().remove(customerCollectionNewCustomer);
                        oldAddressIdOfCustomerCollectionNewCustomer = em.merge(oldAddressIdOfCustomerCollectionNewCustomer);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = address.getAddressId();
                if (findAddress(id) == null) {
                    throw new NonexistentEntityException("The address with id " + id + " no longer exists.");
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
            Address address;
            try {
                address = em.getReference(Address.class, id);
                address.getAddressId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The address with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Staff> staffCollectionOrphanCheck = address.getStaffCollection();
            for (Staff staffCollectionOrphanCheckStaff : staffCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Staff " + staffCollectionOrphanCheckStaff + " in its staffCollection field has a non-nullable addressId field.");
            }
            Collection<Store> storeCollectionOrphanCheck = address.getStoreCollection();
            for (Store storeCollectionOrphanCheckStore : storeCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Store " + storeCollectionOrphanCheckStore + " in its storeCollection field has a non-nullable addressId field.");
            }
            Collection<Customer> customerCollectionOrphanCheck = address.getCustomerCollection();
            for (Customer customerCollectionOrphanCheckCustomer : customerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Customer " + customerCollectionOrphanCheckCustomer + " in its customerCollection field has a non-nullable addressId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            City cityId = address.getCityId();
            if (cityId != null) {
                cityId.getAddressCollection().remove(address);
                cityId = em.merge(cityId);
            }
            em.remove(address);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Address> findAddressEntities() {
        return findAddressEntities(true, -1, -1);
    }

    public List<Address> findAddressEntities(int maxResults, int firstResult) {
        return findAddressEntities(false, maxResults, firstResult);
    }

    private List<Address> findAddressEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Address.class));
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

    public Address findAddress(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Address.class, id);
        } finally {
            em.close();
        }
    }

    public int getAddressCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Address> rt = cq.from(Address.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
