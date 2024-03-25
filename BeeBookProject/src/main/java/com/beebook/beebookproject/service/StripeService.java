package com.beebook.beebookproject.service;

import com.beebook.beebookproject.dto.StripeChargeDto;
import com.beebook.beebookproject.dto.StripeSubscriptionDto;
import com.beebook.beebookproject.dto.StripeSubscriptionResponse;
import com.beebook.beebookproject.dto.StripeTokenDto;
import com.beebook.beebookproject.entities.Book;
import com.beebook.beebookproject.entities.PointTransaction;
import com.beebook.beebookproject.entities.TransactionType;
import com.beebook.beebookproject.entities.User;
import com.beebook.beebookproject.repositories.BookRepository;
import com.beebook.beebookproject.repositories.PointTransactionRepository;
import com.beebook.beebookproject.repositories.UserRepository;
import com.beebook.beebookproject.sec.StripeConfig;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class StripeService {
    @Autowired
    PointTransactionRepository pointTransactionRepository;
    @Autowired
    PointTransactionService pointTransactionService;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;


    @PostConstruct
    public void init(){
        Stripe.apiKey = StripeConfig.getStripeApiKey();
    }

    public StripeTokenDto createCardToken(StripeTokenDto model) {
        Stripe.apiKey = StripeConfig.getStripePublishableKey();
        try {
            Map<String, Object> card = new HashMap<>();
            card.put("number", model.getCardNumber());
            card.put("exp_month", Integer.parseInt(model.getExpMonth()));
            card.put("exp_year", Integer.parseInt(model.getExpYear()));
            card.put("cvc", model.getCvc());
            Map<String, Object> params = new HashMap<>();
            params.put("card", card);
            Token token = Token.create(params);
            if (token != null && token.getId() != null) {
                model.setSuccess(true);
                model.setToken(token.getId());
            }
            return model;
        } catch (StripeException e) {
            log.error("StripeService (createCardToken)", e);
            throw new RuntimeException(e.getMessage());
        }

    }

    public StripeChargeDto charge(StripeChargeDto chargeRequest) {
        Stripe.apiKey = StripeConfig.getStripeApiKey();
        try {
            chargeRequest.setSuccess(false);
            String userName = chargeRequest.getUsername();
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
            chargeParams.put("currency", "USD");
            chargeParams.put("description", "Payment for id " + chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
            chargeParams.put("source", chargeRequest.getStripeToken());
            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", chargeRequest.getChargeId());
            metaData.putAll(chargeRequest.getAdditionalInfo());
            chargeParams.put("metadata", metaData);
            Charge charge = Charge.create(chargeParams);
            chargeRequest.setMessage(charge.getOutcome().getSellerMessage());
            if (charge.getPaid()) {
                List<Object[]> users = pointTransactionRepository.fineTransactionByUserName(userName);
                User user = new User();
                user.setId((long) users.getFirst()[0]);
                System.out.println("user Id" + user.getId());
                PointTransaction pointTransaction = new PointTransaction();
                pointTransaction.setIdTransaction(UUID.randomUUID().toString());
                pointTransaction.setTransactionType(new TransactionType(1L, ""));
                pointTransaction.setPointsAdded((long) (chargeRequest.getAmount()*100 ));
                pointTransaction.setTransactionDate(java.sql.Date.from(Instant.now()));
                pointTransaction.setUser(user);
                System.out.println("Id:" + users.getFirst()[0]);
                pointTransactionRepository.save(pointTransaction);
                chargeRequest.setChargeId(charge.getId());
                chargeRequest.setSuccess(true);
            }
            return chargeRequest;
        } catch (StripeException e) {
            log.error("StripeService (charge)", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    public ResponseEntity<?> buy(Long bookId, String username){
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return ResponseEntity.badRequest().body("Book not found");
        }
        User user = userRepository.findByUsername(username);
        System.out.println(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        if (user.getPoint() >= book.getPointPrice()) {
            String userName = user.getUsername();
            List<Object[]> users = pointTransactionRepository.fineTransactionByUserName(userName);
            user.setId((long) users.getFirst()[0]);
            PointTransaction pointTransaction = new PointTransaction();
            pointTransaction.setIdTransaction(UUID.randomUUID().toString());
            pointTransaction.setTransactionType(new TransactionType(2L, ""));
            pointTransaction.setPointsAdded((book.getPointPrice()));
            pointTransaction.setTransactionDate(Date.from(Instant.now()));
            pointTransaction.setUser(user);
            pointTransactionRepository.save(pointTransaction);
            return ResponseEntity.ok("User has enough points to purchase this book");
        } else {
            return ResponseEntity.badRequest().body("User does not have enough points to purchase this book");
        }
    }


    public StripeSubscriptionResponse createSubscription(StripeSubscriptionDto subscriptionDto){
        Stripe.apiKey = StripeConfig.getStripePublishableKey();
        PaymentMethod paymentMethod = createPaymentMethod(subscriptionDto);
        Stripe.apiKey = StripeConfig.getStripeApiKey();
        Customer customer = createCustomer(paymentMethod, subscriptionDto);
        Stripe.apiKey = StripeConfig.getStripeApiKey();
        paymentMethod = attachCustomerToPaymentMethod(customer, paymentMethod);
        Stripe.apiKey = StripeConfig.getStripeApiKey();
        Subscription subscription = createSubscription(subscriptionDto, paymentMethod, customer);
        Stripe.apiKey = StripeConfig.getStripeApiKey();
        return createResponse(subscriptionDto,paymentMethod,customer,subscription);
    }

    private StripeSubscriptionResponse createResponse(StripeSubscriptionDto subscriptionDto, PaymentMethod paymentMethod, Customer customer, Subscription subscription) {

        return StripeSubscriptionResponse.builder()
                .username(subscriptionDto.getUsername())
                .stripePaymentMethodId(paymentMethod.getId())
                .stripeSubscriptionId(subscription.getId())
                .stripeCustomerId(customer.getId())
                .build();
    }

    private PaymentMethod createPaymentMethod(StripeSubscriptionDto subscriptionDto){

        try {

            Map<String, Object> card = new HashMap<>();

            card.put("number", subscriptionDto.getCardNumber());
            card.put("exp_month", Integer.parseInt(subscriptionDto.getExpMonth()));
            card.put("exp_year", Integer.parseInt(subscriptionDto.getExpYear()));
            card.put("cvc", subscriptionDto.getCvc());

            Map<String, Object> params = new HashMap<>();
            params.put("type", "card");
            params.put("card", card);

            return PaymentMethod.create(params);

        } catch (StripeException e) {
            log.error("StripeService (createPaymentMethod)", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Customer createCustomer(PaymentMethod paymentMethod,StripeSubscriptionDto subscriptionDto){

        try {

            Map<String, Object> customerMap = new HashMap<>();
            customerMap.put("name", subscriptionDto.getUsername());
            customerMap.put("email", subscriptionDto.getEmail());
            customerMap.put("payment_method", paymentMethod.getId());

            return Customer.create(customerMap);
        } catch (StripeException e) {
            log.error("StripeService (createCustomer)", e);
            throw new RuntimeException(e.getMessage());
        }

    }

    private PaymentMethod attachCustomerToPaymentMethod(Customer customer,PaymentMethod paymentMethod){

        try {

            paymentMethod = com.stripe.model.PaymentMethod.retrieve(paymentMethod.getId());

            Map<String, Object> params = new HashMap<>();
            params.put("customer", customer.getId());
            paymentMethod = paymentMethod.attach(params);
            return paymentMethod;


        } catch (StripeException e) {
            log.error("StripeService (attachCustomerToPaymentMethod)", e);
            throw new RuntimeException(e.getMessage());
        }

    }

    private Subscription createSubscription(StripeSubscriptionDto subscriptionDto,PaymentMethod paymentMethod,Customer customer){

        try {

            List<Object> items = new ArrayList<>();
            Map<String, Object> item1 = new HashMap<>();
            item1.put(
                    "price",
                    subscriptionDto.getPriceId()
            );
            item1.put("quantity",subscriptionDto.getNumberOfLicense());
            items.add(item1);

            Map<String, Object> params = new HashMap<>();
            params.put("customer", customer.getId());
            params.put("default_payment_method", paymentMethod.getId());
            params.put("items", items);
            return Subscription.create(params);
        } catch (StripeException e) {
            log.error("StripeService (createSubscription)", e);
            throw new RuntimeException(e.getMessage());
        }

    }

    public  Subscription cancelSubscription(String subscriptionId){
        Stripe.apiKey = StripeConfig.getStripeApiKey();
        try {
            Subscription retrieve = Subscription.retrieve(subscriptionId);
            return retrieve.cancel();
        } catch (StripeException e) {

            log.error("StripeService (cancelSubscription)",e);
        }

        return null;
    }

}

