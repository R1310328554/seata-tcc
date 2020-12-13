package io.seata.samples.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication//(scanBasePackages = "io.seata.samples", exclude = DataSourceAutoConfiguration.class)
@ImportResource("classpath:spring/*.xml")
public class SpringbootMybatisBusinessApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMybatisBusinessApplication.class, args);
	}

}
