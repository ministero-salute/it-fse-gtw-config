/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.finanze.sanita.fse2.ms.gtw.config.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.WhoIsResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class for testing classes.
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
abstract class AbstractTest {

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @SpyBean
    protected MongoTemplate mongoTemplate;

    private String getBaseURL() {
        return "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + webServerAppCtxt.getServletContext().getContextPath() + "/v1";
    }

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    protected ConfigItemDTO getConfigurationItems(final ConfigItemTypeEnum type) {

        final String url = getBaseURL() + "/config-items";
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("type", type.getName());
        
        return restTemplate.getForObject(builder.toUriString(), ConfigItemDTO.class);
    }

    protected ResponseEntity<WhoIsResponseDTO> getGatewayName() {

        final String url = getBaseURL() + "/whois";
        return restTemplate.getForEntity(url, WhoIsResponseDTO.class);
    }

    protected ResponseDTO saveConfigurationItems(final ConfigItemTypeEnum type, final Map<String, String> configItems) {

        try {
            final String url = getBaseURL() + "/config-items";
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            final ObjectMapper objectMapper = new ObjectMapper(); 
            objectMapper.registerModule(new JavaTimeModule());
            String stringifiedObj = null;
            try {
                stringifiedObj = objectMapper.writeValueAsString(configItems);
            } catch (Exception e) {
                log.error("Error while parsing object in request");
            }
            
            final HttpEntity<String> entity = new HttpEntity<String>(stringifiedObj, headers);
            final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).
                    queryParam("type", type.getName());
            
            return restTemplate.postForObject(builder.toUriString(), entity, ResponseDTO.class);
        } catch (Exception e) {
            log.error("Error while calling endpoint to save configuration items", e);
            throw e;
        }
    }

    protected void deleteConfigurationItems(final ConfigItemTypeEnum type, final String keyItem) {

        try {
            final String url = getBaseURL() + "/config-items";
            
            final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("type", type.getName())
                .queryParam("itemKey", keyItem);
            
            restTemplate.delete(builder.toUriString(), ResponseDTO.class);
        } catch (Exception e) {
            log.error("Error while calling endpoint to delete configuration items", e);
            throw e;
        }
    }

    protected void updateConfigurationItems(final ConfigItemTypeEnum type, final String itemKey, final String value) {

        try {
            final String url = getBaseURL() + "/config-items";
            
            final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("type", type.getName())
                .queryParam("itemKey", itemKey)
                .queryParam("value", value);
            
            restTemplate.put(builder.toUriString(), ResponseDTO.class);
        } catch (Exception e) {
            log.error("Error while calling endpoint to update configuration items", e);
            throw e;
        }
    }

}
