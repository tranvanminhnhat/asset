package com.vn.asset.startup;
import com.vn.asset.service.AssetDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
@Slf4j
//@Component  // Disabled - DataInsertTestRunner handles all initialization
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final AssetDataSyncService syncService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing master data...");
        try {
            syncService.initializeMasterData();
            log.info("Master data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error initializing master data", e);
        }
    }
}