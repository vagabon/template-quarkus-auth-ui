package org.vagabond.template.api.stripe;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.vagabond.common.user.UserEntity;
import org.vagabond.engine.crud.resource.BaseSecurityResource;
import org.vagabond.template.api.stripe.payload.StripePayloadRequest;
import org.vagabond.template.api.stripe.payment.UserPaymentService;

import io.quarkus.logging.Log;
import io.quarkus.security.Authenticated;

@Path("/stripe")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class StripeResource extends BaseSecurityResource<UserEntity> {

    private static final int AMOUNT = 999; // TODO : change this after 50 subscription

    @ConfigProperty(name = "website.url")
    private String websiteUrl;

    @ConfigProperty(name = "website.url.payment.ok")
    private String websiteUrlPaymentOk;

    @ConfigProperty(name = "website.url.payment.ko")
    private String websiteUrlPaymentKo;

    @Inject
    private StripeConfiguration stripeConfiguration;

    @Inject
    private UserPaymentService userPaymentService;

    @POST
    @Path("/payment-intent")
    public String createPaymentIntent() throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", AMOUNT);
        params.put("currency", "eur"); // Set currency to Euros
        // params.put("payment_method_types", Arrays.asList("card")); // Accept card payments
        PaymentIntent intent = stripeConfiguration.create(params);
        return intent.getClientSecret();
    }

    @GET
    @Path("/payment-intent")
    @PermitAll
    public Response validatePaypmentIntent(@QueryParam("payment_intent") String paymentIntent,
            @QueryParam("payment_intent_client_secret") String pamentIntentCLientSecret,
            @QueryParam("redirect_status") String redirectStatus) throws StripeException {
        PaymentIntent intent = stripeConfiguration.retrieve(paymentIntent);
        String htmlPageUrl = websiteUrl + websiteUrlPaymentKo;
        if (intent != null && pamentIntentCLientSecret.equals(intent.getClientSecret())) {
            htmlPageUrl = websiteUrl + websiteUrlPaymentOk + paymentIntent + "/" + pamentIntentCLientSecret;
        }
        return Response.status(Response.Status.FOUND).header("Location", htmlPageUrl).build();
    }

    @POST
    @Path("/validate")
    public Response validatePayment(@Context SecurityContext contexte, StripePayloadRequest stripePayload) throws StripeException {
        var intent = stripeConfiguration.retrieve(stripePayload.intent());
        if (intent != null && stripePayload.secret().equals(intent.getClientSecret())) {
            Log.infof("%s %s ", stripePayload.secret(), intent.getClientSecret());
        }
        UserEntity userConnected = hasRole(contexte, "USER");
        userPaymentService.createPayment(userConnected, stripePayload.intent(), stripePayload.secret(), intent);
        return responseOk(userConnected);
    }

    @Override
    public Object toDto(UserEntity entity) {
        return entity;
    }
}
