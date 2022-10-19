/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.config.repository;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemType;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;

/**
 * Interface of configuration items repository.
 * 
 * @author Simone Lungarella
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
    List<ConfigItemETY> getConfigurationItems(ConfigItemType type);

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
}
