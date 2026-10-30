/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.gtw.config.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.api.trace.Tracer;
import it.finanze.sanita.fse2.ms.gtw.config.controller.handler.ControllerExceptionHandler;
import it.finanze.sanita.fse2.ms.gtw.config.controller.impl.ConfigItemsCTL;
import it.finanze.sanita.fse2.ms.gtw.config.controller.impl.GatewayCTL;
import it.finanze.sanita.fse2.ms.gtw.config.controller.impl.LivenessCheckCTL;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.NotFoundException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.gtw.config.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.gtw.config.repository.entity.ConfigItemETY;
import it.finanze.sanita.fse2.ms.gtw.config.service.IConfigItemsSRV;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({ConfigItemsCTL.class, GatewayCTL.class, LivenessCheckCTL.class,
        ControllerExceptionHandler.class})
class ControllersIsolationTest {

    private static final String TRACE_ID = "0123456789abcdef0123456789abcdef";
    private static final String SPAN_ID = "0123456789abcdef";

    @Autowired
    private ConfigItemsCTL controller;
    @Autowired
    private GatewayCTL gateway;
    @Autowired
    private LivenessCheckCTL liveness;
    @Autowired
    private ControllerExceptionHandler handler;

    @MockitoBean
    private IConfigItemsSRV service;
    @MockitoBean
    private LoggerHelper logger;
    @MockitoBean
    private Tracer tracer;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        final SpanBuilder builder = mock(SpanBuilder.class);
        when(tracer.spanBuilder(anyString())).thenReturn(builder);
        when(builder.startSpan()).thenReturn(Span.wrap(SpanContext.create(
                TRACE_ID, SPAN_ID, TraceFlags.getSampled(), TraceState.getDefault())));
        // Dispatches real Spring MVC requests in memory; no server or Boot auto-configuration.
        mvc = MockMvcBuilders.standaloneSetup(controller, gateway, liveness)
                .setControllerAdvice(handler).build();
    }

    @Test
    void readsItemsWithTotalSizeAndTraceInformation() throws Exception {
        when(service.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenReturn(List.of(
                new ConfigItemETY("GENERIC", Map.of("a", "1", "b", "2"))));

        mvc.perform(get("/v1/config-items").param("type", "GENERIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.configurationItems[0].key").value("GENERIC"))
                .andExpect(jsonPath("$.traceID").value(TRACE_ID))
                .andExpect(jsonPath("$.spanID").value(SPAN_ID));
        verify(service).getConfigurationItems(ConfigItemTypeEnum.GENERIC);
    }

    @Test
    void emptyReadHasZeroSize() throws Exception {
        when(service.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenReturn(List.of());
        mvc.perform(get("/v1/config-items").param("type", "GENERIC"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(0));
    }

    @Test
    void createsItemsAndPassesRequestDataToService() throws Exception {
        mvc.perform(post("/v1/config-items").param("type", "GENERIC")
                .contentType(MediaType.APPLICATION_JSON).content("{\"audit-enabled\":\"true\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.traceID").value(TRACE_ID));

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<List<ConfigItemETY>> items = ArgumentCaptor.forClass(List.class);
        verify(service).saveConfigurationItems(items.capture());
        assertEquals(1, items.getValue().size());
        assertEquals("GENERIC", items.getValue().get(0).getKey());
        assertEquals(Map.of("audit-enabled", "true"), items.getValue().get(0).getItems());
    }

    @Test
    void rejectsEmptyItemsBeforeCallingService() throws Exception {
        mvc.perform(post("/v1/config-items").param("type", "GENERIC")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Bad request"));
        verifyNoInteractions(service);
        assertThrows(ValidationException.class,
                () -> controller.saveConfigurationItems(ConfigItemTypeEnum.GENERIC, null,
                        new MockHttpServletRequest()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "key"})
    void deletesAllOrSingleItem(String key) throws Exception {
        mvc.perform(delete("/v1/config-items").param("type", "GENERIC").param("itemKey", key))
                .andExpect(status().isOk()).andExpect(content().string(""));
        verify(service).deleteItemByKey("GENERIC", key);
    }

    @Test
    void deletesAllWithoutOptionalKey() throws Exception {
        mvc.perform(delete("/v1/config-items").param("type", "GENERIC"))
                .andExpect(status().isOk());
        verify(service).deleteItemByKey("GENERIC", null);
    }

    @Test
    void updatesItem() throws Exception {
        mvc.perform(put("/v1/config-items").param("type", "GENERIC")
                .param("itemKey", "key").param("value", "new"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.spanID").value(SPAN_ID));
        verify(service).updateItem(ConfigItemTypeEnum.GENERIC, "key", "new");
    }

    @Test
    void readsSpecificProperty() throws Exception {
        when(service.getConfigurationItemsValue(ConfigItemTypeEnum.GENERIC, "key")).thenReturn("value");
        mvc.perform(get("/v1/config-items/props").param("type", "GENERIC").param("props", "key"))
                .andExpect(status().isOk()).andExpect(content().string("value"));
    }

    @Test
    void returnsGatewayNameAndHealth() throws Exception {
        when(service.retrieveGatewayName()).thenReturn("gateway-01");
        mvc.perform(get("/v1/whois")).andExpect(status().isOk())
                .andExpect(jsonPath("$.gatewayName").value("gateway-01"));
        mvc.perform(get("/status")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void translatesNotFoundToProblemResponse() throws Exception {
        doThrow(new NotFoundException("missing")).when(service).deleteItemByKey("GENERIC", "key");
        mvc.perform(delete("/v1/config-items").param("type", "GENERIC").param("itemKey", "key"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Configuration items not found"));
    }

    @Test
    void translatesBusinessFailureWithoutLeakingDetails() throws Exception {
        when(service.getConfigurationItems(ConfigItemTypeEnum.GENERIC))
                .thenThrow(new BusinessException("secret database details"));
        mvc.perform(get("/v1/config-items").param("type", "GENERIC"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void rejectsMissingOrInvalidType() throws Exception {
        mvc.perform(get("/v1/config-items")).andExpect(status().isBadRequest());
        mvc.perform(get("/v1/config-items").param("type", "INVALID"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void toleratesMissingSpanBuilder() throws Exception {
        when(tracer.spanBuilder(anyString())).thenReturn(null);
        mvc.perform(get("/v1/config-items").param("type", "GENERIC"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.traceID").doesNotExist())
                .andExpect(jsonPath("$.spanID").doesNotExist());
    }
}
