package com.example.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/translate")
public class TranslateResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String translate(@QueryParam("txt") String txt) {
        return Dictionary.json(txt);
    }
}
