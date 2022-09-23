package it.finanze.sanita.fse2.ms.gtw.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.finanze.sanita.fse2.ms.gtw.config.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.config.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.config.enums.ConfigItemType;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class for testing classes.
 * 
 * @author Simone Lungarella
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
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

    protected ConfigItemDTO getConfigurationItems(final ConfigItemType type) {

        final String url = getBaseURL() + "/config-items";
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("type", type.getName());
        
        return restTemplate.getForObject(builder.toUriString(), ConfigItemDTO.class);
    }

    protected ResponseDTO saveConfigurationItems(final ConfigItemType type, final Map<String, String> configItems) {

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

    protected void deleteConfigurationItems(final ConfigItemType type, final String keyItem) {

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

    protected void updateConfigurationItems(final ConfigItemType type, final String itemKey, final String value) {

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
