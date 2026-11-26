package com.example.service;

import com.example.kafka.Json;
import com.example.kafka.KafkaConfig;
import com.example.kafka.KafkaSender;
import com.example.kafka.MessageStore;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.kafka.clients.producer.RecordMetadata;

@Path("/kafka")
public class KafkaResource {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String info() {
        return "{\"node\":" + Json.str(KafkaConfig.nodeName())
                + ",\"role\":" + Json.str(KafkaConfig.role())
                + ",\"bootstrap\":" + Json.str(KafkaConfig.bootstrap())
                + ",\"topic\":" + Json.str(KafkaConfig.topic())
                + ",\"group\":" + Json.str(KafkaConfig.group())
                + ",\"status\":" + Json.str(MessageStore.status()) + "}";
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response send(@FormParam("text") String text, @FormParam("type") String type) {
        if (text == null || text.trim().length() == 0) {
            return Response.status(400).entity("{\"ok\":false,\"error\":\"пустое сообщение\"}").build();
        }
        String kind = "html".equalsIgnoreCase(type) ? "html" : "text";
        try {
            RecordMetadata m = KafkaSender.send(kind, text);
            return Response.ok("{\"ok\":true,\"topic\":" + Json.str(m.topic())
                    + ",\"partition\":" + m.partition() + ",\"offset\":" + m.offset() + "}").build();
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            return Response.status(500)
                    .entity("{\"ok\":false,\"error\":" + Json.str(cause.toString()) + "}").build();
        }
    }

    @GET
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String messages() {
        return MessageStore.json();
    }
}
