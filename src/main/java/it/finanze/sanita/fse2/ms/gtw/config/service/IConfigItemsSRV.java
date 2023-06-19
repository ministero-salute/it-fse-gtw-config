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
package it.finanze.sanita.fse2.ms.gtw.config.service;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.NotFoundException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;

/**
 * Interface of configuration items service.
 */
public interface IConfigItemsSRV {
    
    /**
     * Consent to save a list of configuration items.
     * 
     * @param items Items to persist.
     */
    void saveConfigurationItems(List<ConfigItemETY> items);

    /**
     * Returns the list of all configuration items.
     * 
     * @param type Type of configuration items to retrieve.
     * @return All existing configuration items.
     */
    List<ConfigItemETY> getConfigurationItems(ConfigItemTypeEnum type);
    
    /**
     * Updates a configuration item.
     * 
     * @param type Type of item to update.
     * @param itemKey Key of configuration item to update.
     * @param newValue Value to set.
     */
    void updateItem(ConfigItemTypeEnum type, String itemKey, String newValue);

    /**
     * Deletes a configuration item identified by its {@code key}.
     * 
     * @param key Key of configuration item to delete.
     * @param itemKey Key of specific configuration to delete.
     * @throws NotFoundException If the item to delete does not exist on database.
     */
    void deleteItemByKey(String key, String itemKey);

     /**
     * Deletes a list of configuration items identified by their {@code keys}.
     * 
     * @param key Key of configuration items class to delete.
     * @return {@code true} if the configuration items are deleted correctly, {@code false} otherwise.
     */
    boolean deleteItemsByKeys(String key);

    /**
     * Returns the gateway name that identify the gateway where this microservice is running.
     * 
     * @return Gateway name.
     */
    String retrieveGatewayName();
    
    String getConfigurationItemsValue(ConfigItemTypeEnum type, String props);
}
