package restletlab.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatClient extends Thread {
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private Socket clientSocket = null;
    private DataOutputStream streamOut = null;
    private String username = null;
	private boolean status;
	
	public ChatClient(String username, String hostname, int port) {
		this.username = username;
		this.status = true;
		this.registerUser(username, hostname, Integer.toString(port));
	}
	
	public void run() {
    	boolean done = false;
		while (!done) {
			try {
				System.out.println("\n-------------------------------------------------------------------");
				System.out.println( "\nAvailable Commands - friends, talk {username} {message}, " +
									"broadcast {message}, busy, available, exit");
				System.out.print("Enter Command: ");
				String command = reader.readLine();
				
				if(command==null || command.equalsIgnoreCase("exit")) {
					this.deleteUser();
					System.out.println("Exiting...");
					done = true;
					System.exit(0);
				} else if (command.equalsIgnoreCase("friends")) {
					this.friends();
				} 
				else if (command.startsWith("talk")) {
					String[] parameter = command.split(" ", 3);
					this.talk(parameter[1], parameter[2]);
		        }
				else if (command.equalsIgnoreCase("busy")) {
					this.updateStatus("false");
		        } 
				else if (command.equalsIgnoreCase("available")) {
					this.updateStatus("true");					            
		        } 
				else if (command.startsWith("broadcast")) {
					String[] parameter = command.split(" ", 2);
					this.broadcast(parameter[1]);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Not a valid command. Please try again.");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
    public void broadcast (String line) {
    	HttpURLConnection conn = null;
    	URL url;
    	try {
	    	url = new URL("http://restletlab.appspot.com/users");
//	    	url = new URL("http://localhost:8888/users");
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuffer response = new StringBuffer();
			System.out.println("\nGET - " + conn.getResponseCode() + conn.getResponseMessage() + "\n");
			while ((output = br.readLine()) != null) {
				response.append(output);
				response.append('\r');
			}
			br.close();
		    
		    JSONArray array = new JSONArray(response.toString());
		    for (int i = 0; i < array.length(); i++) {
		        JSONObject row = array.getJSONObject(i);
		        if(row.getBoolean("status") == true && !row.getString("username").equalsIgnoreCase(this.username)) {
		        	try {  
		        		clientSocket = new Socket(row.getString("host"), row.getInt("port"));
		        		streamOut = new DataOutputStream(clientSocket.getOutputStream());
		        	}
		        	catch(UnknownHostException uhe) {  
		        		System.out.println("Host unknown: " + uhe.getMessage());
		        	}
		        	catch(IOException ioe) {  
		        		System.out.println("Unexpected exception: " + ioe.getMessage());
		        	}
		        	try {  
		    			streamOut.writeUTF("\n\t<<Broadcast>> " + this.username + ": " + line);
		    			streamOut.flush();
		    		}
		    		catch (IOException ioe) {  
		    			System.out.println("Sending error: " + ioe.getMessage());
		    		}
		        	try {  
		        		if (streamOut != null)  streamOut.close();
		        		if (clientSocket != null)  clientSocket.close();
		        	}
		        	catch(IOException ioe) {  
		        		System.out.println("Error closing ..." + ioe.getMessage());
		        	}
		        }
		    }
    	} catch (MalformedURLException e) {
	    	e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect(); 
			}
		}    	
    }
    public void talk (String username, String line) {
    	HttpURLConnection conn = null;
    	URL url;
    	try {
	    	url = new URL("http://restletlab.appspot.com/users/" + username);
//	    	url = new URL("http://localhost:8888/users/" + username);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuffer response = new StringBuffer();
			System.out.println("\nGET - " + conn.getResponseCode() + conn.getResponseMessage() + "\n");
			while ((output = br.readLine()) != null) {
				response.append(output);
				response.append('\r');
			}
			br.close();
		    
		    JSONArray array = new JSONArray('['+response.toString()+']');
		    for (int i = 0; i < array.length(); i++) {
		        JSONObject row = array.getJSONObject(i);
		        if(row.getBoolean("status") == true) {
		        	System.out.println("User is available...");
		        	System.out.println("Sending message to " + row.getString("host") + ":" + row.getInt("port"));
		        	try {  
		        		clientSocket = new Socket(row.getString("host"), row.getInt("port"));
		        		streamOut = new DataOutputStream(clientSocket.getOutputStream());
		        	}
		        	catch(UnknownHostException uhe) {  
		        		System.out.println("Host unknown: " + uhe.getMessage());
		        	}
		        	catch(IOException ioe) {  
		        		System.out.println("Unexpected exception: " + ioe.getMessage());
		        	}
		        	try {  
		    			streamOut.writeUTF("\n\t" + this.username + ": " + line);
		    			streamOut.flush();
		    		}
		    		catch (IOException ioe) {  
		    			System.out.println("Sending error: " + ioe.getMessage());
		    		}
		        	try {  
		        		if (streamOut != null)  streamOut.close();
		        		if (clientSocket != null)  clientSocket.close();
		        	}
		        	catch(IOException ioe) {  
		        		System.out.println("Error closing ..." + ioe.getMessage());
		        	}
		        } else {
		        	System.out.println("That user is currently busy...");
		        }
		    }
	    } catch (MalformedURLException e) {
	    	e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect(); 
			}
		} 
    }  	
    
    public void friends() {
    	HttpURLConnection conn = null;
    	URL url;
    	try {
	    	url = new URL("http://restletlab.appspot.com/users");
//	    	url = new URL("http://localhost:8888/users");
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuffer response = new StringBuffer();
			System.out.println("\nGET - " + conn.getResponseCode() + conn.getResponseMessage() + "\n");
			while ((output = br.readLine()) != null) {
				response.append(output);
				response.append('\r');
			}
			br.close();
		    
		    JSONArray array = new JSONArray(response.toString());
		    for (int i = 0; i < array.length(); i++) {
		        JSONObject row = array.getJSONObject(i);
		        if(!row.getString("username").equalsIgnoreCase(this.username)) {
		        	String theStatus = row.getBoolean("status") ? "Available" : "Not Available";
		        	System.out.println(row.getString("username") + " - " + theStatus);
		        }
		    }
	    } catch (MalformedURLException e) {
	    	e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect(); 
			}
		}    	
    }
    public void deleteUser() {
    	HttpURLConnection conn = null;
    	URL url;
    	try {
	    	url = new URL("http://restletlab.appspot.com/users/" + this.username);
//	    	url = new URL("http://localhost:8888/users/" + this.username);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuffer response = new StringBuffer();
			System.out.println("\nDELETE - " + conn.getResponseCode() + conn.getResponseMessage() + "\n");
			while ((output = br.readLine()) != null) {
				response.append(output);
				response.append('\r');
			}
			br.close();
			System.out.println(response.toString());
			System.out.println("\nDeleting user " + this.username + "...");
	    } catch (MalformedURLException e) {
	    	e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect(); 
			}
		}
    } 
    public void registerUser(String username, String host, String port) {
    	HttpURLConnection conn = null;
    	URL url;
    	try {
    		System.out.println("Registering: " + username + " (" + host + ":" + port + ")");
    		String input = 	"username=" + URLEncoder.encode(username, "UTF-8") +
		        			"&host=" + URLEncoder.encode(host, "UTF-8") +
		        			"&port=" + URLEncoder.encode(port, "UTF-8");
			url = new URL("http://restletlab.appspot.com/users");
//			url = new URL("http://localhost:8888/users");
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuffer response = new StringBuffer();
			System.out.println("POST - " + conn.getResponseCode() + conn.getResponseMessage() + "\n");
			System.out.println("User Successfully Registered...");
			while ((output = br.readLine()) != null) {
				response.append(output);
				response.append('\r');
			}
			br.close();
		    System.out.println(response.toString());
	    } catch (MalformedURLException e) {
	    	e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect(); 
			}
		}    	
    }

    public void updateStatus(String newStatus) {
    	HttpURLConnection conn = null;
    	URL url;
    	if (this.status != Boolean.parseBoolean(newStatus)) {
	    	try {
	    		String input = 	"status=" + URLEncoder.encode(newStatus, "UTF-8");
				url = new URL("http://restletlab.appspot.com/users/" + this.username);
//	    		url = new URL("http://localhost:8888/users/" + this.username);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Accept", "application/json");
				OutputStream os = conn.getOutputStream();
				os.write(input.getBytes());
				os.flush();
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
				}    
				this.status = Boolean.parseBoolean(newStatus);
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				StringBuffer response = new StringBuffer();
				System.out.println("\nPUT - " + conn.getResponseCode() + conn.getResponseMessage() + "\n");
				while ((output = br.readLine()) != null) {
					response.append(output);
					response.append('\r');
				}
				br.close();
				String availability = this.status ? "available" : "busy";
			    System.out.println(response.toString());
			    System.out.println("\nYou are now " + availability);
		    } catch (MalformedURLException e) {
		    	e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(conn != null) {
					conn.disconnect(); 
				}
			}
    	} else {
    		String availability = this.status ? "available" : "busy";
    		System.out.println("\nYou are already " + availability);
    	}
    }    
    
	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			System.out.println("usage:\n\tjava ChatClient {user} [host[:port]]");
		}
		TalkServer clientTalkServer = new TalkServer();
	    ChatClient client = new ChatClient(	args[0], 
	    									args[1].substring(0, args[1].indexOf(":")), 
	    									clientTalkServer.getLocalPort());
    	Thread talkThread = new Thread(clientTalkServer);
    	Thread inputThread = new Thread(client);
    	talkThread.start();
    	inputThread.start();
	}	
}