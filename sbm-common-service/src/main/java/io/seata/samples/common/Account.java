package io.seata.samples.common;

import org.apache.ibatis.type.Alias;

/**
 * 账户
 *
 * @author zhangsen
 */
@Alias("account")
public class Account {

    public Account(){
    }
    /**
     * 账户
     */
    private String accountNo;
    /**
     * 余额
     */
    private double amount;
    /**
     * 冻结金额
     */
    private double freezedAmount;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFreezedAmount() {
        return freezedAmount;
    }

    public void setFreezedAmount(double freezedAmount) {
        this.freezedAmount = freezedAmount;
    }



}
