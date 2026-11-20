/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.gtw.config.isolation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.finanze.sanita.fse2.ms.gtw.config.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.NotFoundException;
import it.finanze.sanita.fse2.ms.gtw.config.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.gtw.config.repository.CollectionNaming;
import it.finanze.sanita.fse2.ms.gtw.config.utility.ProfileUtility;
import it.finanze.sanita.fse2.ms.gtw.config.utility.StringUtility;

@Tag("ISOLATION")
class UtilitiesIsolationTest {

    @Test
    void identifiesNullAndEmptyStringsWithoutTrimming() {
        assertTrue(StringUtility.isNullOrEmpty(null));
        assertTrue(StringUtility.isNullOrEmpty(""));
        assertFalse(StringUtility.isNullOrEmpty(" "));
        assertFalse(StringUtility.isNullOrEmpty("value"));
    }

    @Test
    void serializesDatesAndOmitsNullProperties() throws Exception {
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put("date", LocalDate.of(2026, 1, 2));
        values.put("text", "quoted \"value\"");
        values.put("absent", null);

        final JsonNode json = new ObjectMapper().readTree(StringUtility.toJSONJackson(values));

        assertEquals("2026-01-02", json.get("date").asText());
        assertEquals("quoted \"value\"", json.get("text").asText());
        assertFalse(json.has("absent"));
        assertEquals("null", StringUtility.toJSONJackson(null));
    }

    public static class BrokenBean {
        public String getValue() {
            throw new IllegalStateException("Cannot serialize");
        }
    }

    @Test
    void wrapsSerializationErrors() {
        final BusinessException exception = assertThrows(BusinessException.class,
                () -> StringUtility.toJSONJackson(new BrokenBean()));
        assertNotNull(exception.getCause());
    }

    @Test
    void detectsTestProfileAndChoosesCollectionName() {
        final ProfileUtility profiles = new ProfileUtility();
        assertFalse(profiles.isTestProfile());
        final MockEnvironment environment = new MockEnvironment();
        ReflectionTestUtils.setField(profiles, "environment", environment);
        final CollectionNaming naming = new CollectionNaming();
        ReflectionTestUtils.setField(naming, "profileUtility", profiles);

        assertFalse(profiles.isTestProfile());
        assertEquals("config_data", naming.getConfigDataCollection());
        environment.setActiveProfiles("TEST");
        assertTrue(profiles.isTestProfile());
        assertEquals("test_config_data", naming.getConfigDataCollection());
        environment.setActiveProfiles("ISOLATION");
        assertFalse(profiles.isTestProfile());
        assertEquals("config_data", naming.getConfigDataCollection());
    }

    @Test
    void responseConstructorsCopyTraceAndErrorDetails() {
        final LogTraceInfoDTO trace = new LogTraceInfoDTO("span", "trace");
        final ResponseDTO response = new ResponseDTO(trace, 400, "Bad request");
        assertEquals("span", response.getSpanID());
        assertEquals("trace", response.getTraceID());
        assertEquals(400, response.getError().getCode());
        assertEquals("Bad request", response.getError().getMessage());
        assertNull(new ResponseDTO().getError());
    }

    @Test
    void exceptionConstructorsPreserveMessageAndCause() {
        final Exception cause = new Exception("cause");
        assertEquals("business", new BusinessException("business").getMessage());
        assertSame(cause, new BusinessException(cause).getCause());
        assertSame(cause, new BusinessException("business", cause).getCause());
        assertEquals("missing", new NotFoundException("missing").getMessage());
        assertSame(cause, new NotFoundException(cause).getCause());
        assertSame(cause, new NotFoundException("missing", cause).getCause());
        assertEquals("invalid", new ValidationException("invalid").getMessage());
        assertSame(cause, new ValidationException(cause).getCause());
        assertSame(cause, new ValidationException("invalid", cause).getCause());
    }
}
