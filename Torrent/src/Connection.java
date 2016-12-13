import java.io.*;
import java.net.*;

public class Connection {
	
	
	private static String ip;
	private static int port;
	private static String protocol;
	private boolean lock = false;
	
	// TCP protocol vars
	private Socket peerSocket = null;
	private PrintWriter TCP_out = null;
	private BufferedReader TCP_input = null;
	
	// UDP protocol vars
	private DatagramSocket peerSocker = null;
	
	private static Connection instance = null;
	// FILETRANSFER LIST
	
	protected Connection(String ip, int port, String protocol)
	{
		this.ip = ip;
		this.port = port;
		this.protocol = protocol;
	}
	
	public Connection getInstance(String ip, int port, String protocol)
	{
		if(instance == null)
			instance = new Connection(ip, port, protocol);
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
	
	public String getProtocol()
	{
		return protocol;
	}
	
	public void connect()
	{
		if(protocol.equals("TCP") && lock == false)
		{
			try 
			{
				peerSocket = new Socket(ip, port);
				TCP_out = new PrintWriter(peerSocket.getOutputStream(), true);
				TCP_input = new BufferedReader(
						new InputStreamReader(peerSocket.getInputStream()));
				
				String handshake;
				TCP_out.println("Connect");
				while((handshake = TCP_input.readLine()) != null)
				{
					// Czy to w ogóle ma sens ? 
					if(handshake.equals("ACK"))
						lock = true;
					else
						System.out.println("Can't establish connection");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}









