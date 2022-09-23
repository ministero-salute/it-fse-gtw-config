package it.finanze.sanita.fse2.ms.gtw.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.*;

import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import it.finanze.sanita.fse2.ms.gtw.config.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemType;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Test class for configuration items API.
 * 
 * @author Simone Lungarella
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
class ControllerTest extends AbstractTest {
    
    @Autowired
    IConfigItemsSRV configItemsSRV;

    @BeforeEach
    void setup() {
        for (ConfigItemType type : ConfigItemType.values()) {
            configItemsSRV.deleteItemsByKeys(type.getName());
        }
    }

    @DisplayName("Get configuration items test")
    @ParameterizedTest
    @ValueSource(ints = {10, 50})
    void getConfigurationItemsTest(final int numItems) {

        // Data preparation
        List<ConfigItemETY> items = new ArrayList<>();

        Map<String, String> configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            configItems.put(UUID.randomUUID().toString().substring(24), "Property " + i);
        }

        items.add(new ConfigItemETY(ConfigItemType.GENERIC.getName(), configItems));
        configItemsSRV.saveConfigurationItems(items);

        assumeTrue(mongoTemplate.findAll(ConfigItemETY.class).size() > 0, "Content must be present in database");
        // Starting test

        final ConfigItemDTO response = getConfigurationItems(ConfigItemType.GENERIC);
        assertEquals(1, response.getConfigurationItems().size(), "Only one configuration item bulk should have been returned");
        assertEquals(ConfigItemType.GENERIC.getName(), response.getConfigurationItems().get(0).getKey(), "Items returned should refer to the same configuration type");
        assertEquals(numItems, response.getConfigurationItems().get(0).getItems().size(), "All items should have been present in collection");
    }

    @DisplayName("Save configuration items test")
    @ParameterizedTest
    @ValueSource(ints = {10, 50})
    void saveConfigurationItemsTest(final int numItems) {

        // Data preparation
        Map<String, String> configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            configItems.put(UUID.randomUUID().toString().substring(24), "Property " + i);
        }

        // Starting test
        saveConfigurationItems(ConfigItemType.GENERIC, configItems);
        int insertedItems = mongoTemplate.findAll(ConfigItemETY.class).stream().mapToInt(item -> item.getItems().size()).sum();
        assertEquals(numItems, insertedItems, "Every item should have been saved");
    
        // Generating more configuration items
        configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            configItems.put(UUID.randomUUID().toString().substring(24), "Other Property " + i);
        }

        // Starting test
        saveConfigurationItems(ConfigItemType.GENERIC, configItems);
        insertedItems = mongoTemplate.findAll(ConfigItemETY.class).stream().mapToInt(item -> item.getItems().size()).sum();
        assertEquals(numItems*2, insertedItems, "Every item should have been saved also at second execution");
    }

    @DisplayName("Delete configuration items test")
    @ParameterizedTest
    @ValueSource(ints = {10, 50})
    void deleteConfigurationItemsTest(final int numItems) {

        // Data preparation
        String randomProperty = null;
        Map<String, String> configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            randomProperty = UUID.randomUUID().toString().substring(24);
            configItems.put(randomProperty, "Property " + i);
        }

        saveConfigurationItems(ConfigItemType.GENERIC, configItems);
        assumeTrue(!CollectionUtils.isEmpty(mongoTemplate.findAll(ConfigItemETY.class)), "Entity should exist to execute deletion");
        
        // Starting test
        deleteConfigurationItems(ConfigItemType.GENERIC, null);
        assertTrue(CollectionUtils.isEmpty(mongoTemplate.findAll(ConfigItemETY.class)), "All configuration items should have been deleted");
    
        // Preparing data again
        saveConfigurationItems(ConfigItemType.GENERIC, configItems);
        assumeTrue(!CollectionUtils.isEmpty(mongoTemplate.findAll(ConfigItemETY.class)), "Entity should exist to execute deletion");
        
        // Testing single deletion
        deleteConfigurationItems(ConfigItemType.GENERIC, randomProperty);
        final int insertedItems = mongoTemplate.findAll(ConfigItemETY.class).stream().mapToInt(item -> item.getItems().size()).sum();

        assertEquals(numItems, insertedItems + 1, "Only one property should have been deleted");
    }

    @DisplayName("Update configuration items test")
    @ParameterizedTest
    @ValueSource(ints = {10, 50})
    void updateConfigurationItemsTest(final int numItems) {

        // Data preparation
        String randomProperty = null;
        Map<String, String> configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            randomProperty = UUID.randomUUID().toString().substring(24);
            configItems.put(randomProperty, "Property " + i);
        }

        saveConfigurationItems(ConfigItemType.GENERIC, configItems);
        assumeTrue(!CollectionUtils.isEmpty(mongoTemplate.findAll(ConfigItemETY.class)), "Entity should exist to execute deletion");

        final String testValue = "TEST_VALUE";
        
        // Starting test
        updateConfigurationItems(ConfigItemType.GENERIC, randomProperty, testValue);

        Map<String, String> items = mongoTemplate.findAll(ConfigItemETY.class).get(0).getItems();
        assertEquals(testValue, items.get(randomProperty), "The value should have been updated correctly");
    }

    @Test
    void testErrors() {

        assumeTrue(CollectionUtils.isEmpty(mongoTemplate.findAll(ConfigItemETY.class)), "Db should have no configuration items");
        
        assertAll(
            () -> assertThrows(HttpClientErrorException.BadRequest.class, () -> saveConfigurationItems(ConfigItemType.GARBAGE, new HashMap<>())),
            () -> assertThrows(HttpClientErrorException.NotFound.class, () -> getConfigurationItems(ConfigItemType.GARBAGE)),
            () -> assertThrows(HttpClientErrorException.NotFound.class, () -> deleteConfigurationItems(ConfigItemType.GARBAGE, null)),
            () -> assertThrows(HttpClientErrorException.NotFound.class, () -> updateConfigurationItems(ConfigItemType.GARBAGE, UUID.randomUUID().toString(), UUID.randomUUID().toString()))
        );
    }

    @Test
    @DisplayName("Get configuration items error test")
    void getConfigurationItemsErrorTest() {
        Mockito.doThrow(new MongoException("Mongo error")).when(mongoTemplate).find(any(Query.class), eq(ConfigItemETY.class));
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> getConfigurationItems(ConfigItemType.GENERIC));
    }

    @DisplayName("Save configuration items error test")
    @ParameterizedTest
    @ValueSource(ints = {10})
    void saveConfigurationItemsErrorTest(final int numItems) {

        // Data preparation
        Map<String, String> configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            configItems.put(UUID.randomUUID().toString().substring(24), "Property " + i);
        }

        Mockito.doThrow(new MongoException("Mongo error")).when(mongoTemplate).insertAll(any(Collection.class));
        // Starting test
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> saveConfigurationItems(ConfigItemType.GENERIC, configItems));
    }

    @DisplayName("Delete configuration items error test")
    @ParameterizedTest
    @ValueSource(ints = {10})
    void deleteConfigurationItemsErrorTest(final int numItems) {

        // Data preparation
        String randomProperty = null;
        Map<String, String> configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            randomProperty = UUID.randomUUID().toString().substring(24);
            configItems.put(randomProperty, "Property " + i);
        }

        saveConfigurationItems(ConfigItemType.GENERIC, configItems);
        assumeTrue(!CollectionUtils.isEmpty(mongoTemplate.findAll(ConfigItemETY.class)), "Entity should exist to execute deletion");

        Mockito.doThrow(new MongoException("Mongo error")).when(mongoTemplate).remove(any(Query.class), eq(ConfigItemETY.class));
        // Starting test
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> deleteConfigurationItems(ConfigItemType.GENERIC, null));

        Mockito.doThrow(new MongoException("Mongo error")).when(mongoTemplate).updateMulti(any(Query.class), any(UpdateDefinition.class), eq(ConfigItemETY.class));
        // Starting test
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> deleteConfigurationItems(ConfigItemType.GENERIC, "genericKey"));

    }

    @DisplayName("Update configuration items error test")
    @ParameterizedTest
    @ValueSource(ints = {10})
    void updateConfigurationItemsErrorTest(final int numItems) {

        // Data preparation
        String randomProperty = null;
        Map<String, String> configItems = new HashMap<>();
        for (int i=0; i<numItems; i++) {
            randomProperty = UUID.randomUUID().toString().substring(24);
            configItems.put(randomProperty, "Property " + i);
        }

        saveConfigurationItems(ConfigItemType.GENERIC, configItems);
        assumeTrue(!CollectionUtils.isEmpty(mongoTemplate.findAll(ConfigItemETY.class)), "Entity should exist to execute deletion");

        final String testValue = "TEST_VALUE";

        // Starting test
        Mockito.doThrow(new MongoException("Mongo error")).when(mongoTemplate).updateMulti(any(Query.class), any(UpdateDefinition.class), eq(ConfigItemETY.class));
        String finalRandomProperty = randomProperty;
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> updateConfigurationItems(ConfigItemType.GENERIC, finalRandomProperty, testValue));
    }
}
