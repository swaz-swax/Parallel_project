package com.capg.project.bank.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.capg.project.bank.bean.Customer;
import com.capg.project.bank.bean.Passbook;
import com.capg.project.bank.exception.AccountException;
import com.capgemini.jpa.utility.JPAUtil;


public class CustomerDaoImp implements ICustomerDao {
	EntityManager entityManager=null;
	Passbook passbook= new Passbook();
	String transactionDetail;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date;
	
	
	@Override
	public boolean createAccount(Customer c) throws AccountException {
		try
		{
			entityManager=JPAUtil.getEntityManager();
			EntityTransaction transaction=entityManager.getTransaction();
			transaction.begin();
			//c.setAccountNumber(accountNumber);
			entityManager.persist(c);
			transaction.commit();
			return true;
		}
		catch(PersistenceException e) 
		{
			e.printStackTrace();
			//TODO: Log to file
			throw new AccountException(e.getMessage());
		}
		finally 
		{
			entityManager.close();
		}
		
	}

	@Override
	public Customer showBalance(long accountNumber, int pin) throws AccountException {
		try
		{
			entityManager=JPAUtil.getEntityManager();
			Customer customer=entityManager.find(Customer.class, accountNumber);
			//String pin1=Integer.parseInt(customer.getPin());
			if((customer.getAccountNumber()==accountNumber) && (customer.getPin()==(pin)))
			{
				return customer;
			}
			else
			{
				
				System.err.println("Invalid Account number or password....");
				return customer;
			}
			
						
		}
		catch(PersistenceException e) 
		{
			e.printStackTrace();
			//TODO: Log to file
			throw new AccountException(e.getMessage());
		}
		finally 
		{
			entityManager.close();
		}
		
	}

	@Override
	public boolean deposit(long accountNumber, int pin, double dAmount) throws AccountException 
	{
		boolean flag=true;
		
		
				
		entityManager=JPAUtil.getEntityManager();
		Customer customer=entityManager.find(Customer.class, accountNumber);
		if((customer.getAccountNumber()==accountNumber) && (customer.getPin()==pin))
		{	
			
			
			
			entityManager.getTransaction().begin();
			date = new Date();

			double balance=customer.getBalance();
			balance=balance+dAmount;
			customer.setBalance(balance);
			
			entityManager.merge(customer);
			System.out.println("New Balance after deposit= " + customer.getBalance());
			
			
			/////////////////////////////////////// For Passbook ////////////////////////////////////////////////
				
				transactionDetail="\n"+dateFormat.format(date) +" : "+ accountNumber + "\tdeposited Rs. " + dAmount ;
				passbook.setTransactionDetails(transactionDetail);
				passbook.setCredit(dAmount);
				passbook.setDebit(null);
				passbook.setBalance(balance);
				//passbook.set
			customer.addPassbook(passbook);
				entityManager.merge(passbook);
			flag=true;
			entityManager.getTransaction().commit();
		}
		else
		{ 
			flag=false;
			System.err.println("Invalid Account number or password....");
		}
		
		return flag;
	}

	@Override
	public boolean withdraw(long accountNumber, int pin, double wAmount) throws AccountException 
	{
		boolean flag=true;
		entityManager=JPAUtil.getEntityManager();
		Customer customer=entityManager.find(Customer.class, accountNumber);
		if((customer.getAccountNumber()==accountNumber) && (customer.getPin() == pin) && (customer.getBalance()>wAmount))
		{	
			flag=true;
			
			entityManager.getTransaction().begin();
			date = new Date();

			double balance=customer.getBalance();
			balance=balance - wAmount;
			customer.setBalance(balance);

			entityManager.merge(customer);
			
			////////////////////////// for passbook ////////////////////////////////////
			
	
			
			transactionDetail="\n"+dateFormat.format(date) +" : "+accountNumber + "\t withdrawn Rs. " + wAmount ;
			passbook.setTransactionDetails(transactionDetail);
			passbook.setCredit(null);
			passbook.setDebit(wAmount);
			passbook.setBalance(balance);
			customer.addPassbook(passbook);
			entityManager.merge(passbook);
			
			System.out.println("New Balance after withdrawal= " + customer.getBalance());
			
			entityManager.getTransaction().commit();
		}
		else
		{
			flag=false;
			System.err.println("Invalid Account number or password....");
		}
		return flag;
	}

	@Override
	public boolean fundTransfer(long dAccountNumber, int dpin, long cAccountNumber, double dAmount)
			throws AccountException 
	{
		boolean flag=true;
		entityManager=JPAUtil.getEntityManager();
		Customer customer1=entityManager.find(Customer.class, dAccountNumber);
		if((customer1!=null) && (customer1.getPin() == dpin) && (customer1.getBalance()>dAmount))
		{	
			
			Customer customer2=entityManager.find(Customer.class, cAccountNumber);
			if(customer2!=null)
			{
				flag=true;
				
				//-------Transferrer's transaction-----------------------------------
				entityManager.getTransaction().begin();
				
				date=new Date();
				
				double balance1=customer1.getBalance();
				balance1=balance1 - dAmount;
				customer1.setBalance(balance1);
				entityManager.merge(customer1);
				
			
				
			
				transactionDetail="\n"+dateFormat.format(date) +" : "+dAccountNumber + "\t Transferred Rs. " + dAmount +" to " + cAccountNumber ;
				passbook.setTransactionDetails(transactionDetail);
				passbook.setCredit(null);
				passbook.setDebit(dAmount);
				passbook.setBalance(balance1);
				customer1.addPassbook(passbook);
				entityManager.merge(passbook);
				
				
				//-------Benificiary's transaction-----------------------------------
				
				
				double balance2=customer2.getBalance();
				balance2=balance2 + dAmount;
				customer2.setBalance(balance2);	
				entityManager.merge(customer2);
				
				
				

				transactionDetail="\n" + dateFormat.format(date) +" : "+cAccountNumber + "\t Credited by Rs. " + dAmount +" on Fund Transfer from A/C " + dAccountNumber;
				passbook.setTransactionDetails(transactionDetail);
				passbook.setCredit(dAmount);
				passbook.setDebit(null);
				passbook.setBalance(balance2);
				customer2.addPassbook(passbook);
				entityManager.merge(passbook);
				entityManager.getTransaction().commit();
				
			}
			else
			{
				flag=false;
				System.err.println("Beneficiary's account does not exists");
			}
			
			
		}
		else
		{
			flag=false;
			System.err.println("Invalid Account number or password or transfering amount is not sufficient....");
		}
		return flag;
		
	}

	@Override
	public List<Passbook> printTansaction(long accountNumber, int pin) throws AccountException {
		
		try
		{
			entityManager=JPAUtil.getEntityManager();
			Query query=entityManager.createQuery("from Passbook where accountNumber=?");
			query.setParameter(1, accountNumber);
			List<Passbook> passbookList=query.getResultList();
			return passbookList;
		}
		catch(PersistenceException e)
		{
			//TODO: Log to file
			e.printStackTrace();
			throw new AccountException(e.getMessage());
		}
		finally
		{
			entityManager.close();
		}
	}

}

