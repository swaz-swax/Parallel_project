package com.capg.project.bank.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Passbook 
{
	@Id
	@GeneratedValue( strategy= GenerationType.IDENTITY )
	private int transactionId;
	
	private long pAccountNumber;
	private String transactionDetails;
	/*@ManyToOne
	@JoinColumn(name="accountNumber")//in the owning side
	private Customer customer;*/
	

	public Passbook() 
	{
		super();
	}


	public long getpAccountNumber() {
		return pAccountNumber;
	}


	public void setpAccountNumber(long pAccountNumber) {
		this.pAccountNumber = pAccountNumber;
	}


	public String getTransactionDetails() {
		return transactionDetails;
	}


	public void setTransactionDetails(String transactionDetails) {
		this.transactionDetails = transactionDetails;
	}


	@Override
	public String toString() {
		return "Passbook [pAccountNumber=" + pAccountNumber + ", transactionDetails=" + transactionDetails + "]";
	}


	public Passbook(long pAccountNumber, String transactionDetails) {
		super();
		this.pAccountNumber = pAccountNumber;
		this.transactionDetails = transactionDetails;
	}

	

	
	
	
}