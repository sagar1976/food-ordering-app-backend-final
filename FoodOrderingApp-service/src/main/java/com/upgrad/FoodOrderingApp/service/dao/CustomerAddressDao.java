package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CustomerAddressDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<CustomerAddressEntity> getAddressByCustomer(final CustomerEntity customer){
        try {
            return entityManager.createNamedQuery("getCustomerAddress", CustomerAddressEntity.class).setParameter("customer", customer ).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public CustomerAddressEntity getSingleAddress(final AddressEntity address){
        try {
            return entityManager.createNamedQuery("getAddress", CustomerAddressEntity.class).setParameter("address", address).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    //Added check this later for conflicts
    public TypedQuery<CustomerAddressEntity> getCustomerAddressByUuId(String id) {
        try {
            return entityManager.createNamedQuery("getCustomerAddressById", CustomerAddressEntity.class).setParameter("id", id);
        } catch (NoResultException nre) {
            return null;
        }
    }
}
