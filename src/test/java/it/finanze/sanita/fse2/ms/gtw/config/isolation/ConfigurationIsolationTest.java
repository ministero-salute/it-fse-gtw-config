/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.gtw.config.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import it.finanze.sanita.fse2.ms.gtw.config.config.MockDbCFG;
import it.finanze.sanita.fse2.ms.gtw.config.config.OpenApiCFG;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(ConfigurationIsolationTest.IsolatedContext.class)
class ConfigurationIsolationTest {

    @org.springframework.context.annotation.Profile("ISOLATION")
    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
    static class IsolatedContext {
    }

    @MockitoBean
    private IConfigItemsSRV service;

    private MockDbCFG configuration;

    @BeforeEach
    void setup() {
        // Construct directly: do not activate MOCK or publish ApplicationStartedEvent.
        configuration = new MockDbCFG();
        ReflectionTestUtils.setField(configuration, "service", service);
        for (String field : List.of("controlLogPersistenceEnabled", "kpiLogPersistenceEnabled",
                "issuerCfCleaning", "subjectCleaning", "auditEnabled", "deleteEarlyStrategy",
                "removeEdsEnabled", "auditIniEnabled")) {
            ReflectionTestUtils.setField(configuration, field, true);
        }
        for (String field : List.of("cfgRetentionDay", "validatedDocumentRetentionDay", "expiringDateDay")) {
            ReflectionTestUtils.setField(configuration, field, 30);
        }
    }

    @Test
    void seedsAllMissingDefaultsWithCorrectTypesAndValues() {
        configuration.initialize();

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<List<ConfigItemETY>> batches = ArgumentCaptor.forClass(List.class);
        verify(service, times(12)).saveConfigurationItems(batches.capture());
        final Map<String, String> actual = batches.getAllValues().stream().flatMap(List::stream)
                .collect(Collectors.toMap(item -> item.getKey() + ":" + item.getItems().keySet().iterator().next(),
                        item -> item.getItems().values().iterator().next()));
        assertEquals(Map.ofEntries(
                Map.entry("GENERIC:control-log-persistence-enabled", "true"),
                Map.entry("GENERIC:kpi-log-persistence-enabled", "true"),
                Map.entry("GENERIC:issuer-cf-cleaning", "true"),
                Map.entry("GENERIC:subject-cleaning", "true"),
                Map.entry("GENERIC:audit-enabled", "true"),
                Map.entry("GENERIC:cfg-items-retention-day", "30"),
                Map.entry("GARBAGE:cfg-items-retention-day", "30"),
                Map.entry("GARBAGE:validated-document-retention-day", "30"),
                Map.entry("GENERIC:expiring-date-day", "30"),
                Map.entry("GENERIC:delete-early-strategy", "true"),
                Map.entry("GENERIC:remove-eds-enabled", "true"),
                Map.entry("GENERIC:audit-ini-enabled", "true")), actual);
    }

    @Test
    void seedsEmptyValuesButPreservesExistingValues() {
        when(service.getConfigurationItemsValue(any(), anyString())).thenReturn("", "existing");
        configuration.initialize();
        verify(service, times(1)).saveConfigurationItems(anyList());
        verify(service, times(12)).getConfigurationItemsValue(any(), anyString());
    }

    @Test
    void customizesOpenApiSchemasServersAndDefaultErrorResponses() {
        final OpenApiCFG config = new OpenApiCFG();
        final Schema<Object> schema = new Schema<>();
        final Operation operation = new Operation().responses(new ApiResponses()
                .addApiResponse("200", new ApiResponse().description("OK")));
        final Server http = new Server().url("http://localhost");
        final Server https = new Server().url("https://example.org");
        final OpenAPI api = new OpenAPI().components(new Components().addSchemas("Item", schema))
                .servers(List.of(http, https))
                .paths(new Paths().addPathItem("/items", new PathItem().get(operation)));

        config.openApiCustomiser().customise(api);
        config.customerGlobalHeaderOpenApiCustomiser().customise(api);

        assertEquals(false, schema.getAdditionalProperties());
        assertEquals(true, http.getExtensions().get("x-sandbox"));
        assertNull(https.getExtensions());
        assertEquals("OK", operation.getResponses().get("200").getDescription());
        assertEquals("#/components/schemas/ErrorResponseDTO", operation.getResponses().get("default")
                .getContent().get("application/problem+json").getSchema().get$ref());
    }
}
