package it.finanze.sanita.fse2.ms.gtw.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.config.config.Constants;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
class ConfigApplicationTests {

	@Autowired
	Environment env;

	@Test
	void contextLoads() {
		assertNotNull(env);
	}

}
