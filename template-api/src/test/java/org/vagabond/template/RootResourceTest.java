package org.vagabond.template; 

import org.junit.jupiter.api.Test;
import org.vagabond.template.utils.BaseDataTest;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class RootResourceTest extends BaseDataTest {

    @Test
    void ping() {
        given().when().get("/").then().statusCode(200);
    }
}
