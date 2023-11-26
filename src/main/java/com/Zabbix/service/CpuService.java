package com.Zabbix.service;

import static com.Zabbix.domain.Parser.extractHostArray;
import static com.Zabbix.service.ItemService.getItemId;
import static com.Zabbix.service.ItemService.getItemValue;

import com.Zabbix.domain.Agent;
import com.Zabbix.domain.AuthToken;
import com.Zabbix.domain.Host;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CpuService {
    private final Agent agent;

    public CpuService(Agent agent) {
        this.agent = agent;
    }

    public void getHostCpuUsage() {

        try {
            AuthToken auth = new AuthToken(agent.getZabbixApiUrl(), agent.getZabbixUserName(), agent.getZabbixPassword());
            String authToken = auth.getAuthToken();

            Host host = new Host(agent.getZabbixApiUrl(), authToken);
            String hostInfo = host.getHostInfo();
            System.out.println("Host Information:\n" + hostInfo);

            Instant now = Instant.now();
            long endTime = now.getEpochSecond();
            //long startTime = now.minus(24, ChronoUnit.HOURS).withHour(0).withMinute(0).withSecond(0).getEpochSecond();

            String cpuUsageInfo = getCPUUsage(authToken, hostInfo);
            System.out.println("CPU Usage Information:\n" + cpuUsageInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCPUUsage(String auth, String hostInfo) throws Exception {
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

            // CPU 정보 가져오기
            String idleItemId = getItemId(zabbixApiUrl, auth, hostId, "system.cpu.util[,idle]");
            String userItemId = getItemId(zabbixApiUrl, auth, hostId, "system.cpu.util[,user]");
            String systemItemId = getItemId(zabbixApiUrl, auth, hostId, "system.cpu.util[,system]");
            String niceItemId = getItemId(zabbixApiUrl, auth, hostId, "system.cpu.util[,nice]");

            // 각 CPU 정보 가져오기
            String idleUsage = getItemValue(zabbixApiUrl, auth, idleItemId);
            String userUsage = getItemValue(zabbixApiUrl, auth, userItemId);
            String systemUsage = getItemValue(zabbixApiUrl, auth, systemItemId);
            String niceUsage = getItemValue(zabbixApiUrl, auth, niceItemId);

            // 결과를 StringBuilder에 추가
            resultBuilder.append("Information for Host ").append(hostId).append("(").append(hostName).append("):\n");
            resultBuilder.append("CPU idle Usage: ").append(idleUsage).append("\n");
            resultBuilder.append("CPU User Usage: ").append(userUsage).append("\n");
            resultBuilder.append("CPU System Usage: ").append(systemUsage).append("\n");
            resultBuilder.append("CPU Nice Usage: ").append(niceUsage).append("\n\n");
        }

        return resultBuilder.toString();
    }
}
