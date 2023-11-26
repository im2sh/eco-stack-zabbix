package com.Zabbix.controller;

import com.Zabbix.service.CpuService;
import com.Zabbix.service.DiskService;
import com.Zabbix.service.MemoryService;

public class ZabbixController {
    private final CpuService cpuService;
    private final MemoryService memoryService;
    private final DiskService diskService;

    public ZabbixController(CpuService cpuService, MemoryService memoryService, DiskService diskService) {
        this.cpuService = cpuService;
        this.memoryService = memoryService;
        this.diskService = diskService;
    }

    public void run(){
        //cpuService.getHostCpuUsage();
        memoryService.getHostMemoryUsage();
        //diskService.getHostDiskUsage();
    }
}
