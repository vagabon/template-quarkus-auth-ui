package org.vagabond.template.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.vagabond.engine.crud.resource.BaseResource;

import io.smallrye.common.annotation.RunOnVirtualThread;

@Path("/")
@RunOnVirtualThread
public class RootResource implements BaseResource {

    public static final String END_METADATRA = "\" />";

    @ConfigProperty(name = "website.url")
    private String websiteUrl;

    @GET()
    public Response root() {
        return responseOk("OK");
    }
}