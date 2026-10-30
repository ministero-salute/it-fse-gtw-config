/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.gtw.config.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.repository.impl.ConfigItemsRepo;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(ConfigItemsRepo.class)
class ConfigItemsRepositoryIsolationTest {

    @Autowired
    private ConfigItemsRepo repository;

    @MockitoBean
    private MongoTemplate mongo;

    private List<ConfigItemETY> items() {
        return List.of(new ConfigItemETY("GENERIC", Map.of("key", "value")));
    }

    @Test
    void insertsAllItems() {
        final List<ConfigItemETY> items = items();
        repository.save(items);
        verify(mongo).insertAll(items);
    }

    @Test
    void wrapsInsertFailure() {
        final RuntimeException failure = new RuntimeException("Insert failed");
        when(mongo.insertAll(anyCollection())).thenThrow(failure);
        final BusinessException exception = assertThrows(BusinessException.class,
                () -> repository.save(items()));
        // Both the abstract repository and concrete repository wrap the error.
        assertSame(failure, exception.getCause().getCause());
    }

    @Test
    void mergesPropertiesWithoutDiscardingExistingOnes() {
        when(mongo.findOne(any(Query.class), eq(ConfigItemETY.class))).thenReturn(
                new ConfigItemETY("GENERIC", new HashMap<>(Map.of("old", "preserved", "key", "old"))));

        repository.addToSet(items());

        final ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        final ArgumentCaptor<Update> update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateFirst(query.capture(), update.capture(), eq(ConfigItemETY.class));
        assertEquals(new Document("key", "GENERIC"), query.getValue().getQueryObject());
        assertEquals(Map.of("old", "preserved", "key", "value"),
                update.getValue().getUpdateObject().get("$set", Document.class).get("config_items"));
    }

    @Test
    void doesNotMergeWhenDocumentIsMissing() {
        repository.addToSet(items());
        verify(mongo, never()).updateFirst(any(Query.class), any(Update.class), eq(ConfigItemETY.class));
    }

    @Test
    void wrapsMergeFailure() {
        final RuntimeException failure = new RuntimeException("Read failed");
        when(mongo.findOne(any(Query.class), eq(ConfigItemETY.class))).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> repository.addToSet(items())).getCause());
    }

    @Test
    void findsItemsByType() {
        final List<ConfigItemETY> items = items();
        when(mongo.find(any(Query.class), eq(ConfigItemETY.class))).thenReturn(items);
        assertEquals(items, repository.getConfigurationItems(ConfigItemTypeEnum.GENERIC));
        final ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).find(query.capture(), eq(ConfigItemETY.class));
        assertEquals(ConfigItemTypeEnum.GENERIC, query.getValue().getQueryObject().get("key"));
    }

    @Test
    void wrapsFindFailure() {
        final RuntimeException failure = new RuntimeException("Read failed");
        when(mongo.find(any(Query.class), eq(ConfigItemETY.class))).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> repository.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).getCause());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void updatesPropertyAndReportsWhetherItChanged(long modified) {
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(ConfigItemETY.class)))
                .thenReturn(UpdateResult.acknowledged(1, modified, null));
        assertEquals(modified > 0, repository.update("GENERIC", "key", "value"));
        final ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        final ArgumentCaptor<Update> update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateMulti(query.capture(), update.capture(), eq(ConfigItemETY.class));
        assertEquals(new Document("key", "GENERIC"), query.getValue().getQueryObject());
        assertEquals(new Document("$set", new Document("config_items.key", "value")),
                update.getValue().getUpdateObject());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void unsetsPropertyAndReportsWhetherItChanged(long modified) {
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(ConfigItemETY.class)))
                .thenReturn(UpdateResult.acknowledged(1, modified, null));
        assertEquals(modified > 0, repository.deleteByKey("GENERIC", "key"));
        final ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        final ArgumentCaptor<Update> update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateMulti(query.capture(), update.capture(), eq(ConfigItemETY.class));
        assertEquals(new Document("key", "GENERIC"), query.getValue().getQueryObject());
        assertEquals(new Document("$unset", new Document("config_items.key", 1)),
                update.getValue().getUpdateObject());
    }

    @Test
    void wrapsUpdateAndUnsetFailures() {
        final RuntimeException failure = new RuntimeException("Update failed");
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(ConfigItemETY.class)))
                .thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> repository.update("GENERIC", "key", "value")).getCause());
        assertSame(failure, assertThrows(BusinessException.class,
                () -> repository.deleteByKey("GENERIC", "key")).getCause());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void deletesByTypeAndReportsWhetherAnythingWasDeleted(long deleted) {
        when(mongo.remove(any(Query.class), eq(ConfigItemETY.class)))
                .thenReturn(DeleteResult.acknowledged(deleted));
        assertEquals(deleted > 0, repository.deleteAll("GENERIC"));
        final ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).remove(query.capture(), eq(ConfigItemETY.class));
        assertEquals(new Document("key", "GENERIC"), query.getValue().getQueryObject());
    }

    @Test
    void wrapsDeleteFailure() {
        final RuntimeException failure = new RuntimeException("Delete failed");
        when(mongo.remove(any(Query.class), eq(ConfigItemETY.class))).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> repository.deleteAll("GENERIC")).getCause());
    }

    @Test
    void projectsOnlyRequestedProperty() {
        when(mongo.findOne(any(Query.class), eq(ConfigItemETY.class))).thenReturn(items().get(0));
        assertEquals("value", repository.getPropsValue(ConfigItemTypeEnum.GENERIC, "key"));
        final ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).findOne(query.capture(), eq(ConfigItemETY.class));
        assertEquals(new Document("config_items.key", 1), query.getValue().getFieldsObject());
        assertEquals(ConfigItemTypeEnum.GENERIC, query.getValue().getQueryObject().get("key"));
    }

    @Test
    void missingDocumentMapOrPropertyReturnsEmptyString() {
        when(mongo.findOne(any(Query.class), eq(ConfigItemETY.class)))
                .thenReturn(null, new ConfigItemETY("GENERIC", null),
                        new ConfigItemETY("GENERIC", Map.of()));
        for (int i = 0; i < 3; i++) {
            assertEquals("", repository.getPropsValue(ConfigItemTypeEnum.GENERIC, "key"));
        }
    }

    @Test
    void wrapsPropertyLookupFailure() {
        final RuntimeException failure = new RuntimeException("Read failed");
        when(mongo.findOne(any(Query.class), eq(ConfigItemETY.class))).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> repository.getPropsValue(ConfigItemTypeEnum.GENERIC, "key")).getCause());
    }
}
