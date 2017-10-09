package com.xbyg_plus.silicon.data.factory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public interface EntryFactory<Entry> {
    String serialize(Entry entry, ObjectMapper mapper) throws IOException;

    Entry deserialize(String jsonString, ObjectMapper mapper) throws IOException;
}
