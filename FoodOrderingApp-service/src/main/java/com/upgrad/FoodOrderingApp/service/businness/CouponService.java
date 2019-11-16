package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CouponService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private CustomerDao customerDao;

    public CouponEntity getCouponDetails(final String couponName, final String authorization) throws AuthorizationFailedException, CouponNotFoundException {

        CustomerAuthTokenEntity customerAuthEntity = customerDao.checkAuthToken(authorization);
        if(customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        }

        if(customerAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime current = ZonedDateTime.now();
        if(customerAuthEntity != null && (customerAuthEntity.getExpiresAt().isBefore(current) || customerAuthEntity.getExpiresAt().isEqual(current))){
            throw new AuthorizationFailedException("ATHR-003" , "Your session is expired. Log in again to access this endpoint");
        }

        if(couponName == ""){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }

        CouponEntity couponEntity = couponDao.getCouponByName(couponName);
        if(couponEntity == null) {
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }
        return couponEntity;

    }
}
