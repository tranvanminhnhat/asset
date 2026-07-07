package com.vn.asset.scheduler;

import com.vn.asset.service.AssetDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetDataScheduler {
    private final AssetDataSyncService syncService;

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        log.info("Application started - initializing master data and running immediate data sync...");
        try {
            syncService.initializeMasterData();
            syncService.syncStockData();
            syncService.syncEtfData();
            syncService.syncGoldData();
            log.info("Immediate startup sync completed successfully");
        } catch (Exception e) {
            log.error("Error during immediate startup sync", e);
        }
    }

    @Scheduled(cron = "${asset.schedule.daily-sync-cron}", zone = "${asset.schedule.timezone}")
    public void syncDailyData() {
        log.info("Starting daily asset data sync...");
        try {
            syncService.syncStockData();
            syncService.syncEtfData();
            log.info("Daily sync completed successfully");
        } catch (Exception e) {
            log.error("Error in daily data sync", e);
        }
    }

    @Scheduled(cron = "${asset.schedule.intraday-sync-cron}", zone = "${asset.schedule.timezone}")
    public void syncIntraday() {
        log.info("Starting intraday asset data sync...");
        try {
            syncService.syncStockData();
            log.info("Intraday sync completed successfully");
        } catch (Exception e) {
            log.error("Error in intraday data sync", e);
        }
    }

    @Scheduled(cron = "${asset.schedule.gold-sync-cron}", zone = "${asset.schedule.timezone}")
    public void syncGoldPrices() {
        log.info("Starting gold price sync...");
        try {
            syncService.syncGoldData();
            log.info("Gold price sync completed successfully");
        } catch (Exception e) {
            log.error("Error in gold price sync", e);
        }
    }
}