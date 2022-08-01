package it.finanze.sanita.fse2.ms.gtw.config.service;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemType;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.ConfigItemsNotFoundException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;

/**
 * Interface of configuration items service.
 * 
 * @author Simone Lungarella
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
    List<ConfigItemETY> getConfigurationItems(ConfigItemType type);

    /**
     * Updates a configuration item.
     * 
     * @param type Type of item to update.
     * @param itemKey Key of configuration item to update.
     * @param newValue Value to set.
     */
    void updateItem(ConfigItemType type, String itemKey, String newValue);

    /**
     * Deletes a configuration item identified by its {@code key}.
     * 
     * @param key Key of configuration item to delete.
     * @param itemKey Key of specific configuration to delete.
     * @throws ConfigItemsNotFoundException If the item to delete does not exist on database.
     */
    void deleteItemByKey(String key, String itemKey);

     /**
     * Deletes a list of configuration items identified by their {@code keys}.
     * 
     * @param key Key of configuration items class to delete.
     * @return {@code true} if the configuration items are deleted correctly, {@code false} otherwise.
     */
    boolean deleteItemsByKeys(String key);
}
