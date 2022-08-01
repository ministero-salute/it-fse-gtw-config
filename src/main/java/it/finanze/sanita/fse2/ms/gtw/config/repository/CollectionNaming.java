package it.finanze.sanita.fse2.ms.gtw.config.repository;

import it.finanze.sanita.fse2.ms.gtw.config.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.config.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CollectionNaming {

    @Autowired
    private ProfileUtility profileUtility;

    @Bean("configDataBean")
    public String getConfigDataCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.CONFIG_DATA;
        }
        return Constants.ComponentScan.Collections.CONFIG_DATA;
    }
}
