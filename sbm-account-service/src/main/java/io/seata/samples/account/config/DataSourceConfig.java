package io.seata.samples.account.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource druidDataSource() {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        return druidDataSource;
//    }

//    @Bean
////    @Primary
//    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource);
//        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
//                .getResources("classpath*:/mapper/*.xml"));
//        return factoryBean;
//    }

//    @Bean
//    public SqlSessionFactory sqlSessionFactory(SqlSessionFactoryBean factoryBean) throws Exception {
//        return factoryBean.getObject();
//    }
}
