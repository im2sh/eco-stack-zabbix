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

public class DiskService {
    private final Agent agent;

    public DiskService(Agent agent) {
        this.agent = agent;
    }

    public void getHostDiskUsage(){
        try {
            AuthToken auth = new AuthToken(agent.getZabbixApiUrl(), agent.getZabbixUserName(), agent.getZabbixPassword());
            String authToken = auth.getAuthToken();

            Host host = new Host(agent.getZabbixApiUrl(), authToken);
            String hostInfo = host.getHostInfo();


            Instant now = Instant.now();
            long endTime = now.getEpochSecond();
            //long startTime = now.minus(24, ChronoUnit.HOURS).withHour(0).withMinute(0).withSecond(0).getEpochSecond();

            String diskUsageInfo = getDiskUsage(authToken, hostInfo);
            System.out.println("Disk I/O Information:\n" + diskUsageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDiskUsage(String auth, String hostInfo) throws Exception {
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

            String diskReadId = getItemId(zabbixApiUrl, auth, hostId, "vfs.dev.read.rate[vda]");
            String diskReadStatistics = getItemValue(zabbixApiUrl, auth, diskReadId);

            resultBuilder.append("Disk(").append(hostId).append("(").append(hostName).append(") : ").append("\n");
            resultBuilder.append("The disk read statistics:").append(diskReadStatistics).append("\n");

            String diskWriteId = getItemId(zabbixApiUrl, auth, hostId, "vfs.dev.write.rate[vda]");
            String diskWriteStatistics = getItemValue(zabbixApiUrl, auth, diskWriteId);
            resultBuilder.append("The disk write statistics:").append(diskWriteStatistics).append("\n\n");
        }

        return resultBuilder.toString();
    }
}
