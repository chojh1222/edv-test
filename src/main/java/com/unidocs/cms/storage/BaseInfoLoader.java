package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;

public abstract class BaseInfoLoader implements InfoLoader {
	private InfoLoaderService infoLoaderService;
	private String nodeType;
	private Class javaType;

	public BaseInfoLoader(InfoLoaderService infoLoaderService, String nodeType, Class javaType) {
		this.infoLoaderService = infoLoaderService;
		this.nodeType = nodeType;
		this.javaType = javaType;
	}

	public final boolean canLoadfor(String nodeType, Class javaType) {
		return this.nodeType.equals(nodeType) && this.javaType.isAssignableFrom(javaType);
	}

	public final InfoLoaderService getLoadService() {
		return this.infoLoaderService;
	}

	public final StorageSpaceProvider getStorageSpaceProvider() {
		return this.getLoadService().getStorageSpaceProvider();
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public final ItemInfo load(String storageName, String id, String nodeType, Class javaType)
			throws RepositoryException {
		StorageSpace ss = this.getStorageSpaceProvider().getStorageSpace(storageName);
		if (ss instanceof SmartStorageSpace) {
			SmartStorageSpace sss = (SmartStorageSpace) ss;
			return sss.load(id, nodeType, javaType);
		} else {
			ItemInfo itemInfo = this.loadItem(storageName, id, nodeType);
			itemInfo.setNodeType(nodeType);
			return itemInfo;
		}
	}

	public ItemInfo build(CachedNodeInfo cachedNodeInfo, String nodeType, Class javaType) throws RepositoryException {
		return null;
	}

	public final ItemInfo save(ItemInfo info, String nodeType, NodeInfo parentInfo) throws RepositoryException {
		if (info.isCreated() && !info.isDeleted()) {
			info.setSaved();
			return this.addItem(parentInfo, info, nodeType);
		} else if (info.isCreated() && info.isDeleted()) {
			info.setSaved();
			return info;
		} else if (info.isDeleted() && info.isLoaded()) {
			info.setSaved();
			return this.deleteItem(info, nodeType);
		} else if (info.isModified()) {
			info.setSaved();
			return this.modifyItem(info, nodeType);
		} else if (info.isDestructed()) {
			info.setSaved();
			return info;
		} else if (info.isLoaded()) {
			info.setSaved();
			return info;
		} else {
			throw new RepositoryException(nodeType + "is in unsupported state");
		}
	}

	protected abstract ItemInfo loadItem(String var1, String var2, String var3) throws RepositoryException;

	protected abstract ItemInfo addItem(NodeInfo var1, ItemInfo var2, String var3) throws RepositoryException;

	protected abstract ItemInfo modifyItem(ItemInfo var1, String var2) throws RepositoryException;

	protected abstract ItemInfo deleteItem(ItemInfo var1, String var2) throws RepositoryException;
}