import java.util.*;
import java.io.*;
import java.net.*;

public class Connection {
	
	private static Connection instance = null;	
	private static String ip;
	private static int port;
	protected static boolean lock = false;
		
	// FILETRANSFER LIST
	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	
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
	
	public static String getIp()
	{
		return ip;
	}
	
	public static int getPort()
	{
		return port;
	}	
	
	public void connect()
	{
		try 
		{
			Socket socket = new Socket();
			socket.setSoTimeout(15000);
			socket.connect(new InetSocketAddress(ip, port));
			PrintWriter TCP_out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader TCP_input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
					
			String handshake;
			TCP_out.println("Connect");
			try 
			{
				while((handshake = TCP_input.readLine()) != null)
				{
					if(handshake.contains("ACK0"))
					{
						TCP_out.println("ACK1 "+HostListener.PORT);
						lock = true;
						System.out.println("Connection established");
					}
					else
					{
						System.out.println("Can't establish connection");
					}
				}
				TCP_out.close();
				TCP_input.close();
				socket.close();
			} 
			catch (IOException e) 
			{	
				System.out.println("Socket closed."); 
			}
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void disconnect()
	{
		try {
			Socket socket = new Socket(ip, port);
			PrintWriter TCP_out = new PrintWriter(socket.getOutputStream(), true);
			TCP_out.println("Disconnect");
			lock = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getFileList()
	{
		try 
		{
			if(lock)
			{
				Socket socket = new Socket(ip, port);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out.println("Get List");
				String response;
				while((response = in.readLine()) != null)
				{
					System.out.println(response);
				}
				socket.close();
			}
			else
				System.out.println("No connection established");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
}









