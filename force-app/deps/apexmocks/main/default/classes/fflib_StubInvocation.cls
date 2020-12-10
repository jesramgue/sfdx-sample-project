/*
 * Copyright (c) 2017 FinancialForce.com, inc.  All rights reserved.
 */
/**
 * Describes an invocation of a method on a stubbed object.
 * @nodoc
 */
@IsTest
public class fflib_StubInvocation
{
	public Object StubbedObject {get; private set;}
	public String StubbedMethodName {get; private set;}
	public Type ReturnType {get; private set;}
	public List<Type> ListOfParamTypes {get; private set;}
	public List<String> ListOfParamNames {get; private set;}
	public List<Object> ListOfArgs {get; private set;}

	public fflib_StubInvocation withStubbedObject(Object value)
	{
		StubbedObject = value;
		return this;
	}
	public fflib_StubInvocation withStubbedMethodName(String value)
	{
		StubbedMethodName = value;
		return this;
	}
	public fflib_StubInvocation withReturnType(Type value)
	{
		ReturnType = value;
		return this;
	}
	public fflib_StubInvocation withListOfParamTypes(List<Type> value)
	{
		ListOfParamTypes = value;
		return this;
	}
	public fflib_StubInvocation withListOfParamNames(List<String> value)
	{
		ListOfParamNames = value;
		return this;
	}
	public fflib_StubInvocation withListOfArgs(List<Object> value)
	{
		ListOfArgs = value;
		return this;
	}
}