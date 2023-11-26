package com.Zabbix.service;

import static com.Zabbix.domain.Parser.extractHostArray;
import static com.Zabbix.service.ItemService.getItemId;
import static com.Zabbix.service.ItemService.getItemValue;

import com.Zabbix.domain.Agent;
import com.Zabbix.domain.AuthToken;
import com.Zabbix.domain.Host;
import java.time.Instant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MemoryService {
    private final Agent agent;

    public MemoryService(Agent agent) {
        this.agent = agent;
    }

    public void getHostMemoryUsage() {
        try {
            AuthToken auth = new AuthToken(agent.getZabbixApiUrl(), agent.getZabbixUserName(), agent.getZabbixPassword());
            String authToken = auth.getAuthToken();

            Host host = new Host(agent.getZabbixApiUrl(), authToken);
            String hostInfo = host.getHostInfo();


            Instant now = Instant.now();
            long endTime = now.getEpochSecond();
            //long startTime = now.minus(24, ChronoUnit.HOURS).withHour(0).withMinute(0).withSecond(0).getEpochSecond();

            String memoryUsageInfo = getMemoryUsage(authToken, hostInfo);
            System.out.println("Memory Usage Information:\n" + memoryUsageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String getMemoryUsage(String auth, String hostInfo) throws Exception {
        // 호스트 정보에서 hostid와 호스트 이름 추출
        JSONArray hostArray = extractHostArray(hostInfo);

        String zabbixApiUrl = agent.getZabbixApiUrl();
        // 결과를 저장할 StringBuilder
        StringBuilder resultBuilder = new StringBuilder();

        // 각 호스트의 CPU 정보 가져오기
        for (Object hostObj : hostArray) {
            JSONObject host = (JSONObject) hostObj;
            String hostId = host.get("hostid").toString();
            String hostName = host.get("name").toString();

            String itemId = getItemId(zabbixApiUrl, auth, hostId, "vm.memory.size[pavailable]");
            String availableMemory = getItemValue(zabbixApiUrl, auth, itemId);
            resultBuilder.append("The memory size in bytes or in percentage from total(").append(hostId).append("(").append(hostName).append(") : ").append(availableMemory).append("\n");
        }

        return resultBuilder.toString();
    }
}
