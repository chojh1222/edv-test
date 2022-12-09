package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.ValueFormatException;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;
import com.unidocs.cms.info.ReferenceInfo;
import com.unidocs.cms.info.RowInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;

public interface VirtualStorageSpace {
	String getName();

	StorageSpaceProvider getStorageSpaceProvider();

	void setStorageSpaceProvider(StorageSpaceProvider var1);

	ItemInfo getNextSibling(ItemInfo var1) throws ItemNotFoundException, RepositoryException;

	ItemInfo getPreviousSibling(ItemInfo var1) throws ItemNotFoundException, RepositoryException;

	ItemInfo getFirstChild(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	ItemInfo getLastChild(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	long getChildLength(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getRootNode() throws ItemNotFoundException, RepositoryException;

	boolean hasNode(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	boolean hasChildNode(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	long getChildNodesLength(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	long getChildNodesLength(NodeInfo var1, String var2) throws ItemNotFoundException, RepositoryException;

	boolean hasNode(NodeInfo var1, String var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo getChildNode(NodeInfo var1, String var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getChildNodes(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getChildNodes(NodeInfo var1, String var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo getFirstChildNode(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getLastChildNode(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getNextNode(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getPrevNode(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getParentNode(ItemInfo var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getProperty(PropertyInfo var1) throws ItemNotFoundException, RepositoryException;

	long getChildPropertiesLength(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	long getChildPropertiesLength(NodeInfo var1, String var2) throws ItemNotFoundException, RepositoryException;

	boolean hasProperty(PropertyInfo var1) throws ItemNotFoundException, RepositoryException;

	boolean hasProperty(NodeInfo var1, String var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(NodeInfo var1, String var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(NodeInfo var1, String[] var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(NodeInfo var1, String var2, long var3)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo getChildProperty(NodeInfo var1, long var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo[] getChildProperties(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getFirstChildProperty(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getLastChildProperty(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getNextProperty(PropertyInfo var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getPrevProperty(PropertyInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo addChildNode(NodeInfo var1, String var2, String var3) throws ItemNotFoundException, RepositoryException;

	NodeInfo moveNode(NodeInfo var1, NodeInfo var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo deleteNode(NodeInfo var1, boolean var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo addChildProperty(NodeInfo var1, String var2, String var3, String var4, int var5)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo setPropertyValue(PropertyInfo var1, String var2, int var3)
			throws ValueFormatException, IllegalStateException, RepositoryException;

	PropertyInfo moveProperty(NodeInfo var1, PropertyInfo var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo deleteProperty(PropertyInfo var1) throws ItemNotFoundException, RepositoryException;

	long getPropertyIndexof(PropertyInfo var1) throws ItemNotFoundException, RepositoryException;

	long getNodeIndexof(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getNodeByUUID(String var1) throws ItemNotFoundException, RepositoryException;

	PropertyInfo getPropertyByUUID(String var1) throws ItemNotFoundException, RepositoryException;

	ItemInfo getItemByUUID(String var1) throws ItemNotFoundException, RepositoryException;

	NodeInfo getNodeByReference(ReferenceInfo var1) throws ItemNotFoundException, RepositoryException;

	RowInfo[] executeQuery(String var1, String[] var2, String[] var3, int[] var4) throws RepositoryException;

	ItemInfo[] executeQuery4XPath(String var1) throws RepositoryException;

	void executeUpdate(String var1) throws RepositoryException;

	void registerNamespace(String var1, String var2) throws RepositoryException;

	void unRegisterNamespace(String var1) throws RepositoryException;

	void unRegisterNamespace() throws RepositoryException;

	String[] getPrefixes() throws RepositoryException;

	String[] getURIs() throws RepositoryException;

	String getURI(String var1) throws RepositoryException;

	String getPrefix(String var1) throws RepositoryException;

	void saveNode(NodeInfo var1) throws ItemNotFoundException, RepositoryException;

	void saveProperty(PropertyInfo var1) throws ItemNotFoundException, RepositoryException;

	void saveNode(NodeInfo var1, boolean var2) throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getTransientNodeInfo(NodeInfo var1) throws RepositoryException;

	void saveSession() throws ItemNotFoundException, RepositoryException;

	NodeInfo[] getTransientNodeInfo() throws RepositoryException;

	boolean prepare() throws RepositoryException;

	boolean commit() throws RepositoryException;

	boolean rollback() throws RepositoryException;

	boolean forget() throws RepositoryException;

	boolean canLoadfor(String var1, Class var2);

	ItemInfo load(ItemInfo var1, String var2, Class var3) throws RepositoryException;

	ItemInfo save(ItemInfo var1, String var2, NodeInfo var3) throws RepositoryException;

	ItemInfo build(CachedNodeInfo var1, String var2, Class var3) throws RepositoryException;

	String getNodePathByUUID(String var1) throws ItemNotFoundException, RepositoryException;

	CachedNodeInfo getCachedNode(String var1) throws ItemNotFoundException, RepositoryException;
}