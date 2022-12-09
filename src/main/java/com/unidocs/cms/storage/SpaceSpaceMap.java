package com.unidocs.cms.storage;

import java.util.Collection;
import java.util.Iterator;

public class SpaceSpaceMap implements StorageSpaceIterator {
	private Iterator si;

	public SpaceSpaceMap(Collection storageSpaces) {
		this.si = storageSpaces.iterator();
	}

	public boolean hasNext() {
		return this.si.hasNext();
	}

	public StorageSpace next() {
		return (StorageSpace)this.si.next();
	}
}