package it.finanze.sanita.fse2.ms.gtw.config.config;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import java.util.*;

import static it.finanze.sanita.fse2.ms.gtw.config.config.Constants.Properties.*;
import static it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum.GARBAGE;
import static it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum.GENERIC;

@Slf4j
@Profile(value = Constants.Profile.DOCKER)
@Configuration
public class DockerSetupCFG {

    @Value("${control-log-persistence-enabled}")
    private Boolean controlLogPersistenceEnabled;

    @Value("${kpi-log-persistence-enabled}")
    private Boolean kpiLogPersistenceEnabled;

    @Value("${issuer-cf-cleaning}")
    private Boolean issuerCfCleaning;

    @Value("${subject-cleaning}")
    private Boolean subjectCleaning;

    @Value("${audit-enabled}")
    private Boolean auditEnabled;

    @Value("${cfg-items-retention-day}")
    private Integer cfgRetentionDay;

    @Value("${validated-document-retention-day}")
    private Integer validatedDocumentRetentionDay;

    @Value("${expiring-date-day}")
    private Integer expiringDateDay;

    @Value("${delete-early-strategy}")
    private Boolean deleteEarlyStrategy;

    @Value("${remove-eds-enabled}")
    private Boolean removeEdsEnabled;

    @Autowired
    private IConfigItemsSRV service;

    @EventListener(value = ApplicationStartedEvent.class)
    public void initialize() {
        log.info("Docker setup for properties");
        // Set configs
        addConfigItem(GENERIC, CONTROL_LOG_PERSISTENCE_ENABLED, controlLogPersistenceEnabled.toString());
        addConfigItem(GENERIC, KPI_LOG_PERSISTENCE_ENABLED, kpiLogPersistenceEnabled.toString());
        addConfigItem(GENERIC, ISSUER_CF_CLEANING, issuerCfCleaning.toString());
        addConfigItem(GENERIC, SUBJECT_CLEANING, subjectCleaning.toString());
        addConfigItem(GENERIC, AUDIT_ENABLED, auditEnabled.toString());
        addConfigItem(GARBAGE, CFG_ITEMS_RETENTION_DAY, cfgRetentionDay.toString());
        addConfigItem(GARBAGE, VALIDATED_DOCUMENT_RETENTION_DAY, validatedDocumentRetentionDay.toString());
        addConfigItem(GENERIC, EXPIRING_DATE_DAY, expiringDateDay.toString());
        addConfigItem(GENERIC, DELETE_EARLY_STRATEGY, deleteEarlyStrategy.toString());
        addConfigItem(GENERIC, REMOVE_EDS_ENABLED, removeEdsEnabled.toString());
    }

    private void addConfigItem(ConfigItemTypeEnum type, String key, String value) {
        log.info("Using " + key + " on {} ", value);
        String item = service.getConfigurationItemsValue(type, key);
        if(item != null && !item.isEmpty())  {
            log.info("Property already set, skipping ...");
        } else {
            // Create object
            Map<String, String> map = new HashMap<>();
            map.put(key, value);
            ConfigItemETY config = new ConfigItemETY(type.name(), map);
            // Insert
            service.saveConfigurationItems(
                    Collections.singletonList(config)
            );
            log.info(key + " property has been set!");
        }
    }

}
