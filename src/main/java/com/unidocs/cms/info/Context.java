package com.unidocs.cms.info;

import java.io.Serializable;

public interface Context extends Serializable {
	EDVCredentials getCredentials();

	String getId();

	String getUserId();

	WorkspaceInfo getWorkspaceInfo();

	String getWorkspaceId();

	String getWorkspaceName();

	TransactionInfo getTransactionInfo();

	void setTransactionInfo(TransactionInfo var1);

	boolean isInSession();

	void isInSession(boolean var1);

	boolean isInTransaction();
}