package com.texastoc.service.calculator;

import com.texastoc.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PayoutCalculatorTest implements TestConstants {

    private PayoutCalculator payoutCalculator;

    @Before
    public void before() {
        payoutCalculator = new PayoutCalculator();
    }

    @Test
    public void testNoPlayersNoPayouts() {

    }
}
