package com.xbyg_plus.silicon.data.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xbyg_plus.silicon.model.WebNoticeInfo;

import java.io.IOException;

public class NoticeInfoFactory implements EntryFactory<WebNoticeInfo> {
    @Override
    public String serialize(WebNoticeInfo noticeInfo, ObjectMapper mapper) throws IOException {
        ObjectNode out = mapper.createObjectNode();
        out.put("name", noticeInfo.getName());
        out.put("id", noticeInfo.getId());
        out.put("address", noticeInfo.getDownloadAddress());
        return mapper.writeValueAsString(out);
    }

    @Override
    public WebNoticeInfo deserialize(String jsonString, ObjectMapper mapper) throws IOException {
        ObjectNode in = mapper.readValue(jsonString, ObjectNode.class);
        String name = in.get("name").asText();
        String id = in.get("id").asText();
        String downloadAddress = in.get("address").textValue(); //asText() may return "null" string
        return new WebNoticeInfo(name, id, downloadAddress);
    }
}
