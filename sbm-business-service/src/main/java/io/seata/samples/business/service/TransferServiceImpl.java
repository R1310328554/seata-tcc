package io.seata.samples.business.service;

import io.seata.samples.business.service.TransferService;
import io.seata.samples.common.FirstTccAction;
import io.seata.samples.common.SecondTccAction;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 转账服务实现
 *
 * @author zhangsen
 */
@Component
@Slf4j
public class TransferServiceImpl implements TransferService, BeanNameAware {


    @Autowired
    private FirstTccAction firstTccAction;

    @Autowired
    private SecondTccAction secondTccAction;

    /**
     * 转账操作
     * @param from  扣钱账户
     * @param to  加钱账户
     * @param amount  转账金额
     * @return
     */
    @Override
    @GlobalTransactional
    public boolean transfer(final String from, final String to, final double amount) {
        //扣钱参与者，一阶段执行
        boolean ret = firstTccAction.prepareMinus(null, from, amount);
        if(!ret){
            //扣钱参与者，一阶段失败; 回滚本地事务和分布式事务
            throw new RuntimeException("账号:["+from+"] 预扣款失败");
        }
        //加钱参与者，一阶段执行
        ret = secondTccAction.prepareAdd(null, to, amount);
        if(!ret){
            throw new RuntimeException("账号:["+to+"] 预收款失败");
        }
        log.info(String.format("transfer amount[%s] from [%s] to [%s] finish.", String.valueOf(amount), from, to));
        return true;
    }

    @Override
    public void setBeanName(String s) {
        log.info("TransferServiceImpl.setBeanName     " + s);
    }
}
