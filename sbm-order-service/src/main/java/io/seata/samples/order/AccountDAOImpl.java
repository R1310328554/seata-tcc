//package io.seata.samples.order;
//
//import io.seata.samples.common.Account;
//import io.seata.samples.common.AccountDAO;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.springframework.stereotype.Component;
//
//import java.sql.SQLException;
//
///**
// * 余额账户 DAO 实现
// */
//@Component
//public class AccountDAOImpl implements AccountDAO {
//
//	public SqlSessionTemplate sqlSession;
//
//	public void setSqlSession(SqlSessionTemplate sqlSession) {
//        this.sqlSession = sqlSession;
//	}
//
//    @Override
//    public void addAccount(Account account) throws SQLException {
//    	sqlSession.insert("addAccount", account);
//    }
//
//    @Override
//    public int updateAmount(Account account) throws SQLException {
//    	return sqlSession.update("updateAmount", account);
//    }
//
//    @Override
//    public Account getAccount(String accountNo) throws SQLException {
//        return (Account) sqlSession.selectOne("getAccount", accountNo);
//    }
//
//    @Override
//    public  Account getAccountForUpdate(String accountNo) throws SQLException {
//    	return (Account) sqlSession.selectOne("getAccountForUpdate", accountNo);
//    }
//
//    @Override
//    public int updateFreezedAmount(Account account) throws SQLException {
//    	return sqlSession.update("updateFreezedAmount", account);
//    }
//
//    @Override
//    public void deleteAllAccount() throws SQLException {
//    	sqlSession.update("deleteAllAccount");
//    }
//
//}
