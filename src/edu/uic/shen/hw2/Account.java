package edu.uic.shen.hw2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Account is the class object for all operations
 * Author: Sushen Kumar
 */
public class Account {

    // Balance for the account
    private long balance;

    // Init Lock
    private final ReentrantLock aLock = new ReentrantLock();

    // Conditions for deposit and withdrawals
    private final Condition iDeposit = aLock.newCondition();
    private final Condition pWithdrawal = aLock.newCondition();

    // Counter for preferred withdrawals
    private int preferredWithdrawals = 0;

    /**
     * Constructor
     * @param balance
     */
    public Account(long balance) {
        this.balance = balance;
    }

    /**
     * Getter for account balance
     * @return long balance
     */
    private long getBalance() {
        return balance;
    }

    /**
     * Method to deposit balance into an account
     * @param amount
     */
    void deposit(long amount) {
        System.out.println("Initial balance is " + this.balance);
        aLock.lock();
        try {
            this.balance +=amount;
            System.out.println("Success! Balance is " + this.balance);
            iDeposit.signal();
        } finally {
            aLock.unlock();
        }
    }

    /**
     * Method to make a preferred withdrawal from an account
     * @param amount
     */
    public void preferredWithdraw(long amount) {
        preferredWithdrawals++;
        aLock.lock();
        while (balance <= amount){
            try{
                iDeposit.await();
                if(this.balance >= amount) {
                    this.balance -= amount;
                    System.out.println("Withdraw Successful, balance is = "+balance);
                    preferredWithdrawals--;
                    pWithdrawal.signalAll();
                }
            }
            catch (InterruptedException e){
                System.out.println("Withdrawal was interrupted.");
            } finally {
                aLock.unlock();
            }
        }
    }

    /**
     * Method for withdrawing from an account
     * @param amount
     * @param type
     */
    public void withdraw(long amount, TransactionType type) {
        if(type == TransactionType.Preferred) {
            preferredWithdraw(amount);
        } else {
            aLock.lock();

            while (balance < amount) {
                try {
                    iDeposit.await();
                } catch (InterruptedException e) {
                    System.out.println("Withdrawal interrupted.");
                }
            }

            while (preferredWithdrawals > 0) {
                try {
                    pWithdrawal.await();
                    this.balance -= amount;
                    System.out.println("Withdraw Successful, balance is " + balance);
                } catch (InterruptedException e) {
                    System.out.println("Withdrawal interrupted.");
                } finally {
                    aLock.unlock();
                }
            }
        }
    }

    /**
     * Method to transfer an amount from one account to another
     * @param amount
     * @param reserve
     */
    void transfer(int amount, Account reserve) {
        aLock.lock();
        try {
            reserve.withdraw(amount, TransactionType.Ordinary);
            deposit(amount);
            System.out.println("Success! Transferred.");
        } finally {
            aLock.unlock();
        }
    }
}
