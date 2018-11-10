package com.capgemini.jpa.utility;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class JPAUtil 
{

	public static EntityManager getEntityManager() 
	{
		
		return Persistence.createEntityManagerFactory("Parallelpro").createEntityManager();
	}
	
}
