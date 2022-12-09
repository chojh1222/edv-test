package com.unidocs.cms.storage;

public interface StorageSpaceIterator {
	boolean hasNext();

	StorageSpace next();
}