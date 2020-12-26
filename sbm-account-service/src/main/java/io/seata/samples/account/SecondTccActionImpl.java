package io.seata.samples.account;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.samples.common.Account;
import io.seata.samples.common.SecondTccAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

/**
 * 加钱参与者实现
 *
 * @author zhangsen
 */
@Component
@Slf4j
public class SecondTccActionImpl implements SecondTccAction {

    /**
     * 加钱账户 DAP
     */
    @Autowired
    private Account2ndDAO toAccountDAO;

    @Autowired
    private TransactionTemplate toDsTransactionTemplate;

    @Autowired
    private MdDAO mdDAO;


    /**
     * 一阶段准备，转入资金 准备
     * @param businessActionContext
     * @param accountNo
     * @param amount
     * @return
     */
    @Override
    public boolean prepareAdd(final BusinessActionContext businessActionContext, final String accountNo, final double amount) {
        //分布式事务ID
        final String xid = businessActionContext.getXid();
        final long branchId = businessActionContext.getBranchId();

        return toDsTransactionTemplate.execute(new TransactionCallback<Boolean>(){

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    mdDAO.insertMd(xid, branchId, 1);
                    //校验账户
                    Account account = toAccountDAO.getAccountForUpdate(accountNo);
                    if(account == null){
                        log.info("prepareAdd: 账户["+accountNo+"]不存在, txId:" + businessActionContext.getXid());
                        return false;
                    }
                    //待转入资金作为 不可用金额
                    double freezedAmount = account.getFreezedAmount() + amount;
                    account.setFreezedAmount(freezedAmount);
                    toAccountDAO.updateFreezedAmount(account);
                    log.info(String.format("prepareAdd account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
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
        return toDsTransactionTemplate.execute(new TransactionCallback<Boolean>() {

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
                    int i = mdDAO.updateMd(xid, branchId, 2);

                    Account account = toAccountDAO.getAccountForUpdate(accountNo);
                    //加钱
                    double newAmount = account.getAmount() + amount;
                    account.setAmount(newAmount);
                    //冻结金额 清除
                    account.setFreezedAmount(account.getFreezedAmount()  - amount);
                    toAccountDAO.updateAmount(account);
                    log.info(String.format("add account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
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
        return toDsTransactionTemplate.execute(new TransactionCallback<Boolean>() {

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
                    int i = mdDAO.updateMd(xid, branchId, 3);

                    Account account = toAccountDAO.getAccountForUpdate(accountNo);
                    if(account == null){
                        //账户不存在, 无需回滚动作
                        return true;
                    }

//                    System.out.println(" errrrrrrrrrr  = " + (1/0));// 此处异常会导致一直不断的重试！！

                    //冻结金额 清除
                    account.setFreezedAmount(account.getFreezedAmount()  - amount);
                    toAccountDAO.updateFreezedAmount(account);

                    log.error(String.format("Undo prepareAdd account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
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
