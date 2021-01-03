package io.seata.samples.order;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.samples.common.Account;
import io.seata.samples.common.FirstTccAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

/**
 * 扣钱参与者实现
 *
 * @author zhangsen
 */
@Component
@Slf4j
@Transactional
public class FirstTccAction2Impl implements FirstTccAction {

    /**
     * 扣钱账户 DAO
     */
    @Autowired
    private AccountDAO fromAccountDAO;

    @Autowired
    private MdService mdService;
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
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Account account = new Account();
            account.setAccountNo(accountNo);
            account.setAmount(amount);
            int i = fromAccountDAO.minusFreezedAmount(account);
            if (i <= 0) {
//                throw new RuntimeException("余额不足");
                return false;
            }
            watch.stop();
            watch.start("t2");
            mdService.beforeTx(businessActionContext);
            log.info(String.format("prepareMinus account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
            watch.stop();
            String s = watch.prettyPrint();
            System.out.println(" try " + s);
            return true;
        }catch (Throwable t){
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

        StopWatch watch = new StopWatch();
        watch.start();
        //账户ID
        final String accountNo = String.valueOf(businessActionContext.getActionContext("accountNo"));
        //转出金额
        final double amount = Double.valueOf(String.valueOf(businessActionContext.getActionContext("amount")));
        try{
            if (mdService.checkMd(businessActionContext)) return true;
            Account account = new Account();
            account.setAccountNo(accountNo);
            account.setAmount(amount);
            int i = fromAccountDAO.updateAmountForMinus(account);
            if (i <= 0) {
//                throw new RuntimeException("余额不足");
                return false;
            }
            watch.stop();
            watch.start("t2");
            mdService.completeTx(businessActionContext);
            log.info(String.format("minus account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
            watch.stop();
            String s = watch.prettyPrint();
            System.out.println(" commit " +  s);
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
            int i = fromAccountDAO.addFreezedAmount(account);
            if (i <= 0) {
                return false;
            }
            mdService.completeTx(businessActionContext);
            log.error(String.format("Undo prepareMinus account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
            return true;
        }catch (Throwable t){
            log.error(t.toString());
            return false;
        }
    }

}
