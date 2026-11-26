package com.example.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/calc")
public class CalcResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String calc(@Encoded @QueryParam("expr") String expr) {
        return Calculator.json(decode(expr));
    }

    /** В адресе "+" означает пробел, а в ?expr=2+4*7 это сложение. Поэтому "+" сохраняем как есть. */
    private static String decode(String raw) {
        if (raw == null) {
            return null;
        }
        try {
            return URLDecoder.decode(raw.replace("+", "%2B"), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return raw;
        }
    }
}
