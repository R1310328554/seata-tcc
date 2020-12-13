package io.seata.samples.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ImportResource("classpath:spring/*.xml")
@MapperScan(basePackages = "io.seata.samples.account")
public class SpringbootMybatisAccountApplication implements ApplicationRunner {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SpringbootMybatisAccountApplication.class, args);
    }


    /**
     * 扣钱账户 数据源
     */
    @Autowired
    private DataSource toAccountDataSource;

    /**
     * 初始化账户数据
     * @param dataSource
     * @param accountNo
     * @param amount
     */
    protected void prepareData(DataSource dataSource, String accountNo, double amount) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            String sql = "insert into account(account_no, amount, freezed_amount) values(?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, accountNo);
            ps.setDouble(2, amount);
            ps.setDouble(3, 0);
            ps.executeUpdate();
            ps.close();
        }catch (Throwable t){
            t.printStackTrace();
        }finally {
            if(conn != null){
                conn.close();
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化表数据
        double initAmount = 1_000_000_000;
        try {
            prepareData(toAccountDataSource, "C", initAmount);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
