package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.OrderBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private OrderBusinessService orderBusinessService;

    @RequestMapping(method= RequestMethod.POST, path = "/order")
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization, SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, CouponNotFoundException, PaymentMethodNotFoundException, AddressNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        List<UUID> orderItemsUuid = new ArrayList<>();
        Integer billAmount = 0;
        for(ItemQuantity item : saveOrderRequest.getItemQuantities()){
            orderItemsUuid.add(item.getItemId());
            billAmount = billAmount+(item.getPrice()*item.getQuantity());
        }

        String [] bearerToken = authorization.split("Bearer ");
        OrdersEntity response =  orderBusinessService.saveOrder(bearerToken[1],saveOrderRequest.getPaymentId(),orderItemsUuid, billAmount, saveOrderRequest.getCouponId(), saveOrderRequest.getAddressId(), saveOrderRequest.getRestaurantId());
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
        saveOrderResponse.status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>((SaveOrderResponse) saveOrderResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CustomerOrderResponse>> getPastOrdersForCustomer(@RequestHeader("authorization") final String authorization)throws AuthorizationFailedException{

        String [] bearerToken = authorization.split("Bearer ");
        List<OrdersEntity> ordersEntity = orderBusinessService.getPastOrdersForCustomer(bearerToken[1]).getResultList();

        OrderList orderList1 = new OrderList();
        OrderListCoupon orderListCoupon = new OrderListCoupon();
        OrderListPayment orderListPayment = new OrderListPayment();
        OrderListCustomer orderListCustomer = new OrderListCustomer();
        OrderListAddress orderListAddress = new OrderListAddress();
        OrderListAddressState statesList = new OrderListAddressState();


        List<OrderItemEntity> orderItemEntity = new ArrayList<>();
        ItemQuantityResponse itemQuantityResponses = new ItemQuantityResponse();
        ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
        List<ItemQuantityResponse> itemQuantityResponses1 = new ArrayList<>();


        for (OrdersEntity ordersEntity1 : ordersEntity){

            orderList1.setId(UUID.fromString(ordersEntity1.getUuid()));
            orderList1.setBill(BigDecimal.valueOf(ordersEntity1.getBill()));
            orderListCoupon.setCouponName(ordersEntity1.getCouponId().getCouponName());
            orderListCoupon.setId(UUID.fromString(ordersEntity1.getCouponId().getUuid()));
            orderListCoupon.setPercent(ordersEntity1.getCouponId().getPercent());
            orderList1.setCoupon(orderListCoupon);

            orderList1.setDiscount(BigDecimal.valueOf(ordersEntity1.getDiscount()));
            orderList1.setDate(String.valueOf(ordersEntity1.getDate()));

            orderListPayment.setId(UUID.fromString(ordersEntity1.getPaymentId().getUuid()));
            orderListPayment.setPaymentName(ordersEntity1.getPaymentId().getPaymentName());
            orderList1.setPayment(orderListPayment);

            orderListCustomer.setId(UUID.fromString(ordersEntity1.getCustomerId().getUuid()));
            orderListCustomer.setFirstName(ordersEntity1.getCustomerId().getFirstname());
            orderListCustomer.setLastName(ordersEntity1.getCustomerId().getLastname());
            orderListCustomer.setEmailAddress(ordersEntity1.getCustomerId().getEmail());
            orderListCustomer.setContactNumber(ordersEntity1.getCustomerId().getContact_Number());
            orderList1.setCustomer(orderListCustomer);

            orderListAddress.setId(UUID.fromString(ordersEntity1.getAddressId().getUuid()));
            orderListAddress.setFlatBuildingName(ordersEntity1.getAddressId().getFlat_buil_number());
            orderListAddress.setLocality(ordersEntity1.getAddressId().getLocality());
            orderListAddress.setCity(ordersEntity1.getAddressId().getCity());
            orderListAddress.setPincode(ordersEntity1.getAddressId().getPincode());
            statesList.setId(UUID.fromString(ordersEntity1.getAddressId().getStateEntity().getUuid()));
            statesList.setStateName(ordersEntity1.getAddressId().getStateEntity().getState_name());
            orderListAddress.setState(statesList);
            orderList1.setAddress(orderListAddress);

            orderItemEntity = orderBusinessService.getItemByOrder(ordersEntity1);
            for (OrderItemEntity orderItemEntity1: orderItemEntity){
                itemQuantityResponseItem.setId(UUID.fromString(orderItemEntity1.getItemId().getUuid()));
                itemQuantityResponseItem.setItemName(orderItemEntity1.getItemId().getItemName());
                itemQuantityResponseItem.setItemPrice(orderItemEntity1.getItemId().getPrice());
                itemQuantityResponseItem.setType(ItemQuantityResponseItem.TypeEnum.fromValue(orderItemEntity1.getItemId().getType()));
                itemQuantityResponses.setItem(itemQuantityResponseItem);
                itemQuantityResponses.setQuantity(orderItemEntity1.getQuantity());
                itemQuantityResponses.setPrice(orderItemEntity1.getPrice());
            }

            itemQuantityResponses1.add(itemQuantityResponses);
            orderList1.setItemQuantities(itemQuantityResponses1);
        }

        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse().addOrdersItem(orderList1);
        List<CustomerOrderResponse> customerOrderResponses1 = new ArrayList<>();
        customerOrderResponses1.add(customerOrderResponse);

        return new ResponseEntity<List<CustomerOrderResponse>>(customerOrderResponses1,HttpStatus.OK);
    }

}

