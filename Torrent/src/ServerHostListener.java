

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerHostListener extends Thread{

	static ServerHostListener instance = null;
	private int idnumber = 0;
	private ServerSocket listener = null;
	static ArrayList<ServerConnection> peers =  new ArrayList<ServerConnection>();
	
	private ServerHostListener(int port)
	{
		try 
		{
			listener = new ServerSocket(port);
		} 
		catch (IOException e) { System.out.println("Port might be choosen by another application. Restart program and choose another port."); System.exit(0); }       
	}
	
	public static ServerHostListener getInstance(int port)
	{
		if(instance == null)
			instance = new ServerHostListener(port);
		
		return instance;
	}
	
	public void run() 
	{
		while(true)
		{
			try 
			{
				Socket socket = listener.accept();
				try
				{
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					switch(in.readLine())
					{
						case "Connect" :
						{
							ServerConnection newCon = new ServerConnection(socket, out, in, idnumber++);
							peers.add(newCon);
						}
//						case "PushFile" :
//						{
//							String filename = in.readLine();
//							String id = in.readLine();
//							
//							int host = Integer.parseInt(id);
//							getHost(host).receive(socket, filename);
//						}
					}
					
				}	
				catch (IOException e){ System.out.println("Could not create streams."); }
				
			}
			catch (IOException e) { System.out.println("Could not create socket."); }
		}	
	}
	
	public static String sendList()
	{
		StringBuffer sb = new StringBuffer();
		for(ServerConnection sc : peers)
		{
			sb.append("List from host "+sc.getID()+"\n");
			sb.append(sc.getList());
		}
		return sb.toString();
	}
	
	public static ServerConnection getHost(int id)
	{
		for(ServerConnection s : peers)
		{
			if(s.getID() == id)
				return s;
		}
		return null;
	}
	
//	public void receive(Socket socket, BufferedReader in, PrintWriter out)
//	{
//		String handshake;
//		try 
//		{
//			handshake = in.readLine();
//			switch(handshake)
//			{
//				case "Connect" :
//				{
//					Connection newCon = new Connection(socket, idnumber++);
//				}
//				break;
//			}
//		}
//		catch (IOException e) { System.out.println("Error while receiving from stream"); }
//	}
}
