package com.Zabbix.service;

import static com.Zabbix.domain.Parser.extractHostArray;
import static com.Zabbix.service.ItemService.getItemId;
import static com.Zabbix.service.ItemService.getItemValue;
import static com.Zabbix.service.ItemService.getItemValueInHourlyIntervals;

import com.Zabbix.domain.Agent;
import com.Zabbix.domain.AuthToken;
import com.Zabbix.domain.Host;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CpuService {
    private final Agent agent;

    public CpuService(Agent agent) {
        this.agent = agent;
    }

    public void getHostCpuUsage() {

        try {
            AuthToken auth = new AuthToken(agent.getZabbixApiUrl(), agent.getZabbixUserName(),
                    agent.getZabbixPassword());
            String authToken = auth.getAuthToken();

            Host host = new Host(agent.getZabbixApiUrl(), authToken);
            String hostInfo = host.getHostInfo();
            System.out.println("Host Information:\n" + hostInfo);

            String cpuUsage = get24CPUUsage(authToken, hostInfo);
            System.out.println(cpuUsage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String get24CPUUsage(String authToken, String hostInfo) throws Exception {
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(seoulZoneId);
        ZonedDateTime oneHourAgo = now.minusHours(24);

        long endTime = now.toEpochSecond();
        long startTime = oneHourAgo.toEpochSecond();

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
            String itemId = getItemId(zabbixApiUrl, authToken, hostId, "system.cpu.util");

            // 각 CPU 정보 가져오기
            String cpuUsage = getItemValueInHourlyIntervals(zabbixApiUrl, authToken, itemId,
                    seoulZoneId, startTime, endTime);

            // 결과를 StringBuilder에 추가
            resultBuilder.append(formatCPUInfo(hostId, hostName, cpuUsage)).append("\n");
        }

        return resultBuilder.toString();

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

    private String formatCPUInfo(String hostId, String hostName, String cpuInfo) {
        return String.format("CPU Usage Information(%s(%s))\n%s", hostId, hostName, cpuInfo);
    }
}
