package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.dto.*;
import com.beebook.beebookproject.service.StripeService;
import com.stripe.model.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/public/stripe")
public class StripeController {

    //  https://www.youtube.com/watch?v=kakuRkFhW3M

    private final StripeService stripeService;


    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }


    @PostMapping("/card/token")
    @ResponseBody
    public StripeTokenDto createCardToken(@RequestBody StripeTokenDto model) {

        return stripeService.createCardToken(model);
    }

    @PostMapping("/charge")
    @ResponseBody
    public StripeChargeDto charge(@RequestBody StripeChargeDto model) {
        return stripeService.charge(model);
    }
    @PostMapping("/buy")
    @ResponseBody
    public ResponseEntity buy(@RequestParam(name = "bookId") Long bookId) {
        return stripeService.buy(bookId);
    }
    @PostMapping("/customer/subscription")
    @ResponseBody
    public StripeSubscriptionResponse subscription(@RequestBody StripeSubscriptionDto model) {

        return stripeService.createSubscription(model);
    }

    @DeleteMapping("/subscription/{id}")
    @ResponseBody
    public SubscriptionCancelRecord cancelSubscription(@PathVariable String id){

        Subscription subscription = stripeService.cancelSubscription(id);
        if(nonNull(subscription)){

            return new SubscriptionCancelRecord(subscription.getStatus());
        }

        return null;
    }

}

