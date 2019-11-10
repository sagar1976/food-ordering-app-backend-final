package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    EntityManager entityManager;

    public CustomerEntity createCustomer(CustomerEntity customerEntity){
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    public CustomerEntity checkUUID(final String uuid){
        try{
            return entityManager.createNamedQuery("getCustomerByUUID", CustomerEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public CustomerEntity getCustomerById(Integer id){
        try{
            return entityManager.createNamedQuery("getCustomerByID", CustomerEntity.class).setParameter("id", id)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public CustomerEntity checkContactNo (final String contactNo){
        try{
            return entityManager.createNamedQuery("getCustomerByContactNo", CustomerEntity.class).setParameter("contact_Number", contactNo).
                    getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public CustomerAuthTokenEntity createAuthToken (CustomerAuthTokenEntity customerAuthTokenEntity){
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }
}
