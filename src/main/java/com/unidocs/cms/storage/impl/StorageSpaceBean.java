// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
package com.unidocs.cms.storage.impl;

import com.unidocs.cms.ItemNotFoundException;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.info.InfoUtil;
import com.unidocs.cms.info.ItemInfo;
import com.unidocs.cms.info.NodeInfo;
import com.unidocs.cms.info.PropertyInfo;
import com.unidocs.cms.info.RowInfo;
import com.unidocs.cms.info.TransactionInfo;
import com.unidocs.cms.info.ValueInfo;
import com.unidocs.cms.info.cache.CachedNodeInfo;
import com.unidocs.cms.internal.common.Logger;
import com.unidocs.cms.internal.system.ServiceInternalManager;
import com.unidocs.cms.storage.LocalStorageSpace;
import com.unidocs.cms.storage.impl.dao.DaoFactory;
import com.unidocs.cms.storage.impl.dao.NamespaceLocal;
import com.unidocs.cms.storage.impl.dao.NamespaceOR;
import com.unidocs.cms.storage.impl.dao.NodeFlagLocal;
import com.unidocs.cms.storage.impl.dao.NodeFlagOR;
import com.unidocs.cms.storage.impl.dao.NodeLocal;
import com.unidocs.cms.storage.impl.dao.NodeOR;
import com.unidocs.cms.storage.impl.dao.PropertyFlagLocal;
import com.unidocs.cms.storage.impl.dao.PropertyFlagOR;
import com.unidocs.cms.storage.impl.dao.PropertyLocal;
import com.unidocs.cms.storage.impl.dao.PropertyOR;
import com.unidocs.cms.storage.impl.dao.PropertyValueSetLocal;
import com.unidocs.cms.storage.impl.dao.PropertyValueSetOR;
import com.unidocs.cms.storage.impl.dao.QueryExecutorLocal;
import com.unidocs.cms.storage.impl.dao.SessionFlagLocal;
import com.unidocs.cms.storage.impl.dao.SessionFlagOR;
import com.unidocs.cms.storage.impl.dao.ValueLocal;
import com.unidocs.cms.storage.impl.dao.ValueOR;
import com.unidocs.cms.storage.impl.ns.NameSapceCacheService;
import com.unidocs.cms.util.UUIDGen;
import java.util.ArrayList;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class StorageSpaceBean implements SessionBean, LocalStorageSpace {

	private SessionContext sessionContext;

	public StorageSpaceBean() {
		sessionContext = null;
	}

	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
		Context _ctx = null;
		try {
			_ctx = new InitialContext();
		} catch (NamingException ne) {
			Logger.fatal(Logger.LOG_RANGE_ENGINE,
					"StorageSpaceBean.setSessionContext() : fails to make a new InitialContext object.", ne);
			throw new EJBException("StorageSpaceBean.setSessionContext() : fails to make a new InitialContext object.",
					ne);
		}
	}

	public void ejbCreate() throws CreateException {
	}

	public void ejbActivate() {
	}

	public void ejbPassivate() {
	}

	public void ejbRemove() {
	}

	public String getName(com.unidocs.cms.info.Context context) {
		return (new StringBuilder("edv:transaction_")).append(context.getId()).toString();
	}

	public NodeInfo getRootNode(com.unidocs.cms.info.Context context)
			throws ItemNotFoundException, RepositoryException {
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000000");
		if (nodeOR == null) {
			throw new ItemNotFoundException("StorageSpaceBean.getRootNode can't find root node");
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
			return nodeInfo;
		}
	}

	public boolean hasNode(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		return nodeOR != null;
	}

	public boolean hasChildNode(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findFistNode(suid, tuid, nodeId);
		return nodeOR != null;
	}

	public NodeInfo getNode(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null) {
			Logger.debug(Logger.LOG_RANGE_ENGINE,
					(new StringBuilder("StorageSpaceBean.getNode(")).append(nodeId).append(")").toString());
			Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder("suid : ")).append(suid).toString());
			Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder("tuid : ")).append(tuid).toString());
			Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder("nodeOR : ")).append(nodeOR).toString());
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
			return nodeInfo;
		}
	}

	public NodeInfo getParentNode(com.unidocs.cms.info.Context context, String itemId)
			throws ItemNotFoundException, RepositoryException {
		if (itemId.equals("00000000-0000-0000-0000-000000000000"))
			return null;
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, itemId);
		if (nodeOR != null)
			return getNode(context, nodeOR.getPuid());
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, itemId);
		if (propertyOR != null)
			return getNode(context, propertyOR.getPuid());
		else
			throw new ItemNotFoundException(
					(new StringBuilder("The item[id: ")).append(itemId).append("] is not existed").toString());
	}

	public boolean hasNode(com.unidocs.cms.info.Context context, String parentNodeId, String name)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(name);
		String url = findUrl(context.getWorkspaceId(), name);
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		if (prefix.equals(""))
			return nodeLocal.count(suid, tuid, parentNodeId, name) > 0L;
		return nodeLocal.count(suid, tuid, parentNodeId, url, name) > 0L;
	}

	public NodeInfo getChildNode(com.unidocs.cms.info.Context context, String parentNodeId, String name)
			throws ItemNotFoundException, RepositoryException {
		NodeOR nodeOR = null;
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(name);
		String localName = InfoUtil.getLocalName(name);
		String url = findUrl(context.getWorkspaceId(), name);
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		if (prefix.equals(""))
			nodeOR = nodeLocal.findNode(suid, tuid, parentNodeId, localName);
		else
			nodeOR = nodeLocal.findNode(suid, tuid, parentNodeId, url, localName);
		if (nodeOR == null) {
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
			return nodeInfo;
		}
	}

	public NodeInfo[] getChildNodes(com.unidocs.cms.info.Context context, String parentNodeId)
			throws ItemNotFoundException, RepositoryException {
		ArrayList result = new ArrayList();
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		ArrayList nodeORs = nodeLocal.findNodes(suid, tuid, parentNodeId);
		for (int i = 0; i < nodeORs.size(); i++) {
			NodeOR nodeOR = (NodeOR) nodeORs.get(i);
			NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
			result.add(nodeInfo);
		}

		return (NodeInfo[]) result.toArray(new NodeInfo[0]);
	}

	public NodeInfo getFirstChildNode(com.unidocs.cms.info.Context context, String parentNodeId)
			throws ItemNotFoundException, RepositoryException {
		NodeOR nodeOR = null;
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		nodeOR = nodeLocal.findFistNode(suid, tuid, parentNodeId);
		if (nodeOR == null) {
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
			return nodeInfo;
		}
	}

	public NodeInfo getLastChildNode(com.unidocs.cms.info.Context context, String parentNodeId)
			throws ItemNotFoundException, RepositoryException {
		NodeOR nodeOR = null;
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		nodeOR = nodeLocal.findLastNode(suid, tuid, parentNodeId);
		if (nodeOR == null) {
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
			return nodeInfo;
		}
	}

	public NodeInfo getNextNode(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The node[id: ")).append(nodeId).append("] is not existed").toString());
		NodeOR next = nodeLocal.findNextNode(suid, tuid, nodeOR.getPuid(), nodeOR.getId());
		if (next == null) {
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, next);
			return nodeInfo;
		}
	}

	public NodeInfo getPrevNode(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The node[id: ")).append(nodeId).append("] is not existed").toString());
		NodeOR next = nodeLocal.findPrevNode(suid, tuid, nodeOR.getPuid(), nodeOR.getId());
		if (next == null) {
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, next);
			return nodeInfo;
		}
	}

	public PropertyInfo getProperty(com.unidocs.cms.info.Context context, String propertyId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyValueSetLocal propertyValueSetLocal = DaoFactory.createPropertyValueSetLocal();
		PropertyValueSetOR propertyValueSetOR = propertyValueSetLocal.findProperty(suid, tuid, propertyId);
		PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyValueSetOR);
		return propertyInfo;
	}

	public boolean hasProperty(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		long childSize = propertyLocal.count(suid, tuid, nodeId);
		return childSize > 0L;
	}

	public boolean hasProperty(com.unidocs.cms.info.Context context, String nodeId, String name)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(name);
		String localName = InfoUtil.getLocalName(name);
		String url = findUrl(context.getWorkspaceId(), name);
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		long childSize = 0L;
		if (prefix.equals(""))
			childSize = propertyLocal.count(suid, tuid, nodeId, localName);
		else
			childSize = propertyLocal.count(suid, tuid, nodeId, url, localName);
		return childSize > 0L;
	}

	public PropertyInfo getChildProperty(com.unidocs.cms.info.Context context, String nodeId, long index)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, nodeId, index);
		return getPropertyInfo(propertyOR);
	}

	public PropertyInfo getChildProperty(com.unidocs.cms.info.Context context, String nodeId, String name)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(name);
		String localName = InfoUtil.getLocalName(name);
		String url = findUrl(context.getWorkspaceId(), name);
		PropertyValueSetLocal propertyValueSetLocal = DaoFactory.createPropertyValueSetLocal();
		PropertyValueSetOR propertyValueSetOR = null;
		if (prefix.equals(""))
			propertyValueSetOR = propertyValueSetLocal.findProperty(suid, tuid, nodeId, localName);
		else
			propertyValueSetOR = propertyValueSetLocal.findProperty(suid, tuid, nodeId, url, localName);
		PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyValueSetOR);
		return propertyInfo;
	}

	public PropertyInfo[] getChildProperties(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		ArrayList propertyInfos = new ArrayList();
		PropertyValueSetLocal propertyValueSetLocal = DaoFactory.createPropertyValueSetLocal();
		ArrayList propertyValueSetORList = propertyValueSetLocal.findProperties(suid, tuid, nodeId);
		for (int i = 0; i < propertyValueSetORList.size(); i++) {
			PropertyValueSetOR propertyValueSetOR = (PropertyValueSetOR) propertyValueSetORList.get(i);
			PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyValueSetOR);
			propertyInfos.add(propertyInfo);
		}

		return (PropertyInfo[]) propertyInfos.toArray(new PropertyInfo[0]);
	}

	public PropertyInfo getFirstChildProperty(com.unidocs.cms.info.Context context, String parentId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyValueSetLocal propertyValueSetLocal = DaoFactory.createPropertyValueSetLocal();
		PropertyValueSetOR propertyValueSetOR = propertyValueSetLocal.findFistProperty(suid, tuid, parentId);
		PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyValueSetOR);
		return propertyInfo;
	}

	private PropertyInfo getPropertyInfo(PropertyOR propertyOR) {
		if (propertyOR == null) {
			return null;
		} else {
			ValueLocal valueLocal = DaoFactory.createValueLocal();
			ValueOR valueOR = valueLocal.findByPrimaryKey(propertyOR.getId());
			PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyOR, valueOR);
			return propertyInfo;
		}
	}

	public PropertyInfo getLastChildProperty(com.unidocs.cms.info.Context context, String parentId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyValueSetLocal propertyValueSetLocal = DaoFactory.createPropertyValueSetLocal();
		PropertyValueSetOR propertyValueSetOR = propertyValueSetLocal.findLastProperty(suid, tuid, parentId);
		PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyValueSetOR);
		return propertyInfo;
	}

	public PropertyInfo getNextProperty(com.unidocs.cms.info.Context context, String propertyId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
		if (propertyOR == null) {
			throw new ItemNotFoundException(
					(new StringBuilder("The property[id: ")).append(propertyId).append("] is not existed").toString());
		} else {
			PropertyOR next = propertyLocal.findNextProperty(suid, tuid, propertyOR.getPuid(), propertyOR.getId());
			return getPropertyInfo(next);
		}
	}

	public PropertyInfo getPrevProperty(com.unidocs.cms.info.Context context, String propertyId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
		if (propertyOR == null) {
			throw new ItemNotFoundException(
					(new StringBuilder("The property[id: ")).append(propertyId).append("] is not existed").toString());
		} else {
			PropertyOR prev = propertyLocal.findPrevProperty(suid, tuid, propertyOR.getPuid(), propertyOR.getId());
			return getPropertyInfo(prev);
		}
	}

	private void setSessionFlagORTree(com.unidocs.cms.info.Context context, SessionFlagOR sessionFlagOR)
			throws ItemNotFoundException, RepositoryException {
		SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
		sessionFlagLocal.store(sessionFlagOR);
		SessionFlagOR parentFlag = sessionFlagLocal.findByPrimaryKey(sessionFlagOR.getSuid(), sessionFlagOR.getPuid());
		if (parentFlag == null) {
			NodeInfo parentNode = getParentNode(context, sessionFlagOR.getPuid());
			if (parentNode != null) {
				int sstate = 0;
				if (sessionFlagOR.getType().equals("P"))
					sstate = 13;
				parentFlag = new SessionFlagOR(sessionFlagOR.getSuid(), sessionFlagOR.getPuid(), parentNode.getId(),
						sstate, "N");
				setSessionFlagORTree(context, parentFlag);
			}
		} else if (sessionFlagOR.getType().equals("P") && parentFlag.getSstate() == 0) {
			parentFlag.setSstate(13);
			sessionFlagLocal.update(parentFlag);
		}
	}

	public NodeInfo addChildNode(com.unidocs.cms.info.Context context, String parentNodeId, String childNodeId,
			String name) throws ItemNotFoundException, RepositoryException {
		String suid = "00000000-0000-0000-0000-000000000000";
		String tuid = "00000000-0000-0000-0000-000000000000";
		int sstate = 0;
		int tstate = 0;
		if (context.isInSession()) {
			suid = context.getId();
			sstate = 11;
			SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, childNodeId, parentNodeId, sstate, "N");
			setSessionFlagORTree(context, sessionFlagOR);
		} else if (context.isInTransaction()) {
			tuid = context.getTransactionInfo().getId();
			tstate = 31;
		}
		String prefix = InfoUtil.getPrefix(name);
		String localName = InfoUtil.getLocalName(name);
		String url = findUrl(context.getWorkspaceId(), name);
		if (url.equals("")) {
			throw new RepositoryException(
					(new StringBuilder("The Prefix - ")).append(prefix).append(" is not supported").toString());
		} else {
			NodeOR nodeOR = new NodeOR();
			nodeOR.setId("autoCreate");
			nodeOR.setUuid(childNodeId);
			nodeOR.setPuid(parentNodeId);
			nodeOR.setSuid(suid);
			nodeOR.setTuid(tuid);
			nodeOR.setSstate(sstate);
			nodeOR.setTstate(tstate);
			nodeOR.setUrl(url);
			nodeOR.setPrefix(prefix);
			nodeOR.setName(localName);
			NodeLocal nodeLocal = DaoFactory.createNodeLocal();
			nodeLocal.create(nodeOR);
			return getNode(context, childNodeId);
		}
	}

	public NodeInfo moveNode(com.unidocs.cms.info.Context context, String parentNodeId, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		int sstate = 0;
		int tstate = 0;
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The Node[ID: ")).append(nodeId).append("] is not existed").toString());
		suid = "00000000-0000-0000-0000-000000000000";
		tuid = "00000000-0000-0000-0000-000000000000";
		if (context.isInSession()) {
			suid = context.getId();
			sstate = nodeOR.getSstate();
			if (sstate == 11) {
				nodeOR.setPuid(parentNodeId);
				nodeLocal.update(nodeOR);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, nodeId, parentNodeId, 13, "N");
				SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
				sessionFlagLocal.store(sessionFlagOR);
			} else if (sstate == 14) {
				nodeOR.setPuid(parentNodeId);
				nodeLocal.update(nodeOR);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, nodeId, parentNodeId, 13, "N");
				SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
				sessionFlagLocal.store(sessionFlagOR);
			} else {
				NodeFlagOR deleteFlag = new NodeFlagOR(nodeOR.getId(), suid);
				NodeFlagLocal nodeFlagLocal = DaoFactory.createNodeFlagLocal();
				nodeFlagLocal.store(deleteFlag);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, nodeId, parentNodeId, 12, "N");
				setSessionFlagORTree(context, sessionFlagOR);
				nodeOR.setPuid(parentNodeId);
				nodeOR.setSuid(suid);
				nodeOR.setSstate(14);
				nodeLocal.create(nodeOR);
			}
		} else if (context.isInTransaction()) {
			tuid = context.getTransactionInfo().getId();
			tstate = nodeOR.getTstate();
			if (tstate == 31) {
				nodeOR.setPuid(parentNodeId);
				nodeLocal.update(nodeOR);
			} else if (sstate == 34) {
				nodeOR.setPuid(parentNodeId);
				nodeLocal.update(nodeOR);
			} else {
				nodeOR.setTuid(tuid);
				nodeOR.setTstate(32);
				nodeLocal.update(nodeOR);
				nodeOR.setPuid(parentNodeId);
				nodeOR.setTuid(tuid);
				nodeOR.setTstate(34);
				nodeLocal.create(nodeOR);
			}
		} else {
			nodeOR.setPuid(parentNodeId);
			nodeLocal.update(nodeOR);
		}
		return getNode(context, nodeId);
	}

	public NodeInfo deleteNode(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		int sstate = 0;
		int tstate = 0;
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The Node[ID: ")).append(nodeId).append("] is not existed").toString());
		sstate = nodeOR.getSstate();
		tstate = nodeOR.getTstate();
		if (context.isInSession()) {
			suid = context.getId();
			if (sstate == 11)
				nodeLocal.delete(nodeOR);
			else if (sstate == 14) {
				nodeLocal.delete(nodeOR);
			} else {
				Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder(String.valueOf(nodeOR.getId())))
						.append("를 NodeFlag에 담습니다. suid : ").append(suid).toString());
				NodeFlagOR deleteFlag = new NodeFlagOR(nodeOR.getId(), suid);
				NodeFlagLocal nodeFlagLocal = DaoFactory.createNodeFlagLocal();
				nodeFlagLocal.store(deleteFlag);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, nodeId, nodeOR.getPuid(), 12, "N");
				setSessionFlagORTree(context, sessionFlagOR);
			}
		} else if (context.isInTransaction()) {
			tuid = context.getTransactionInfo().getId();
			if (tstate == 31)
				nodeLocal.delete(nodeOR);
			else if (tstate == 34) {
				nodeLocal.delete(nodeOR);
			} else {
				nodeOR.setTuid(tuid);
				nodeOR.setTstate(32);
				nodeLocal.update(nodeOR);
			}
		} else {
			suid = UUIDGen.newUUID();
			Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder(String.valueOf(nodeOR.getId())))
					.append("를 그냥 삭제 합니다.. suid : ").append(suid).toString());
			SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
			sessionFlagLocal.buidDeleteItemTree(Integer.parseInt(nodeOR.getId()), suid,
					"00000000-0000-0000-0000-000000000000");
			sessionFlagLocal.removeItemsByDeleteItemTreeBySuid(suid);
			nodeLocal.delete(nodeOR);
			propertyLocal.deleteByPuid(nodeOR.getUuid());
		}
		NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
		return nodeInfo;
	}

	public PropertyInfo addChildProperty(com.unidocs.cms.info.Context context, String parentNodeId, String propertyId,
			String qname, String value, int type) throws ItemNotFoundException, RepositoryException {
		String suid = "00000000-0000-0000-0000-000000000000";
		String tuid = "00000000-0000-0000-0000-000000000000";
		int sstate = 0;
		int tstate = 0;
		if (context.isInSession()) {
			suid = context.getId();
			sstate = 11;
			SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, propertyId, parentNodeId, 11, "P");
			setSessionFlagORTree(context, sessionFlagOR);
		} else if (context.isInTransaction()) {
			tuid = context.getTransactionInfo().getId();
			tstate = 31;
		}
		String prefix = InfoUtil.getPrefix(qname);
		String localName = InfoUtil.getLocalName(qname);
		String url = findUrl(context.getWorkspaceId(), qname);
		if (url.equals("")) {
			throw new RepositoryException(
					(new StringBuilder("The Prefix - ")).append(prefix).append(" is not supported").toString());
		} else {
			PropertyOR propertyOR = new PropertyOR();
			propertyOR.setId("autoCreate");
			propertyOR.setUuid(propertyId);
			propertyOR.setPuid(parentNodeId);
			propertyOR.setSuid(suid);
			propertyOR.setTuid(tuid);
			propertyOR.setSstate(sstate);
			propertyOR.setTstate(tstate);
			propertyOR.setUrl(url);
			propertyOR.setPrefix(prefix);
			propertyOR.setName(localName);
			PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
			propertyLocal.create(propertyOR);
			propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
			ValueOR valueOR = new ValueOR(propertyOR.getId(), type, value);
			ValueLocal valueLocal = DaoFactory.createValueLocal();
			valueLocal.create(valueOR);
			PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyOR, valueOR);
			return propertyInfo;
		}
	}

	public PropertyInfo setPropertyValue(com.unidocs.cms.info.Context context, String propertyId, String value,
			int type) throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		int sstate = 0;
		int tstate = 0;
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		ValueLocal valueLocal = DaoFactory.createValueLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
		if (propertyOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The Property[ID: ")).append(propertyId).append("] is not existed").toString());
		ValueOR valueOR = valueLocal.findByPrimaryKey(propertyOR.getId());
		suid = "00000000-0000-0000-0000-000000000000";
		tuid = "00000000-0000-0000-0000-000000000000";
		if (context.isInSession()) {
			suid = context.getId();
			sstate = propertyOR.getSstate();
			if (sstate == 11) {
				valueOR.setType(type);
				valueOR.setValue(value);
				valueLocal.update(valueOR);
			} else if (sstate == 14 || sstate == 13) {
				valueOR.setType(type);
				valueOR.setValue(value);
				valueLocal.update(valueOR);
			} else {
				PropertyFlagOR deleteFlag = new PropertyFlagOR(propertyOR.getId(), suid);
				PropertyFlagLocal propertyFlagLocal = DaoFactory.createPropertyFlagLocal();
				propertyFlagLocal.store(deleteFlag);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, propertyId, propertyOR.getPuid(), 13, "P");
				setSessionFlagORTree(context, sessionFlagOR);
				propertyOR.setSuid(suid);
				propertyOR.setSstate(13);
				propertyLocal.create(propertyOR);
				tuid = context.isInTransaction()
						? context.getTransactionInfo().getId()
						: "00000000-0000-0000-0000-000000000000";
				propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
				valueOR.setId(propertyOR.getId());
				valueOR.setType(type);
				valueOR.setValue(value);
				valueLocal.create(valueOR);
			}
		} else if (context.isInTransaction()) {
			tuid = context.getTransactionInfo().getId();
			tstate = propertyOR.getTstate();
			if (tstate == 31) {
				valueOR.setType(type);
				valueOR.setValue(value);
				valueLocal.update(valueOR);
			} else if (sstate == 14 || sstate == 33) {
				valueOR.setType(type);
				valueOR.setValue(value);
				valueLocal.update(valueOR);
			} else {
				Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder("속성을 삭제합니다. UUID : "))
						.append(propertyOR.getUuid()).append("TUID").append(tuid).toString());
				propertyOR.setTuid(tuid);
				propertyOR.setTstate(32);
				propertyLocal.store(propertyOR);
				propertyOR.setSuid(suid);
				propertyOR.setTstate(33);
				propertyLocal.create(propertyOR);
				suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
				propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
				valueOR.setId(propertyOR.getId());
				valueOR.setType(type);
				valueOR.setValue(value);
				valueLocal.create(valueOR);
			}
		} else {
			suid = UUIDGen.newUUID();
			propertyOR.setSuid(suid);
			propertyLocal.create(propertyOR);
			PropertyOR newPropertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
			valueOR.setId(newPropertyOR.getId());
			valueLocal.create(valueOR);
			Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder("속성을 삭제 합니다, ")).append(propertyOR.getId())
					.append("를 그냥 삭제 합니다. suid : ").append(suid).toString());
			SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
			sessionFlagLocal.addProperty2DeleteTree(Integer.parseInt(newPropertyOR.getId()), suid, tuid);
			sessionFlagLocal.removeItemsByDeleteItemTreeBySuid(suid);
			suid = "00000000-0000-0000-0000-000000000000";
			propertyOR.setSuid(suid);
			valueOR.setId(propertyOR.getId());
			valueOR.setType(type);
			valueOR.setValue(value);
			valueLocal.update(valueOR);
		}
		PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyOR, valueOR);
		return propertyInfo;
	}

	public PropertyInfo moveProperty(com.unidocs.cms.info.Context context, String parentNodeId, String propertyId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		int sstate = 0;
		int tstate = 0;
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
		if (propertyOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The Property[ID: ")).append(propertyId).append("] is not existed").toString());
		suid = "00000000-0000-0000-0000-000000000000";
		tuid = "00000000-0000-0000-0000-000000000000";
		if (context.isInSession()) {
			suid = context.getId();
			sstate = propertyOR.getSstate();
			if (sstate == 11) {
				propertyOR.setPuid(parentNodeId);
				propertyLocal.update(propertyOR);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, propertyId, propertyOR.getPuid(), 13, "P");
				SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
				sessionFlagLocal.store(sessionFlagOR);
			} else if (sstate == 14) {
				propertyOR.setPuid(parentNodeId);
				propertyLocal.update(propertyOR);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, propertyId, propertyOR.getPuid(), 13, "P");
				SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
				sessionFlagLocal.store(sessionFlagOR);
			} else {
				PropertyFlagOR deleteFlag = new PropertyFlagOR(propertyOR.getId(), suid);
				PropertyFlagLocal propertyFlagLocal = DaoFactory.createPropertyFlagLocal();
				propertyFlagLocal.store(deleteFlag);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, propertyId, propertyOR.getPuid(), 12, "P");
				setSessionFlagORTree(context, sessionFlagOR);
				propertyOR.setPuid(parentNodeId);
				propertyOR.setSuid(suid);
				propertyOR.setSstate(14);
				propertyLocal.create(propertyOR);
			}
		} else if (context.isInTransaction()) {
			tuid = context.getTransactionInfo().getId();
			tstate = propertyOR.getTstate();
			if (tstate == 31) {
				propertyOR.setPuid(parentNodeId);
				propertyLocal.update(propertyOR);
			} else if (sstate == 34) {
				propertyOR.setPuid(parentNodeId);
				propertyLocal.update(propertyOR);
			} else {
				propertyOR.setTuid(tuid);
				propertyOR.setTstate(32);
				propertyLocal.update(propertyOR);
				propertyOR.setPuid(parentNodeId);
				propertyOR.setTuid(tuid);
				propertyOR.setTstate(34);
				propertyLocal.create(propertyOR);
			}
		} else {
			propertyOR.setPuid(parentNodeId);
			propertyLocal.update(propertyOR);
		}
		return getProperty(context, propertyId);
	}

	public PropertyInfo deleteProperty(com.unidocs.cms.info.Context context, String propertyId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		int sstate = 0;
		int tstate = 0;
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
		if (propertyOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The Property[ID: ")).append(propertyId).append("] is not existed").toString());
		sstate = propertyOR.getSstate();
		tstate = propertyOR.getTstate();
		if (context.isInSession()) {
			suid = context.getId();
			if (sstate == 11)
				propertyLocal.delete(propertyOR);
			else if (sstate == 14) {
				propertyLocal.delete(propertyOR);
			} else {
				Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder(String.valueOf(propertyOR.getId())))
						.append("를 SessionFlag에 담습니다. suid : ").append(suid).toString());
				PropertyFlagOR deleteFlag = new PropertyFlagOR(propertyOR.getId(), suid);
				PropertyFlagLocal propertyFlagLocal = DaoFactory.createPropertyFlagLocal();
				propertyFlagLocal.store(deleteFlag);
				SessionFlagOR sessionFlagOR = new SessionFlagOR(suid, propertyId, propertyOR.getPuid(), 12, "P");
				setSessionFlagORTree(context, sessionFlagOR);
			}
		} else if (context.isInTransaction()) {
			tuid = context.getTransactionInfo().getId();
			if (tstate == 31)
				propertyLocal.delete(propertyOR);
			else if (tstate == 34) {
				propertyLocal.delete(propertyOR);
			} else {
				propertyOR.setTuid(tuid);
				propertyOR.setTstate(32);
				propertyLocal.update(propertyOR);
			}
		} else {
			suid = UUIDGen.newUUID();
			Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder("속성 ")).append(propertyOR.getId())
					.append("를 그냥 삭제 합니다.. suid : ").append(suid).toString());
			SessionFlagLocal sessionFlagLocal = DaoFactory.createSessionFlagLocal();
			sessionFlagLocal.addProperty2DeleteTree(Integer.parseInt(propertyOR.getId()), suid, tuid);
			sessionFlagLocal.removeItemsByDeleteItemTreeBySuid(suid);
			propertyLocal.delete(propertyOR);
		}
		PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyOR);
		return propertyInfo;
	}

	public long getChildNodesLength(com.unidocs.cms.info.Context context, String parentId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		long result = nodeLocal.count(suid, tuid, parentId);
		return result;
	}

	public long getChildNodesLength(com.unidocs.cms.info.Context context, String parentId, String name)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(name);
		String localName = InfoUtil.getLocalName(name);
		String url = findUrl(context.getWorkspaceId(), name);
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		long result = 0L;
		if (prefix.equals(""))
			result = nodeLocal.count(suid, tuid, parentId, localName);
		else
			result = nodeLocal.count(suid, tuid, parentId, url, localName);
		return result;
	}

	public NodeInfo getChildNode(com.unidocs.cms.info.Context context, String parentNodeId, long index)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, parentNodeId, index);
		if (nodeOR == null)
			return null;
		else
			return buildNodeInfo(context, nodeOR);
	}

	public NodeInfo getChildNode(com.unidocs.cms.info.Context context, String parentNodeId, String qname, long index)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(qname);
		String localName = InfoUtil.getLocalName(qname);
		String url = findUrl(context.getWorkspaceId(), qname);
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = null;
		if (prefix.equals(""))
			nodeOR = nodeLocal.findNode(suid, tuid, parentNodeId, localName, index);
		else
			nodeOR = nodeLocal.findNode(suid, tuid, parentNodeId, url, localName, index);
		if (nodeOR == null)
			return null;
		else
			return buildNodeInfo(context, nodeOR);
	}

	public NodeInfo[] getChildNodes(com.unidocs.cms.info.Context context, String parentNodeId, String qname)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(qname);
		String localName = InfoUtil.getLocalName(qname);
		String url = findUrl(context.getWorkspaceId(), qname);
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		ArrayList nodeORs = null;
		if (prefix.equals(""))
			nodeORs = nodeLocal.findNodes(suid, tuid, parentNodeId, localName);
		else
			nodeORs = nodeLocal.findNodes(suid, tuid, parentNodeId, url, localName);
		NodeInfo result[] = new NodeInfo[nodeORs.size()];
		for (int i = 0; i < nodeORs.size(); i++) {
			NodeOR nodeOR = (NodeOR) nodeORs.get(i);
			result[i] = buildNodeInfo(context, nodeOR);
		}

		return result;
	}

	public long getChildPropertiesLength(com.unidocs.cms.info.Context context, String parentId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		return propertyLocal.count(suid, tuid, parentId);
	}

	public long getChildPropertiesLength(com.unidocs.cms.info.Context context, String parentId, String qname)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(qname);
		String localName = InfoUtil.getLocalName(qname);
		String url = findUrl(context.getWorkspaceId(), qname);
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		if (prefix.equals(""))
			return propertyLocal.count(suid, tuid, parentId, localName);
		else
			return propertyLocal.count(suid, tuid, parentId, url, localName);
	}

	public PropertyInfo getChildProperty(com.unidocs.cms.info.Context context, String nodeId, String qname, long index)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(qname);
		String localName = InfoUtil.getLocalName(qname);
		String url = findUrl(context.getWorkspaceId(), qname);
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = null;
		if (prefix.equals(""))
			propertyOR = propertyLocal.findProperty(suid, tuid, nodeId, localName, index);
		else
			propertyOR = propertyLocal.findProperty(suid, tuid, nodeId, url, localName, index);
		if (propertyOR == null) {
			return null;
		} else {
			ValueLocal valueLocal = DaoFactory.createValueLocal();
			ValueOR valueOR = valueLocal.findByPrimaryKey(propertyOR.getId());
			PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyOR, valueOR);
			return propertyInfo;
		}
	}

	public PropertyInfo[] getChildProperties(com.unidocs.cms.info.Context context, String nodeId, String qname)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = InfoUtil.getPrefix(qname);
		String localName = InfoUtil.getLocalName(qname);
		String url = findUrl(context.getWorkspaceId(), qname);
		ArrayList propertyInfos = new ArrayList();
		PropertyValueSetLocal propertyValueSetLocal = DaoFactory.createPropertyValueSetLocal();
		ArrayList propertyValueSetORList = null;
		if (prefix.equals(""))
			propertyValueSetORList = propertyValueSetLocal.findProperties(suid, tuid, nodeId, localName);
		else
			propertyValueSetORList = propertyValueSetLocal.findProperties(suid, tuid, nodeId, url, localName);
		for (int i = 0; i < propertyValueSetORList.size(); i++) {
			PropertyValueSetOR propertyValueSetOR = (PropertyValueSetOR) propertyValueSetORList.get(i);
			PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyValueSetOR);
			propertyInfos.add(propertyInfo);
		}

		return (PropertyInfo[]) propertyInfos.toArray(new PropertyInfo[0]);
	}

	public PropertyInfo[] getChildProperties(com.unidocs.cms.info.Context context, String nodeId, String qnames[])
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		String prefix = null;
		String localName = null;
		String url = null;
		ArrayList propertyInfos = new ArrayList();
		PropertyValueSetLocal propertyValueSetLocal = DaoFactory.createPropertyValueSetLocal();
		ArrayList propertyValueSetORList = propertyValueSetLocal.findProperties(suid, tuid, nodeId);
		for (int i = 0; i < propertyValueSetORList.size(); i++) {
			PropertyValueSetOR propertyValueSetOR = (PropertyValueSetOR) propertyValueSetORList.get(i);
			if (isMatch(context, propertyValueSetOR.getUrl(), propertyValueSetOR.getName(), qnames)) {
				PropertyInfo propertyInfo = InfoUtil.createPropertyInfo(propertyValueSetOR);
				propertyInfos.add(propertyInfo);
			}
		}

		return (PropertyInfo[]) propertyInfos.toArray(new PropertyInfo[0]);
	}

	private boolean isMatch(com.unidocs.cms.info.Context context, String url, String name, String qnames[]) {
		for (int i = 0; i < qnames.length; i++) {
			String qname = qnames[i];
			String localName = InfoUtil.getLocalName(qname);
			String prefix = InfoUtil.getPrefix(qname);
			String turl = findUrl(context.getWorkspaceId(), qname);
			if (name.equals(localName) && url.equals(turl))
				return true;
		}

		return false;
	}

	public long getNodeIndexof(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The node[id: ")).append(nodeId).append("] is not existed").toString());
		else
			return nodeLocal.index(suid, tuid, nodeOR.getPuid(), nodeId);
	}

	public long getPropertyIndexof(com.unidocs.cms.info.Context context, String propertyId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
		if (propertyOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The property[id: ")).append(propertyId).append("] is not existed").toString());
		else
			return propertyLocal.index(suid, tuid, propertyOR.getPuid(), propertyId);
	}

	public NodeInfo getFirstChildNode(com.unidocs.cms.info.Context context, String parentNodeId, String namepattern[])
			throws ItemNotFoundException, RepositoryException {
		NodeOR nodeOR = null;
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		nodeOR = nodeLocal.findFistNode(suid, tuid, parentNodeId, namepattern);
		if (nodeOR == null) {
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, nodeOR);
			return nodeInfo;
		}
	}

	public NodeInfo getNextNode(com.unidocs.cms.info.Context context, String nodeId, String namepattern[])
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The node[id: ")).append(nodeId).append("] is not existed").toString());
		NodeOR next = nodeLocal.findNextNode(suid, tuid, nodeOR.getPuid(), nodeOR.getId(), namepattern);
		if (next == null) {
			return null;
		} else {
			NodeInfo nodeInfo = buildNodeInfo(context, next);
			return nodeInfo;
		}
	}

	public PropertyInfo getFirstChildProperty(com.unidocs.cms.info.Context context, String parentId,
			String namepattern[]) throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findFistProperty(suid, tuid, parentId, namepattern);
		return getPropertyInfo(propertyOR);
	}

	public PropertyInfo getNextProperty(com.unidocs.cms.info.Context context, String propertyId, String namepattern[])
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		PropertyLocal propertyLocal = DaoFactory.createPropertyLocal();
		PropertyOR propertyOR = propertyLocal.findProperty(suid, tuid, propertyId);
		if (propertyOR == null) {
			throw new ItemNotFoundException(
					(new StringBuilder("The property[id: ")).append(propertyId).append("] is not existed").toString());
		} else {
			PropertyOR nextOR = propertyLocal.findNextProperty(suid, tuid, propertyOR.getPuid(), propertyOR.getId(),
					namepattern);
			return getPropertyInfo(nextOR);
		}
	}

	public RowInfo[] executeQuery(com.unidocs.cms.info.Context context, String query, String propertyNames[],
			String columnNames[], int propertyTypes[]) throws RepositoryException {
		QueryExecutorLocal queryExecutorLocal = DaoFactory.createQueryExecutorLocal();
		return queryExecutorLocal.executeQuery(query, propertyNames, columnNames, propertyTypes);
	}

	public ItemInfo[] executeQuery4XPath(com.unidocs.cms.info.Context context, String query)
			throws RepositoryException {
		QueryExecutorLocal queryExecutorLocal = DaoFactory.createQueryExecutorLocal();
		String uuids[] = queryExecutorLocal.executeQuery4XPath(query);
		ArrayList result = new ArrayList();
		for (int i = 0; i < uuids.length; i++) {
			ItemInfo itemInfo = getNode(context, uuids[i]);
			if (itemInfo == null)
				itemInfo = getProperty(context, uuids[i]);
			if (itemInfo != null)
				result.add(itemInfo);
		}

		return (ItemInfo[]) result.toArray(new ItemInfo[0]);
	}

	public void executeUpdate(com.unidocs.cms.info.Context context, String query) throws RepositoryException {
		QueryExecutorLocal queryExecutorLocal = DaoFactory.createQueryExecutorLocal();
		queryExecutorLocal.executeUpdate(query);
	}

	private String findUrl(String workspaceId, String qname) {
		NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
				.getService("NameSapceCacheService");
		String prefix = InfoUtil.getPrefix(qname);
		String url = nscService.getURI(workspaceId, prefix);
		return url;
	}

	public void registerNamespace(com.unidocs.cms.info.Context context, String prefix, String url)
			throws RepositoryException {
		NamespaceLocal namespaceLocal = DaoFactory.createNamespaceLocal();
		if (prefix.equals(""))
			prefix = "default_prefix";
		boolean hasPrefix = namespaceLocal.exists(context.getWorkspaceId(), prefix);
		NamespaceOR namespace = null;
		if (hasPrefix)
			namespace = namespaceLocal.findByWID_PRFIX(context.getWorkspaceId(), prefix);
		else
			namespace = new NamespaceOR(UUIDGen.newUUID(), context.getWorkspaceId(), prefix, url);
		namespaceLocal.store(namespace);
		NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
				.getService("NameSapceCacheService");
		nscService.registerNamespace(context.getWorkspaceId(), prefix, url);
	}

	public void unRegisterNamespace(com.unidocs.cms.info.Context context) throws RepositoryException {
		NamespaceLocal namespaceLocal = DaoFactory.createNamespaceLocal();
		ArrayList result = namespaceLocal.findByWID(context.getWorkspaceId());
		namespaceLocal.delete(result);
		NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
				.getService("NameSapceCacheService");
		nscService.unRegisterNamespace(context.getWorkspaceId());
	}

	public void unRegisterNamespace(com.unidocs.cms.info.Context context, String prefix) throws RepositoryException {
		NamespaceLocal namespaceLocal = DaoFactory.createNamespaceLocal();
		if (prefix.equals(""))
			prefix = "default_prefix";
		NamespaceOR namespace = namespaceLocal.findByWID_PRFIX(context.getWorkspaceId(), prefix);
		if (namespace == null) {
			return;
		} else {
			namespaceLocal.delete(namespace);
			NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
					.getService("NameSapceCacheService");
			nscService.unRegisterNamespace(context.getWorkspaceId(), prefix);
			return;
		}
	}

	public String[] getPrefixes(com.unidocs.cms.info.Context context) throws RepositoryException {
		NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
				.getService("NameSapceCacheService");
		return nscService.getPrefixes(context.getWorkspaceId());
	}

	public String[] getURIs(com.unidocs.cms.info.Context context) throws RepositoryException {
		NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
				.getService("NameSapceCacheService");
		return nscService.getURIs(context.getWorkspaceId());
	}

	public String getURI(com.unidocs.cms.info.Context context, String prefix) throws RepositoryException {
		NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
				.getService("NameSapceCacheService");
		return nscService.getURI(context.getWorkspaceId(), prefix);
	}

	public String getPrefix(com.unidocs.cms.info.Context context, String url) throws RepositoryException {
		NameSapceCacheService nscService = (NameSapceCacheService) ServiceInternalManager.getInstance()
				.getService("NameSapceCacheService");
		return nscService.getPrefix(context.getWorkspaceId(), url);
	}

	private NodeInfo buildNodeInfo(com.unidocs.cms.info.Context context, NodeOR nodeOR)
			throws ItemNotFoundException, RepositoryException {
		NodeInfo nodeInfo = InfoUtil.createNodeInfo(nodeOR);
		PropertyInfo propertyInfos[] = getChildProperties(context, nodeOR.getUuid());
		PropertyInfo primaryType = InfoUtil.findPropertyInfo("primaryType", propertyInfos);
		PropertyInfo mixinTypes = InfoUtil.findPropertyInfo("mixinTypes", propertyInfos);
		if (primaryType != null)
			nodeInfo.setPrimaryType(primaryType.getValueInfo().getValue());
		if (mixinTypes != null)
			nodeInfo.setMixinNodeTypes(mixinTypes.getValueInfo().getValue());
		return nodeInfo;
	}

	public String getNodePath(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		return getNodePathInternal(context, nodeId);
	}

	private String getNodePathInternal(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		Logger.debug(Logger.LOG_RANGE_ENGINE,
				Logger.format("getNodePathInternal[SUID: {0} TUID: {1}]'s PrimaryType is", new String[]{suid, tuid}));
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		NodeOR nodeOR = nodeLocal.findNode(suid, tuid, nodeId);
		if (nodeOR == null)
			throw new ItemNotFoundException(
					(new StringBuilder("The node[id: ")).append(nodeId).append("] is not existed").toString());
		String puid = nodeOR.getPuid();
		if (puid.equals("00000000-0000-0000-0000-000000000000"))
			return (new StringBuilder("/")).append(nodeId).toString();
		else
			return (new StringBuilder(String.valueOf(getNodePathInternal(context, puid)))).append("/").append(nodeId)
					.toString();
	}

	public CachedNodeInfo getCachedNode(com.unidocs.cms.info.Context context, String nodeId)
			throws ItemNotFoundException, RepositoryException {
		String suid = context.isInSession() ? context.getId() : "00000000-0000-0000-0000-000000000000";
		String tuid = context.isInTransaction()
				? context.getTransactionInfo().getId()
				: "00000000-0000-0000-0000-000000000000";
		ArrayList propertyInfos = new ArrayList();
		NodeLocal nodeLocal = DaoFactory.createNodeLocal();
		ArrayList list = nodeLocal.findTreeItems(suid, tuid, nodeId);
		CachedNodeInfo result = InfoUtil.createCachedNodeInfoTree(list);
		writeCachedNodeInfo(result, 0);
		return result;
	}

	private void writeCachedNodeInfo(CachedNodeInfo nodeInfo, int tabCount) {
		String tabs = "";
		for (int i = 0; i < tabCount; i++)
			tabs = (new StringBuilder(String.valueOf(tabs))).append("  ").toString();

		Logger.debug(Logger.LOG_RANGE_ENGINE,
				(new StringBuilder(String.valueOf(tabs))).append("<").append(nodeInfo.getName()).toString());
		PropertyInfo propertyInfo;
		for (Iterator pi = nodeInfo.getChildPropertyInfos(); pi.hasNext(); Logger.debug(Logger.LOG_RANGE_ENGINE,
				(new StringBuilder(String.valueOf(tabs))).append("@").append(propertyInfo.getName()).append("=\"")
						.append(propertyInfo.getValueInfo().getValue()).append("\"").toString()))
			propertyInfo = (PropertyInfo) pi.next();

		Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder(String.valueOf(tabs))).append(">").toString());
		CachedNodeInfo childNodeInfo;
		for (Iterator ni = nodeInfo.getChildNodeInfos(); ni.hasNext(); writeCachedNodeInfo(childNodeInfo, tabCount + 1))
			childNodeInfo = (CachedNodeInfo) ni.next();

		Logger.debug(Logger.LOG_RANGE_ENGINE, (new StringBuilder(String.valueOf(tabs))).append("</")
				.append(nodeInfo.getName()).append(">").toString());
	}
}