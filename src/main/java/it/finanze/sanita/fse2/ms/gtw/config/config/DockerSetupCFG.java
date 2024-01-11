package it.finanze.sanita.fse2.ms.gtw.config.config;

import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.gtw.config.config.Constants.Properties.*;
import static it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum.GARBAGE;
import static it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum.GENERIC;

@Slf4j
@Profile(value = Constants.Profile.DOCKER)
@Configuration
public class DockerSetupCFG {

    @Value("${cfg.retention-days.fallback}")
    private Integer fallbackDataRetention;

    @Value("${cfg.control-log-persistence-enabled}")
    private Boolean controlLogPersistenceEnabled;

    @Value("${cfg.expiring-date-day}")
    private Integer expiringDateDay;

    @Autowired
    private IConfigItemsSRV service;

    @EventListener(value = ApplicationStartedEvent.class)
    public void initialize() {
        log.info("Docker setup for properties");
        // Set properties
        setFallbackDataRetention();
        setControlLogPersistenceEnabled();
        setExpiringDateDay();
    }

    private void setFallbackDataRetention() {
        log.info("Using fallback value of {} days", fallbackDataRetention);
        String retention = service.getConfigurationItemsValue(GARBAGE, CFG_ITEMS_RETENTION_DAY);
        if(retention != null && !retention.isEmpty())  {
            log.info("Property already set, skipping ...");
        } else {
            // Create object
            Map<String, String> map = new HashMap<>();
            map.put(CFG_ITEMS_RETENTION_DAY, fallbackDataRetention.toString());
            // Insert
            service.saveConfigurationItems(
                    Collections.singletonList(
                            new ConfigItemETY(GARBAGE.name(), map)
                    )
            );
            log.info(CFG_ITEMS_RETENTION_DAY + " property has been set!");
        }
    }

    private void setControlLogPersistenceEnabled() {
        log.info("Using control-log-persistence-enabled on {} ", controlLogPersistenceEnabled);
        String logPersistence = service.getConfigurationItemsValue(GENERIC, CONTROL_LOG_PERSISTENCE_ENABLED);
        if(logPersistence != null && !logPersistence.isEmpty())  {
            log.info("Property already set, skipping ...");
        } else {
            // Create object
            Map<String, String> map = new HashMap<>();
            map.put(CONTROL_LOG_PERSISTENCE_ENABLED, controlLogPersistenceEnabled.toString());
            // Insert
            service.saveConfigurationItems(
                    Collections.singletonList(
                            new ConfigItemETY(GENERIC.name(), map)
                    )
            );
            log.info(CONTROL_LOG_PERSISTENCE_ENABLED + " property has been set!");
        }
    }

    private void setExpiringDateDay() {
        log.info("Using expiring-date value of {} days", expiringDateDay);
        String expiring = service.getConfigurationItemsValue(GENERIC, EXPIRING_DATE_DAY);
        if(expiring != null && !expiring.isEmpty())  {
            log.info("Property already set, skipping ...");
        } else {
            // Create object
            Map<String, String> map = new HashMap<>();
            map.put(EXPIRING_DATE_DAY, expiringDateDay.toString());
            // Insert
            service.saveConfigurationItems(
                    Collections.singletonList(
                            new ConfigItemETY(GENERIC.name(), map)
                    )
            );
            log.info(EXPIRING_DATE_DAY + " property has been set!");
        }
    }

}
