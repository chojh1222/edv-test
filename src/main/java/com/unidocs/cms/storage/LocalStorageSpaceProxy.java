package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.Context;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;
import com.unidocs.cms.info.RowInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;
import com.unidocs.cms.storage.impl.SessionManagerLocal;
import com.unidocs.cms.storage.impl.StorageFactory;
import com.unidocs.cms.storage.impl.TransactionManagerLocal;
import java.util.Iterator;

public class LocalStorageSpaceProxy implements StorageSpace {
	private Context context;
	private String name;

	public LocalStorageSpaceProxy(Context context, String name) {
		this.context = context;
		this.name = name;
	}

	private void checkConnection() throws RepositoryException {
	}

	private boolean isConnected() {
		return true;
	}

	public String getName() {
		return this.name;
	}

	public LocalStorageSpace getStorage() {
		return StorageFactory.createStorageSpaceLocal();
	}

	public NodeInfo getRootNode() throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getRootNode(this.context);
		nodeInfo.setStorageName(this.getName());
		return nodeInfo;
	}

	public boolean hasNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().hasNode(this.context, nodeId);
	}

	public boolean hasChildNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().hasChildNode(this.context, nodeId);
	}

	public long getChildNodesLength(String parentId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getChildNodesLength(this.context, parentId);
	}

	public long getChildNodesLength(String parentId, String qname) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getChildNodesLength(this.context, parentId, qname);
	}

	public NodeInfo getNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getNode(this.context, nodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo getParentNode(String itemId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getParentNode(this.context, itemId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public boolean hasNode(String parentNodeId, String qname) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().hasNode(this.context, parentNodeId, qname);
	}

	public NodeInfo getChildNode(String parentNodeId, String qname) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getChildNode(this.context, parentNodeId, qname);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo getChildNode(String parentNodeId, long index) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getChildNode(this.context, parentNodeId, index);
	}

	public NodeInfo getChildNode(String parentNodeId, String qname, long index)
			throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getChildNode(this.context, parentNodeId, qname, index);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo[] getChildNodes(String parentNodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo[] nodeInfo = this.getStorage().getChildNodes(this.context, parentNodeId);

		for (int i = 0; i < nodeInfo.length; ++i) {
			nodeInfo[i].setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo[] getChildNodes(String parentNodeId, String qname)
			throws ItemNotFoundException, RepositoryException {
		NodeInfo[] nodeInfo = this.getStorage().getChildNodes(this.context, parentNodeId, qname);

		for (int i = 0; i < nodeInfo.length; ++i) {
			nodeInfo[i].setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo getFirstChildNode(String parentNodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getFirstChildNode(this.context, parentNodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo getLastChildNode(String parentNodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getLastChildNode(this.context, parentNodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo getNextNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getNextNode(this.context, nodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo getPrevNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().getPrevNode(this.context, nodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public PropertyInfo getProperty(String propertyId) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getProperty(this.context, propertyId);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public boolean hasProperty(String nodeId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().hasProperty(this.context, nodeId);
	}

	public boolean hasProperty(String nodeId, String qname) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().hasProperty(this.context, nodeId, qname);
	}

	public long getChildPropertiesLength(String parentId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getChildPropertiesLength(this.context, parentId);
	}

	public long getChildPropertiesLength(String parentId, String qname)
			throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getChildPropertiesLength(this.context, parentId, qname);
	}

	public PropertyInfo getChildProperty(String nodeId, String qname)
			throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getChildProperty(this.context, nodeId, qname);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo getChildProperty(String nodeId, long index) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getChildProperty(this.context, nodeId, index);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo getChildProperty(String nodeId, String qname, long index)
			throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getChildProperty(this.context, nodeId, qname, index);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo[] getChildProperties(String propertyId) throws ItemNotFoundException, RepositoryException {
		PropertyInfo[] propertyInfo = this.getStorage().getChildProperties(this.context, propertyId);

		for (int i = 0; i < propertyInfo.length; ++i) {
			propertyInfo[i].setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo[] getChildProperties(String propertyId, String qname)
			throws ItemNotFoundException, RepositoryException {
		PropertyInfo[] propertyInfo = this.getStorage().getChildProperties(this.context, propertyId, qname);

		for (int i = 0; i < propertyInfo.length; ++i) {
			propertyInfo[i].setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo[] getChildProperties(String propertyId, String[] qnames)
			throws ItemNotFoundException, RepositoryException {
		PropertyInfo[] propertyInfo = this.getStorage().getChildProperties(this.context, propertyId, qnames);

		for (int i = 0; i < propertyInfo.length; ++i) {
			propertyInfo[i].setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo getFirstChildProperty(String parentId) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getFirstChildProperty(this.context, parentId);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo getLastChildProperty(String parentId) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getLastChildProperty(this.context, parentId);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo getNextProperty(String propertyId) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getNextProperty(this.context, propertyId);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo getPrevProperty(String propertyId) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().getPrevProperty(this.context, propertyId);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public NodeInfo addChildNode(String parentNodeId, String childNodeId, String qname)
			throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().addChildNode(this.context, parentNodeId, childNodeId, qname);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo moveNode(String parentNodeId, String nodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().moveNode(this.context, parentNodeId, nodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public NodeInfo deleteNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = this.getStorage().deleteNode(this.context, nodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		return nodeInfo;
	}

	public PropertyInfo addChildProperty(String parentNodeId, String propertyId, String qname, String value,
			int propertyType) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().addChildProperty(this.context, parentNodeId, propertyId, qname,
				value, propertyType);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo setPropertyValue(String propertyId, String value, int propertyTye)
			throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().setPropertyValue(this.context, propertyId, value, propertyTye);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo moveProperty(String parentNodeId, String propertyId)
			throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().moveProperty(this.context, parentNodeId, propertyId);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public PropertyInfo deleteProperty(String propertyId) throws ItemNotFoundException, RepositoryException {
		PropertyInfo propertyInfo = this.getStorage().deleteProperty(this.context, propertyId);
		if (propertyInfo != null) {
			propertyInfo.setStorageName(this.getName());
		}

		return propertyInfo;
	}

	public long getItemIndexof(String itemId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getPropertyIndexof(this.context, itemId);
	}

	public long getNodeIndexof(String nodeId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getPropertyIndexof(this.context, nodeId);
	}

	public long getPropertyIndexof(String propertyId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getPropertyIndexof(this.context, propertyId);
	}

	public RowInfo[] executeQuery(String query, String[] propertyNames, String[] columnNames, int[] propertyTypes)
			throws RepositoryException {
		return this.getStorage().executeQuery(this.context, query, propertyNames, columnNames, propertyTypes);
	}

	public ItemInfo[] executeQuery4XPath(String query) throws RepositoryException {
		ItemInfo[] itemInfos = this.getStorage().executeQuery4XPath(this.context, query);

		for (int i = 0; i < itemInfos.length; ++i) {
			ItemInfo itemInfo = itemInfos[i];
			itemInfo.setStorageName(this.getName());
		}

		return itemInfos;
	}

	public void executeUpdate(String query) throws RepositoryException {
		this.getStorage().executeUpdate(this.context, query);
	}

	public void registerNamespace(String prefix, String url) throws RepositoryException {
		this.getStorage().registerNamespace(this.context, prefix, url);
	}

	public void unRegisterNamespace() throws RepositoryException {
		this.getStorage().unRegisterNamespace(this.context);
	}

	public void unRegisterNamespace(String prefix) throws RepositoryException {
		this.getStorage().unRegisterNamespace(this.context, prefix);
	}

	public String[] getPrefixes() throws RepositoryException {
		return this.getStorage().getPrefixes(this.context);
	}

	public String[] getURIs() throws RepositoryException {
		return this.getStorage().getURIs(this.context);
	}

	public String getURI(String prefix) throws RepositoryException {
		return this.getStorage().getURI(this.context, prefix);
	}

	public String getPrefix(String url) throws RepositoryException {
		return this.getStorage().getPrefix(this.context, url);
	}

	public void saveNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		SessionManagerLocal sessionManagerLocal = StorageFactory.createSessionManagerLocal();
		sessionManagerLocal.saveNode(this.context, nodeId);
	}

	public void saveProperty(String propertyId) throws ItemNotFoundException, RepositoryException {
		SessionManagerLocal sessionManagerLocal = StorageFactory.createSessionManagerLocal();
		sessionManagerLocal.saveProperty(this.context, propertyId);
	}

	public void saveNode(String nodeId, boolean isDeep) throws ItemNotFoundException, RepositoryException {
		SessionManagerLocal sessionManagerLocal = StorageFactory.createSessionManagerLocal();
		sessionManagerLocal.saveNode(this.context, nodeId, isDeep);
	}

	public NodeInfo[] getTransientNodeInfo(String nodeId) throws RepositoryException {
		SessionManagerLocal sessionManagerLocal = StorageFactory.createSessionManagerLocal();
		NodeInfo[] nodeInfos = sessionManagerLocal.getTransientNodeInfo(this.context, nodeId);

		for (int i = 0; i < nodeInfos.length; ++i) {
			nodeInfos[i].setStorageName(this.getName());
		}

		return nodeInfos;
	}

	public void saveSession() throws ItemNotFoundException, RepositoryException {
		SessionManagerLocal sessionManagerLocal = StorageFactory.createSessionManagerLocal();
		sessionManagerLocal.saveSession(this.context);
	}

	public NodeInfo[] getTransientNodeInfo() throws RepositoryException {
		SessionManagerLocal sessionManagerLocal = StorageFactory.createSessionManagerLocal();
		NodeInfo[] nodeInfos = sessionManagerLocal.getTransientNodeInfo(this.context);

		for (int i = 0; i < nodeInfos.length; ++i) {
			nodeInfos[i].setStorageName(this.getName());
		}

		return nodeInfos;
	}

	public boolean prepare() throws RepositoryException {
		TransactionManagerLocal transactionManagerLocal = StorageFactory.createTransactionManagerLocal();
		return transactionManagerLocal.prepare(this.context);
	}

	public boolean commit() throws RepositoryException {
		TransactionManagerLocal transactionManagerLocal = StorageFactory.createTransactionManagerLocal();
		return transactionManagerLocal.commit(this.context);
	}

	public boolean rollback() throws RepositoryException {
		TransactionManagerLocal transactionManagerLocal = StorageFactory.createTransactionManagerLocal();
		return transactionManagerLocal.rollback(this.context);
	}

	public boolean forget() throws RepositoryException {
		TransactionManagerLocal transactionManagerLocal = StorageFactory.createTransactionManagerLocal();
		return transactionManagerLocal.forget(this.context);
	}

	public String getNodePath(String nodeId) throws ItemNotFoundException, RepositoryException {
		return this.getStorage().getNodePath(this.context, nodeId);
	}

	public CachedNodeInfo getCachedNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		CachedNodeInfo nodeInfo = this.getStorage().getCachedNode(this.context, nodeId);
		if (nodeInfo != null) {
			nodeInfo.setStorageName(this.getName());
		}

		this.setChildStorageName(nodeInfo);
		return nodeInfo;
	}

	private void setChildStorageName(CachedNodeInfo nodeInfo) {
		Iterator cni = nodeInfo.getChildNodeInfos();

		while (cni.hasNext()) {
			NodeInfo childNodeInfo = (NodeInfo) cni.next();
			childNodeInfo.setStorageName(this.getName());
			if (childNodeInfo instanceof CachedNodeInfo) {
				this.setChildStorageName((CachedNodeInfo) childNodeInfo);
			}
		}

	}
}