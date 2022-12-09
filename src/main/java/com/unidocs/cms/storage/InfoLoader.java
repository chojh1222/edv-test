package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;

public interface InfoLoader extends Cloneable {
	StorageSpaceProvider getStorageSpaceProvider();

	InfoLoaderService getLoadService();

	boolean canLoadfor(String var1, Class var2);

	ItemInfo load(String var1, String var2, String var3, Class var4) throws RepositoryException;

	ItemInfo build(CachedNodeInfo var1, String var2, Class var3) throws RepositoryException;

	ItemInfo save(ItemInfo var1, String var2, NodeInfo var3) throws RepositoryException;

	Object clone() throws CloneNotSupportedException;
}