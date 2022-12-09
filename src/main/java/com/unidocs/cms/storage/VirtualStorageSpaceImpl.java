package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.ValueFormatException;
import com.unidocs.cms.info.Context;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;
import com.unidocs.cms.info.ReferenceInfo;
import com.unidocs.cms.info.RowInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;

public class VirtualStorageSpaceImpl implements VirtualStorageSpace {
	private String name;
	private StorageSpaceProvider storageSpaceProvider;
	private InfoLoaderService infoLoaderService;
	private Context context;

	public VirtualStorageSpaceImpl(Context context, String name, StorageSpaceProvider storageSpaceProvider) {
		this.context = context;
		this.name = name;
		this.storageSpaceProvider = storageSpaceProvider;
		this.infoLoaderService = storageSpaceProvider.getInfoLoaderService();
	}

	public String getName() {
		return this.name;
	}

	public StorageSpaceProvider getStorageSpaceProvider() {
		return this.storageSpaceProvider;
	}

	public void setStorageSpaceProvider(StorageSpaceProvider storageSpaceProvider) {
		this.storageSpaceProvider = storageSpaceProvider;
	}

	public NodeInfo getRootNode() throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getRootNode();
	}

	public boolean hasNode(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();
			if (ss.hasNode(nodeInfo.getId())) {
				return true;
			}
		}

		return false;
	}

	public boolean hasChildNode(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.hasChildNode(nodeInfo.getId());
	}

	public long getChildNodesLength(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildNodesLength(nodeInfo.getId());
	}

	public long getChildNodesLength(NodeInfo nodeInfo, String qname) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildNodesLength(nodeInfo.getId(), nodeInfo.getName());
	}

	public NodeInfo getNode(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getNode(nodeInfo.getId());
	}

	public NodeInfo getParentNode(ItemInfo itemInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(itemInfo.getStorageName());
		NodeInfo parentNodeInfo = ss.getParentNode(itemInfo.getId());
		return parentNodeInfo == null ? null : parentNodeInfo;
	}

	public boolean hasNode(NodeInfo parentNodeInfo, String name) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		return ss.hasNode(parentNodeInfo.getId(), name);
	}

	public NodeInfo getChildNode(NodeInfo parentNodeInfo, String name)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		NodeInfo nodeInfo = ss.getChildNode(parentNodeInfo.getId(), name);
		return nodeInfo == null ? null : nodeInfo;
	}

	public NodeInfo[] getChildNodes(NodeInfo parentNodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		NodeInfo[] childNodeInfos = ss.getChildNodes(parentNodeInfo.getId());
		return childNodeInfos;
	}

	public NodeInfo[] getChildNodes(NodeInfo parentNodeInfo, String qname)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		NodeInfo[] childNodeInfos = ss.getChildNodes(parentNodeInfo.getId(), qname);
		return childNodeInfos;
	}

	public NodeInfo getFirstChildNode(NodeInfo parentNodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		NodeInfo firstChildNodeInfo = ss.getFirstChildNode(parentNodeInfo.getId());
		return firstChildNodeInfo == null ? null : firstChildNodeInfo;
	}

	public NodeInfo getLastChildNode(NodeInfo parentNodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		NodeInfo childNodeInfo = ss.getLastChildNode(parentNodeInfo.getId());
		return childNodeInfo == null ? null : childNodeInfo;
	}

	public ItemInfo getNextSibling(ItemInfo itemInfo) throws ItemNotFoundException, RepositoryException {
		ItemInfo next = null;
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(itemInfo.getStorageName());
		if (itemInfo.isNode()) {
			next = ss.getNextNode(itemInfo.getId());
		} else {
			next = ss.getNextProperty(itemInfo.getId());
			if (next == null) {
				NodeInfo parentNodeInfo = ss.getParentNode(itemInfo.getId());
				next = ss.getFirstChildNode(parentNodeInfo.getId());
			}
		}

		return (ItemInfo) (next == null ? null : next);
	}

	public ItemInfo getPreviousSibling(ItemInfo itemInfo) throws ItemNotFoundException, RepositoryException {
		ItemInfo next = null;
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(itemInfo.getStorageName());
		if (itemInfo.isNode()) {
			next = ss.getPrevNode(itemInfo.getId());
			if (next == null) {
				NodeInfo parentNodeInfo = ss.getParentNode(itemInfo.getId());
				next = ss.getLastChildProperty(parentNodeInfo.getId());
			}
		} else {
			next = ss.getPrevNode(itemInfo.getId());
		}

		return (ItemInfo) (next == null ? null : next);
	}

	public ItemInfo getFirstChild(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		ItemInfo itemInfo = ss.getFirstChildProperty(nodeInfo.getId());
		if (itemInfo == null) {
			itemInfo = ss.getFirstChildNode(nodeInfo.getId());
		}

		return (ItemInfo) (itemInfo == null ? null : itemInfo);
	}

	public ItemInfo getLastChild(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		ItemInfo itemInfo = ss.getLastChildNode(nodeInfo.getId());
		if (itemInfo == null) {
			itemInfo = ss.getLastChildProperty(nodeInfo.getId());
		}

		return (ItemInfo) (itemInfo == null ? null : itemInfo);
	}

	public NodeInfo getNextNode(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		NodeInfo childNodeInfo = ss.getNextNode(nodeInfo.getId());
		return childNodeInfo == null ? null : childNodeInfo;
	}

	public NodeInfo getPrevNode(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		NodeInfo childNodeInfo = ss.getPrevNode(nodeInfo.getId());
		return childNodeInfo == null ? null : childNodeInfo;
	}

	public PropertyInfo getProperty(PropertyInfo propertyInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		return ss.getProperty(propertyInfo.getId());
	}

	public long getChildPropertiesLength(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildPropertiesLength(nodeInfo.getId());
	}

	public long getChildPropertiesLength(NodeInfo nodeInfo, String qname)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildPropertiesLength(nodeInfo.getId(), qname);
	}

	public boolean hasProperty(PropertyInfo propertyInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		return ss.hasProperty(propertyInfo.getId());
	}

	public boolean hasProperty(NodeInfo nodeInfo, String qname) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.hasProperty(nodeInfo.getId(), qname);
	}

	public boolean hasProperty(NodeInfo nodeInfo, String qname, short xmlNodeType)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.hasProperty(nodeInfo.getId(), qname);
	}

	public PropertyInfo getChildProperty(NodeInfo nodeInfo, String qname)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildProperty(nodeInfo.getId(), qname);
	}

	public PropertyInfo getChildProperty(NodeInfo nodeInfo, long index)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildProperty(nodeInfo.getId(), index);
	}

	public PropertyInfo getChildProperty(NodeInfo nodeInfo, String qname, long index)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildProperty(nodeInfo.getId(), qname, index);
	}

	public PropertyInfo[] getChildProperties(NodeInfo nodeInfo, String[] qnames)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildProperties(nodeInfo.getId(), qnames);
	}

	public PropertyInfo[] getChildProperties(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildProperties(nodeInfo.getId());
	}

	public PropertyInfo[] getChildProperties(NodeInfo nodeInfo, String qname)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getChildProperties(nodeInfo.getId(), qname);
	}

	public PropertyInfo getFirstChildProperty(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getFirstChildProperty(nodeInfo.getId());
	}

	public PropertyInfo getLastChildProperty(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getLastChildProperty(nodeInfo.getId());
	}

	public PropertyInfo getNextProperty(PropertyInfo propertyInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		return ss.getNextProperty(propertyInfo.getId());
	}

	public PropertyInfo getPrevProperty(PropertyInfo propertyInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		return ss.getPrevProperty(propertyInfo.getId());
	}

	public long getChildLength(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		long nodelength = ss.getChildNodesLength(nodeInfo.getId());
		long propertylength = ss.getChildPropertiesLength(nodeInfo.getId());
		return nodelength + propertylength;
	}

	public NodeInfo addChildNode(NodeInfo parentNodeInfo, String childNodeId, String qname)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		if (parentNodeInfo == null) {
			parentNodeInfo = new NodeInfo(this.storageSpaceProvider.getRootStorageSpace().getName(), "-1",
					"virtual root");
		}

		return ss.addChildNode(parentNodeInfo.getId(), childNodeId, qname);
	}

	public NodeInfo moveNode(NodeInfo parentNodeInfo, NodeInfo nodeInfo)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		return ss.moveNode(parentNodeInfo.getId(), nodeInfo.getId());
	}

	public NodeInfo deleteNode(NodeInfo nodeInfo, boolean deep) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.deleteNode(nodeInfo.getId());
	}

	public PropertyInfo addChildProperty(NodeInfo parentNodeInfo, String propertyId, String qname, String value,
			int type) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		return ss.addChildProperty(parentNodeInfo.getId(), propertyId, qname, value, type);
	}

	public PropertyInfo setPropertyValue(PropertyInfo propertyInfo, String value, int type)
			throws ValueFormatException, IllegalStateException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		ss.setPropertyValue(propertyInfo.getId(), value, type);
		propertyInfo.getValueInfo().setValue(value);
		return propertyInfo;
	}

	public PropertyInfo moveProperty(NodeInfo parentNodeInfo, PropertyInfo propertyInfo)
			throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(parentNodeInfo.getStorageName());
		return ss.moveProperty(parentNodeInfo.getId(), propertyInfo.getId());
	}

	public PropertyInfo deleteProperty(PropertyInfo propertyInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		return ss.deleteProperty(propertyInfo.getId());
	}

	public long getNodeIndexof(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getNodeIndexof(nodeInfo.getId());
	}

	public long getPropertyIndexof(PropertyInfo propertyInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		return ss.getPropertyIndexof(propertyInfo.getId());
	}

	public boolean canLoadfor(String nodeType, Class javaType) {
		InfoLoaderService infoLoaderService = this.storageSpaceProvider.getInfoLoaderService();
		return infoLoaderService.canLoadfor(nodeType, javaType);
	}

	public ItemInfo load(ItemInfo itemInfo, String nodeType, Class javaType) throws RepositoryException {
		InfoLoaderService infoLoaderService = this.storageSpaceProvider.getInfoLoaderService();
		return infoLoaderService.load(itemInfo.getStorageName(), itemInfo.getId(), nodeType, javaType);
	}

	public ItemInfo build(CachedNodeInfo nodeInfo, String nodeType, Class javaType) throws RepositoryException {
		InfoLoaderService infoLoaderService = this.storageSpaceProvider.getInfoLoaderService();
		return infoLoaderService.build(nodeInfo, nodeType, javaType);
	}

	public ItemInfo save(ItemInfo info, String nodeType, NodeInfo parentInfo) throws RepositoryException {
		InfoLoaderService infoLoaderService = this.storageSpaceProvider.getInfoLoaderService();
		return infoLoaderService.save(info, nodeType, parentInfo);
	}

	public NodeInfo getNodeByReference(ReferenceInfo reference) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(reference.getStorageName());
		return ss.getNode(reference.getId());
	}

	public NodeInfo getNodeByUUID(String uuid) throws ItemNotFoundException, RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();

			try {
				NodeInfo nodeInfo = ss.getNode(uuid);
				if (nodeInfo != null) {
					return nodeInfo;
				}
			} catch (ItemNotFoundException var5) {
				;
			}
		}

		throw new ItemNotFoundException("There is not node that's id is " + uuid);
	}

	public PropertyInfo getPropertyByUUID(String uuid) throws ItemNotFoundException, RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();

			try {
				PropertyInfo propertyInfo = ss.getProperty(uuid);
				if (propertyInfo != null) {
					return propertyInfo;
				}
			} catch (ItemNotFoundException var5) {
				;
			}
		}

		throw new ItemNotFoundException("There is not node that's id is " + uuid);
	}

	public ItemInfo getItemByUUID(String uuid) throws ItemNotFoundException, RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();

			try {
				ItemInfo itemInfo = ss.getNode(uuid);
				if (itemInfo != null) {
					return itemInfo;
				}
			} catch (ItemNotFoundException var5) {
				;
			}
		}

		throw new ItemNotFoundException("There is not node that's id is " + uuid);
	}

	public RowInfo[] executeQuery(String query, String[] propertyNames, String[] columnNames, int[] propertyTypes)
			throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.executeQuery(query, propertyNames, columnNames, propertyTypes);
	}

	public ItemInfo[] executeQuery4XPath(String query) throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.executeQuery4XPath(query);
	}

	public void executeUpdate(String query) throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		ss.executeUpdate(query);
	}

	public void registerNamespace(String prefix, String url) throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		ss.registerNamespace(prefix, url);
	}

	public void unRegisterNamespace(String prefix) throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		ss.unRegisterNamespace(prefix);
	}

	public void unRegisterNamespace() throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		ss.unRegisterNamespace();
	}

	public String[] getPrefixes() throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getPrefixes();
	}

	public String[] getURIs() throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getURIs();
	}

	public String getURI(String prefix) throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getURI(prefix);
	}

	public String getPrefix(String url) throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getPrefix(url);
	}

	public void saveNode(NodeInfo nodeInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		ss.saveNode(nodeInfo.getId());
	}

	public void saveProperty(PropertyInfo propertyInfo) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(propertyInfo.getStorageName());
		ss.saveProperty(propertyInfo.getId());
	}

	public void saveNode(NodeInfo nodeInfo, boolean isDeep) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		ss.saveNode(nodeInfo.getId(), isDeep);
	}

	public NodeInfo[] getTransientNodeInfo(NodeInfo nodeInfo) throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getStorageSpace(nodeInfo.getStorageName());
		return ss.getTransientNodeInfo(nodeInfo.getId());
	}

	public void saveSession() throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		ss.saveSession();
	}

	public NodeInfo[] getTransientNodeInfo() throws RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getTransientNodeInfo();
	}

	public boolean prepare() throws RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();

			try {
				boolean ret = ss.prepare();
				if (!ret) {
					return false;
				}
			} catch (ItemNotFoundException var4) {
				;
			}
		}

		return true;
	}

	public boolean commit() throws RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();

			try {
				boolean ret = ss.commit();
				if (!ret) {
					return false;
				}
			} catch (ItemNotFoundException var4) {
				;
			}
		}

		return true;
	}

	public boolean rollback() throws RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();

			try {
				boolean ret = ss.rollback();
				if (!ret) {
					return false;
				}
			} catch (ItemNotFoundException var4) {
				;
			}
		}

		return true;
	}

	public boolean forget() throws RepositoryException {
		StorageSpaceIterator si = this.storageSpaceProvider.getAllStorageSpace();

		while (si.hasNext()) {
			StorageSpace ss = si.next();

			try {
				boolean ret = ss.forget();
				if (!ret) {
					return false;
				}
			} catch (ItemNotFoundException var4) {
				;
			}
		}

		return true;
	}

	public String getNodePathByUUID(String uuid) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getNodePath(uuid);
	}

	public CachedNodeInfo getCachedNode(String nodeId) throws ItemNotFoundException, RepositoryException {
		StorageSpace ss = this.storageSpaceProvider.getRootStorageSpace();
		return ss.getCachedNode(nodeId);
	}
}