package com.Zabbix.domain;

public class Agent {
    private final String zabbixApiUrl;
    private final String zabbixUserName;
    private final String zabbixPassword;

    public Agent(String zabbixApiUrl, String zabbixUserName, String zabbixPassword) {
        this.zabbixApiUrl = zabbixApiUrl;
        this.zabbixUserName = zabbixUserName;
        this.zabbixPassword = zabbixPassword;
    }


    public String getZabbixApiUrl() {
        return zabbixApiUrl;
    }

    public String getZabbixUserName() {
        return zabbixUserName;
    }

    public String getZabbixPassword() {
        return zabbixPassword;
    }
}
