package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.RowInfo;

public interface QueryExecutor {
	RowInfo[] executeQuery(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2,
			int[] paramArrayOfint) throws RepositoryException;

	ItemInfo[] executeQuery4XPath(String paramString) throws RepositoryException;

	void executeUpdate(String paramString) throws RepositoryException;
}