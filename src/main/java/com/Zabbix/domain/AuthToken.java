package com.Zabbix.domain;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class AuthToken {
    private final String apiUrl;
    private final String userName;
    private final String password;

    public AuthToken(String apiUrl, String userName, String password) {
        this.apiUrl = apiUrl;
        this.userName = userName;
        this.password = password;
    }

    public String getAuthToken() throws Exception {
        HttpResponse<JsonNode> response = Unirest.post(apiUrl)
                .header("Content-Type", "application/json")
                .body(String.format(
                        "{\"jsonrpc\":\"2.0\",\"method\":\"user.login\",\"params\":{\"user\":\"%s\",\"password\":\"%s\"},\"id\":1}",
                        userName, password))
                .asJson();

        return response.getBody().getObject().getString("result");
    }

}
