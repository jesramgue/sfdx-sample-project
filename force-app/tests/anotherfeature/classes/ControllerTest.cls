/*
 * Copyright (c) 2019 FinancialForce.com, inc.  All rights reserved.
 */
@isTest
public class ControllerTest {
    @isTest static void testIncrease(){
        Integer inValue = 1;
        Integer outValue = Controller.increase(inValue);
        System.assertEquals(inValue + 1, outValue);
    }
}