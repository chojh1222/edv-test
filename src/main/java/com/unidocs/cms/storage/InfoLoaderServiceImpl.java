package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;
import com.unidocs.cms.info.node.WorkspaceInfoLoader;
import com.unidocs.cms.info.nodetype.NodeDefInfoLoader;
import com.unidocs.cms.info.nodetype.NodeTypeInfoLoader;
import com.unidocs.cms.info.nodetype.PropertyDefInfoLoader;
import java.util.ArrayList;

public class InfoLoaderServiceImpl implements InfoLoaderService {
	private ArrayList infoLoaders = new ArrayList();
	public StorageSpaceProvider storageSpaceProvider;

	public void addInfoLoader(InfoLoader loader) {
		if (!this.infoLoaders.contains(loader)) {
			this.infoLoaders.add(loader);
		}

	}

	public static InfoLoaderServiceImpl getInstance(StorageSpaceProvider storageSpaceProvider) {
		InfoLoaderServiceImpl service = new InfoLoaderServiceImpl(storageSpaceProvider);
		service.addInfoLoader(new NodeTypeInfoLoader(service, "nt:nodeType"));
		service.addInfoLoader(new NodeDefInfoLoader(service, "nt:childNodeDef"));
		service.addInfoLoader(new PropertyDefInfoLoader(service, "nt:propertyDef"));
		service.addInfoLoader(new WorkspaceInfoLoader(service, "nt:workspace"));
		return service;
	}

	private InfoLoaderServiceImpl(StorageSpaceProvider storageSpaceProvider) {
		this.storageSpaceProvider = storageSpaceProvider;
		this.storageSpaceProvider.bindInfoLoaderService(this);
	}

	public StorageSpaceProvider getStorageSpaceProvider() {
		return this.storageSpaceProvider;
	}

	public InfoLoaderService getLoadService() {
		return this;
	}

	public boolean canLoadfor(String nodeType, Class javaType) {
		InfoLoader loader = null;

		for (int i = 0; i < this.infoLoaders.size(); ++i) {
			loader = (InfoLoader) this.infoLoaders.get(i);
			if (loader.canLoadfor(nodeType, javaType)) {
				return true;
			}
		}

		return false;
	}

	public InfoLoader getInfoLoader(String nodeType, Class javaType) throws RepositoryException {
		InfoLoader loader = null;

		for (int i = 0; i < this.infoLoaders.size(); ++i) {
			loader = (InfoLoader) this.infoLoaders.get(i);
			if (loader.canLoadfor(nodeType, javaType)) {
				try {
					return (InfoLoader) loader.clone();
				} catch (CloneNotSupportedException var6) {
					var6.printStackTrace();
					throw new RepositoryException("can't make clone InfoLoader for " + nodeType);
				}
			}
		}

		throw new RepositoryException("don't have InfoLoader for " + nodeType);
	}

	public ItemInfo load(String storageName, String id, String nodeType, Class javaType) throws RepositoryException {
		InfoLoader loader = this.getInfoLoader(nodeType, javaType);
		return loader.load(storageName, id, nodeType, javaType);
	}

	public ItemInfo build(CachedNodeInfo nodeInfo, String nodeType, Class javaType) throws RepositoryException {
		InfoLoader loader = this.getInfoLoader(nodeType, javaType);
		return loader.build(nodeInfo, nodeType, javaType);
	}

	public ItemInfo save(ItemInfo info, String nodeType, NodeInfo parentInfo) throws RepositoryException {
		InfoLoader loader = this.getInfoLoader(nodeType, info.getClass());
		return loader.save(info, nodeType, parentInfo);
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}