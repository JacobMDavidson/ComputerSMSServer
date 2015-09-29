package com.jacobmdavison.computersmsserver;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class TCPServer extends Thread{
		//public static final int SERVERPORT = 4444;
	
		public final static String SERVICE_TYPE = "_http._tcp.local.";
	    private boolean running = false;
	    private PrintWriter mOut;
	    private DiffieHellmanModule diffieHellmanModule;
	    private OnMessageReceived messageListener;
	    private int port;
	    private String clientMessage;
	    private ServerBoard serverBoard;
	 
	    /**
	     * Constructor of the class
	     * @param messageListener listens for the messages
	     */
	    
	    public TCPServer(ServerBoard serverBoard, OnMessageReceived messageListener, DiffieHellmanModule diffieHellmanModule) {
	        this.messageListener = messageListener;
	        this.diffieHellmanModule = diffieHellmanModule;
	        this.serverBoard = serverBoard;
	    }
	 
	    /**
	     * Method to send the messages from server to client
	     * @param message the message sent by the server
	     */
	    public void sendMessage(String message){
	    	
	        if (mOut != null && !mOut.checkError()) {
	        	
	        	// If encryption connection has been made
	        	
	            if (diffieHellmanModule.isConnected()) {
	                message = diffieHellmanModule.encryptString(message);
	            }
	            
	            mOut.println(message);
	            mOut.flush();
	        }
	    }
	 
	    @Override
	    public void run() {
	        super.run();
	 
	        running = true;
	 
	        try {
	            
	 
	        	// Find the next available port
	        	ServerSocket serverSocket = new ServerSocket(0);
	            
	            port = serverSocket.getLocalPort();
	            
	            // Announce the service
	        	JmDNS jmdns = JmDNS.create();
	        	//System.out.println("Opened JmDNS!");
	        	serverBoard.appendToPane("Opening Port", Color.ORANGE);
	        	
	        	Random random = new Random();
	            int id = random.nextInt(100000);
	            
	            final HashMap<String, String> values = new HashMap<String, String>();
	            values.put("DvNm", "ComputerSMS" + id);
	            values.put("txtvers", "1");
	            
	            
	            ServiceInfo pairService = ServiceInfo.create(SERVICE_TYPE, "ComputerSMS", port, 0, 0, values);
	            
	            jmdns.registerService(pairService);
	            //System.out.println("\nRegistered Service as " + pairService);
	            //System.out.println("\nService is on port " + pairService.getPort());
	            serverBoard.appendToPane("\nReady to Connect", Color.ORANGE);
	            // Make the connection
	            Socket client = serverSocket.accept();
	            //System.out.println("Connected");
	      
	            
	            try {
	            	
	 
	                //sends the message to the client
	                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
	                
	                
	                // Send the public Diffie Hellman key
	                String message = new String(Base64.getEncoder().encode(diffieHellmanModule.getPublicKey().getEncoded()));
	                //System.out.println(message);
	                sendMessage(message);
	                
	 
	                //read the message received from client
	                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	 
	                
	                while (!diffieHellmanModule.isConnected()) {
	                    clientMessage = in.readLine();
	                    //System.out.print(clientMessage);
	                    if (clientMessage != null) {
	                        // @ TODO look for specific xml message with key built in and extract it
	                    	
	                        //attempt to calculate the AES key usig the DH key exchange
	                        byte[] data = Base64.getDecoder().decode(clientMessage);
	                        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
	                        KeyFactory keyFactory = KeyFactory.getInstance("DH");
	                        diffieHellmanModule.generateSecretKey(keyFactory.generatePublic(spec), true);
	                    }
	                    clientMessage = null;
	                }
	                
	                // Enable the send button
	                serverBoard.enableSendButton();
	                serverBoard.appendToPane("\nService Started", Color.ORANGE);
	             
	                
	                //in this while we wait to receive messages from client (it's an infinite loop)
	                //this while it's like a listener for messages
	                while (running) {
	                    clientMessage = in.readLine();
	 
	                    if (clientMessage != null && messageListener != null) {
	                        //call the method messageReceived from ServerBoard class
	                    	if(diffieHellmanModule.isConnected()) {
	                    		clientMessage = diffieHellmanModule.decryptString(clientMessage);
	                    	}
	                        messageListener.messageReceived(clientMessage);
	                    }
	                }
	 
	            } catch (Exception e) {
	                System.out.println("S: Error");
	                e.printStackTrace();
	            } finally {
	                client.close();
	                serverSocket.close();
	                //System.out.println("S: Done.");
	                //System.out.println("Closing JmDNS...");
		            jmdns.unregisterService(pairService);
		            jmdns.unregisterAllServices();
		            jmdns.close();
		            //System.out.println("Done!");
		            System.exit(0);
	            }
	 
	        } catch (Exception e) {
	            System.out.println("S: Error");
	            e.printStackTrace();
	        }
	 
	    }
	 
	    //Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
	    //class at on startServer button click
	    public interface OnMessageReceived {
	        public void messageReceived(String message);
	    }
	 
}
