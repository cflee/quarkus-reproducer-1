package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class AuthControllerTest {
    @Test
    @TestSecurity(user = "test", roles = "testrole")
    @OidcSecurity(claims = {@Claim(key = "username", value = "test_user")})
    void testLogout_authenticated_deletesCookiesSuccessfully() {
        Response response =
                given()
                        .when()
                        .cookie("q_session", "mock_session")
                        .cookie("q_session_at", "mock_access_token")
                        .cookie("q_session_rt", "mock_refresh_token")
                        .post("/auth/logout")
                        .thenReturn();

        assertIsCookieClearedByHeaders(response.getHeaders(), "q_session");
        assertIsCookieClearedByHeaders(response.getHeaders(), "q_session_at");
        assertIsCookieClearedByHeaders(response.getHeaders(), "q_session_rt");
    }

    private void assertIsCookieClearedByHeaders(final Headers headers, final String cookieName) {
        List<String> setCookieHeaders = headers.getValues("set-cookie");
        Optional<String> cookieValue =
                setCookieHeaders.stream().filter(s -> s.startsWith(cookieName)).findFirst();
        assertTrue(
                cookieValue.isPresent(),
                String.format("Targeted cookie \"%s\" was not found in headers", cookieName));
        assertTrue(
                cookieValue.get().contains("Max-Age=0;"),
                String.format("Targeted cookie \"%s\" was not deleted using Max-Age", cookieName));
        assertTrue(
                cookieValue.get().contains(String.format("%s=;", cookieName)),
                String.format("Targeted cookie \"%s\" was not cleared", cookieName));
    }
}
