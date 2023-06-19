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
package it.finanze.sanita.fse2.ms.gtw.config.repository;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;

/**
 * Interface of configuration items repository.
 */
public interface IConfigItemsRepo {
    
    /**
     * Persists a list of configuration items.
     * 
     * @param items Items to persist.
     */
    void save(List<ConfigItemETY> items);

    /**
     * Returns a list of all configuration items.
     * 
     * @param type Type of configuration items.
     * @return All existing configuration items.
     */
    List<ConfigItemETY> getConfigurationItems(ConfigItemTypeEnum type);

    /**
     * Updates a configuration item.
     * 
     * @param key Class of item to update.
     * @param itemKey Key of item to update.
     * @param newValue Value to set as a new value.
     * @return {@code true} if the updated altered any configuration items, {@code false} otherwise.
     */
    boolean update(String key, String itemKey, String newValue);

    void addToSet(List<ConfigItemETY> items);

    /**
     * Deletes a configuration item identified by its {@code key}.
     * 
     * @param key Key of configuration item class to delete.
     * @param itemKey Key of specific configuration to delete.
     * @return {@code true} if the configuration item is deleted correctly, {@code false} otherwise.
     */
    boolean deleteByKey(String key, String itemKey);

     /**
     * Deletes a list of configuration items identified by their {@code key}.
     * 
     * @param key Keys of configuration items class to delete.
     * @return {@code true} if the configuration items are deleted correctly, {@code false} otherwise.
     */
    boolean deleteAll(String key);
    
    String getPropsValue(ConfigItemTypeEnum type, String props);
}
