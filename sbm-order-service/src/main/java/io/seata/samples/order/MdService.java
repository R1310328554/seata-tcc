package io.seata.samples.order;


import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.samples.account.ResultHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * 余额账户 DAO
 */
@Service
public class MdService {

    //    @Autowired
//    private MdDAO mdDAO;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public void beforeTx(BusinessActionContext businessActionContext) throws SQLException {
        String xid = businessActionContext.getXid();
        long branchId = businessActionContext.getBranchId();
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set( xid + "-" + branchId,"", 10, TimeUnit.MINUTES);
//        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
    }
    public boolean checkMd(BusinessActionContext businessActionContext) throws SQLException {
        String xid = businessActionContext.getXid();
        long branchId = businessActionContext.getBranchId();
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String s = stringStringValueOperations.get(xid + "-" + branchId);
        if (s == null) {
            return true;
        }
        return false;
    }
    public void completeTx(BusinessActionContext businessActionContext) throws SQLException {
        String xid = businessActionContext.getXid();
        long branchId = businessActionContext.getBranchId();
        stringRedisTemplate.delete(xid + "-" + branchId);
    }

    private void completeTx3(BusinessActionContext businessActionContext) throws SQLException {
        io.seata.samples.account.ResultHolder.removeResult(getClass(), businessActionContext.getXid());
    }
    private boolean checkMd3(BusinessActionContext businessActionContext) throws SQLException {
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }
        return false;
    }

}
