package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.ItemInfo;

public interface SmartStorageSpace extends StorageSpace {
	String[] getAllSupportedTypes();

	boolean canLoadfor(String paramString, Class paramClass);

	ItemInfo load(String paramString1, String paramString2, Class paramClass) throws RepositoryException;

	ItemInfo save(ItemInfo paramItemInfo, String paramString1, String paramString2, String paramString3)
			throws RepositoryException;
}