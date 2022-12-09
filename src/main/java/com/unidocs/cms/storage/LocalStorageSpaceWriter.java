package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.Context;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;

public interface LocalStorageSpaceWriter {
	NodeInfo addChildNode(Context var1, String var2, String var3, String var4)
			throws ItemNotFoundException, RepositoryException;

	NodeInfo moveNode(Context var1, String var2, String var3) throws ItemNotFoundException, RepositoryException;

	NodeInfo deleteNode(Context var1, String var2) throws ItemNotFoundException, RepositoryException;

	PropertyInfo addChildProperty(Context var1, String var2, String var3, String var4, String var5, int var6)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo setPropertyValue(Context var1, String var2, String var3, int var4)
			throws ItemNotFoundException, RepositoryException;

	PropertyInfo moveProperty(Context var1, String var2, String var3) throws ItemNotFoundException, RepositoryException;

	PropertyInfo deleteProperty(Context var1, String var2) throws ItemNotFoundException, RepositoryException;
}