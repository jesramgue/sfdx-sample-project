/*
 * Copyright (c) 2017 FinancialForce.com, inc.  All rights reserved.
 */
 /**
  * Contains static properties that control ApexMocks behavior.
  * @group Core
  */
@isTest
public class fflib_ApexMocksConfig
{
	/**
	 * When false, stubbed behavior and invocation counts are shared among all test spies.
	 * - See fflib_ApexMocksTest.thatMultipleInstancesCanBeMockedDependently
	 * - This is the default for backwards compatibility.
	 * When true, each test spy instance has its own stubbed behavior and invocations.
	 * - See fflib_ApexMocksTest.thatMultipleInstancesCanBeMockedIndependently
	 */
	public static Boolean HasIndependentMocks {get; set;}

	/**
	 * When false, invoking non-void methods without explicit stubs will simply return null.
	 * - This is the default for backwards compatibility.
	 * When true, this will result in a test failure, with a stack that identifies the unexpected call.
	 * - Being strict gives more rigorous tests.
	 * - It also helps when writing tests, if actual and expected arguments differ slightly.
	 */
	public static Boolean IsStrict {get; set;}

	static
	{
		HasIndependentMocks = false;
		IsStrict = false;
	}
}