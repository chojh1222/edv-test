package com.unidocs.cms.rom;

import com.unidocs.cms.Credentials;
import com.unidocs.cms.LoginException;
import com.unidocs.cms.NoSuchWorkspaceException;
import com.unidocs.cms.Repository;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.Session;
import com.unidocs.cms.info.Context;
import com.unidocs.cms.info.ContextImpl;
import com.unidocs.cms.info.EDVCredentials;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;
import com.unidocs.cms.info.SimpleValueInfo;
import com.unidocs.cms.info.UserInfo;
import com.unidocs.cms.info.WorkspaceInfo;
import com.unidocs.cms.info.nodetype.NodeDefInfo;
import com.unidocs.cms.info.nodetype.NodeTypeInfo;
import com.unidocs.cms.info.nodetype.PropertyDefInfo;
import com.unidocs.cms.internal.common.Logger;
import com.unidocs.cms.rom.nodetype.NodeTypeManagerImpl;
import com.unidocs.cms.storage.VirtualStorageSpace;
import com.unidocs.cms.util.UUIDGen;
import java.util.ArrayList;
import java.util.HashMap;

public class RepositoryImpl implements Repository {
	private ArrayList descriptorKeys = new ArrayList();
	private VirtualStorageSpaceFactory storageFactory;
	private Context context;
	private static HashMap cashedWorkspace = new HashMap();
	private static NodeInfo repositoryInfo = null;

	public RepositoryImpl(Context context) {
		this.context = context;
		this.setDescriptorKeys();
		this.storageFactory = VirtualStorageSpaceFactory.getInstance();
	}

	public VirtualStorageSpace getStorage() throws RepositoryException {
		return this.storageFactory.getVirtualStorageSpace(this.context);
	}

	private void setDescriptorKeys() {
		this.descriptorKeys.add("level.1.supported");
		this.descriptorKeys.add("level.2.supported");
		this.descriptorKeys.add("option.locking.supported");
		this.descriptorKeys.add("option.observation.supported");
		this.descriptorKeys.add("option.query.sql.supported");
		this.descriptorKeys.add("option.transactions.supported");
		this.descriptorKeys.add("option.versioning.supported");
		this.descriptorKeys.add("query.jcrpath");
		this.descriptorKeys.add("query.jcrscore");
		this.descriptorKeys.add("query.xpath.doc.order");
		this.descriptorKeys.add("query.xpath.pos.index");
		this.descriptorKeys.add("jcr.repository.name");
		this.descriptorKeys.add("jcr.repository.vendor");
		this.descriptorKeys.add("jcr.repository.vendor.url");
		this.descriptorKeys.add("jcr.repository.version");
		this.descriptorKeys.add("jcr.specification.name");
		this.descriptorKeys.add("jcr.specification.version");
	}

	public String[] getDescriptorKeys() {
		return (String[]) this.descriptorKeys.toArray(new String[0]);
	}

	public String getDescriptor(String key) {
		return this.descriptorKeys.contains(key) ? "" : null;
	}

	private NodeInfo getRepositoryInfo() throws RepositoryException {
		if (repositoryInfo == null) {
			repositoryInfo = this.getStorage().getRootNode();
		}

		return repositoryInfo;
	}

	public String[] getAccessible44WorkspaceNames() throws RepositoryException {
		NodeInfo repositoryInfo = this.getRepositoryInfo();
		NodeInfo[] workspaces = this.getStorage().getChildNodes(repositoryInfo);
		String[] workspaceNames = new String[workspaces.length];

		for (int i = 0; i < workspaces.length; ++i) {
			workspaceNames[i] = workspaces[i].getName();
		}

		return workspaceNames;
	}

	public Session getSession(Context context, String workspaceName)
			throws LoginException, NoSuchWorkspaceException, RepositoryException {
		NodeInfo repositoryInfo = this.getRepositoryInfo();
		WorkspaceInfo workspaceInfo = this.getWorkspaceInfo(workspaceName);
		return this.getSession(workspaceInfo, context);
	}

	public Session login(Credentials credentials, String workspaceName)
			throws LoginException, NoSuchWorkspaceException, RepositoryException {
		EDVCredentials edvc = this.credentials2EDVCredentials(credentials);
		NodeInfo repositoryInfo = this.getRepositoryInfo();
		WorkspaceInfo workspaceInfo = this.getWorkspaceInfo(workspaceName);
		Logger.debug(Logger.LOG_RANGE_ENGINE,
				"check user to workspace id:" + edvc.getUserId() + ", pass:" + new String(edvc.getPassword()));
		if (!workspaceInfo.isUser(edvc.getUserId(), new String(edvc.getPassword()))) {
			throw new LoginException("you don't access the workspace - " + workspaceName);
		} else {
			Context context = ContextImpl.newContext((EDVCredentials) credentials, workspaceInfo);
			return this.getSession((WorkspaceInfo) workspaceInfo, (Context) context);
		}
	}

	private WorkspaceInfo getWorkspaceInfo(String workspaceName) throws RepositoryException {
		WorkspaceInfo workspaceInfo = null;
		if (cashedWorkspace.containsKey(workspaceName)) {
			workspaceInfo = (WorkspaceInfo) cashedWorkspace.get(workspaceName);
		} else {
			workspaceInfo = this.getWorkspaceInfoFromStorage(workspaceName);
			cashedWorkspace.put(workspaceName, workspaceInfo);
		}

		return workspaceInfo;
	}

	private WorkspaceInfo getWorkspaceInfoFromStorage(String workspaceName) throws RepositoryException {
		this.context.isInSession(false);
		NodeInfo nodeInfo = this.getStorage().getChildNode(repositoryInfo, workspaceName);
		if (nodeInfo == null) {
			throw new NoSuchWorkspaceException(workspaceName + " is not existed");
		} else {
			PropertyInfo ownerIdInfo = this.getStorage().getChildProperty(nodeInfo, "edv:ownerId");
			String ownerId = ownerIdInfo.getValueInfo().getString();
			PropertyInfo ownerPassInfo = this.getStorage().getChildProperty(nodeInfo, "edv:ownerPass");
			String ownerPass = ownerPassInfo.getValueInfo().getString();
			WorkspaceInfo workspaceInfo = new WorkspaceInfo(nodeInfo);
			NodeInfo systemNodeInfo = this.getStorage().getChildNode(workspaceInfo, "edv:system");
			NodeInfo sessionsNodeInfo = this.getStorage().getChildNode(workspaceInfo, "edv:sessions");
			NodeInfo transactionNodeInfo = this.getStorage().getChildNode(workspaceInfo, "edv:transactions");
			NodeInfo nodeTypesNodeInfo = this.getStorage().getChildNode(workspaceInfo, "edv:nodeTypes");
			NodeInfo usersNodeInfo = this.getStorage().getChildNode(workspaceInfo, "edv:users");
			NodeInfo[] userNodeInfos = this.getStorage().getChildNodes(usersNodeInfo, "edv:user");
			workspaceInfo.setSystemNodeInfo(systemNodeInfo);
			workspaceInfo.setSessionsNodeInfo(sessionsNodeInfo);
			workspaceInfo.setTransactionNodeInfo(transactionNodeInfo);
			workspaceInfo.setNodeTypesNodeInfo(nodeTypesNodeInfo);

			for (int i = 0; i < userNodeInfos.length; ++i) {
				NodeInfo userNodeInfo = userNodeInfos[i];
				UserInfo userInfo = new UserInfo(userNodeInfo);
				PropertyInfo idInfo = this.getStorage().getChildProperty(userNodeInfo, "edv:id");
				PropertyInfo passInfo = this.getStorage().getChildProperty(userNodeInfo, "edv:pass");
				userInfo.setId(idInfo.getValueInfo().getString());
				userInfo.setPass(passInfo.getValueInfo().getString());
				Logger.debug(Logger.LOG_RANGE_ENGINE,
						"add user to workspace id:" + userInfo.getId() + ", pass:" + userInfo.getPass());
				workspaceInfo.addUser(userInfo);
			}

			return workspaceInfo;
		}
	}

	private EDVCredentials credentials2EDVCredentials(Credentials credentials) throws LoginException {
		if (credentials == null) {
			throw new LoginException("The credentials mustn't The Null");
		} else if (credentials instanceof EDVCredentials) {
			return (EDVCredentials) credentials;
		} else {
			throw new LoginException("This repository support only EDVCredentials");
		}
	}

	public Session login(Credentials credentials) throws LoginException, RepositoryException {
		throw new LoginException("Unsupport login(Credentials credentials)");
	}

	public Session login(String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
		throw new LoginException("login(String workspaceName)");
	}

	public Session login() throws LoginException, RepositoryException {
		throw new LoginException("login()");
	}

	public Session getSession(WorkspaceInfo workspaceInfo, Context context) throws RepositoryException {
		return new SessionImpl(this, this.storageFactory, workspaceInfo, context);
	}

	public void addUser(Credentials credentials, String workspaceName, String id, String pass)
			throws LoginException, NoSuchWorkspaceException, RepositoryException {
		EDVCredentials edvc = this.credentials2EDVCredentials(credentials);
		NodeInfo repositoryInfo = this.getRepositoryInfo();
		WorkspaceInfo workspaceInfo = this.getWorkspaceInfo(workspaceName);
		if (!workspaceInfo.isUser(edvc.getUserId(), new String(edvc.getPassword()))) {
			throw new LoginException("you don't access the workspace - " + workspaceName);
		} else if (workspaceInfo.isUser(id, pass)) {
			throw new RepositoryException(id + " alread exists");
		} else {
			NodeInfo usersInfo = this.getStorage().getChildNode(workspaceInfo, "edv:users");
			NodeInfo userNodeInfo = this.getStorage().addChildNode(usersInfo, UUIDGen.newUUID(), "edv:user");
			this.getStorage().addChildProperty(userNodeInfo, UUIDGen.newUUID(), "edv:id", id, 1);
			this.getStorage().addChildProperty(userNodeInfo, UUIDGen.newUUID(), "edv:pass", pass, 1);
			UserInfo userInfo = new UserInfo(userNodeInfo);
			userInfo.setId(pass);
			userInfo.setPass(pass);
			workspaceInfo.addUser(userInfo);
		}
	}

	public void createWorkspace(Credentials credentials, String workspaceName)
			throws LoginException, NoSuchWorkspaceException, RepositoryException {
		EDVCredentials edvc = this.credentials2EDVCredentials(credentials);
		NodeInfo repositoryInfo = this.getRepositoryInfo();
		NodeInfo nodeInfo = this.getStorage().getChildNode(repositoryInfo, workspaceName);
		if (nodeInfo != null) {
			throw new RepositoryException("There is a worksapce already  [Worksacpe : " + nodeInfo.getName() + "]");
		} else {
			nodeInfo = this.getStorage().addChildNode(repositoryInfo, UUIDGen.newUUID(), workspaceName);
			WorkspaceInfo workspaceInfo = new WorkspaceInfo(nodeInfo);
			this.getStorage().addChildProperty(workspaceInfo, UUIDGen.newUUID(), "edv:ownerId", edvc.getUserId(), 1);
			this.getStorage().addChildProperty(workspaceInfo, UUIDGen.newUUID(), "edv:ownerPass",
					new String(edvc.getPassword()), 1);
			this.getStorage().addChildProperty(workspaceInfo, UUIDGen.newUUID(), "edv:primaryType", "nt:workspace", 1);
			String mixins = "mix:lockable";
			this.getStorage().addChildProperty(workspaceInfo, UUIDGen.newUUID(), "edv:mixinTypes", mixins, 1);
			NodeInfo systemInfo = this.getStorage().addChildNode(workspaceInfo, UUIDGen.newUUID(), "edv:system");
			NodeInfo sessionsInfo = this.getStorage().addChildNode(workspaceInfo, UUIDGen.newUUID(), "edv:sessions");
			NodeInfo transactionsInfo = this.getStorage().addChildNode(workspaceInfo, UUIDGen.newUUID(),
					"edv:transactions");
			NodeInfo nodeTypesInfo = this.getStorage().addChildNode(workspaceInfo, UUIDGen.newUUID(), "edv:nodeTypes");
			NodeInfo usersInfo = this.getStorage().addChildNode(workspaceInfo, UUIDGen.newUUID(), "edv:users");
			NodeInfo userNodeInfo = this.getStorage().addChildNode(usersInfo, UUIDGen.newUUID(), "edv:user");
			this.getStorage().addChildProperty(userNodeInfo, UUIDGen.newUUID(), "edv:id", edvc.getUserId(), 1);
			this.getStorage().addChildProperty(userNodeInfo, UUIDGen.newUUID(), "edv:pass",
					new String(edvc.getPassword()), 1);
			UserInfo userInfo = new UserInfo(userNodeInfo);
			userInfo.setId(edvc.getUserId());
			userInfo.setPass(new String(edvc.getPassword()));
			NodeInfo rootInfo = this.getStorage().addChildNode(workspaceInfo, UUIDGen.newUUID(), "edv:root");
			this.getStorage().addChildProperty(rootInfo, UUIDGen.newUUID(), "edv:primaryType", "nt:root", 1);
			mixins = "mix:lockable";
			this.getStorage().addChildProperty(rootInfo, UUIDGen.newUUID(), "edv:mixinTypes", mixins, 1);
			workspaceInfo.setSystemNodeInfo(systemInfo);
			workspaceInfo.setSessionsNodeInfo(sessionsInfo);
			workspaceInfo.setTransactionNodeInfo(transactionsInfo);
			workspaceInfo.setNodeTypesNodeInfo(nodeTypesInfo);
			workspaceInfo.addUser(userInfo);
			this.createDefaultNamespace(edvc, workspaceInfo);
			Context context = ContextImpl.newContext(edvc, workspaceInfo);
			Session session = this.getSession((WorkspaceInfo) workspaceInfo, (Context) context);
			this.createDefaultNodeType(session, nodeTypesInfo);
			this.createSampleNamespace(edvc, workspaceInfo);
			this.createSampleNodeTypes(session, nodeTypesInfo);
			session.save();
		}
	}

	private void createDefaultNamespace(EDVCredentials edvc, WorkspaceInfo workspaceInfo) throws RepositoryException {
		this.context = ContextImpl.newContext(edvc, workspaceInfo);
		this.getStorage().registerNamespace("xml", "http://www.w3c.org/xml");
		this.getStorage().registerNamespace("xmlns", "http://www.w3c.org/XML/2002/namespace");
		this.getStorage().registerNamespace("", "http://www.unidocs.co.kr/cms");
		this.getStorage().registerNamespace("edv", "http://www.unidocs.co.kr/cms");
		this.getStorage().registerNamespace("jcr", "http://www.jcp.org/jcr/1.0");
		this.getStorage().registerNamespace("nt", "http://www.jcp.org/nt/1.0");
		this.getStorage().registerNamespace("mix", "http://www.jcp.org/mix/1.0");
		this.getStorage().registerNamespace("sv", "http://www.jcp.org/sv/1.0");
		this.getStorage().registerNamespace("edvfn", "http://www.unidocs.co.kr/cms/fn/1.0");
	}

	private void createSampleNamespace(EDVCredentials edvc, WorkspaceInfo workspaceInfo) throws RepositoryException {
		this.context = ContextImpl.newContext(edvc, workspaceInfo);
		this.getStorage().registerNamespace("kyobo", "http://www.kyobobook.co.kr/ebook/1.0");
		this.getStorage().registerNamespace("na", "http://www.na.co.kr/bill/1.0");
	}

	private void createSampleNodeTypes(Session session, NodeInfo nodeTypesInfo) throws RepositoryException {
		NodeTypeManagerImpl nodeTypeManager = (NodeTypeManagerImpl) session.getWorkspace().getNodeTypeManager();
		NodeTypeInfo bookContainerType = new NodeTypeInfo("nt:container", false, false, (String) null);
		bookContainerType.setSupertypes("nt:base");
		NodeDefInfo bookNodeDef = new NodeDefInfo("kyobo:book", false, false, 5, false, "nt:book", true);
		bookNodeDef.setRequiredPrimaryType("nt:book");
		bookContainerType.addNodeDef(bookNodeDef);
		nodeTypeManager.CreateNodeType(bookContainerType);
		NodeTypeInfo bookType = new NodeTypeInfo("nt:book", false, false, "kyobo:id");
		bookType.setSupertypes("nt:base");
		PropertyDefInfo book_id = new PropertyDefInfo("kyobo:id", false, true, 1, true, 1, false);
		bookType.addPropertyDef(book_id);
		PropertyDefInfo book_name = new PropertyDefInfo("kyobo:name", false, true, 1, true, 1, false);
		bookType.addPropertyDef(book_name);
		PropertyDefInfo book_pdf = new PropertyDefInfo("kyobo:pdf", false, true, 1, true, 2, false);
		bookType.addPropertyDef(book_pdf);
		PropertyDefInfo book_idx = new PropertyDefInfo("kyobo:idx", false, true, 1, true, 2, false);
		bookType.addPropertyDef(book_idx);
		nodeTypeManager.CreateNodeType(bookType);
		NodeTypeInfo billContainerType = new NodeTypeInfo("nt:billcontainer", false, false, (String) null);
		billContainerType.addSupertype("nt:base");
		NodeDefInfo billNodeDef = new NodeDefInfo("na:bill", false, false, 5, false, "nt:bill", true);
		billNodeDef.setRequiredPrimaryType("nt:bill");
		billContainerType.addNodeDef(billNodeDef);
		nodeTypeManager.CreateNodeType(billContainerType);
		NodeTypeInfo billType = new NodeTypeInfo("nt:bill", false, false, "na:id");
		billType.addSupertype("nt:base");
		billType.addSupertype("mix:versionable");
		PropertyDefInfo bill_id = new PropertyDefInfo("na:id", false, true, 1, true, 1, false);
		billType.addPropertyDef(bill_id);
		PropertyDefInfo bill_class = new PropertyDefInfo("na:class", false, true, 1, true, 1, false);
		billType.addPropertyDef(bill_class);
		PropertyDefInfo bill_name = new PropertyDefInfo("na:name", false, true, 1, true, 1, false);
		billType.addPropertyDef(bill_name);
		PropertyDefInfo bill_initiator = new PropertyDefInfo("na:initiator", false, true, 1, true, 1, false);
		billType.addPropertyDef(bill_initiator);
		PropertyDefInfo bill_alias = new PropertyDefInfo("na:alias", false, true, 1, true, 1, false);
		billType.addPropertyDef(bill_alias);
		PropertyDefInfo bill_date = new PropertyDefInfo("na:date", false, true, 1, true, 1, false);
		billType.addPropertyDef(bill_date);
		PropertyDefInfo bill_pdf = new PropertyDefInfo("na:pdf", false, true, 1, true, 2, false);
		billType.addPropertyDef(bill_pdf);
		nodeTypeManager.CreateNodeType(billType);
	}

	private void createDefaultNodeType(Session session, NodeInfo nodeTypesInfo) throws RepositoryException {
		NodeTypeManagerImpl nodeTypeManager = (NodeTypeManagerImpl) session.getWorkspace().getNodeTypeManager();
		NodeTypeInfo base = new NodeTypeInfo("nt:base", false, false, (String) null);
		PropertyDefInfo primaryType = new PropertyDefInfo("edv:primaryType", false, true, 4, true, 7, false);
		base.addPropertyDef(primaryType);
		PropertyDefInfo mixinTypes = new PropertyDefInfo("edv:mixinTypes", false, true, 4, true, 7, false);
		base.addPropertyDef(mixinTypes);
		nodeTypeManager.CreateNodeType(base);
		NodeTypeInfo unstructured = new NodeTypeInfo("nt:unstructured", false, true, (String) null);
		unstructured.setSupertypes("nt:base");
		PropertyDefInfo undefinedProperty = new PropertyDefInfo("*", false, false, 1, false, 0, false);
		unstructured.addPropertyDef(undefinedProperty);
		PropertyDefInfo undefinedProperty4Multiple = new PropertyDefInfo("*", false, false, 1, false, 0, true);
		unstructured.addPropertyDef(undefinedProperty4Multiple);
		NodeDefInfo undefinedNode = new NodeDefInfo("*", false, false, 2, false, "nt:unstructured", true);
		undefinedNode.setRequiredPrimaryType("nt:base");
		unstructured.addNodeDef(undefinedNode);
		nodeTypeManager.CreateNodeType(unstructured);
		NodeTypeInfo nodeType = new NodeTypeInfo("nt:nodeType", false, false, (String) null);
		nodeType.setSupertypes("nt:base");
		PropertyDefInfo nodeTypeName = new PropertyDefInfo("edv:nodeTypeName", false, true, 1, false, 7, false);
		nodeType.addPropertyDef(nodeTypeName);
		PropertyDefInfo supertypes = new PropertyDefInfo("edv:supertypes", false, true, 1, false, 7, false);
		nodeType.addPropertyDef(supertypes);
		PropertyDefInfo isMixin = new PropertyDefInfo("edv:isMixin", false, true, 1, false, 6, false);
		nodeType.addPropertyDef(isMixin);
		PropertyDefInfo hasOrderableChildNodes = new PropertyDefInfo("edv:hasOrderableChildNodes", false, true, 1,
				false, 6, false);
		nodeType.addPropertyDef(hasOrderableChildNodes);
		PropertyDefInfo primaryItemName = new PropertyDefInfo("edv:primaryItemName", false, true, 1, false, 1, false);
		nodeType.addPropertyDef(primaryItemName);
		NodeDefInfo propertyDef = new NodeDefInfo("edv:propertyDef", false, false, 2, false, "nt:unstructured", true);
		propertyDef.setRequiredPrimaryType("nt:propertyDef");
		nodeType.addNodeDef(propertyDef);
		NodeDefInfo childNodeDef = new NodeDefInfo("edv:childNodeDef", false, false, 2, false, "nt:unstructured", true);
		childNodeDef.setRequiredPrimaryType("nt:childNodeDef");
		nodeType.addNodeDef(childNodeDef);
		nodeTypeManager.CreateNodeType(nodeType);
		NodeTypeInfo propertyDefType = new NodeTypeInfo("nt:propertyDef", false, false, (String) null);
		propertyDefType.setSupertypes("nt:base");
		PropertyDefInfo namePD = new PropertyDefInfo("edv:name", false, false, 1, false, 7, false);
		propertyDefType.addPropertyDef(namePD);
		PropertyDefInfo autoCreatePD = new PropertyDefInfo("edv:autoCreate", false, true, 1, false, 6, false);
		propertyDefType.addPropertyDef(autoCreatePD);
		PropertyDefInfo mandatoryPD = new PropertyDefInfo("edv:mandatory", false, true, 1, false, 6, false);
		propertyDefType.addPropertyDef(mandatoryPD);
		PropertyDefInfo onParentVersionPD = new PropertyDefInfo("edv:onParentVersion", false, true, 1, false, 3, false);
		propertyDefType.addPropertyDef(onParentVersionPD);
		PropertyDefInfo protectedPD = new PropertyDefInfo("edv:protected", false, true, 1, false, 6, false);
		propertyDefType.addPropertyDef(protectedPD);
		PropertyDefInfo requiredTypePD = new PropertyDefInfo("edv:requiredType", false, true, 1, false, 1, false);
		propertyDefType.addPropertyDef(requiredTypePD);
		PropertyDefInfo valueConstraintsPD = new PropertyDefInfo("edv:valueConstraints", false, true, 1, false, 1,
				false);
		propertyDefType.addPropertyDef(valueConstraintsPD);
		PropertyDefInfo defaultValuesPD = new PropertyDefInfo("edv:defaultValues", false, true, 1, false, 0, false);
		propertyDefType.addPropertyDef(defaultValuesPD);
		PropertyDefInfo multiplePD = new PropertyDefInfo("edv:multiple", false, true, 1, false, 6, false);
		propertyDefType.addPropertyDef(multiplePD);
		nodeTypeManager.CreateNodeType(propertyDefType);
		NodeTypeInfo nodeDefType = new NodeTypeInfo("nt:childNodeDef", false, false, (String) null);
		nodeDefType.setSupertypes("nt:base");
		PropertyDefInfo nameND = new PropertyDefInfo("edv:name", false, false, 1, false, 7, false);
		nodeDefType.addPropertyDef(nameND);
		PropertyDefInfo autoCreateND = new PropertyDefInfo("edv:autoCreate", false, true, 1, false, 6, false);
		nodeDefType.addPropertyDef(autoCreateND);
		PropertyDefInfo mandatoryND = new PropertyDefInfo("edv:mandatory", false, true, 1, false, 6, false);
		nodeDefType.addPropertyDef(mandatoryND);
		PropertyDefInfo onParentVersionND = new PropertyDefInfo("edv:onParentVersion", false, true, 1, false, 3, false);
		nodeDefType.addPropertyDef(onParentVersionND);
		PropertyDefInfo protectedND = new PropertyDefInfo("edv:protected", false, true, 1, false, 6, false);
		nodeDefType.addPropertyDef(protectedND);
		PropertyDefInfo requiredPrimaryTypesND = new PropertyDefInfo("edv:requiredPrimaryTypes", false, true, 1, false,
				1, true);
		SimpleValueInfo defaultValue = new SimpleValueInfo("nt:base", 1);
		requiredPrimaryTypesND.addDefaultValue(defaultValue);
		nodeDefType.addPropertyDef(requiredPrimaryTypesND);
		PropertyDefInfo defaultPrimaryTypeND = new PropertyDefInfo("edv:defaultPrimaryType", false, false, 1, false, 1,
				false);
		nodeDefType.addPropertyDef(defaultPrimaryTypeND);
		PropertyDefInfo sameNameSibsND = new PropertyDefInfo("edv:sameNameSibs", false, true, 1, false, 6, false);
		nodeDefType.addPropertyDef(sameNameSibsND);
		nodeTypeManager.CreateNodeType(nodeDefType);
		NodeTypeInfo queryType = new NodeTypeInfo("nt:query", false, false, (String) null);
		queryType.setSupertypes("nt:base");
		PropertyDefInfo statement = new PropertyDefInfo("edv:statement", false, false, 1, false, 1, false);
		queryType.addPropertyDef(statement);
		PropertyDefInfo language = new PropertyDefInfo("edv:language", false, false, 1, false, 1, false);
		queryType.addPropertyDef(language);
		NodeDefInfo result = new NodeDefInfo("edv:result", false, false, 2, false, "nt:unstructured", true);
		result.setRequiredPrimaryType("nt:query");
		queryType.addNodeDef(result);
		nodeTypeManager.CreateNodeType(queryType);
		NodeTypeInfo lockableType = new NodeTypeInfo("mix:lockable", true, false, (String) null);
		lockableType.setSupertypes("");
		PropertyDefInfo lockowner = new PropertyDefInfo("edv:lockOwner", false, false, 1, false, 1, false);
		lockableType.addPropertyDef(lockowner);
		PropertyDefInfo lockisDeep = new PropertyDefInfo("edv:lockIsDeep", false, false, 1, false, 1, false);
		lockableType.addPropertyDef(lockisDeep);
		PropertyDefInfo locktoken = new PropertyDefInfo("edv:lockToken", false, false, 1, false, 1, false);
		lockableType.addPropertyDef(locktoken);
		nodeTypeManager.CreateNodeType(lockableType);
		NodeTypeInfo reposityrType = new NodeTypeInfo("nt:repository", false, false, (String) null);
		reposityrType.setSupertypes("nt:base");
		NodeDefInfo workspace = new NodeDefInfo("*", false, false, 2, false, "nt:workspace", false);
		workspace.setRequiredPrimaryType("nt:workspace");
		reposityrType.addNodeDef(workspace);
		nodeTypeManager.CreateNodeType(reposityrType);
		NodeTypeInfo workspaceType = new NodeTypeInfo("nt:workspace", false, false, (String) null);
		workspaceType.setSupertypes("nt:base");
		NodeDefInfo root = new NodeDefInfo("edv:root", true, true, 2, false, "nt:root", false);
		root.setRequiredPrimaryType("nt:root");
		workspaceType.addNodeDef(root);
		nodeTypeManager.CreateNodeType(workspaceType);
		NodeTypeInfo rootType = new NodeTypeInfo("nt:root", false, false, (String) null);
		rootType.setSupertypes("nt:unstructured");
		nodeTypeManager.CreateNodeType(rootType);
		NodeTypeInfo referencis = new NodeTypeInfo("nt:refrencis", false, false, (String) null);
		referencis.setSupertypes("nt:base");
		PropertyDefInfo referencis_reference = new PropertyDefInfo("edv:reference", false, false, 1, false, 9, true);
		referencis.addPropertyDef(referencis_reference);
		nodeTypeManager.CreateNodeType(referencis);
		NodeTypeInfo referenceable = new NodeTypeInfo("mix:refrenceable", true, false, (String) null);
		referenceable.setSupertypes("");
		NodeDefInfo referenceable_Referencis = new NodeDefInfo("edv:referencis", true, false, 5, false, "nt:refrencis",
				false);
		referenceable_Referencis.setRequiredPrimaryType("nt:refrencis");
		referenceable.addNodeDef(referenceable_Referencis);
		nodeTypeManager.CreateNodeType(referenceable);
		NodeTypeInfo frozenProperty = new NodeTypeInfo("nt:frozenProperty", false, false, (String) null);
		frozenProperty.addSupertype("nt:base");
		PropertyDefInfo frozenProperty_Uuide = new PropertyDefInfo("edv:frozenUuid", false, false, 5, false, 1, false);
		frozenProperty.addPropertyDef(frozenProperty_Uuide);
		PropertyDefInfo frozenProperty_Property = new PropertyDefInfo("*", false, false, 5, false, 0, true);
		frozenProperty.addPropertyDef(frozenProperty_Property);
		nodeTypeManager.CreateNodeType(frozenProperty);
		NodeTypeInfo SYSTEM_VERSIONEDCHILD_NT = new NodeTypeInfo("nt:versionedChild", false, false, (String) null);
		SYSTEM_VERSIONEDCHILD_NT.addSupertype("nt:base");
		PropertyDefInfo SYSTEM_VERSIONEDCHILD_PROPERTYDEF_CHILD = new PropertyDefInfo("edv:child", true, true, 5, true,
				9, false);
		SYSTEM_VERSIONEDCHILD_NT.addPropertyDef(SYSTEM_VERSIONEDCHILD_PROPERTYDEF_CHILD);
		nodeTypeManager.CreateNodeType(SYSTEM_VERSIONEDCHILD_NT);
		NodeTypeInfo frozenNode = new NodeTypeInfo("nt:frozenNode", false, false, (String) null);
		frozenNode.addSupertype("nt:base");
		PropertyDefInfo frozenNode_PrimaryType = new PropertyDefInfo("edv:frozenPrimaryType", false, false, 5, false, 1,
				false);
		frozenNode.addPropertyDef(frozenNode_PrimaryType);
		PropertyDefInfo frozenNode_MixinTypes = new PropertyDefInfo("edv:frozenMixinTypes", false, false, 5, false, 1,
				false);
		frozenNode.addPropertyDef(frozenNode_MixinTypes);
		PropertyDefInfo frozenNode_Uuid = new PropertyDefInfo("edv:frozenUuid", false, false, 5, false, 1, false);
		frozenNode.addPropertyDef(frozenNode_Uuid);
		NodeDefInfo frozenNode_ChildProperty = new NodeDefInfo("*", false, false, 5, false, "nt:frozenProperty", true);
		frozenNode_ChildProperty.setRequiredPrimaryType("nt:frozenProperty");
		frozenNode.addNodeDef(frozenNode_ChildProperty);
		NodeDefInfo frozenNode_ChildNode = new NodeDefInfo("*", false, false, 5, false, "nt:frozenNode", true);
		frozenNode_ChildNode.setRequiredPrimaryType("nt:frozenNode");
		frozenNode.addNodeDef(frozenNode_ChildNode);
		NodeDefInfo frozenNode_VersionedChildNode = new NodeDefInfo("edv:versionedNode", false, false, 5, false,
				"nt:versionedChild", true);
		frozenNode_VersionedChildNode.setRequiredPrimaryType("nt:versionedChild");
		frozenNode.addNodeDef(frozenNode_VersionedChildNode);
		nodeTypeManager.CreateNodeType(frozenNode);
		NodeTypeInfo SYSTEM_VERSION_NT = new NodeTypeInfo("nt:version", false, false, (String) null);
		SYSTEM_VERSION_NT.addSupertype("nt:base");
		PropertyDefInfo SYSTEM_VERSION_PROPERTYDEF_NAME = new PropertyDefInfo("edv:name", false, false, 5, false, 1,
				false);
		SYSTEM_VERSION_NT.addPropertyDef(SYSTEM_VERSION_PROPERTYDEF_NAME);
		PropertyDefInfo SYSTEM_VERSION_PROPERTYDEF_CREATED = new PropertyDefInfo("edv:created", false, false, 5, false,
				5, false);
		SYSTEM_VERSION_NT.addPropertyDef(SYSTEM_VERSION_PROPERTYDEF_CREATED);
		PropertyDefInfo SYSTEM_VERSION_PROPERTYDEF_PREDECESSORS = new PropertyDefInfo("edv:predecessors", false, false,
				5, false, 9, true);
		SYSTEM_VERSION_NT.addPropertyDef(SYSTEM_VERSION_PROPERTYDEF_PREDECESSORS);
		PropertyDefInfo SYSTEM_VERSION_PROPERTYDEF_SUCCESSORS = new PropertyDefInfo("edv:successors", false, false, 5,
				false, 9, true);
		SYSTEM_VERSION_NT.addPropertyDef(SYSTEM_VERSION_PROPERTYDEF_SUCCESSORS);
		NodeDefInfo SYSTEM_VERSION_CHILDNODEDEF_FROZENNODE = new NodeDefInfo("edv:frozenNode", false, false, 5, false,
				"nt:frozenNode", false);
		SYSTEM_VERSION_CHILDNODEDEF_FROZENNODE.setRequiredPrimaryType("nt:frozenNode");
		SYSTEM_VERSION_NT.addNodeDef(SYSTEM_VERSION_CHILDNODEDEF_FROZENNODE);
		nodeTypeManager.CreateNodeType(SYSTEM_VERSION_NT);
		NodeTypeInfo SYSTEM_VERSIONLABEL_NT = new NodeTypeInfo("nt:versionLabel", false, false, (String) null);
		SYSTEM_VERSIONLABEL_NT.addSupertype("nt:base");
		PropertyDefInfo SYSTEM_VERSIONLABEL_PROPERTYDEF_NAME = new PropertyDefInfo("edv:name", false, false, 5, false,
				1, false);
		SYSTEM_VERSIONLABEL_NT.addPropertyDef(SYSTEM_VERSIONLABEL_PROPERTYDEF_NAME);
		PropertyDefInfo SYSTEM_VERSIONLABEL_PROPERTYDEF_CREATED = new PropertyDefInfo("edv:created", false, false, 5,
				false, 5, false);
		SYSTEM_VERSIONLABEL_NT.addPropertyDef(SYSTEM_VERSIONLABEL_PROPERTYDEF_CREATED);
		PropertyDefInfo SYSTEM_VERSIONLABEL_PROPERTYDEF_DESCRIPTION = new PropertyDefInfo("edv:description", false,
				false, 5, false, 1, false);
		SYSTEM_VERSIONLABEL_NT.addPropertyDef(SYSTEM_VERSIONLABEL_PROPERTYDEF_DESCRIPTION);
		PropertyDefInfo SYSTEM_VERSIONLABEL_PROPERTYDEF_VERSION = new PropertyDefInfo("edv:version", false, false, 5,
				false, 9, false);
		SYSTEM_VERSIONLABEL_NT.addPropertyDef(SYSTEM_VERSIONLABEL_PROPERTYDEF_VERSION);
		nodeTypeManager.CreateNodeType(SYSTEM_VERSIONLABEL_NT);
		NodeTypeInfo SYSTEM_VERSIONLABELS_NT = new NodeTypeInfo("nt:versionLabels", false, false, (String) null);
		SYSTEM_VERSIONLABELS_NT.addSupertype("nt:base");
		NodeDefInfo SYSTEM_VERSIONLABELS_CHILDNODEDEF_LABEL = new NodeDefInfo("edv:versionLabel", false, false, 5,
				false, "nt:versionLabel", true);
		SYSTEM_VERSIONLABELS_CHILDNODEDEF_LABEL.setRequiredPrimaryType("nt:versionLabel");
		SYSTEM_VERSIONLABELS_NT.addNodeDef(SYSTEM_VERSIONLABELS_CHILDNODEDEF_LABEL);
		nodeTypeManager.CreateNodeType(SYSTEM_VERSIONLABELS_NT);
		NodeTypeInfo SYSTEM_VERSIONHISTORY_NT = new NodeTypeInfo("nt:versionHistory", false, false, (String) null);
		SYSTEM_VERSIONHISTORY_NT.addSupertype("nt:base");
		PropertyDefInfo SYSTEM_VERSIONHISTORY_PROPERTYDEF_ROOTVERION = new PropertyDefInfo("edv:rootVersion", false,
				false, 5, false, 9, false);
		SYSTEM_VERSIONHISTORY_NT.addPropertyDef(SYSTEM_VERSIONHISTORY_PROPERTYDEF_ROOTVERION);
		NodeDefInfo SYSTEM_VERSIONLABELS_CHILDNODEDEF_LABELS = new NodeDefInfo("edv:versionLabels", false, false, 5,
				false, "nt:versionLabel", false);
		SYSTEM_VERSIONLABELS_CHILDNODEDEF_LABELS.setRequiredPrimaryType("nt:versionLabels");
		SYSTEM_VERSIONHISTORY_NT.addNodeDef(SYSTEM_VERSIONLABELS_CHILDNODEDEF_LABELS);
		NodeDefInfo SYSTEM_VERSIONLABELS_CHILDNODEDEF_VERSIONS = new NodeDefInfo("edv:version", false, false, 5, false,
				"nt:versionLabel", true);
		SYSTEM_VERSIONLABELS_CHILDNODEDEF_VERSIONS.setRequiredPrimaryType("nt:version");
		SYSTEM_VERSIONHISTORY_NT.addNodeDef(SYSTEM_VERSIONLABELS_CHILDNODEDEF_VERSIONS);
		nodeTypeManager.CreateNodeType(SYSTEM_VERSIONHISTORY_NT);
		NodeTypeInfo SYSTEM_VERSIONSTORAGE_NT = new NodeTypeInfo("nt:versionStorage", false, false, (String) null);
		nodeTypeManager.CreateNodeType(SYSTEM_VERSIONSTORAGE_NT);
		NodeTypeInfo SYSTEM_VERSIONABLE_MIX = new NodeTypeInfo("mix:versionable", false, false, (String) null);
		PropertyDefInfo SYSTEM_VERSIONABLE_PROPERTYDEF_HISTORY = new PropertyDefInfo("edv:versionHistory", false, false,
				5, false, 9, false);
		SYSTEM_VERSIONABLE_MIX.addPropertyDef(SYSTEM_VERSIONABLE_PROPERTYDEF_HISTORY);
		PropertyDefInfo SYSTEM_VERSIONABLE_PROPERTYDEF_BASE = new PropertyDefInfo("edv:baseVersion", false, false, 5,
				false, 9, false);
		SYSTEM_VERSIONABLE_MIX.addPropertyDef(SYSTEM_VERSIONABLE_PROPERTYDEF_BASE);
		PropertyDefInfo SYSTEM_VERSIONABLE_PROPERTYDEF_ISCHECKEDOUT = new PropertyDefInfo("edv:isCheckedOut", true,
				true, 5, false, 6, false);
		SYSTEM_VERSIONABLE_PROPERTYDEF_ISCHECKEDOUT.addDefaultValue(new SimpleValueInfo("true", 6));
		SYSTEM_VERSIONABLE_MIX.addPropertyDef(SYSTEM_VERSIONABLE_PROPERTYDEF_ISCHECKEDOUT);
		PropertyDefInfo SYSTEM_VERSIONABLE_PROPERTYDEF_USER = new PropertyDefInfo("edv:user", false, false, 5, false, 1,
				false);
		SYSTEM_VERSIONABLE_MIX.addPropertyDef(SYSTEM_VERSIONABLE_PROPERTYDEF_USER);
		PropertyDefInfo SYSTEM_VERSIONABLE_PROPERTYDEF_PREDECESSORS = new PropertyDefInfo("edv:predecessors", false,
				false, 5, false, 9, true);
		SYSTEM_VERSIONABLE_MIX.addPropertyDef(SYSTEM_VERSIONABLE_PROPERTYDEF_PREDECESSORS);
		PropertyDefInfo SYSTEM_VERSIONABLE_PROPERTYDEF_MERGEFAILED = new PropertyDefInfo("edv:mergeFailed", false,
				false, 5, false, 9, true);
		SYSTEM_VERSIONABLE_MIX.addPropertyDef(SYSTEM_VERSIONABLE_PROPERTYDEF_MERGEFAILED);
		NodeDefInfo SYSTEM_VERSIONSTORAGE_CHILDNODEDEF_HISTORY = new NodeDefInfo("edv:versionHistory", false, false, 5,
				false, "nt:versionHistory", true);
		SYSTEM_VERSIONSTORAGE_CHILDNODEDEF_HISTORY.setRequiredPrimaryType("nt:versionHistory");
		SYSTEM_VERSIONABLE_MIX.addNodeDef(SYSTEM_VERSIONSTORAGE_CHILDNODEDEF_HISTORY);
		nodeTypeManager.CreateNodeType(SYSTEM_VERSIONABLE_MIX);
		NodeTypeInfo SYSTEM_FILE_NT = new NodeTypeInfo("nt:file", false, false, (String) null);
		SYSTEM_FILE_NT.addSupertype("nt:base");
		PropertyDefInfo SYSTEM_FILE_PROPERTYDEF_DISPLAYNAME = new PropertyDefInfo("edv:displayname", false, false, 1,
				false, 1, false);
		SYSTEM_FILE_NT.addPropertyDef(SYSTEM_FILE_PROPERTYDEF_DISPLAYNAME);
		PropertyDefInfo SYSTEM_FILE_PROPERTYDEF_REALNAME = new PropertyDefInfo("edv:realname", false, false, 1, false,
				1, false);
		SYSTEM_FILE_NT.addPropertyDef(SYSTEM_FILE_PROPERTYDEF_REALNAME);
		PropertyDefInfo SYSTEM_FILE_PROPERTYDEF_EXTENSION = new PropertyDefInfo("edv:extension", false, false, 1, false,
				1, false);
		SYSTEM_FILE_NT.addPropertyDef(SYSTEM_FILE_PROPERTYDEF_EXTENSION);
		PropertyDefInfo SYSTEM_FILE_PROPERTYDEF_DISPLAYAPP = new PropertyDefInfo("edv:dispayApp", false, false, 1,
				false, 1, false);
		SYSTEM_FILE_NT.addPropertyDef(SYSTEM_FILE_PROPERTYDEF_DISPLAYAPP);
		PropertyDefInfo SYSTEM_FILE_PROPERTYDEF_EDITAPP = new PropertyDefInfo("edv:editApp", false, false, 1, false, 1,
				false);
		SYSTEM_FILE_NT.addPropertyDef(SYSTEM_FILE_PROPERTYDEF_EDITAPP);
		PropertyDefInfo SYSTEM_FILE_PROPERTYDEF_STREAM = new PropertyDefInfo("edv:stream", false, false, 1, false, 2,
				false);
		SYSTEM_FILE_NT.addPropertyDef(SYSTEM_FILE_PROPERTYDEF_STREAM);
		nodeTypeManager.CreateNodeType(SYSTEM_FILE_NT);
		NodeTypeInfo SYSTEM_FILELIST_NT = new NodeTypeInfo("nt:fileList", false, false, (String) null);
		SYSTEM_FILELIST_NT.addSupertype("nt:base");
		NodeDefInfo SYSTEM_FILELIST_CHILDNODEDEF_FILE = new NodeDefInfo("edv:file", false, false, 1, false, "nt:file",
				true);
		SYSTEM_FILELIST_CHILDNODEDEF_FILE.setRequiredPrimaryType("nt:file");
		SYSTEM_FILELIST_NT.addNodeDef(SYSTEM_FILELIST_CHILDNODEDEF_FILE);
		nodeTypeManager.CreateNodeType(SYSTEM_FILELIST_NT);
	}

	public void deleteWorkspace(Credentials credentials, String workspaceName)
			throws LoginException, NoSuchWorkspaceException, RepositoryException {
		EDVCredentials edvc = this.credentials2EDVCredentials(credentials);
		NodeInfo repositoryInfo = this.getRepositoryInfo();
		NodeInfo workspaceInfo = this.getStorage().getChildNode(repositoryInfo, workspaceName);
		if (workspaceInfo == null) {
			throw new NoSuchWorkspaceException(workspaceName + " is not existed");
		} else {
			PropertyInfo ownerIdInfo = this.getStorage().getChildProperty(workspaceInfo, "edv:ownerId");
			String ownerId = ownerIdInfo.getValueInfo().getString();
			PropertyInfo ownerPassInfo = this.getStorage().getChildProperty(workspaceInfo, "edv:ownerPass");
			String ownerPass = ownerPassInfo.getValueInfo().getString();
			if (!edvc.getUserId().equals(ownerId)) {
				throw new LoginException("you don't access the workspace - " + workspaceName);
			} else if (!(new String(edvc.getPassword())).equals(ownerPass)) {
				throw new LoginException("invalid pass");
			} else {
				if (cashedWorkspace.containsKey(workspaceName)) {
					cashedWorkspace.remove(workspaceName);
				}

				this.getStorage().deleteNode(workspaceInfo, true);
			}
		}
	}

	public VersionStorage getVersionStorage(SessionImpl session) throws RepositoryException {
		NodeInfo versionStorageInfo = this.getStorage().getChildNode(this.getRepositoryInfo(), "edv:versionStorage");
		return new VersionStorage(this, versionStorageInfo, session, session.getStorage());
	}
}