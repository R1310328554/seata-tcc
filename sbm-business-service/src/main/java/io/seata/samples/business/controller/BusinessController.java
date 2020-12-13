package io.seata.samples.business.controller;

import io.seata.samples.business.service.TransferService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@RequestMapping("/")
@RestController
@Slf4j
public class BusinessController {


    /**
     * 转账服务
     */
    @Autowired
    protected TransferService transferService ;

    /**
     //执行 A->C 转账成功 demo, 分布式事务提交
     *
     * @return
     */
    @RequestMapping("/purchase/commit")
    public Boolean purchaseCommit(HttpServletRequest request) {
        doTransferSuccess(1_000_000_000, 10);
        return true;
    }
    /**
     * 执行转账成功 demo
     *
     * @param initAmount 初始化余额
     * @param transferAmount  转账余额
     */
    private void doTransferSuccess(double initAmount, double transferAmount)  {
        //执行转账操作
        doTransfer("A", "C", transferAmount);

        //校验A账户余额：initAmount - transferAmount
//        checkAmount(fromAccountDAO, "A", initAmount - transferAmount);

        //校验C账户余额：initAmount + transferAmount
//        checkAmount(toAccountDAO, "C", initAmount + transferAmount);
    }

    /**
     * 执行转账 操作
     * @param transferAmount 转账金额
     */
    private boolean doTransfer(String from, String to, double transferAmount) {
        //转账操作
        boolean ret = transferService.transfer(from, to, transferAmount);
        if(ret){
            log.info("从账户"+from+"向"+to+"转账 "+transferAmount+"元 成功.");
        }else {
            log.info("从账户"+from+"向"+to+"转账 "+transferAmount+"元 失败.");
        }
        return ret;
    }

    /**
     //执行 B->XXX 转账失败 demo， 分布式事务回滚
     *
     * @return
     */
    @RequestMapping("/purchase/rollback")
    public Boolean purchaseRollback() {
        try {
            doTransferFailed(100, 10);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 执行转账 失败 demo， 'B' 向未知用户 'XXX' 转账，转账失败分布式事务回滚
     * @param initAmount 初始化余额
     * @param transferAmount  转账余额
     */
    private void doTransferFailed(int initAmount, int transferAmount) throws SQLException {
        // 'B' 向未知用户 'XXX' 转账，转账失败分布式事务回滚
        try{
            doTransfer("B", "XXX", transferAmount);
        }catch (Throwable t){
            log.info("从账户B向未知账号XXX转账失败.");
        }

        //校验A2账户余额：initAmount
//        checkAmount(fromAccountDAO, "B", initAmount);

        //账户XXX 不存在，无需校验
    }

    /**
     * 校验账户余额
//     * @param accountDAO
     * @param accountNo
     * @param expectedAmount
     * @throws SQLException
     */
    private void checkAmount(String accountNo, double expectedAmount) {
        try {
//            AccountDAO accountDAO,
//            Account account = accountDAO.getAccount(accountNo);
//            Assert.isTrue(account != null, "账户不存在");
//            double amount = account.getAmount();
//            double freezedAmount = account.getFreezedAmount();
//            Assert.isTrue(expectedAmount == amount, "账户余额校验失败");
//            Assert.isTrue(freezedAmount == 0, "账户冻结余额校验失败");
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

}
