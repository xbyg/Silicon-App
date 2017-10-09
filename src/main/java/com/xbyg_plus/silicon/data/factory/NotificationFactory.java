package com.xbyg_plus.silicon.data.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xbyg_plus.silicon.model.Notification;

import java.io.IOException;

public class NotificationFactory implements EntryFactory<Notification> {
    @Override
    public String serialize(Notification notification, ObjectMapper mapper) throws IOException {
        ObjectNode out = mapper.createObjectNode();
        out.put("title", notification.getTitle())
                .put("msg", notification.getMessage())
                .put("date", notification.getDate());
        return mapper.writeValueAsString(out);
    }

    @Override
    public Notification deserialize(String jsonString, ObjectMapper mapper) throws IOException {
        ObjectNode in = mapper.readValue(jsonString, ObjectNode.class);
        return new Notification(in.get("title").asText(), in.get("msg").asText(), in.get("date").asLong());
    }
}
