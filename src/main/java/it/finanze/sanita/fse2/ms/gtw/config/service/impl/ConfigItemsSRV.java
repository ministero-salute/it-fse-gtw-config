/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.config.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.NotFoundException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.IConfigItemsRepo;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import it.finanze.sanita.fse2.ms.gtw.config.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for configuration items.
 */
@Slf4j
@Service
public class ConfigItemsSRV extends AbstractService implements IConfigItemsSRV {
    
    @Value("${gateway.full-qualified-name}")
    private String gatewayName;

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 3840282801652564751L;
	
	@Autowired
    private IConfigItemsRepo configItemsRepo;

    @Override
    public void saveConfigurationItems(final List<ConfigItemETY> items) {
        try {
            final List<ConfigItemETY> existingItems = configItemsRepo.getConfigurationItems(ConfigItemTypeEnum.valueOf(items.get(0).getKey()));
            if (CollectionUtils.isEmpty(existingItems)) {
                configItemsRepo.save(items);
            } else {
                configItemsRepo.addToSet(items);
            }
        } catch (final Exception e) {
            log.error("Error while persisting configuration items", e);
            throw new BusinessException("Error while persisting configuration items", e);
        }
    }

    @Override
    public List<ConfigItemETY> getConfigurationItems(final ConfigItemTypeEnum type) {
        try {
            final List<ConfigItemETY> items = configItemsRepo.getConfigurationItems(type);
            if (CollectionUtils.isEmpty(items)) {
                throw new NotFoundException("No configuration items exists in database.");
            } else {
                return items;
            }
        } catch (final NotFoundException c) {
            throw c;
        } catch (final Exception e) {
            log.error("Error while retrieving configuration items", e);
            throw new BusinessException("Error while retrieving configuration items", e);
        }
    }

    @Override
    public void updateItem(final ConfigItemTypeEnum type, final String itemKey, final String newValue) {
        try {
            final boolean isUpdated = configItemsRepo.update(type.getName(), itemKey, newValue);
            if (!isUpdated) {
                throw new NotFoundException(String.format("Item of class %s and with key %s not found.", type, itemKey));
            }
        } catch (final NotFoundException c) {
            throw c;
        } catch (final Exception e) {
            log.error(String.format("Error while updating configuration item with key: %s", itemKey), e);
            throw new BusinessException(String.format("Error while updating configuration item with key: %s", itemKey), e);
        }
    }

    @Override
    public void deleteItemByKey(final String key, final String itemKey) {
        try {
            boolean isDeleted = false;

            if (StringUtility.isNullOrEmpty(itemKey)) {
                log.debug("Deleting all configuration items of class {}", key);
                isDeleted = configItemsRepo.deleteAll(key);
            } else {
                log.debug("Deleting item of class {}, having key {}", key, itemKey);
                isDeleted = configItemsRepo.deleteByKey(key, itemKey);
            }
            if (!isDeleted) {
                throw new NotFoundException(String.format("Item of class %s and with key %s not found.", key, itemKey));
            }
        } catch (final NotFoundException c) {
            throw c;
        } catch (final Exception e) {
            log.error(String.format("Error while deleting configuration item with key: %s", key), e);
            throw new BusinessException(String.format("Error while deleting configuration item with key: %s", key), e);
        }
    }

    @Override
    public boolean deleteItemsByKeys(final String key) {
        try {
            return configItemsRepo.deleteAll(key);
        } catch (final Exception e) {
            log.error("Error while deleting configuration items", e);
            throw new BusinessException("Error while deleting configuration items", e);
        }
    }

    @Override
    public String retrieveGatewayName() {
        return gatewayName;
    }

	@Override
	public String getConfigurationItemsValue(final ConfigItemTypeEnum type, final String props) {
		String out = "";
		try {
			out = configItemsRepo.getPropsValue(type, props);
		} catch(Exception ex) {
			log.error("Error while get configuration items value : " , ex);
			throw new BusinessException("Error while get configuration items value : " , ex);
		}
		return out;
	}
    
}
