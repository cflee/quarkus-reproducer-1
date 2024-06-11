package org.acme;

import io.quarkus.oidc.OidcSession;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthController {
    private final OidcSession oidcSession;

    @Inject
    public AuthController(
            OidcSession oidcSession
    ) {
        this.oidcSession = oidcSession;
    }

    @POST
    @Path("/logout")
    public Response logout() {
        oidcSession.logout().await().indefinitely();
        return Response.ok("logout ok").build();
    }
}
