package io.seata.samples.account;


import io.seata.samples.common.Account;
import org.apache.ibatis.annotations.Mapper;

import java.sql.SQLException;

/**
 * 余额账户 DAO
 */
@Mapper
public interface Account2ndDAO {

    void addAccount(Account account) throws SQLException;
    
    int updateAmount(Account account) throws SQLException;
    
    int updateFreezedAmount(Account account) throws SQLException;
    
    Account getAccount(String accountNo) throws SQLException;
    
    Account getAccountForUpdate(String accountNo) throws SQLException;
    
    void deleteAllAccount() throws SQLException;
}
