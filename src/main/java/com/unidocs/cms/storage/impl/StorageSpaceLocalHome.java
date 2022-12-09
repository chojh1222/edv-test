// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
package com.unidocs.cms.storage.impl;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface StorageSpaceLocalHome extends EJBLocalHome {

	public abstract StorageSpaceLocal create() throws CreateException;
}