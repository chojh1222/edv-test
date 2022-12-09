package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;

public interface StorageSpaceProvider {
	void bindInfoLoaderService(InfoLoaderService var1);

	InfoLoaderService getInfoLoaderService();

	StorageSpaceIterator getAllStorageSpace();

	StorageSpace getRootStorageSpace();

	StorageSpace getStorageSpace(String var1) throws RepositoryException;
}