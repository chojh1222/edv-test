package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.Context;

public interface LocalNamespaceManager {
	void registerNamespace(Context var1, String var2, String var3) throws RepositoryException;

	void unRegisterNamespace(Context var1, String var2) throws RepositoryException;

	void unRegisterNamespace(Context var1) throws RepositoryException;

	String[] getPrefixes(Context var1) throws RepositoryException;

	String[] getURIs(Context var1) throws RepositoryException;

	String getURI(Context var1, String var2) throws RepositoryException;

	String getPrefix(Context var1, String var2) throws RepositoryException;
}