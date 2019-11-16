package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(method = RequestMethod.GET, path = "/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<PaymentListResponse>> getPaymentList(){
        List<PaymentEntity> paymentEntityList = paymentService.getPaymentMethodsDetails();

        List<PaymentListResponse> paymentList = new ArrayList<PaymentListResponse>();
        for(PaymentEntity paymentEntity : paymentEntityList) {
            UUID paymentUUID = UUID.fromString(paymentEntity.getUuid());
            PaymentResponse paymentResponse = new PaymentResponse().id(paymentUUID).paymentName(paymentEntity.getPaymentName());
            PaymentListResponse paymentListResponse = new PaymentListResponse().addPaymentMethodsItem(paymentResponse);
            paymentList.add(paymentListResponse);
        }
        return new ResponseEntity<List<PaymentListResponse>>(paymentList, HttpStatus.OK);
    }
}




