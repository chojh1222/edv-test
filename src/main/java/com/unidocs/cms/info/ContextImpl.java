package com.unidocs.cms.info;

import com.unidocs.cms.util.UUIDGen;
import org.jdom.Element;

public class ContextImpl implements Context, EDVInfo {
	static final long serialVersionUID = -6316501972165719616L;
	private String uuid = null;
	private boolean isInSession = false;
	private EDVCredentials credentials = null;
	private WorkspaceInfo workspaceInfo = null;
	private TransactionInfo transactionInfo = null;

	public ContextImpl() {
	}

	public void fromXml(Element element) {
		String inSesssion = element.getAttributeValue("insession");
		this.isInSession = Boolean.getBoolean(inSesssion);
		if (element.getChild("credentials") != null) {
			this.credentials = new EDVCredentialsImpl();
			this.credentials.fromXml((Element) element.getChild("credentials").getChildren().get(0));
		}

		if (element.getChild("workspace") != null) {
			this.workspaceInfo = new WorkspaceInfo();
			this.workspaceInfo.fromXml((Element) element.getChild("workspace").getChildren().get(0));
		}

		if (element.getChild("transaction") != null) {
			this.transactionInfo = new TransactionInfo();
			this.transactionInfo.fromXml((Element) element.getChild("transaction").getChildren().get(0));
		}

	}

	public Element toXml() {
		Element result = new Element("context");
		result.setAttribute("insession", Boolean.toString(this.isInSession));
		new StringBuffer();
		Element uuid_element = new Element("uuid");
		uuid_element.setText(this.uuid);
		result.addContent(uuid_element);
		Element transaction_element;
		if (this.credentials != null) {
			transaction_element = new Element("credentials");
			transaction_element.addContent(this.credentials.toXml());
			result.addContent(transaction_element);
		}

		if (this.workspaceInfo != null) {
			transaction_element = new Element("workspace");
			transaction_element.addContent(this.workspaceInfo.toXml());
			result.addContent(transaction_element);
		}

		if (this.transactionInfo != null) {
			transaction_element = new Element("transaction");
			transaction_element.addContent(this.transactionInfo.toXml());
			result.addContent(transaction_element);
		}

		return result;
	}

	public static ContextImpl newContext(EDVCredentials credentials, WorkspaceInfo workspaceInfo) {
		return new ContextImpl(UUIDGen.newUUID(), credentials, workspaceInfo);
	}

	public ContextImpl(String uuid, EDVCredentials credentials, WorkspaceInfo workspaceInfo) {
		this.uuid = uuid;
		this.credentials = credentials;
		this.workspaceInfo = workspaceInfo;
		if (credentials != null) {
			this.isInSession = true;
		}

	}

	public EDVCredentials getCredentials() {
		return this.credentials;
	}

	public String getId() {
		return this.uuid;
	}

	public String getUserId() {
		return this.credentials.getUserId();
	}

	public WorkspaceInfo getWorkspaceInfo() {
		return this.workspaceInfo;
	}

	public String getWorkspaceId() {
		return this.workspaceInfo.getId();
	}

	public String getWorkspaceName() {
		return this.workspaceInfo.getName();
	}

	public TransactionInfo getTransactionInfo() {
		return this.transactionInfo;
	}

	public void setTransactionInfo(TransactionInfo transactionInfo) {
		this.transactionInfo = transactionInfo;
	}

	public boolean isInSession() {
		return this.isInSession;
	}

	public void isInSession(boolean value) {
		this.isInSession = value;
	}

	public boolean isInTransaction() {
		return this.transactionInfo == null ? false : this.transactionInfo.isInTransaction();
	}
}