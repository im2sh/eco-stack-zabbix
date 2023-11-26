package com.Zabbix.domain;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Parser {
    public static JSONArray extractHostArray(String hostInfo) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(hostInfo);

        Object resultObj = jsonObject.get("result");
        if (resultObj instanceof JSONArray) {
            return (JSONArray) resultObj;
        } else {
            throw new RuntimeException("Failed to extract host array from hostInfo.");
        }
    }
}
