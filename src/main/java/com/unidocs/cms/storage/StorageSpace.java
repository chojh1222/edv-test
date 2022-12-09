package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.NodeInfo;

public interface StorageSpace extends StorageSpaceReader, StorageSpaceWriter, QueryExecutor, NamespaceManager {
	String getName();

	void saveNode(String paramString) throws ItemNotFoundException, RepositoryException;

	void saveProperty(String paramString) throws ItemNotFoundException, RepositoryException;

	void saveNode(String paramString, boolean paramBoolean) throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getTransientNodeInfo(String paramString) throws RepositoryException;

	void saveSession() throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getTransientNodeInfo() throws RepositoryException;

	boolean prepare() throws RepositoryException;

	boolean commit() throws RepositoryException;

	boolean rollback() throws RepositoryException;

	boolean forget() throws RepositoryException;

	String getNodePath(String paramString) throws ItemNotFoundException, RepositoryException;
}