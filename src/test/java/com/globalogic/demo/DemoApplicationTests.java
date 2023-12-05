package com.globalogic.demo;

import com.globalogic.demo.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertFalse;


@ContextConfiguration(classes = DemoApplication.class)
class DemoApplicationTests {

	@Test
	void contextLoads() {
		DemoApplication.main(new String[]{});
		assertFalse(DemoApplication.class.getName().isEmpty());
	}

}
