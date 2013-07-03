//----------------------------------------------------------------------
//
//  Filename: RegistrationInfo.java
//  Description: 
// 
//  $Id:$
//
//----------------------------------------------------------------------
package restletlab.common;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONObject;

/**
 * This class represents the information that the chat client registers
 * with the presence server.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class RegistrationInfo 
{
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long gid;
	
    @Persistent
    private String userName;
    @Persistent
    private String host;
    @Persistent
    private boolean status;
    @Persistent
    private int port;

    public RegistrationInfo() 
    {
    	this.userName = null;
    	this.host = null;
    	this.status = false;
    	this.port = -1;
    }
    
    /**
     * RegistrationInfo  constructor.
     * @param uname Name of the user being registered.
     * @param h Name of the host their client is running on.
     * @param p The port # their client is listening for connections on.
     * @param s The status, true if the client is available to host a game, false otherwise.
     */
    public RegistrationInfo(String uname, String h, int p, boolean s)
    {
        this.userName = uname;
        this.host = h;
        this.port = p;
        this.status = s;
    }

    /**
     * Determine the key.
     * @return The key.
     */
    public Long getGid() {
		return gid;
	}    
    
    /**
     * Determine the name of the user.
     * @return The name of the user.
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
     * Determine the host the user is on.
     * @return The name of the host client resides on.
     */
    public String getHost()
    {
        return this.host;
    }
    
    /**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
     * Get the port the client is listening for connections on.
     * @return port value.
     */
    public int getPort()
    {
        return this.port;
    }

    /**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
     * Get the status of the client - true means availability, false means don't disturb.
     * @return status value.
     */
    public boolean getStatus()
    {
    	return this.status;
    }
    
    /**
     * Modify the user's busy/available status.
     * @param status set to true if user is available, false otherwise.
     */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * Convert this object to a JSON object for representation
	 */
	public JSONObject toJSON() {
		try{
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("username", this.userName);
			jsonobj.put("status", this.status);
			jsonobj.put("host", this.host);
			jsonobj.put("port", this.port);
			return jsonobj;
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * Convert this object to a string for representation
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("username:");
		sb.append(this.userName);
		sb.append(",status:");
		sb.append(this.status);		
		sb.append(",host:");
		sb.append(this.host);
		sb.append(",port:");
		sb.append(this.port);
		return sb.toString();
	}
	
	/** 
	 * Convert this object into an HTML representation.
	 * @param fragment if true, generate an html fragment, otherwise a complete document.
	 * @return an HTML representation.
	 */
	public String toHtml(boolean fragment) 
	{
		String retval = "";
		if(fragment) {
			StringBuffer sb = new StringBuffer();
			sb.append("<b>Username:</b> ");
			sb.append(this.userName);
			sb.append("<br/><b>Status:</b> ");
			sb.append(this.status);			
			sb.append("<br/><b>Host:</b> ");
			sb.append(this.host);
			sb.append("<br/><b>Port:</b> ");
			sb.append(this.port);
			sb.append(" <a href=\"/users/" + this.userName + "\">View</a>");
			sb.append("<br/><br/>");
			retval = sb.toString();
		} else {
			StringBuffer sb = new StringBuffer("<html><head><title>User Resource</title></head><body><h1>User Representation</h1>");
			sb.append("<b>Username:</b> ");
			sb.append(this.userName);
			sb.append("<br/><b>Status:</b>");
			sb.append(this.status);
			sb.append("<br/><b>Host:</b>");
			sb.append(this.host);
			sb.append("<br/><b>Port:</b>");
			sb.append(this.port);
			sb.append("<br/><br/>Return to <a href=\"/users\">user list<a>.</body></html>");
			retval = sb.toString();
		}
		return retval;
	}

}