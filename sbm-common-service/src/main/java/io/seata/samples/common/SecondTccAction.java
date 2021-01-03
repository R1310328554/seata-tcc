package io.seata.samples.common;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * TCC参与者：加钱
 *
 * @author zhangsen
 */
public interface SecondTccAction {

	 /**
     * 一阶段方法
     * 
     * @param businessActionContext
     * @param amount
     */
    @TwoPhaseBusinessAction(name = "secondTccAction", commitMethod = "commit", rollbackMethod = "rollback")
    public boolean prepareAdd(BusinessActionContext businessActionContext,
                              @BusinessActionContextParameter(paramName = "fromAccountNo") String fromAccountNo,
                              @BusinessActionContextParameter(paramName = "accountNo") String toAccountNo,
                              @BusinessActionContextParameter(paramName = "amount") double amount);

    /**
     * 二阶段提交
     * @param businessActionContext
     * @return
     */
    public boolean commit(BusinessActionContext businessActionContext);

    /**
     * 二阶段回滚
     * @param businessActionContext
     * @return
     */
    public boolean rollback(BusinessActionContext businessActionContext);

}
