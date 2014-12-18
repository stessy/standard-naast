package com.standardnaast.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerFactoryHelper {

	private static EntityManagerFactory factory;

	static {
		try {
			EntityManagerFactoryHelper.factory = Persistence
					.createEntityManagerFactory("StandardNaastPeristenceUnit");
		} catch (final ExceptionInInitializerError e) {
			throw e;
		}
	}

	public static EntityManagerFactory getFactory() {
		return EntityManagerFactoryHelper.factory;
	}

}
