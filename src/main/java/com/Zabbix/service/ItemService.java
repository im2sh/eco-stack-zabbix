package com.Zabbix.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class ItemService {
    public static String getItemId(String apiUrl, String auth, String hostId, String key) throws Exception {
        HttpResponse<JsonNode> response = Unirest.post(apiUrl)
                .header("Content-Type", "application/json")
                .body(String.format(
                        "{\"jsonrpc\":\"2.0\",\"method\":\"item.get\",\"params\":{\"output\":\"extend\",\"hostids\":\"%s\",\"search\":{\"key_\":\"%s\"}},\"auth\":\"%s\",\"id\":1}",
                        hostId, key, auth))
                .asJson();

        org.json.JSONArray resultArray = response.getBody().getObject().getJSONArray("result");

        if (resultArray != null && resultArray.length() > 0) {
            org.json.JSONObject item = resultArray.getJSONObject(0);
            return item.getString("itemid");
        } else {
            throw new RuntimeException("Failed to get itemid for key: " + key);
        }
    }

    public static String getItemValue(String apiUrl, String auth, String itemId) throws Exception {
        HttpResponse<JsonNode> response = Unirest.post(apiUrl)
                .header("Content-Type", "application/json")
                .body(String.format(
                        "{\"jsonrpc\":\"2.0\",\"method\":\"history.get\",\"params\":{\"output\":\"extend\",\"history\":\"0\",\"itemids\":\"%s\",\"sortfield\":\"clock\",\"sortorder\":\"DESC\",\"limit\":1},\"auth\":\"%s\",\"id\":1}",
                        itemId, auth))
                .asJson();

        org.json.JSONArray resultArray = response.getBody().getObject().getJSONArray("result");

        if (resultArray != null && resultArray.length() > 0) {
            org.json.JSONObject historyEntry = resultArray.getJSONObject(0);
            return historyEntry.getString("value");
        } else {
            throw new RuntimeException("Failed to get value for itemid: " + itemId);
        }
    }

    public static String getItemValueInHourlyIntervals(String apiUrl, String auth, String itemId, ZoneId seoulZoneId, long startTime, long endTime) throws Exception {
        StringBuilder hourlyData = new StringBuilder();

        for (int i = 0; i < 24; i++) {
            long intervalStartTime = startTime + (i * 3600);
            long intervalEndTime = intervalStartTime + 3600;

            ZonedDateTime hour = ZonedDateTime.ofInstant(new Date(intervalStartTime * 1000).toInstant(), seoulZoneId);
            HttpResponse<JsonNode> response = Unirest.post(apiUrl)
                    .header("Content-Type", "application/json")
                    .body(String.format(
                            "{\"jsonrpc\":\"2.0\",\"method\":\"history.get\",\"params\":{\"output\":\"extend\",\"history\":\"0\",\"itemids\":\"%s\",\"sortfield\":\"clock\",\"sortorder\":\"DESC\",\"time_from\":\"%s\",\"time_till\":\"%s\",\"limit\":1},\"auth\":\"%s\",\"id\":1}",
                            itemId, intervalStartTime, intervalEndTime, auth))
                    .asJson();

            org.json.JSONArray resultArray = response.getBody().getObject().getJSONArray("result");

            if (resultArray != null && resultArray.length() > 0) {
                org.json.JSONObject historyEntry = resultArray.getJSONObject(0);
                String value = historyEntry.getString("value");
//                double parsedValue = Double.parseDouble(value);
//                hourlyData.append(hour).append(String.format(": %.4f", parsedValue)).append("\n");
                hourlyData.append(hour + ": "+ value).append("\n");
            } else {
                hourlyData.append(hour).append(" : N/A").append("\n ");
            }
        }

        return hourlyData.toString().trim(); // 공백 제거
    }
}
