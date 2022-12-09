package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.Context;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.RowInfo;

public interface LocalQueryExecutor {
	RowInfo[] executeQuery(Context var1, String var2, String[] var3, String[] var4, int[] var5)
			throws RepositoryException;

	ItemInfo[] executeQuery4XPath(Context var1, String var2) throws RepositoryException;

	void executeUpdate(Context var1, String var2) throws RepositoryException;
}