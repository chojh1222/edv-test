package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;

public interface StorageSpaceReader {
	NodeInfo getRootNode() throws ItemNotFoundException, RepositoryException;

	boolean hasNode(String var1) throws ItemNotFoundException, RepositoryException;

	boolean hasChildNode(String var1) throws ItemNotFoundException, RepositoryException;

	long getChildNodesLength(String var1) throws ItemNotFoundException, RepositoryException;

	long getChildNodesLength(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo getNode(String var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getParentNode(String var1) throws ItemNotFoundException, RepositoryException;

	boolean hasNode(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo getChildNode(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getChildNodes(String var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getChildNodes(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo getFirstChildNode(String var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getLastChildNode(String var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getNextNode(String var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getPrevNode(String var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getProperty(String var1) throws ItemNotFoundException, RepositoryException;

	boolean hasProperty(String var1) throws ItemNotFoundException, RepositoryException;

	boolean hasProperty(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	long getChildPropertiesLength(String var1) throws ItemNotFoundException, RepositoryException;

	long getChildPropertiesLength(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(String var1, long var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(String var1, String var2, long var3)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(String var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(String var1, String var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(String var1, String[] var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getFirstChildProperty(String var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getLastChildProperty(String var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getNextProperty(String var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getPrevProperty(String var1) throws ItemNotFoundException, RepositoryException;

	long getNodeIndexof(String var1) throws ItemNotFoundException, RepositoryException;

	long getPropertyIndexof(String var1) throws ItemNotFoundException, RepositoryException;

	CachedNodeInfo getCachedNode(String var1) throws ItemNotFoundException, RepositoryException;
}