package com.capg.project.bank.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.capg.project.bank.bean.Customer;
import com.capg.project.bank.bean.Passbook;
import com.capg.project.bank.exception.AccountException;
import com.capg.project.bank.service.CustomerServiceImp;
import com.capg.project.bank.service.ICustomerService;

public class CustomerDaoImpTest {
Customer bean=new Customer();
ICustomerService service=new CustomerServiceImp();

	@Test
	public void testCreateAccount() throws AccountException 
	{/*
		bean.setAccountNumber(1111);
		bean.setAddress("Chennai");
		bean.setBalance(20);
		bean.setGovernmentID("123456789012");
		bean.setName("Swarup");
		bean.setPanNumber("145UIOP123");
		bean.setPhoneNumber("9562314789");
		bean.setPin(1111);
		Boolean flag=service.createAccount(bean);
		assertTrue("True",flag);*/
		
	}

	@Test
	public void testShowBalance() throws AccountException 
	{
		Long accountNumber=23769L;
		Integer pin=6747;
		Customer customer=service.showBalance(accountNumber,pin);
		assertNotNull(customer.getBalance());
	}

	@Test
	public void testDeposit() throws AccountException 
	{
		Long accountNumber=23769L;
		Integer pin=6747;
		Double depositAmount=500.0;
		boolean flag=service.deposit(accountNumber, pin, depositAmount);
		assertTrue("True",flag);
	}

	@Test
	public void testWithdraw() throws AccountException 
	{
		Long accountNumber=23769L;
		Integer pin=6747;
		Double withdrawAmount=50.0;
		boolean flag=service.deposit(accountNumber, pin, withdrawAmount);
		assertTrue("True",flag);
	}

	@Test
	public void testFundTransfer() throws AccountException 
	{
		Long dAccountNumber=23769L;
		Integer dPin=6747;
		Long cAccountNumber=61717L;
		Double transferAmount=50.0;
		boolean flag=service.fundTransfer(dAccountNumber, dPin, cAccountNumber, transferAmount);
		assertTrue("True",flag);
	}

	@Test
	public void testPrintTansaction() throws AccountException 
	{
		Long accountNumber=23769L;
		Integer pin=6747;
		List<Passbook> transList=service.printTansaction(accountNumber, pin);
		assertNotNull(transList);
	}

}
