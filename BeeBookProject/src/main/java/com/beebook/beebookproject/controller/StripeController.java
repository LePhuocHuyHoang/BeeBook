package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.Helpers;
import com.beebook.beebookproject.dto.*;
import com.beebook.beebookproject.exception.ResponseEntityErrorException;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.service.StripeService;
import com.stripe.model.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    private final StripeService stripeService;


    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
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
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> buy(@RequestParam(name = "bookId") Long bookId, Authentication authentication ) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);
//        return ResponseEntity.ok().body("OK");
        return stripeService.buy(bookId, username);
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

