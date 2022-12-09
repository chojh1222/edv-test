package com.unidocs.cms.storage;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.Context;

public interface LocalStorageSpace
		extends
			LocalStorageSpaceReader,
			LocalStorageSpaceWriter,
			LocalNamespaceManager,
			LocalQueryExecutor {
	String getName(Context var1);

	String getNodePath(Context var1, String var2) throws ItemNotFoundException, RepositoryException;
}