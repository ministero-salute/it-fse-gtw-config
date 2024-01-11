package it.finanze.sanita.fse2.ms.gtw.config.config;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
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

    @Value("${cfg.control-log-persistence-enabled}")
    private Boolean controlLogPersistenceEnabled;

    @Value("${kpi-log-persistence-enabled}")
    private Boolean kpiLogPersistenceEnabled;

    @Value("${issuer-cf-cleaning}")
    private Boolean issuerCfCleaning;

    @Value("${subject-cleaning}")
    private Boolean subjectCleaning;

    @Value("${audit-enabled}")
    private Boolean auditEnabled;

    @Value("${cfg.retention-days.fallback}")
    private Integer fallbackDataRetention;

    @Value("${validated-document-retention-day}")
    private Integer validatedDocumentRetentionDay;

    @Value("${expiring-date-day}")
    private Integer expiringDateDay;

    @Value("${delete-early-strategy}")
    private Integer deleteEarlyStrategy;

    @Value("${remove-eds-enabled}")
    private Integer removeEdsEnabled;

    @Autowired
    private IConfigItemsSRV service;

    @EventListener(value = ApplicationStartedEvent.class)
    public void initialize() {
        log.info("Docker setup for properties");
        List<ConfigItemETY> configs = new ArrayList<>();
        // Set configs
        addConfigItem(configs, GENERIC, CONTROL_LOG_PERSISTENCE_ENABLED, controlLogPersistenceEnabled.toString());
        addConfigItem(configs, GENERIC, KPI_LOG_PERSISTENCE_ENABLED, kpiLogPersistenceEnabled.toString());
        addConfigItem(configs, GENERIC, ISSUER_CF_CLEANING, issuerCfCleaning.toString());
        addConfigItem(configs, GENERIC, SUBJECT_CLEANING, subjectCleaning.toString());
        addConfigItem(configs, GENERIC, AUDIT_ENABLED, auditEnabled.toString());
        addConfigItem(configs, GARBAGE, CFG_ITEMS_RETENTION_DAY, fallbackDataRetention.toString());
        addConfigItem(configs, GARBAGE, VALIDATED_DOCUMENT_RETENTION_DAY, validatedDocumentRetentionDay.toString());
        addConfigItem(configs, GENERIC, EXPIRING_DATE_DAY, expiringDateDay.toString());
        addConfigItem(configs, GENERIC, DELETE_EARLY_STRATEGY, deleteEarlyStrategy.toString());
        addConfigItem(configs, GENERIC, REMOVE_EDS_ENABLED, removeEdsEnabled.toString());
        // Insert configs
        service.saveConfigurationItems(configs);
    }

    private void addConfigItem(List<ConfigItemETY> configs, ConfigItemTypeEnum type, String key, String value) {
        log.info("Using" + key + " on {} ", value);
        String item = service.getConfigurationItemsValue(type, key);
        if(item != null && !item.isEmpty())  {
            log.info("Property already set, skipping ...");
        } else {
            // Create object
            Map<String, String> map = new HashMap<>();
            map.put(key, value);
            ConfigItemETY config = new ConfigItemETY(type.name(), map);
            // Add in list
            configs.add(config);
        }
    }

}
