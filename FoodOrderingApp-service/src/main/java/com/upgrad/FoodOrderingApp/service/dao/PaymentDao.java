package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    public PaymentEntity getPaymentId(UUID paymentId) {
        try {
            return entityManager.createNamedQuery("paymentById" , PaymentEntity.class).setParameter("uuid" , paymentId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public List<PaymentEntity> getPaymentListDetails(){
        try{
            Query q = entityManager.createQuery("select se from PaymentEntity se order by se.paymentName");
            return new ArrayList<PaymentEntity>(q.getResultList());

        } catch (NoResultException nre ){
            return  null;
        }
    }

}

