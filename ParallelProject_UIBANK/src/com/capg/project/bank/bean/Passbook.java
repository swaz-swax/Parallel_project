package com.capg.project.bank.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;





@Entity
@Table(name="transaction")
public class Passbook 
{
	@Id
	@GeneratedValue( strategy= GenerationType.IDENTITY )
	private Integer transactionId;
	private String transactionDetails;
	
	@ManyToOne
	@JoinColumn(name="accountNumber")//in the owning side
	private Customer customer;
	
	private Double debit;
	private Double credit;
	private Double balance;
	

	public Passbook() 
	{
		super();
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	

	public String getTransactionDetails() {
		return transactionDetails;
	}

	public void setTransactionDetails(String transactionDetails) {
		this.transactionDetails = transactionDetails;
	}

	

	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "Passbook [transactionId=" + transactionId
				+ ", transactionDetails=" + transactionDetails + ", account="
				+ customer + ", debit=" + debit + ", credit=" + credit
				+ ", balance=" + balance + "]";
	}



	

	

	
	
}