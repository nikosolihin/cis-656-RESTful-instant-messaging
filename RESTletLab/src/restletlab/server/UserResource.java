package restletlab.server;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import restletlab.common.RegistrationInfo;

/**
 * Represents a collection of users.  This resource processes HTTP requests that come in on the URIs
 * in the form of:
 * 
 * http://host:port/users/{id}
 * 
 * This resource supports both HTML and JSON representations.
 */

public class UserResource extends ServerResource {

	private PresenceServiceImpl server = new PresenceServiceImpl();
	private RegistrationInfo user = null;
	
	@Override
	public void doInit() {
		// URL requests routed to this resource have the user name on them. 
		String username = (String) getRequest().getAttributes().get("username");
		
		try {
			this.user = server.lookup(username);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		// these are the representation types this resource can use to describe the set of users with. 
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	/**
	 * Represent the user object in the requested format.
	 * 
	 * @param variant
	 * @return
	 * @throws ResourceException
	 */
	@Get
	public Representation get(Variant variant) throws ResourceException {
		Representation result = null;
		if (null == this.user) {
			ErrorMessage em = new ErrorMessage();
			return representError(variant, em);
		} else {
			if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
				result = new JsonRepresentation(this.user.toJSON());
			} else {
				// create a plain text representation of our list of users
				StringBuffer buf = new StringBuffer(this.user.toHtml(false));
				result = new StringRepresentation(buf.toString());				
				result.setMediaType(MediaType.TEXT_HTML);
			}
		}
		return result;
	}
	
	/**
	 * Handle a PUT Http request. Update the status of an existing user
	 * 
	 * @param entity
	 * @throws ResourceException
	 */
	@Put
	public Representation put(Representation entity, Variant variant) throws ResourceException {
		Representation rep = null;
		try {
			if (null == this.user) {
				ErrorMessage em = new ErrorMessage();
				rep = representError(entity.getMediaType(), em);
				getResponse().setEntity(rep);
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return rep;
			}
			if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM, true)) {
				Form form = new Form(entity);
				Boolean newStatus = Boolean.parseBoolean(form.getFirstValue("status"));
				server.setStatus( this.user.getUserName(), newStatus);
				this.user.setStatus(newStatus);
				getResponse().setStatus(Status.SUCCESS_OK);
				rep = new JsonRepresentation(this.user.toJSON());	
				getResponse().setEntity(rep);
			} else {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		return rep;
	}	
	
	/**
	 * Handle a DELETE Http Request. Delete an existing user
	 * 
	 * @param entity
	 * @throws ResourceException
	 */
	@Delete
	public Representation delete(Variant variant) throws ResourceException {
		Representation rep = null;
		try {
			if (null == this.user) {
				ErrorMessage em = new ErrorMessage();
				rep = representError(MediaType.APPLICATION_JSON, em);
				getResponse().setEntity(rep);
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return rep;
			} else {
				server.unregister(this.user.getUserName());
				rep = new JsonRepresentation(this.user.toJSON());
				getResponse().setStatus(Status.SUCCESS_OK);
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