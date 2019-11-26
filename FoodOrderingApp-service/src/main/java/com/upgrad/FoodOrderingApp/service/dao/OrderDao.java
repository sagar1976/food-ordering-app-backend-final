package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<OrdersEntity> getOrderByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("getOrderByRestaurant", OrdersEntity.class).setParameter("restaurantId", restaurantEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public TypedQuery<OrdersEntity> getPastOrdersByCustomer(CustomerEntity customerEntity){
        try{
            return entityManager.createNamedQuery("getAllOrdersByCustomer", OrdersEntity.class).setParameter("customerId", customerEntity);
        }catch (NoResultException nre){
            return null;
        }
    }

    public OrdersEntity createOrder(OrdersEntity ordersEntity) {
        entityManager.persist(ordersEntity);
        return ordersEntity;
    }

    public TypedQuery<OrderItemEntity> getItemByOrder(OrdersEntity orderEntity){
        try{
            return entityManager.createNamedQuery("getItemByOrder", OrderItemEntity.class).setParameter("orderEntity", orderEntity);
        }catch (NoResultException nre){
            return null;
        }
    }
}
