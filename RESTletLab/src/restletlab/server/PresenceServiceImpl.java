package restletlab.server;

import java.util.List;
import javax.jdo.PersistenceManager;

import org.restlet.resource.ServerResource;

import restletlab.common.PresenceService;
import restletlab.common.RegistrationInfo;

public class PresenceServiceImpl extends ServerResource implements PresenceService {
	
	@Override
	public void register(RegistrationInfo reg) throws Exception {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(reg);
        } finally {
            pm.close();
        }
	}

	@Override
	public void unregister(String userName) throws Exception {
		RegistrationInfo user = this.lookup(userName);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
        	pm.makePersistent(user);
        	pm.deletePersistent(user);
        } finally {
            pm.close();
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public RegistrationInfo lookup(String name) throws Exception {
		RegistrationInfo user = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    String query = "select from " + RegistrationInfo.class.getName() + " where userName=='" + name + "'";
		List<RegistrationInfo> users = (List<RegistrationInfo>) pm.newQuery(query).execute();
	    if(!users.isEmpty()) {
	    	user=users.get(0);
	    }
	    pm.close();
	    return user;
	}

	@Override
	public boolean setStatus(String userName, boolean status) throws Exception {
		RegistrationInfo user = this.lookup(userName);
		if (user.getStatus() == status) {
			return false;
		} else {
			user.setStatus(status);
			this.unregister(user.getUserName());
			this.register(user);
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RegistrationInfo> listRegisteredUsers() throws Exception {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    String query = "select from " + RegistrationInfo.class.getName();
	    return (List<RegistrationInfo>) pm.newQuery(query).execute(); 
	}

}
