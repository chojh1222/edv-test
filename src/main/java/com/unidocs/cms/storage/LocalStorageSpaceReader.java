package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.Context;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;

public interface LocalStorageSpaceReader {
	NodeInfo getRootNode(Context paramContext) throws ItemNotFoundException, RepositoryException;

	boolean hasNode(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	boolean hasChildNode(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	long getChildNodesLength(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	long getChildNodesLength(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getNode(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	NodeInfo getParentNode(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	boolean hasNode(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getChildNode(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getChildNode(Context paramContext, String paramString, long paramLong)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getChildNode(Context paramContext, String paramString1, String paramString2, long paramLong)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getChildNodes(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getChildNodes(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getFirstChildNode(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getLastChildNode(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getNextNode(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	NodeInfo getPrevNode(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getProperty(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	boolean hasProperty(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	boolean hasProperty(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	long getChildPropertiesLength(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	long getChildPropertiesLength(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(Context paramContext, String paramString, long paramLong)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(Context paramContext, String paramString1, String paramString2, long paramLong)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(Context paramContext, String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(Context paramContext, String paramString, String[] paramArrayOfString)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getFirstChildProperty(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getLastChildProperty(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getNextProperty(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getPrevProperty(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;

	long getNodeIndexof(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	long getPropertyIndexof(Context paramContext, String paramString) throws ItemNotFoundException, RepositoryException;

	NodeInfo getFirstChildNode(Context paramContext, String paramString, String[] paramArrayOfString)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo getNextNode(Context paramContext, String paramString, String[] paramArrayOfString)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getFirstChildProperty(Context paramContext, String paramString, String[] paramArrayOfString)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getNextProperty(Context paramContext, String paramString, String[] paramArrayOfString)
			throws ItemNotFoundException, RepositoryException;

	CachedNodeInfo getCachedNode(Context paramContext, String paramString)
			throws ItemNotFoundException, RepositoryException;
}