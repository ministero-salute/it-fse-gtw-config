package it.finanze.sanita.fse2.ms.gtw.config.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemType;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.IConfigItemsRepo;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import lombok.extern.slf4j.Slf4j;

/**
 * Data access object for configuration items.
 * 
 * @author Simone Lungarella
 */
@Slf4j
@Repository
public class ConfigItemsRepo extends AbstractMongoRepository<ConfigItemETY, String> implements IConfigItemsRepo {
    
    /**
	 * Template to access at MongoDB.
	 */
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void save(final List<ConfigItemETY> items) {
        try {
            insertAll(items);
        } catch (final Exception e) {
            log.error(String.format("Error while inserting %s configuration items", items), e);
            throw new BusinessException(String.format("Error while inserting %s configuration items", items), e);
        }
    }

    public void addToSet(final List<ConfigItemETY> items) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("key").is(items.get(0).getKey()));
            
            ConfigItemETY existingItem = mongoTemplate.findOne(query, ConfigItemETY.class);
            
            if (existingItem != null) {
                Map<String, String> itemsToUpdate = existingItem.getItems();
                itemsToUpdate.putAll(items.get(0).getItems());

                Update update = new Update();
                update.set("config_items", itemsToUpdate);
    
                mongoTemplate.updateFirst(query, update, ConfigItemETY.class);
            }
        } catch (final Exception e) {
            log.error(String.format("Error while adding to set %s configuration items", items), e);
            throw new BusinessException(String.format("Error while adding to set %s configuration items", items), e);
        }
    }

    @Override
    public List<ConfigItemETY> getConfigurationItems(final ConfigItemType type) {
        
        List<ConfigItemETY> configurationItems = new ArrayList<>();
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("key").in(type, ConfigItemType.GENERIC));
           
            configurationItems = mongoTemplate.find(query, ConfigItemETY.class);
        } catch (final Exception e) {
            log.error("Error while retrieving all configuration items", e);
            throw new BusinessException("Error while retrieving all configuration items", e);
        }
        return configurationItems;
    }

    @Override
    public boolean update(final String key, final String itemKey, final String newValue) {
        
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("key").is(key));
            
            Update update = new Update();
            update.set("config_items." + itemKey, newValue);

            final UpdateResult result = mongoTemplate.updateMulti(query, update, ConfigItemETY.class);
            return result.getModifiedCount() > 0;
        } catch (final Exception e) {
            log.error(String.format("Error while updating item with key %s", itemKey), e);
            throw new BusinessException(String.format("Error while updating item with key %s", itemKey), e);
        }
    }

    @Override
    public boolean deleteByKey(final String key, final String itemKey) {

        boolean isDeleted = false;
        try {
            final Query query = new Query();
            query.addCriteria(Criteria.where("key").is(key));
            
            Update update = new Update();
            update.unset("config_items." + itemKey);
            
            final UpdateResult result = mongoTemplate.updateMulti(query, update, ConfigItemETY.class);
            isDeleted = result.getModifiedCount() > 0;
        } catch (final Exception e) {
            log.error(String.format("Error while deleting item with key %s", key), e);
            throw new BusinessException(String.format("Error while deleting item with key %s", key), e);
        }
        return isDeleted;
    }

    @Override
    public boolean deleteAll(final String key) {
        boolean isDeleted = false;
        try {
            final Query query = new Query();
            query.addCriteria(Criteria.where("key").is(key));

            final DeleteResult result = mongoTemplate.remove(query, ConfigItemETY.class);
            isDeleted = result.getDeletedCount() > 0;
        } catch (final Exception e) {
            log.error("Error while deleting items", e);
            throw new BusinessException("Error while deleting items", e);
        }
        return isDeleted;
    }
    
}