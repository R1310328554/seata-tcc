package io.seata.samples.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@SpringBootApplication//(scanBasePackages = "io.seata.samples", exclude = DataSourceAutoConfiguration.class)
@ImportResource("classpath:spring/*.xml")
@RestController
public class SpringbootMybatisBusinessApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMybatisBusinessApplication.class, args);
	}


	@RequestMapping("/hi")
	public String hi() {
		return "hi~  " + new Date();
	}
}
