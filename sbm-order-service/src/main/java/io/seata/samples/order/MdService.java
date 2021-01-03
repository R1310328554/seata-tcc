package io.seata.samples.order;

import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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


    public boolean beforeTx(BusinessActionContext businessActionContext){
        String xid = businessActionContext.getXid();
        long branchId = businessActionContext.getBranchId();
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String andSet = stringStringValueOperations.getAndSet(xid + "-" + branchId, "1");// , 10, TimeUnit.MINUTES
        if ("1".equals(andSet)) {
            return true;// 已经设置过， 则返回true，表示不需要继续 try， 防止悬挂
        }
        return false;
//        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
    }
    public boolean checkMd2(BusinessActionContext businessActionContext){
        String xid = businessActionContext.getXid();
        long branchId = businessActionContext.getBranchId();
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String s = stringStringValueOperations.getAndSet(xid + "-" + branchId, null);//  Value must not be null!
        if (s == null) {
            return true;
        }
        return false;
    }
    public boolean checkMd(BusinessActionContext businessActionContext) {
        String xid = businessActionContext.getXid();
        long branchId = businessActionContext.getBranchId();
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String s = stringStringValueOperations.get(xid + "-" + branchId);
        if (s == null) {
            return true;// 已经被删除， 则返回true，表示不需要继续 二阶段， 防止 幂等、空回滚问题
        }
        return false;
    }
    public void completeTx(BusinessActionContext businessActionContext) {
        String xid = businessActionContext.getXid();
        long branchId = businessActionContext.getBranchId();
        stringRedisTemplate.delete(xid + "-" + branchId);
    }

    private void completeTx3(BusinessActionContext businessActionContext) {
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
    }
    private boolean checkMd3(BusinessActionContext businessActionContext) {
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }
        return false;
    }

}
