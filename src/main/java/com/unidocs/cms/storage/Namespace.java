package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;

public interface Namespace {
	void registerNamespace(String paramString1, String paramString2) throws RepositoryException;

	void unRegisterNamespace(String paramString) throws RepositoryException;

	void unRegisterNamespace() throws RepositoryException;

	String[] getPrefixes() throws RepositoryException;

	String[] getURIs() throws RepositoryException;

	String getURI(String paramString) throws RepositoryException;

	String getPrefix(String paramString) throws RepositoryException;
}