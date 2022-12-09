package com.unidocs.cms;

public interface Repository {
	String SPEC_VERSION_DESC = "jcr.specification.version";
	String SPEC_NAME_DESC = "jcr.specification.name";
	String REP_VENDOR_DESC = "jcr.repository.vendor";
	String REP_VENDOR_URL_DESC = "jcr.repository.vendor.url";
	String REP_NAME_DESC = "jcr.repository.name";
	String REP_VERSION_DESC = "jcr.repository.version";
	String LEVEL_1_SUPPORTED = "level.1.supported";
	String LEVEL_2_SUPPORTED = "level.2.supported";
	String OPTION_TRANSACTIONS_SUPPORTED = "option.transactions.supported";
	String OPTION_VERSIONING_SUPPORTED = "option.versioning.supported";
	String OPTION_OBSERVATION_SUPPORTED = "option.observation.supported";
	String OPTION_LOCKING_SUPPORTED = "option.locking.supported";
	String OPTION_QUERY_SQL_SUPPORTED = "option.query.sql.supported";
	String QUERY_XPATH_POS_INDEX = "query.xpath.pos.index";
	String QUERY_XPATH_DOC_ORDER = "query.xpath.doc.order";
	String QUERY_JCRPATH = "query.jcrpath";
	String QUERY_JCRSCORE = "query.jcrscore";

	String[] getDescriptorKeys();

	String getDescriptor(String var1);

	Session login(Credentials var1, String var2) throws LoginException, NoSuchWorkspaceException, RepositoryException;

	Session login(Credentials var1) throws LoginException, RepositoryException;

	Session login(String var1) throws LoginException, NoSuchWorkspaceException, RepositoryException;

	Session login() throws LoginException, RepositoryException;
}