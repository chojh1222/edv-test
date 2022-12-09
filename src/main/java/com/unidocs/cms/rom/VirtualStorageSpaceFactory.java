package com.unidocs.cms.rom;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.storage.LocalStorageSpaceProxy;
import com.unidocs.cms.storage.StorageSpace;
import com.unidocs.cms.storage.StorageSpaceProvider;
import com.unidocs.cms.storage.StorageSpaceProviderImpl;
import com.unidocs.cms.storage.VirtualStorageSpace;
import com.unidocs.cms.storage.VirtualStorageSpaceImpl;
import com.unidocs.cms.storage.impl.RemoteStorageSpaceImpl;
import com.unidocs.cms.storage.impl.RemoteStorageSpaceImplHome;
import hcm.lib.misc.StringUtil;
import java.rmi.RemoteException;
import java.util.Hashtable;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class VirtualStorageSpaceFactory {
	public static VirtualStorageSpaceFactory vss;
	private static final String REMOTE_EJB_JNDI_PREFIX = StringUtil.trim("ejb/");
	private static Context ctx = null;
	private static RemoteStorageSpaceImplHome theRemoteStorageSpaceImplHome = null;

	public static VirtualStorageSpaceFactory getInstance() {
		if (vss == null) {
			vss = new VirtualStorageSpaceFactory();
		}

		return vss;
	}

	public synchronized VirtualStorageSpace getVirtualStorageSpace(com.unidocs.cms.info.Context context)
			throws RepositoryException {
		try {
			StorageSpace rootStorageSpace = new LocalStorageSpaceProxy(context, "rootStorageSpace");
			StorageSpaceProvider storageSpaceProvider = new StorageSpaceProviderImpl(context, rootStorageSpace);
			VirtualStorageSpace storage = new VirtualStorageSpaceImpl(context, "#repository", storageSpaceProvider);
			return storage;
		} catch (EJBException var5) {
			throw new RepositoryException(var5);
		}
	}

	private static synchronized Context getContext() throws NamingException {
		if (ctx == null) {
			Hashtable env = new Hashtable();
			env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
			env.put("java.naming.provider.url", "localhost");
			ctx = new InitialContext(env);
		}

		return ctx;
	}

	private static synchronized RemoteStorageSpaceImplHome fetchRemoteStorageSpaceImplHome() throws NamingException {
		if (theRemoteStorageSpaceImplHome == null) {
			theRemoteStorageSpaceImplHome = (RemoteStorageSpaceImplHome) getContext()
					.lookup(REMOTE_EJB_JNDI_PREFIX + "RemoteStorageSpaceImpl");
		}

		return theRemoteStorageSpaceImplHome;
	}

	public static RemoteStorageSpaceImpl createRemoteStorageSpaceImpl() throws EJBException, RemoteException {
		RemoteStorageSpaceImpl result = null;

		try {
			result = fetchRemoteStorageSpaceImplHome().create();
			return result;
		} catch (NamingException var2) {
			throw new EJBException(
					"VirtualStorageSpaceFactory.createRemoteStorageSpaceImpl() : fails to lookup with JNDI", var2);
		} catch (CreateException var3) {
			throw new EJBException(
					"VirtualStorageSpaceFactory.createRemoteStorageSpaceImpl() : fails to create new EJB interface object",
					var3);
		}
	}
}