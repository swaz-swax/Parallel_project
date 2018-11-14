package com.capg.project.bank.exception;

public class AccountException extends Exception{
	private String message;
	public AccountException() {
		super();
	}



	public AccountException(String message) {
		super();
		this.message = message;
	}



	public AccountException(Exception e) {
		e.getMessage();
	}
	

}
