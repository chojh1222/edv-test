package com.unidocs.cms.storage;

import com.unidocs.cms.RepositoryException;

public interface InfoLoaderService extends InfoLoader {
	InfoLoader getInfoLoader(String var1, Class var2) throws RepositoryException;
}