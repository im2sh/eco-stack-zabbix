package com.Zabbix.zabbix;

import static com.Zabbix.domain.Zabbix.ZABBIX_API_URL;
import static com.Zabbix.domain.Zabbix.ZABBIX_PASSWORD;
import static com.Zabbix.domain.Zabbix.ZABBIX_USER_NAME;

import com.Zabbix.controller.ZabbixController;
import com.Zabbix.domain.Agent;
import com.Zabbix.service.CpuService;
import com.Zabbix.service.DiskService;
import com.Zabbix.service.MemoryService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZabbixApplication {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Agent agent = new Agent(ZABBIX_API_URL.getInfo(), ZABBIX_USER_NAME.getInfo(), ZABBIX_PASSWORD.getInfo());
        CpuService cpuService = new CpuService(agent);
        MemoryService memoryService = new MemoryService(agent);
        DiskService diskService = new DiskService(agent);
        ZabbixController zabbixController = new ZabbixController(cpuService, memoryService, diskService);

//		scheduler.scheduleAtFixedRate(() -> {
//			zabbixController.run();
//		}, 0, 1, TimeUnit.DAYS);
//
        zabbixController.run();
    }

}
