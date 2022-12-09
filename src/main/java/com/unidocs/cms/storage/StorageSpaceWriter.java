package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;

public interface StorageSpaceWriter {
	NodeInfo addChildNode(String paramString1, String paramString2, String paramString3)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo moveNode(String paramString1, String paramString2) throws ItemNotFoundException, RepositoryException;

	NodeInfo deleteNode(String paramString) throws ItemNotFoundException, RepositoryException;

	PropertyInfo addChildProperty(String paramString1, String paramString2, String paramString3, String paramString4,
			int paramInt) throws ItemNotFoundException, RepositoryException;

	PropertyInfo moveProperty(String paramString1, String paramString2)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo deleteProperty(String paramString) throws ItemNotFoundException, RepositoryException;

	PropertyInfo setPropertyValue(String paramString1, String paramString2, int paramInt)
			throws ItemNotFoundException, RepositoryException;
}