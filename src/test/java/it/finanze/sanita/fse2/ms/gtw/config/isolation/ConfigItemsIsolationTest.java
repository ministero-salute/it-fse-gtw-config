package it.finanze.sanita.fse2.ms.gtw.config.isolation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.NotFoundException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.IConfigItemsRepo;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.impl.ConfigItemsSRV;

@Tag("ISOLATION") // Maven selects these tests only with -PISOLATION.
@ActiveProfiles("ISOLATION") // Spring profile; this does not select tests.
@SpringJUnitConfig(ConfigItemsSRV.class) // Only the real service, no application startup.
@TestPropertySource(properties = "gateway.full-qualified-name=isolation-gateway") // Set properties for the specific test
class ConfigItemsIsolationTest {

    @Autowired
    private ConfigItemsSRV service;

    @MockitoBean // Replaces the repository with a mock; no MongoDB connection.
    private IConfigItemsRepo repository;

    @Test
    void returnsConfigurationItems() {
        final List<ConfigItemETY> items = List.of(
                new ConfigItemETY("GENERIC", Map.of("audit-enabled", "true")));
        when(repository.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenReturn(items);

        final List<ConfigItemETY> result = service.getConfigurationItems(ConfigItemTypeEnum.GENERIC);

        assertEquals(items, result);
        verify(repository).getConfigurationItems(ConfigItemTypeEnum.GENERIC);
    }

    @Test
    void wrapsRepositoryFailure() {
        final RuntimeException failure = new RuntimeException("Database unavailable");
        when(repository.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenThrow(failure);

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> service.getConfigurationItems(ConfigItemTypeEnum.GENERIC));

        assertSame(failure, exception.getCause());
    }

    @Test
    void rejectsUpdateWhenItemDoesNotExist() {
        when(repository.update("GENERIC", "missing-key", "true")).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> service.updateItem(ConfigItemTypeEnum.GENERIC, "missing-key", "true"));

        verify(repository).update("GENERIC", "missing-key", "true");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void savesOrMergesDependingOnExistingItems(boolean exists) {
        final List<ConfigItemETY> items = List.of(new ConfigItemETY("GENERIC", Map.of("key", "value")));
        when(repository.getConfigurationItems(ConfigItemTypeEnum.GENERIC))
                .thenReturn(exists ? items : List.of());

        service.saveConfigurationItems(items);

        if (exists) {
            verify(repository).addToSet(items);
            verify(repository, never()).save(items);
        } else {
            verify(repository).save(items);
            verify(repository, never()).addToSet(items);
        }
    }

    @Test
    void wrapsSaveFailure() {
        final List<ConfigItemETY> items = List.of(new ConfigItemETY("GENERIC", Map.of("key", "value")));
        final RuntimeException failure = new RuntimeException("Save failed");
        doThrow(failure).when(repository).save(items);

        assertSame(failure, assertThrows(BusinessException.class,
                () -> service.saveConfigurationItems(items)).getCause());
    }

    @Test
    void updatesExistingItem() {
        when(repository.update("GENERIC", "key", "value")).thenReturn(true);

        service.updateItem(ConfigItemTypeEnum.GENERIC, "key", "value");

        verify(repository).update("GENERIC", "key", "value");
    }

    @Test
    void wrapsUpdateFailure() {
        final RuntimeException failure = new RuntimeException("Update failed");
        when(repository.update("GENERIC", "key", "value")).thenThrow(failure);

        assertSame(failure, assertThrows(BusinessException.class,
                () -> service.updateItem(ConfigItemTypeEnum.GENERIC, "key", "value")).getCause());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void deletesAllWhenItemKeyIsAbsent(String itemKey) {
        when(repository.deleteAll("GENERIC")).thenReturn(true);

        service.deleteItemByKey("GENERIC", itemKey);

        verify(repository).deleteAll("GENERIC");
        verify(repository, never()).deleteByKey("GENERIC", itemKey);
    }

    @Test
    void deletesSingleItem() {
        when(repository.deleteByKey("GENERIC", "key")).thenReturn(true);

        service.deleteItemByKey("GENERIC", "key");

        verify(repository).deleteByKey("GENERIC", "key");
        verify(repository, never()).deleteAll("GENERIC");
    }

    @Test
    void rejectsMissingDeletion() {
        assertThrows(NotFoundException.class, () -> service.deleteItemByKey("GENERIC", "missing"));
        assertThrows(NotFoundException.class, () -> service.deleteItemByKey("GENERIC", null));
    }

    @Test
    void wrapsDeleteFailure() {
        final RuntimeException failure = new RuntimeException("Delete failed");
        when(repository.deleteByKey("GENERIC", "key")).thenThrow(failure);

        assertSame(failure, assertThrows(BusinessException.class,
                () -> service.deleteItemByKey("GENERIC", "key")).getCause());
    }

    @Test
    void deletesItemsByClass() {
        when(repository.deleteAll("GENERIC")).thenReturn(true);
        assertTrue(service.deleteItemsByKeys("GENERIC"));
        verify(repository).deleteAll("GENERIC");
    }

    @Test
    void wrapsBulkDeleteFailure() {
        final RuntimeException failure = new RuntimeException("Delete failed");
        when(repository.deleteAll("GENERIC")).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> service.deleteItemsByKeys("GENERIC")).getCause());
    }

    @Test
    void returnsConfiguredGatewayName() {
        assertEquals("isolation-gateway", service.retrieveGatewayName());
    }

    @Test
    void returnsPropertyValue() {
        when(repository.getPropsValue(ConfigItemTypeEnum.GENERIC, "key")).thenReturn("value");
        assertEquals("value", service.getConfigurationItemsValue(ConfigItemTypeEnum.GENERIC, "key"));
    }

    @Test
    void wrapsPropertyLookupFailure() {
        final RuntimeException failure = new RuntimeException("Lookup failed");
        when(repository.getPropsValue(ConfigItemTypeEnum.GENERIC, "key")).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> service.getConfigurationItemsValue(ConfigItemTypeEnum.GENERIC, "key")).getCause());
    }
}
