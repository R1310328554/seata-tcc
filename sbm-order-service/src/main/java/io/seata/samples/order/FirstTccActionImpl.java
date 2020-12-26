package io.seata.samples.order;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.samples.common.Account;
import io.seata.samples.common.FirstTccAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

/**
 * 扣钱参与者实现
 *
 * @author zhangsen
 */
@Component
@Slf4j
public class FirstTccActionImpl implements FirstTccAction {

    /**
     * 扣钱账户 DAO
     */
    @Autowired
    private AccountDAO fromAccountDAO;

    /**
     * 扣钱数据源事务模板
     */
    @Autowired
    private TransactionTemplate fromDsTransactionTemplate;

    @Autowired
    private MdDAO mdDAO;

    /**
     * 一阶段准备，冻结 转账资金
     * @param businessActionContext
     * @param accountNo
     * @param amount
     * @return
     */
    @Override
    public boolean prepareMinus(BusinessActionContext businessActionContext, final String accountNo, final double amount) {

        //分布式事务ID
        final String xid = businessActionContext.getXid();
        final long branchId = businessActionContext.getBranchId();

        return fromDsTransactionTemplate.execute(new TransactionCallback<Boolean>(){

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    //校验账户余额
                    Account account = fromAccountDAO.getAccountForUpdate(accountNo);
                    mdDAO.insertMd(xid, branchId, 1);
                    if(account == null){
                        throw new RuntimeException("账户不存在");
                    }
                    if (account.getAmount() - amount < 0) {
                        throw new RuntimeException("余额不足");
                    }
                    //冻结转账金额
                    double freezedAmount = account.getFreezedAmount() + amount;
                    account.setFreezedAmount(freezedAmount);
                    fromAccountDAO.updateFreezedAmount(account);
                    log.info(String.format("prepareMinus account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));

                    return true;
                } catch (Throwable t) {
                    log.error(t.toString());
                    status.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    /**
     * 二阶段提交
     * @param businessActionContext
     * @return
     */
    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        //分布式事务ID
        final String xid = businessActionContext.getXid();
        final long branchId = businessActionContext.getBranchId();
        //账户ID
        final String accountNo = String.valueOf(businessActionContext.getActionContext("accountNo"));
        //转出金额
        final double amount = Double.valueOf(String.valueOf(businessActionContext.getActionContext("amount")));
        return fromDsTransactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try{
                    Map map = mdDAO.selectMd(xid, branchId, 1);
                    if (map == null) {
                        log.error("全局事务记录 不存在!  xid: " + xid + ", branchId: " + branchId);
                        return true;
                    }
                    Integer log_status = (Integer) map.get("log_status");
                    if (log_status == 2) {
                        return true;
                    }
                    int i = mdDAO.updateMd(xid, branchId, 2L);

                    Account account = fromAccountDAO.getAccountForUpdate(accountNo);
                    //扣除账户余额
                    double newAmount = account.getAmount() - amount;
                    if (newAmount < 0) {
                        throw new RuntimeException("余额不足");
                    }
//                    System.out.println(" errrrrrrrrrr  = " + (1/0));// 此处异常会导致一直不断的重试！！
                    account.setAmount(newAmount);
                    //释放账户 冻结金额
                    account.setFreezedAmount(account.getFreezedAmount()  - amount); // 为什么会出现负数？
                    fromAccountDAO.updateAmount(account);
                    log.info(String.format("minus account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
                    return true;
                }catch (Throwable t){
                    log.error(t.toString());
                    status.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    /**
     * 二阶段回滚
     * @param businessActionContext
     * @return
     */
    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        //分布式事务ID
        final String xid = businessActionContext.getXid();
        final long branchId = businessActionContext.getBranchId();
        //账户ID
        final String accountNo = String.valueOf(businessActionContext.getActionContext("accountNo"));
        //转出金额
        final double amount = Double.valueOf(String.valueOf(businessActionContext.getActionContext("amount")));
        return fromDsTransactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try{
                    Map map = mdDAO.selectMd(xid, branchId, 1);
                    if (map == null) {
                        log.error("全局事务记录 不存在!  xid: " + xid + ", branchId: " + branchId);
                        return true;
                    }
                    Integer log_status = (Integer) map.get("log_status");
                    if (log_status == 3) {
                        return true;
                    }
                    int i = mdDAO.updateMd(xid, branchId, 3L);

                    Account account = fromAccountDAO.getAccountForUpdate(accountNo);
                    if(account == null){
                        //账户不存在，回滚什么都不做
                        return true;
                    }
                    //释放冻结金额
                    account.setFreezedAmount(account.getFreezedAmount()  - amount);
                    fromAccountDAO.updateFreezedAmount(account);
                    log.error(String.format("Undo prepareMinus account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));

                    return true;
                }catch (Throwable t){
                    log.error(t.toString());
                    status.setRollbackOnly();
                    return false;
                }
            }
        });
    }

}
