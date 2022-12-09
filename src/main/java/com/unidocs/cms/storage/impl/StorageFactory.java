package com.unidocs.cms.storage.impl;

import com.unidocs.cms.internal.common.EngineProperty;
import com.unidocs.cms.internal.common.Logger;
import hcm.lib.misc.StringUtil;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class StorageFactory {
	private static final String LOCAL_EJB_JNDI_PREFIX = StringUtil
			.trim(EngineProperty.getProperty("jndi.prefix.ejb.local"));
	private static Context ctx = null;
	private static StorageSpaceLocalHome theStorageSpaceLocalHome = null;
	private static SessionManagerLocalHome theSessionManagerLocalHome = null;
	private static TransactionManagerLocalHome theTransactionManagerLocalHome = null;

	private static synchronized Context getContext() throws NamingException {
		if (ctx == null) {
			ctx = new InitialContext();
		}

		return ctx;
	}

	private static synchronized StorageSpaceLocalHome fetchStorageSpaceLocalHome() throws NamingException {
		if (theStorageSpaceLocalHome == null) {
			theStorageSpaceLocalHome = (StorageSpaceLocalHome) getContext()
					.lookup(LOCAL_EJB_JNDI_PREFIX + "StorageSpaceLocal");
		}

		return theStorageSpaceLocalHome;
	}

	private static synchronized SessionManagerLocalHome fetchSessionManagerLocalHome() throws NamingException {
		if (theSessionManagerLocalHome == null) {
			Logger.debug(Logger.LOG_RANGE_ENGINE, "name : " + LOCAL_EJB_JNDI_PREFIX + "SessionManagerLocal");
			theSessionManagerLocalHome = (SessionManagerLocalHome) getContext()
					.lookup(LOCAL_EJB_JNDI_PREFIX + "SessionManagerLocal");
		}

		return theSessionManagerLocalHome;
	}

	private static synchronized TransactionManagerLocalHome fetchTransactionManagerLocalHome() throws NamingException {
		if (theTransactionManagerLocalHome == null) {
			theTransactionManagerLocalHome = (TransactionManagerLocalHome) getContext()
					.lookup(LOCAL_EJB_JNDI_PREFIX + "TransactionManagerLocal");
		}

		return theTransactionManagerLocalHome;
	}

	public static StorageSpaceLocal createStorageSpaceLocal() throws EJBException {
		StorageSpaceLocal result = null;

		try {
			result = fetchStorageSpaceLocalHome().create();
			return result;
		} catch (NamingException var2) {
			Logger.fatal(Logger.LOG_RANGE_ENGINE,
					"StorageFactory.createStorageSpaceLocal() : fails to lookup with JNDI", var2);
			throw new EJBException("StorageFactory.createStorageSpaceLocal() : fails to lookup with JNDI", var2);
		} catch (CreateException var3) {
			Logger.fatal(Logger.LOG_RANGE_ENGINE,
					"StorageFactory.createStorageSpaceLocal() : fails to create new EJB interface object", var3);
			throw new EJBException(
					"StorageFactory.createStorageSpaceLocal() : fails to create new EJB interface object", var3);
		}
	}

	public static SessionManagerLocal createSessionManagerLocal() throws EJBException {
		SessionManagerLocal result = null;

		try {
			result = fetchSessionManagerLocalHome().create();
			return result;
		} catch (NamingException var2) {
			Logger.fatal(Logger.LOG_RANGE_ENGINE,
					"StorageFactory.createSessionManagerLocal() : fails to lookup with JNDI", var2);
			throw new EJBException("StorageFactory.createSessionManagerLocal() : fails to lookup with JNDI", var2);
		} catch (CreateException var3) {
			Logger.fatal(Logger.LOG_RANGE_ENGINE,
					"StorageFactory.createSessionManagerLocal() : fails to create new EJB interface object", var3);
			throw new EJBException(
					"StorageFactory.createSessionManagerLocal() : fails to create new EJB interface object", var3);
		}
	}

	public static TransactionManagerLocal createTransactionManagerLocal() throws EJBException {
		TransactionManagerLocal result = null;

		try {
			result = fetchTransactionManagerLocalHome().create();
			return result;
		} catch (NamingException var2) {
			Logger.fatal(Logger.LOG_RANGE_ENGINE,
					"StorageFactory.createTransactionManagerLocal() : fails to lookup with JNDI", var2);
			throw new EJBException("StorageFactory.createTransactionManagerLocal() : fails to lookup with JNDI", var2);
		} catch (CreateException var3) {
			Logger.fatal(Logger.LOG_RANGE_ENGINE,
					"StorageFactory.createTransactionManagerLocal() : fails to create new EJB interface object", var3);
			throw new EJBException(
					"StorageFactory.createTransactionManagerLocal() : fails to create new EJB interface object", var3);
		}
	}
}