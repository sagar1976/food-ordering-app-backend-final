package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RestaurantDao {
    @PersistenceContext
    private EntityManager entityManager;

    public TypedQuery<RestaurantEntity> getAllRestaurants() {
        try {
            return entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class);
        } catch (NoResultException nre) {
            return null;
        }
    }



    public TypedQuery<RestaurantCategoryEntity> restaurantsByCategoryId(CategoryEntity categoryEntity) {
        try {
            return entityManager.createNamedQuery("getRestaurantsByCategory", RestaurantCategoryEntity.class).setParameter("categoryId", categoryEntity);
        } catch (NoResultException nre) {
            return null;
        }
    }


    public RestaurantEntity restaurantsByRestaurantId(String Uuid) {
        try {
            return entityManager.createNamedQuery("restaurantsByRestaurantId", RestaurantEntity.class).setParameter("uuid", Uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity updateRestaurantEntity(RestaurantEntity restaurantEntity) {
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }


    public List<RestaurantCategoryEntity> getCategoryByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("getCategoryByRestaurant", RestaurantCategoryEntity.class).setParameter("restaurantId", restaurantEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RestaurantEntity> getRestaurantByRestaurantName(String restaurantName) {
        try {
            return entityManager.createNamedQuery("getRestaurantByName", RestaurantEntity.class).setParameter("restaurantName", restaurantName).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RestaurantItemEntity> getItemsByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("getItemByRestaurant", RestaurantItemEntity.class).setParameter("restaurantId", restaurantEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<CategoryItemEntity> getItemsByCategory(CategoryEntity categoryEntity) {
        try {
            return entityManager.createNamedQuery("getItemByCategoryId", CategoryItemEntity.class).setParameter("categoryId", categoryEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }




}