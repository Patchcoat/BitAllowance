package com.bitallowance;

import android.os.AsyncTask;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetEntityList() {
        SignIn task = new SignIn();
        task.execute("passwordTest");
        try {
            task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Reserve.get_entityList().size() > 0 );
    }

    @Test
    public void testGetTransactionList() {
        SignIn task = new SignIn();
        task.execute("passwordTest");
        try {
            task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(Reserve.get_transactionList().size() > 0 );
    }

    @Test
    public  void testExecuteTransaction() {
        int transactionListSize = Reserve.get_transactionList().size();
        int entityListSize = Reserve.get_entityList().size();
        Random random = new Random();
        int iEntity = random.nextInt(entityListSize);
        int iTransaction = random.nextInt(transactionListSize);
        BigDecimal prevBalance = Reserve.get_entityList().get(iEntity).getCashBalance();
        BigDecimal transactionValue = Reserve.get_transactionList().get(iTransaction).get_value();
        boolean isTask = false;
        if (Reserve.get_transactionList().get(iTransaction).getTransactionType() == TransactionType.TASK) {
            isTask = true;
        }
        Reserve.get_transactionList().get(iTransaction).execute(Reserve.get_entityList().get(iEntity));

        BigDecimal newBalance;
        if (isTask){
            newBalance = prevBalance.add(transactionValue);
        } else {
            newBalance = prevBalance.subtract(transactionValue);
        }
        assertTrue(Reserve.get_entityList().get(iEntity).getCashBalance() == newBalance);
    }




}
