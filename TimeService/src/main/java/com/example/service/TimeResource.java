package com.example.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Path("/time")
public class TimeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String currentTime = LocalTime.now().format(formatter);
        return "{\"time\": \"" + currentTime + "\"}";
    }
}
