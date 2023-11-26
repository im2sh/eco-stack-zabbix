package com.Zabbix.domain;

public enum Zabbix {
    ZABBIX_API_URL("http://133.186.218.139/zabbix/api_jsonrpc.php"),
    ZABBIX_USER_NAME("Admin"),
    ZABBIX_PASSWORD("zabbix");

    private final String info;

    Zabbix(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
