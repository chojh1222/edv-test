import com.unidocs.cms.Credentials;
import com.unidocs.cms.Repository;
import com.unidocs.cms.RepositoryException;
import com.unidocs.cms.Session;
import com.unidocs.cms.info.Context;
import com.unidocs.cms.info.ContextImpl;
import com.unidocs.cms.info.EDVCredentialsImpl;
import com.unidocs.cms.rom.RepositoryImpl;

public class Main {
	public static void main(String[] args) throws RepositoryException {
		Context context = new ContextImpl();
		Repository repository = new RepositoryImpl(context);
		Credentials credential = new EDVCredentialsImpl("edv", "edv".toCharArray());
		String workspaceName = "workspace1";
		Session session = repository.login(credential, workspaceName);
		System.out.println(session);
	}
}
