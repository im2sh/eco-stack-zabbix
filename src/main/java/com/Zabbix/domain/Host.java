package com.Zabbix.domain;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class Host {
    private final String apiUrl;
    private final String authToken;

    public Host(String apiUrl, String authToken) {
        this.apiUrl = apiUrl;
        this.authToken = authToken;
    }

    public String getHostInfo() throws Exception {
        HttpResponse<JsonNode> response = Unirest.post(apiUrl)
                .header("Content-Type", "application/json")
                .body(String.format(
                        "{\"jsonrpc\":\"2.0\",\"method\":\"host.get\",\"params\":{\"output\":\"extend\",\"selectGroups\":\"extend\",\"selectInterfaces\":\"extend\"},\"auth\":\"%s\",\"id\":1}",
                        authToken))
                .asJson();

        return response.getBody().toString();
    }
}
