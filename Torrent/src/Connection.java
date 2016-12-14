import java.util.*;
import java.io.*;
import java.net.*;

public class Connection extends Thread {
	
	private static Connection instance = null;	
	private static String ip;
	private static int port;
	protected static boolean lock = false;
	
	// TCP protocol vars
	private Socket serverPeerSocket = null;
	private PrintWriter TCP_out = null;
	private BufferedReader TCP_input = null;
	
	// UDP protocol vars
	private DatagramSocket peerSocker = null;
	
	// FILETRANSFER LIST
	private static ArrayList<FileTransfer> transfers = new ArrayList();
	
	private Connection(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}
	
	public static Connection getInstance(String ip, int port)
	{
		if(instance == null)
			instance = new Connection(ip, port);
		return instance;
	}
	
	public String getIp()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public Socket getSocket()
	{
		return serverPeerSocket;
	}
	
	public void connect()
	{
		if(lock == false)
		{
			try 
			{
				serverPeerSocket = new Socket(ip, port);
				TCP_out = new PrintWriter(serverPeerSocket.getOutputStream(), true);
				TCP_input = new BufferedReader(
						new InputStreamReader(serverPeerSocket.getInputStream()));
				System.out.println("Socket created");
				run();
				
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() 
	{
		System.out.println("inside run of connection");
		int delay = 500;
		int timeout = 0;
		while(!serverPeerSocket.isConnected()) 
		{ 
			System.out.println("inside loop in run");
			try {
				System.out.println("."); 
				Thread.sleep(delay);
				timeout += delay;
				if(timeout > 15000)
				{
					System.out.println("Timeout ocurred. Connection is not established");
					return;
				}
			} 
			catch (InterruptedException e)
			{
				System.out.println("Could not connect. Thread interrupted.");
			} 
		}
		
		String handshake;
		TCP_out.println("I want to connect");
		System.out.println("After sending req");
		try 
		{
			while((handshake = TCP_input.readLine()) != null)
			{
				System.out.println(handshake);
				if(handshake.equals("ACK"))
				{
					lock = true;
					System.out.println("Connection established");
				}
				else
					System.out.println("Can't establish connection");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
}









