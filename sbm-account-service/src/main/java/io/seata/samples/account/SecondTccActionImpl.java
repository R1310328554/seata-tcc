package io.seata.samples.account;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.samples.common.Account;
import io.seata.samples.common.SecondTccAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 加钱参与者实现
 *
 * @author zhangsen
 */
@Component
@Slf4j
@Transactional
public class SecondTccActionImpl implements SecondTccAction {

    /**
     * 加钱账户 DAP
     */
    @Autowired
    private Account2ndDAO toAccountDAO;

    @Autowired
    private MdService mdService;

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
        try {
            Account account = new Account();
            account.setAccountNo(accountNo);
            account.setAmount(amount);
            int i = toAccountDAO.addFreezedAmount(account);
            if (i <= 0) {
                return false;
            }
            mdService.beforeTx(businessActionContext);
            log.info(String.format("prepareAdd account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
            return true;
        } catch (Throwable t) {
            log.error(t.toString());
            return false;
        }
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
        //账户ID
        final String accountNo = String.valueOf(businessActionContext.getActionContext("accountNo"));
        //转出金额
        final double amount = Double.valueOf(String.valueOf(businessActionContext.getActionContext("amount")));
        try{
            /**
             * 如果已经处理，返回true
             */
            if (mdService.checkMd(businessActionContext)) return true;
            Account account = new Account();
            account.setAccountNo(accountNo);
            account.setAmount(amount);
            int i = toAccountDAO.updateAmountForAdd(account);
            if (i <= 0) {
                return false;
            }
            mdService.completeTx(businessActionContext);
            log.info(String.format("add account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
            return true;
        }catch (Throwable t){
            log.error(t.toString());
            return false;
        }

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
        //账户ID
        final String accountNo = String.valueOf(businessActionContext.getActionContext("accountNo"));
        //转出金额
        final double amount = Double.valueOf(String.valueOf(businessActionContext.getActionContext("amount")));
        try{
            if (mdService.checkMd(businessActionContext)) return true;
            Account account = new Account();
            account.setAccountNo(accountNo);
            account.setAmount(amount);
            int i = toAccountDAO.minusFreezedAmount(account);
            if (i <= 0) {
                return false;
            }
            mdService.completeTx(businessActionContext);
            log.error(String.format("Undo prepareAdd account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
            return true;
        }catch (Throwable t){
            log.error(t.toString());
            return false;
        }
    }

}
