

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerHostListener extends Thread{

	static ServerHostListener instance = null;
	private int idnumber = 0;
	private ServerSocket listener = null;
	private ArrayList<ServerConnection> peers =  new ArrayList<ServerConnection>();
	
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
				Socket clientSocket = listener.accept();
				ServerConnection newCon = new ServerConnection(clientSocket, idnumber++);
				peers.add(newCon);
			}
			catch (IOException e) { System.out.println("Could not create socket."); }
		}	
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
