package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.hibernate.criterion.Order;
import org.hibernate.internal.CriteriaImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderBusinessService {

    @Autowired
    private CouponDao couponDao;
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private ItemDao itemDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdersEntity saveOrder(String authToken, UUID paymentId, List<UUID> itemids, Integer bill,
                                  UUID couponId, String addressId, UUID restaurantId) throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException, RestaurantNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException {


        CustomerAuthTokenEntity customerAuthEntity = customerDao.getCustomerByAccessToken(authToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt() != null && customerAuthEntity.getExpiresAt().isBefore(zonedDateTime)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        //Below logic checks if coupon id entered by customer exists in DB or not
        CouponEntity couponEntity1 = couponDao.getCouponByUuid(couponId.toString());
        if (couponEntity1 == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }

        //Below logic checks if the address entered by customer exists in DB or not
        CustomerEntity customerEntity = customerDao.getCustomerByCustomerId(customerAuthEntity);
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressByUuId(customerEntity.getUuid()).getSingleResult();
        if (customerAddressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

        //Below logic checks if the customer is indeed entering his own address
        String customerAddressId = customerAddressEntity.getAddressEntity().getUuid().toString();

        //addressId is passed as a parameter in the 'saveOrder' function
        if (customerAddressId != addressId) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        //Below logic checks if the payment id matches any payment method that exists in DB
        //paymentId is being passed in the saveOrder function as a parameter
        PaymentEntity paymentEntity = paymentDao.getPaymentId(paymentId);
        if (paymentEntity == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }

        //Below logic checks if the restaurant exists based on the input provided by customer
        RestaurantEntity restaurantEntity = restaurantDao.restaurantsByRestaurantId(restaurantId.toString());
        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }


        //INF-003
        ItemEntity itemEntity = new ItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        for (UUID itemId : itemids) {
            itemEntity = itemDao.getItemById(itemids.toString());
            itemEntities.add(itemEntity);
        }
        //Need logic for comparing against all item ids in the DB for INF-003
        if (itemEntities.size() <= 0) {
            throw new ItemNotFoundException("INF-003", "No item by this Id exists");
        }

        OrdersEntity ordersEntity2 = new OrdersEntity();
        final String uuid = UUID.randomUUID().toString();
        ordersEntity2.setUuid(uuid);
        ordersEntity2.setAddressId(customerAddressEntity.getAddressEntity());
        ordersEntity2.setBill(new Double(bill));
        ordersEntity2.setCouponId(couponEntity1);
        ZonedDateTime value = ZonedDateTime.now();
        ordersEntity2.setDate(value);
        ordersEntity2.setCustomerId(customerEntity);
        ordersEntity2.setRestaurantId(restaurantEntity);


        OrdersEntity entity = orderDao.createOrder(ordersEntity2);//Creating order by calling create order
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TypedQuery<OrdersEntity> getPastOrdersForCustomer(String authToken) throws AuthorizationFailedException
    {
        CustomerAuthTokenEntity customerAuthEntity = customerDao.getCustomerByAccessToken(authToken);


        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        if(customerAuthEntity.getExpiresAt() != null && customerAuthEntity.getExpiresAt().isBefore(zonedDateTime)){
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        CustomerEntity custEntity = customerDao.getCustomerByCustomerId(customerAuthEntity.getCustomer().getId());
        return orderDao.getPastOrdersByCustomer(custEntity);
    }

    /*
     * The below method implements the business logic for fetching items based on Order
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderItemEntity> getItemByOrder(OrdersEntity orderEntity){
        List<OrderItemEntity> orderItemEntity = new ArrayList<>();
        orderItemEntity = orderDao.getItemByOrder(orderEntity).getResultList();
        return orderItemEntity;
    }

}