package it.finanze.sanita.fse2.ms.gtw.config.controller.impl;

import java.util.Arrays;
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
import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemType;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import it.finanze.sanita.fse2.ms.gtw.config.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller implementation to handle configuration items requests.
 * 
 * @author Simone Lungarella
 */
@Slf4j
@RestController
public class ConfigItemsCTL extends AbstractCTL implements IConfigItemsCTL {

    @Autowired
    private IConfigItemsSRV configItemsSRV;

    @Override
    public ResponseEntity<ConfigItemDTO> getConfigurationItems(final ConfigItemType type, final HttpServletRequest request) {
        
        log.info("Searching for configuration items of type {}", type);
        
        final List<ConfigItemETY> configItems = configItemsSRV.getConfigurationItems(type);
        return new ResponseEntity<>(new ConfigItemDTO(getLogTraceInfo(), configItems, configItems.stream().mapToInt(item -> item.getItems().size()).sum()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> saveConfigurationItems(final ConfigItemType type, final Map<String, String> configItems, final HttpServletRequest request) {
        

        if (CollectionUtils.isEmpty(configItems)) {
            throw new ValidationException("Collection of configuration items cannot be empty.");
        } else {
            log.info("Saving {} configuration items of type {}", configItems.size(), type);
            final ConfigItemETY item = new ConfigItemETY(type.getName(), configItems);
            configItemsSRV.saveConfigurationItems(Arrays.asList(item));
        }

        return new ResponseEntity<>(new ResponseDTO(getLogTraceInfo()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteConfigurationItems(final ConfigItemType type, final String itemKey, final HttpServletRequest request) {
        
        if (StringUtility.isNullOrEmpty(itemKey)) {
            log.info("Deleting all configuration items of type {}", type);
        } else {
            log.info("Deleting configuration items of type {}, having key: {}", type, itemKey);
        }

        configItemsSRV.deleteItemByKey(type.getName(), itemKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> updateConfigurationItems(final ConfigItemType type, final String itemKey, final String value, final HttpServletRequest request) {
        
        log.info("Updating configuration items of type {}", type);

        configItemsSRV.updateItem(type, itemKey, value);
        return new ResponseEntity<>(new ResponseDTO(getLogTraceInfo()), HttpStatus.OK);
    }
    
}
