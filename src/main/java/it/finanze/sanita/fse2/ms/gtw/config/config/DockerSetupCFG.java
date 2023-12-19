package it.finanze.sanita.fse2.ms.gtw.config.config;

import static it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum.GARBAGE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = Constants.Profile.DOCKER)
@Configuration
public class DockerSetupCFG {

    private static final String CFG_ITEMS_RETENTION_DAY = "CFG_ITEMS_RETENTION_DAY";

    @Value("${cfg.retention-days.fallback}")
    private Integer fallbackDataRetention;

    @Autowired
    private IConfigItemsSRV service;

    @EventListener(value = ApplicationStartedEvent.class)
    public void initialize() {
        log.info("Docker setup for data-retention days");
        log.info("Using fallback value of {} days", fallbackDataRetention);
        String item = service.getConfigurationItemsValue(GARBAGE, CFG_ITEMS_RETENTION_DAY);
        if(item != null && !item.isEmpty())  {
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
            log.info("Property has been set!");
        }
    }
}
