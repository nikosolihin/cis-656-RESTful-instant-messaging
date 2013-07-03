//----------------------------------------------------------------------
//
//  Filename: PresenceService.java
//  Description: 
// 
//  $Id:$
//
//----------------------------------------------------------------------
package restletlab.common;

import java.util.List;

/**
 * @author Jonathan Engelsma
 *
 */
/**
 * The abstract interface that is to be implemented by a remote
 * presence server. ChatClients will use this interface to
 * register themselves with the presence server, and also to
 * determine and locate other users who are available for chat
 * sessions.
 */
public interface PresenceService {

    /**
     * Register a client with the presence service.
     * @param reg The information that is to be registered about a client.
     */
    void register(RegistrationInfo reg) throws Exception;

    /**
     * Unregister a client from the presence service.  Client must call this
     * method when it terminates execution.
     * @param userName The name of the user to be unregistered.
     */
    void unregister(String userName) throws Exception;

    /**
     * Lookup the registration information of another client.
     * @param name The name of the client that is to be located.
     * @return The RegistrationInfo info for the client, or null if
     * no such client was found.
     */
    RegistrationInfo lookup(String name) throws Exception;

    
    /**
     * Sets the user's presence status.
     * @param name The name of the user whose status is to be set.
     * @param status true if user is available, false otherwise.
     * @return 
     */
    boolean setStatus(String userName, boolean status) throws Exception;
    
    /**
     * Determine all users who are currently registered in the system.
     * @return An array of RegistrationInfo objects - one for each client
     * present in the system.
     */
    List<RegistrationInfo> listRegisteredUsers() throws Exception;    
}