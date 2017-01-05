import java.io.*;
import java.net.*;
import java.util.*;

public class HostListener extends Thread{

	static int PORT;
	
	// TCP Socket
	private static ServerSocket serverSocket = null;
	private static String ack;
	private static int ackport;
	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	
	private static HostListener instance = null;
	private int TIMEOUT = 30000;
	
	private HostListener(int port)
	{
		PORT = port;
		try 
		{
			serverSocket = new ServerSocket(port);
			this.setName("HostListener");
			System.out.println("HostListener ready.");
		} 
		catch (IOException e) { System.out.println("Port might be choosen by another application. Restart program and choose another port."); Main.destroyInstance(); }       
	}
	
	public static HostListener getInstance(int port)
	{
		if(instance == null)
			instance = new HostListener(port);
		
		return instance;
	}
	
	
	public static String getIp()
	{
		return serverSocket.getInetAddress().toString().substring(1);
	}
	
	public void setTimeout(int i)
	{
		TIMEOUT = i*1000;
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			try 
			{
				Socket clientSocket = serverSocket.accept();
				try 
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));;
					PrintWriter out =  new PrintWriter(clientSocket.getOutputStream(), true);
					receive(clientSocket, in, out);
				}
				catch (IOException e) { System.out.println("Could not create stream."); }
			}
			catch (IOException e) { System.out.println("Could not create socket."); }	
			
		}	
	}
	
	public void receive(Socket socket, BufferedReader in, PrintWriter out)
	{
		String handshake;
		try 
		{
			handshake = in.readLine();
			switch(handshake)
			{
				case "Connect" :
				{
					System.out.println("Peer with IP: "+socket.getInetAddress()+" is connecting.");
					
					int idnumber = (int)(Math.random()*1000000)+1;
					
					Connection newCon = new Connection(socket, idnumber);
				}
				break;
//				case "Reconnect" :
//				{
//					
//				}
			}
		}
		catch (IOException e) { System.out.println("Error while receiving from stream"); }
	}
}
