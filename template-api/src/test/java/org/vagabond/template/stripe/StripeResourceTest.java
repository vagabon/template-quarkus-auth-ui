package org.vagabond.template.stripe;

import jakarta.ws.rs.core.MediaType;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vagabond.template.utils.BaseDataTest;
import org.vagabond.template.api.stripe.StripeConfiguration;
import org.vagabond.template.api.stripe.payload.StripePayloadRequest;

import static io.restassured.RestAssured.given;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

@QuarkusTest
class StripeResourceTest extends BaseDataTest {

    @InjectMock
    private StripeConfiguration stripeConfiguration;

    @Test
    @TestSecurity(user = "user")
    void doPaiement() throws StripeException {

        var paymentIntent = new PaymentIntent();
        paymentIntent.setClientSecret("clientSecret");
        Mockito.when(stripeConfiguration.create(Mockito.anyMap())).thenReturn(paymentIntent);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).when().post("/stripe/payment-intent").then().statusCode(200);

        Mockito.when(stripeConfiguration.retrieve(Mockito.any())).thenReturn(paymentIntent);

        given().when().get(
                "/stripe/payment-intent?payment_intent=stripe/payment-intent&payment_intent_client_secret=clientSecret&redirect_status=redirect_status")
                .then().statusCode(400);

        Mockito.when(stripeConfiguration.retrieve(Mockito.any())).thenReturn(paymentIntent);

        given().body(new StripePayloadRequest("", "clientSecret")).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).when()
                .post("/stripe/validate").then().statusCode(200);
    }
}
