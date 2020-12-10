/*
 * Copyright (c) 2017 FinancialForce.com, inc.  All rights reserved.
 */

/**
 * @group Core
 * Contains counterparts for helper methods in the native System class.
 */

public class fflib_System
{
	//There should only be a single matcher registered, with asserts
	//like fflib_System.assertEquals(fflib_Match.stringIsNotBlank(), actual)
	private static final Integer EXPECTED_MATCHER_COUNT_ASSERT_EQUALS = 1;
	private static final String ERROR_ASSERT_EQUALS = 'fflib_System.assertEquals expects you to register exactly '
		+ EXPECTED_MATCHER_COUNT_ASSERT_EQUALS + ' fflib_IMatcher (typically through the helpers in fflib_Match).';

	/**
	 * Verifies that the supplied argument is meaningfully equivalent to the expected argument, as defined by its matcher.
	 * See fflib_SystemTest for examples of usage.
	 * @param expectedValue	Dummy value, returned on registering an fflib_IMatcher.
	 * @param actualValue	The object instance upon which we are checking equality.
	 */
	public static void assertEquals(Object expectedValue, Object actualValue)
	{
		assertEquals(expectedValue, actualValue, null);
	}

	/**
	 * Verifies that the supplied argument is meaningfully equivalent to the expected argument, as defined by its matcher.
	 * See fflib_SystemTest for examples of usage.
	 * @param expectedValue	Dummy value, returned on registering an fflib_IMatcher.
	 * @param actualValue	The object instance upon which we are checking equality.
	 * @param customAssertMessage	Provides context or additional information for the assertion.
	 */
	public static void assertEquals(Object expectedValue, Object actualValue, String customAssertMessage)
	{
		fflib_IMatcher matcher = null;
		try
		{
			List<fflib_IMatcher> matchers = fflib_Match.getAndClearMatchers(EXPECTED_MATCHER_COUNT_ASSERT_EQUALS);
			matcher = matchers[0];
		}
		catch (fflib_ApexMocks.ApexMocksException e)
		{
			throw new fflib_ApexMocks.ApexMocksException(ERROR_ASSERT_EQUALS);
		}
		
		if (!matcher.matches(actualValue))
		{
			throw new fflib_ApexMocks.ApexMocksException(String.format('Expected : {0}, Actual: {1}{2}', new String[]{
				String.valueOf(matcher),
				String.valueOf(actualValue),
				String.isBlank(customAssertMessage) ? '' : (' -- ' + customAssertMessage)
			}));
		}
	}
}