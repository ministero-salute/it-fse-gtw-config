/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.controller.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.gtw.config.controller.IConfigItemsCTL;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ErrorLogEnum;
import it.finanze.sanita.fse2.ms.gtw.config.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.gtw.config.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import it.finanze.sanita.fse2.ms.gtw.config.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@RestController
public class ConfigItemsCTL extends AbstractCTL implements IConfigItemsCTL {

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 8139924873290539837L;
	
	@Autowired
    private IConfigItemsSRV configItemsSRV;

    @Autowired
    private LoggerHelper logger;

    @Override
    public ResponseEntity<ConfigItemDTO> getConfigurationItems(final ConfigItemTypeEnum type, final HttpServletRequest request) {
        
        log.debug("Searching for configuration items of type {}", type);
        
        final List<ConfigItemETY> configItems = configItemsSRV.getConfigurationItems(type);
        return new ResponseEntity<>(new ConfigItemDTO(getLogTraceInfo(), configItems, configItems.stream().mapToInt(item -> item.getItems().size()).sum()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> saveConfigurationItems(final ConfigItemTypeEnum type, final Map<String, String> configItems, final HttpServletRequest request) {
        
        final Date startingDate = new Date();
        try {
            if (CollectionUtils.isEmpty(configItems)) {
                throw new ValidationException("Collection of configuration items cannot be empty.");
            } else {
                log.info("Saving {} configuration items of type {}", configItems.size(), type);
                final ConfigItemETY item = new ConfigItemETY(type.getName(), configItems);
                configItemsSRV.saveConfigurationItems(Arrays.asList(item));
                logger.info("Configuration Properties added successfully", OperationLogEnum.ADD_CONFIG_PROPERTIES, ResultLogEnum.OK, startingDate);
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error encountered while saving new configuration properties", OperationLogEnum.ADD_CONFIG_PROPERTIES, ResultLogEnum.KO, startingDate, ErrorLogEnum.KO_GENERIC);
            throw e;
        }

        return new ResponseEntity<>(new ResponseDTO(getLogTraceInfo()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteConfigurationItems(final ConfigItemTypeEnum type, final String itemKey, final HttpServletRequest request) {
        
        final Date startingDate = new Date();
        try {
            if (StringUtility.isNullOrEmpty(itemKey)) {
                log.debug("Deleting all configuration items of type {}", type);
            } else {
                log.debug("Deleting configuration items of type {}, having key: {}", type, itemKey);
            }
    
            configItemsSRV.deleteItemByKey(type.getName(), itemKey);
            logger.info("Configuration Properties removed successfully", OperationLogEnum.REMOVE_CONFIG_PROPERTIES, ResultLogEnum.OK, startingDate);
        } catch (Exception e) {
            logger.error("Error encountered while removing configuration properties", OperationLogEnum.REMOVE_CONFIG_PROPERTIES, ResultLogEnum.KO, startingDate, ErrorLogEnum.KO_GENERIC);
            throw e;
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> updateConfigurationItems(final ConfigItemTypeEnum type, final String itemKey, final String value, final HttpServletRequest request) {
        
        final Date startingDate = new Date();
        log.debug("Updating configuration items of type {}", type);

        try {
            configItemsSRV.updateItem(type, itemKey, value);
            logger.info("Configuration Properties updated successfully", OperationLogEnum.UPDATE_CONFIG_PROPERTIES, ResultLogEnum.OK, startingDate);
        } catch (Exception e) {
            logger.error("Error encountered while updating configuration properties", OperationLogEnum.UPDATE_CONFIG_PROPERTIES, ResultLogEnum.KO, startingDate, ErrorLogEnum.KO_GENERIC);
            throw e;
        }
        return new ResponseEntity<>(new ResponseDTO(getLogTraceInfo()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> getValueOfSpecificConfigurationItems(ConfigItemTypeEnum type, String props,HttpServletRequest request) {
    	log.debug("Searching for configuration items of type {}", type);

    	String configItems = configItemsSRV.getConfigurationItemsValue(type, props);
    	return new ResponseEntity<>(configItems, HttpStatus.OK);
    }
    
}
