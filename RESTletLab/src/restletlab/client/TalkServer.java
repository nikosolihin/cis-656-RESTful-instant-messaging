package restletlab.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServer extends Thread {
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream streamIn = null;
    private int localPort = 0;
    
    public TalkServer(){
        try {  
        	System.out.println("\n-------------------------------------------------------------------");
        	System.out.println("Binding to a port for talk server:");
        	server = new ServerSocket(0);  
        	this.localPort = server.getLocalPort();
        	System.out.println("\t"+server);
        	System.out.println("Successful...");
        }
        catch(IOException ioe) {  
        	System.out.println(ioe); 
        }       
    }
    
    public int getLocalPort() {
    	return this.localPort;
    }
    
    public void run() {  
		try {  
			socket = server.accept();
			streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			try {  
				String line = streamIn.readUTF();
				System.out.println("\n"+line);
			}
			catch(IOException ioe) {  
				System.out.println("Unexpected exception: " + ioe.getMessage());
			}
	    	if (socket != null)    socket.close();
	    	if (streamIn != null)  streamIn.close();
      }
      catch(IOException ie) {  
    	  System.out.println("Acceptance Error: " + ie);  
      }
    }
}
