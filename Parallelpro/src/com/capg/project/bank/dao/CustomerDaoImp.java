package com.capg.project.bank.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import java.util.List;
import java.util.Map;

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
	public boolean createAccount(long accountNumber, Customer c) throws AccountException {
		try
		{
			entityManager=JPAUtil.getEntityManager();
			EntityTransaction transaction=entityManager.getTransaction();
			transaction.begin();
			c.setAccountNumber(accountNumber);
			entityManager.persist(c);
			transaction.commit();
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
		return false;
	}

	@Override
	public Customer showBalance(long accountNumber, int pin) throws AccountException {
		try
		{
			entityManager=JPAUtil.getEntityManager();
			Customer customer=entityManager.find(Customer.class, accountNumber);
			if((customer.getAccountNumber()==accountNumber) && (customer.getPin() == pin))
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
	
				passbook.setpAccountNumber(accountNumber);
				transactionDetail="\n"+dateFormat.format(date) +" : "+ accountNumber + "\tdeposited Rs. " + dAmount + " \t\t\t\t\t\t" + balance ;
				passbook.setTransactionDetails(transactionDetail);
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
			
			passbook.setpAccountNumber(accountNumber);
			
			transactionDetail="\n"+dateFormat.format(date) +" : "+accountNumber + "\t withdrawn Rs. " + wAmount + " \t\t\t\t\t\t" + balance ;
			passbook.setTransactionDetails(transactionDetail);
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
				
			
				
				passbook.setpAccountNumber(dAccountNumber);
				transactionDetail="\n"+dateFormat.format(date) +" : "+dAccountNumber + "\t Transferred Rs. " + dAmount +" to " + cAccountNumber + "\t\t\t\t\t" + balance1 ;
				passbook.setTransactionDetails(transactionDetail);
				entityManager.merge(passbook);
				
				
				//-------Benificiary's transaction-----------------------------------
				
				
				double balance2=customer2.getBalance();
				balance2=balance2 + dAmount;
				customer2.setBalance(balance2);	
				entityManager.merge(customer2);
				
				
				
				passbook.setpAccountNumber(cAccountNumber);
				transactionDetail="\n" + dateFormat.format(date) +" : "+cAccountNumber + "\t Credited by Rs. " + dAmount +" on Fund Transfer from A/C " + dAccountNumber + " \t\t" + balance2 ;
				passbook.setTransactionDetails(transactionDetail);
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
			Query query=entityManager.createQuery("from Passbook where pAccountNumber=?");
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

	/*static Map<Long, Customer> hashMap=new HashMap<Long, Customer>();
	//static Map<Long, StringBuffer> transactionMap=new HashMap<Long, StringBuffer>();
	
	static List<String> tList=new ArrayList<String>();
	
	static List<Passbook> transList=new ArrayList<Passbook>();
	Customer bean=new Customer();
	Passbook pb=new Passbook();
	
	//StringBuffer sb=new StringBuffer();
	String str=null;
	
	@Override
	public boolean createAccount(long accountNumber, Customer bean) 
	{
		long a  = bean.getAccountNumber();
		hashMap.put(a, bean);
		
		//transactionMap.put(a, sb.append(bean));
		return true;
		
	}

	@Override
	public Customer showBalance(long accountNumber, int pin) 
	{
		Customer cust=null;
		for(Customer c:hashMap.values())
		{
			if((c.getAccountNumber() == accountNumber) && (c.getPin()==pin))
			{
				cust=c;
				System.out.println("Balance = " + c.getBalance());
			}
		}
		return cust;
	}
	

	@Override
	public boolean deposit(long accountNumber, int pin, double dAmount) 
	{	
		boolean flag=false;
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		for(Customer c:hashMap.values())
		{
		if((c.getAccountNumber() == accountNumber) && (c.getPin()==pin))
		{	
			flag=true;
			pb.setpAccountNumber(accountNumber);
		
			double bal=c.getBalance();
			System.out.println("Previous Balance = "+ bal );
			bal=bal+dAmount;
			c.setBalance(bal);
			
			str="\n"+dateFormat.format(date) +" : "+ accountNumber + "\tdeposited Rs. " + dAmount + " \t\t\t\t\t\t" + bal ;
			pb.setTransactionDetails(str);
			//transactionMap.put(accountNumber, sb.append(str));
			
			//tList.add(dateFormat.format(date) +" : "+ accountNumber + "\tdeposited Rs. " + dAmount + " \t\t\t\t\t\t" + bal);
			System.out.println("New Balance = " + c.getBalance());
			transList.add(pb);
		}
		}
		System.out.println("hashmap is " + hashMap);
		return flag;			
	}

	@Override
	public boolean withdraw(long accountNumber, int pin, double wAmount) 
	{
		boolean flag= false;
		Customer cust = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		for(Customer c:hashMap.values()){
		if((c.getAccountNumber() == accountNumber) && (c.getPin()==pin) && (c.getBalance()>wAmount))
		{
			
			flag=true;
			pb.setpAccountNumber(accountNumber);
			
			double bal=c.getBalance();
			System.out.println("Previous Balance = "+ bal );

				bal=bal-wAmount;
				System.out.println("New Balance = "+bal);
				c.setBalance(bal);
				
				str="\n"+dateFormat.format(date) +" : "+accountNumber + "\twithdrawn Rs. " + wAmount + " \t\t\t\t\t\t" + bal ;
				pb.setTransactionDetails(str);
				//transactionMap.put(accountNumber, sb.append(str));	
			//tList.add(dateFormat.format(date) +" : "+accountNumber + "\twithdrawn Rs. " + wAmount + " \t\t\t\t\t\t" + bal);
			transList.add(pb);
			cust=c;
		}
		}
			return flag;	
	}

	@Override
	public boolean fundTransfer(long dAccountNumber, int dpin,
			long cAccountNumber, double dAmount) 
	{
		boolean flag=false;
		double deb=0.0;
		double cred=0.0;
		
		for(Customer c:hashMap.values())
		{
			if((c.getAccountNumber() == dAccountNumber) && (c.getPin()==dpin))
			{
				flag=true;
				pb.setpAccountNumber(dAccountNumber);
	
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();

				deb=c.getBalance();
				deb=deb-dAmount;
				c.setBalance(deb);
				
				str="\n"+dateFormat.format(date) +" : "+dAccountNumber + "\tTransferred Rs. " + dAmount +" to " + cAccountNumber + "\t\t\t\t\t" + deb ;
				pb.setTransactionDetails(str);
				transList.add(pb);
				
				System.out.println(deb+" bal after (debit) fund transfer from" + dAccountNumber);
				
				//transactionMap.put(dAccountNumber, sb.append(str));	
				//tList.add(dateFormat.format(date) +" : "+dAccountNumber + "\tTransferred Rs. " + dAmount +" to " + cAccountNumber + "\t\t\t\t\t" + deb);
				
				for(Customer c1:hashMap.values())
				{
					if(c1.getAccountNumber() == cAccountNumber)
					{
						flag=true;
						pb.setpAccountNumber(cAccountNumber);
						
						DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date date1 = new Date();
	
						cred=c1.getBalance();
						cred=cred+dAmount;
						c1.setBalance(cred);	
						System.out.println(cred+" bal after transaction(credited) to" + cAccountNumber);
											
						str="\n" + dateFormat1.format(date1) +" : "+cAccountNumber + "\tCredited by Rs. " + dAmount +" on Fund Transfer from A/C " + dAccountNumber + " \t\t" + cred ;
						pb.setTransactionDetails(str);
						transList.add(pb);
						//transactionMap.put(cAccountNumber, sb.append(str));
						//tList.add(dateFormat1.format(date1) +" : "+cAccountNumber + "\tCredited by Rs. " + dAmount+" on Fund Transfer from A/C " + dAccountNumber + " \t\t" + cred);
						break;
					}
					else
					{
						System.out.println("Beneficiary's account doesn't exist...");
						flag=false;
						
					}
					
					
				}
			}
		}
		
		return flag;
	}

	@Override
	public boolean printTansaction(long accountNumber, int pin) 
	{						
					Iterator<String> itr = tList.iterator();
					while (itr.hasNext()) 
					{
						Object element = itr.next();
						System.out.println(element + "\n");
						
						
					}
	return true;
	
	}
}

	@Override
	public Passbook printTansaction(long accountNumber, int pin) 
	{				//Passbook transaction=null;
	System.err.println(transList);
	for(Passbook p:transList)
	{
		if(p.getpAccountNumber() == accountNumber)
		{
			//transaction=p;
			System.out.println(p.getTransactionDetails());
		}
	}
	return null;
	
	
	}*/
	
	
	
}
/*public boolean printTansaction(long accountNumber, int pin) 
{				
	System.out.println("\n\n"+ transactionMap +"\n\n");
	
				for (Long ac : transactionMap.keySet()) 
				{
					if(ac == accountNumber)
					{
						System.out.println(transactionMap.values());
					}
					if(transactionMap.containsKey(accountNumber))
					{
						System.out.println(transactionMap.values());
					}
				}
return true;

}
}*/

