/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.gtw.config.isolation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ErrorLogEnum;
import it.finanze.sanita.fse2.ms.gtw.config.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.config.logging.LoggerHelper;

@Tag("ISOLATION")
class LoggingIsolationTest {

    @Test
    void emitsStructuredLogsAtEveryLevel() throws Exception {
        final Logger logger = (Logger) LoggerFactory.getLogger(LoggerHelper.class);
        final Level original = logger.getLevel();
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.TRACE);
        try {
            final LoggerHelper helper = new LoggerHelper();
            final Date start = new Date(0);
            helper.trace("trace", OperationLogEnum.ADD_CONFIG_PROPERTIES, ResultLogEnum.OK, start);
            helper.debug("debug", OperationLogEnum.ADD_CONFIG_PROPERTIES, ResultLogEnum.OK, start);
            helper.info("info", OperationLogEnum.ADD_CONFIG_PROPERTIES, ResultLogEnum.OK, start);
            helper.warn("warn", OperationLogEnum.ADD_CONFIG_PROPERTIES, ResultLogEnum.KO, start);
            helper.error("error", OperationLogEnum.ADD_CONFIG_PROPERTIES, ResultLogEnum.KO, start,
                    ErrorLogEnum.KO_GENERIC);

            assertEquals(5, appender.list.size());
            final Level[] levels = {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR};
            for (int i = 0; i < levels.length; i++) {
                final ILoggingEvent event = appender.list.get(i);
                assertEquals(levels[i], event.getLevel());
                final JsonNode json = new ObjectMapper().readTree(event.getFormattedMessage());
                assertEquals(levels[i].toString().toLowerCase(), json.get("message").asText());
                assertEquals("ADD-PROPS", json.get("operation").asText());
                assertEquals(i < 3 ? "OK" : "KO", json.get("op_result").asText());
                assertFalse(json.get("op_timestamp_start").asText().isEmpty());
                assertFalse(json.get("op_timestamp_end").asText().isEmpty());
                if (i == 4) {
                    assertEquals("KO-GENERIC", json.get("op_error").asText());
                    assertEquals("Errore generico", json.get("op_error_description").asText());
                } else {
                    assertFalse(json.has("op_error"));
                }
            }
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(original);
            appender.stop();
        }
    }
}
