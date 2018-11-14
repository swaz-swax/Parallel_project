package com.capg.project.bank.bean;




import java.util.ArrayList;
import java.util.List;



import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;




@Entity
@Table(name="account")
public class Customer
{
	@Id
	 private long accountNumber;
	 private String name;
	 private String phoneNumber;
	 private String panNumber;
	 private String address;
	 private String governmentID;
	 private int pin;
	 private double balance;
	 
	 @OneToMany(mappedBy="customer",cascade=CascadeType.ALL)
		private List<Passbook> passbookList = new ArrayList<Passbook>();		//Initialization required to avoid NullPointerException
	 
		public Customer() {
			super();
		}


		public Customer(long accountNumber, String name, String phoneNumber, String panNumber,
			String address, String governmentID, int pin, double balance) 
	{
		super();
		this.accountNumber = accountNumber;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.panNumber = panNumber;
		this.address = address;
		this.governmentID = governmentID;
		this.pin = pin;
		this.balance = balance;
	}
	


	public long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPanNumber() {
		return panNumber;
	}
	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getGovernmentID() {
		return governmentID;
	}
	public void setGovernmentID(String governmentID) {
		this.governmentID = governmentID;
	}
	
	public int getPin() {
		return pin;
	}
	public void setPin(int pin) {
		this.pin = pin;
	}
	
	
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	
	public List<Passbook> getPassbookList() {
		return passbookList;
	}


	public void setPassbookList(List<Passbook> passbookList) {
		this.passbookList = passbookList;
	}


	public void addPassbook(Passbook passbook) {
		passbook.setCustomer(this);			//this will avoid nested cascade
		//this.getPassbookList().add(passbook);
	}

	@Override
	public String toString() {
		return "Customer [Account Number="+ accountNumber + ", Name=" + name + "\n Phone Number=" + phoneNumber
				+ ", PAN=" + panNumber + "\n Address=" + address
				+ ", Aadhar Number=" + governmentID + "\n Balance="+ balance + "]";
	}
	 
	 

}