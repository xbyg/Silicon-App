package com.xbyg_plus.silicon.data.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xbyg_plus.silicon.model.WebPastPaperFolderInfo;
import com.xbyg_plus.silicon.model.WebPastPaperInfo;
import com.xbyg_plus.silicon.model.WebResourceInfo;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastPaperFactory implements EntryFactory<Map.Entry<String, List<WebResourceInfo>>> {
    private static final int TYPE_PAST_PAPER = 0;
    private static final int TYPE_PAST_PAPER_FOLDER = 1;

    @Override
    public String serialize(Map.Entry<String, List<WebResourceInfo>> entry, ObjectMapper mapper) throws IOException {
        ObjectNode out = mapper.createObjectNode();
        out.put("path", entry.getKey());

        ArrayNode folderContents = out.putArray("contents");
        for (WebResourceInfo info : entry.getValue()) {
            ObjectNode content = mapper.createObjectNode();
            if (info instanceof WebPastPaperInfo) {
                content.put("type", TYPE_PAST_PAPER)
                        .put("name", info.getName())
                        .put("address", info.getDownloadAddress())
                        .put("date", info.getDate())
                        .put("size", info.getSize());
            } else {
                WebPastPaperFolderInfo folderInfo = (WebPastPaperFolderInfo) info;

                content.put("type", TYPE_PAST_PAPER_FOLDER)
                        .put("name", info.getName())
                        .put("date", info.getDate());
                //.put("parentPath", folderInfo.parentAbsolutePath);

                Map<String, String> requestDataMap = folderInfo.getRequestDataMap();
                content.putObject("requestData")
                        .put("np", requestDataMap.get("namepath"))
                        .put("fp", requestDataMap.get("filepath"))
                        .put("id", requestDataMap.get("id"));
            }
            folderContents.add(content);
        }
        return mapper.writeValueAsString(out);
    }

    @Override
    public Map.Entry<String, List<WebResourceInfo>> deserialize(String jsonString, ObjectMapper mapper) throws IOException {
        ObjectNode in = mapper.readValue(jsonString, ObjectNode.class);
        String path = in.get("path").asText();

        List<WebResourceInfo> folderContents = new ArrayList<>();
        for (JsonNode content : in.get("contents")) {
            if (content.get("type").asInt() == TYPE_PAST_PAPER) {
                String name = content.get("name").asText();
                float size = content.get("size").asInt();
                String date = content.get("date").asText();
                String downloadAddress = content.get("address").asText();
                folderContents.add(new WebPastPaperInfo(name, size, date, downloadAddress));
            } else {
                String name = content.get("name").asText();
                String date = content.get("date").asText();

                HashMap<String, String> requestDataMap = new HashMap<>();
                JsonNode requestData = content.get("requestData");
                requestDataMap.put("namepath", requestData.get("np").asText());
                requestDataMap.put("filepath", requestData.get("fp").asText());
                requestDataMap.put("id", requestData.get("id").asText());

                folderContents.add(new WebPastPaperFolderInfo(name, date, path, requestDataMap));
            }
        }
        return new AbstractMap.SimpleEntry<>(path, folderContents);
    }
}
