/*
 * Copyright (c) 2014, FinancialForce.com, inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 * - Neither the name of the FinancialForce.com, inc nor the names of its contributors
 *      may be used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @group Core
 */
public with sharing class fflib_IDGenerator
{
	private static final Integer ID_SUFFIX_LENGTH = 12;
	private static final Integer MAX_INTEGER = 2147483647; //2^31-1

	private static Map<String, Integer> fakeIdCountByPrefix = new Map<String, Integer>();

	/**
	 * Generate a fake Salesforce Id for the given SObjectType
	 */
	public static Id generate(Schema.SObjectType sobjectType)
	{
		String idPrefix = sobjectType.getDescribe().getKeyPrefix();
		String idSuffix = getSuffix(idPrefix);
			
		return Id.valueOf(idPrefix + idSuffix);
	}

	/**
	 * Resets the internal counters used in ID generation, so that the subsequent
	 * generated IDs start counting up from 1 again.
	 */
	public static void reset()
	{
		fakeIdCountByPrefix = new Map<String, Integer>();
	}

	private static String getSuffix(String idPrefix)
	{
		Integer fakeIdCount = fakeIdCountByPrefix.get(idPrefix);
		if (fakeIdCount == null || fakeIdCount >= MAX_INTEGER)
		{
			fakeIdCount = 0;
		}

		fakeIdCountByPrefix.put(idPrefix, ++fakeIdCount);
		
		return String.valueOf(fakeIdCount).leftPad(ID_SUFFIX_LENGTH).replace(' ', '0');
	}
}