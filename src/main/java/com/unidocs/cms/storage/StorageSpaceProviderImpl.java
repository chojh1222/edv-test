package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.Context;
import java.util.HashMap;

public class StorageSpaceProviderImpl implements StorageSpaceProvider {
	private InfoLoaderService infoLoaderService;
	private StorageSpace rootStorageSpace;
	private HashMap storageSpaceMap = new HashMap();
	private Context context;

	public StorageSpaceProviderImpl(Context context, StorageSpace rootStorageSpace) {
		this.context = context;
		this.infoLoaderService = InfoLoaderServiceImpl.getInstance(this);
		this.rootStorageSpace = rootStorageSpace;
		this.storageSpaceMap.put(rootStorageSpace.getName(), rootStorageSpace);
	}

	public StorageSpaceProviderImpl(InfoLoaderService infoLoaderService, StorageSpace rootStorageSpace) {
		this.infoLoaderService = infoLoaderService;
		this.rootStorageSpace = rootStorageSpace;
		this.storageSpaceMap.put(rootStorageSpace.getName(), rootStorageSpace);
	}

	public void bindInfoLoaderService(InfoLoaderService infoLoaderService) {
		this.infoLoaderService = infoLoaderService;
	}

	public InfoLoaderService getInfoLoaderService() {
		return this.infoLoaderService;
	}

	public StorageSpace getRootStorageSpace() {
		return this.rootStorageSpace;
	}

	public StorageSpace getStorageSpace(String name) throws RepositoryException {
		StorageSpace ret = (StorageSpace) this.storageSpaceMap.get(name);
		if (ret == null) {
			throw new RepositoryException("StorageSpace [NAME:" + name + "] is not supported");
		} else {
			return ret;
		}
	}

	public StorageSpaceIterator getAllStorageSpace() {
		return new SpaceSpaceMap(this.storageSpaceMap.values());
	}
}