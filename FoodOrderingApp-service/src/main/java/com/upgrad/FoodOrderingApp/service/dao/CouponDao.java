package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByName(String couponname) {
        try {
            return entityManager.createNamedQuery("couponByName" , CouponEntity.class).setParameter("couponName" , couponname).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public CouponEntity getCouponById(CouponEntity couponId) {
        try {
            return entityManager.createNamedQuery("couponById" , CouponEntity.class).setParameter("coupon_id" , couponId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
