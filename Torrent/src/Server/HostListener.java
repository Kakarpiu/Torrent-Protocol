package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HostListener extends Thread{

	ServerSocket listener = null;
	private static HostListener instance = null;
	protected static ArrayList<Connection> peers = new ArrayList<Connection>();
	
	private static int PORT;
	
	private HostListener(int port)
	{
		PORT = port;
		try 
		{
			listener = new ServerSocket(port);
		} 
		catch (IOException e) { System.out.println("Port might be choosen by another application. Restart program and choose another port."); }       
	}
	
	public static HostListener getInstance(int port)
	{
		if(instance == null)
			instance = new HostListener(port);
		
		return instance;
	}
	
	public void run()
	{
		while(true)
		{
			try 
			{
				Socket clientSocket = listener.accept();
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
					
					peers.add(newCon);
				}
				break;
			}
		}
		catch (IOException e) { System.out.println("Error while receiving from stream"); }
	}
}

