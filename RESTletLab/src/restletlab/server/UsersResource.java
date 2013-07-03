package restletlab.server;

import java.util.Collection;
import org.json.JSONArray;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import restletlab.common.*;

/**
 * Represents a collection of users. This resource processes HTTP requests that come in on the URIs
 * in the form of:
 * 
 * http://host:port/users
 * 
 * This resource supports both HTML and JSON representations.
 *  
 */

public class UsersResource extends ServerResource {

	private PresenceServiceImpl server = new PresenceServiceImpl();
	private Collection<RegistrationInfo> users = null;

	@Override
	public void doInit() {
		try {
			this.users = server.listRegisteredUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// these are the representation types this resource can use to describe the set of users with. 
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	/**
	 * Handle an HTTP GET. Represent the user object in the requested format.
	 * 
	 * @param variant
	 * @return
	 * @throws ResourceException
	 */
	@Get
	public Representation get(Variant variant) throws ResourceException {
		Representation result = null;
		if (null == this.users) {
			ErrorMessage em = new ErrorMessage();
			return representError(variant, em);
		} else {
			if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
				JSONArray userArray = new JSONArray();
				for(Object o : this.users) {
					RegistrationInfo user = (RegistrationInfo)o;
					userArray.put(user.toJSON());
				}
				result = new JsonRepresentation(userArray);
			} else {
				// create a plain text representation of our list of users
				StringBuffer buf = new StringBuffer("<html><head><title>User Resources</title><head><body><h1>User Resources</h1>");
				buf.append("<form name=\"input\" action=\"/users\" method=\"POST\">");
				buf.append("User name: ");
				buf.append("<input type=\"text\" name=\"username\" /><br/>");
				buf.append("Available: ");
				buf.append("<input type=\"text\" name=\"status\" /><br/>");
				buf.append("Host: ");
				buf.append("<input type=\"text\" name=\"host\" /><br/>");
				buf.append("Port: ");
				buf.append("<input type=\"text\" name=\"port\" />");
				buf.append("<input type=\"submit\" value=\"Create\" />");
				buf.append("</form>");
				buf.append("<br/><h2> There are " + this.users.size() + " total.</h2>");
				for(Object o : this.users) {
					RegistrationInfo user = (RegistrationInfo)o;
					buf.append(user.toHtml(true));
				}
				buf.append("</body></html>");
				result = new StringRepresentation(buf.toString());
				result.setMediaType(MediaType.TEXT_HTML);
			}
		}
		return result;
	}	

	/**
	 * Handle a POST Http request. Create a new user
	 * 
	 * @param entity
	 * @throws ResourceException
	 */
	@Post
	public Representation post(Representation entity, Variant variant) throws ResourceException {
		Representation rep = null;
		
		// We handle only a form request in this example. Other types could be
		// JSON or XML.
		try {
			if ( entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM, true) ) {
				// Use the incoming data in the POST request to create/store a new user resource.
				Form form = new Form(entity);
				RegistrationInfo user = new RegistrationInfo();
				
				user.setUserName(form.getFirstValue("username"));
				user.setStatus(true);
				user.setHost(form.getFirstValue("host"));
				user.setPort(Integer.parseInt(form.getFirstValue("port")));
				server.register(user);
				
				getResponse().setStatus(Status.SUCCESS_OK);
				rep = new StringRepresentation(user.toHtml(false));
				rep.setMediaType(MediaType.TEXT_HTML);				
				getResponse().setEntity(rep);
			} else {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		} catch (Exception e) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
		}
		return rep;
	}
	
	/**
	 * Represent an error message in the requested format.
	 * 
	 * @param variant
	 * @param em
	 * @return
	 * @throws ResourceException
	 */
	private Representation representError(Variant variant, ErrorMessage em)
	throws ResourceException {
		Representation result = null;
		if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			result = new JsonRepresentation(em.toJSON());
		} else {
			result = new StringRepresentation(em.toString());
		}
		return result;
	}

	protected Representation representError(MediaType type, ErrorMessage em)
	throws ResourceException {
		Representation result = null;
		if (type.equals(MediaType.APPLICATION_JSON)) {
			result = new JsonRepresentation(em.toJSON());
		} else {
			result = new StringRepresentation(em.toString());
		}
		return result;
	}
}
