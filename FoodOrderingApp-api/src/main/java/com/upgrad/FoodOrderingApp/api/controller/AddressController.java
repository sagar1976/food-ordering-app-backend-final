package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/")
@CrossOrigin
public class AddressController {

    @Autowired
    AddressBusinessService addressBusinessService;

    @Autowired
    CustomerBusinessService customerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(final SaveAddressRequest saveAddressRequest, @RequestHeader("authorization") final String authorization)
        throws  SaveAddressException,AuthorizationFailedException, AddressNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setActive(1);

        final AddressEntity createdAddress = addressBusinessService.saveAddress(addressEntity, bearerToken[1], saveAddressRequest.getStateUuid());
        final CustomerAuthTokenEntity customerAuthTokenEntity = customerBusinessService.getCustomer(bearerToken[1]);

        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddressEntity(createdAddress);
        customerAddressEntity.setCustomerEntity(customerAuthTokenEntity.getCustomer());
        addressBusinessService.saveCustomerAddress(customerAddressEntity);

        final SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(createdAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

}