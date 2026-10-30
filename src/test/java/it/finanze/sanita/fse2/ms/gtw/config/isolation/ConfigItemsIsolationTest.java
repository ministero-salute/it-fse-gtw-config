package it.finanze.sanita.fse2.ms.gtw.config.isolation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
}
