

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerHostListener extends Thread{

	static ServerHostListener instance = null;
	private ServerSocket listener = null;
	private ArrayList<Connection> peers =  new ArrayList<Connection>();
	
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
					int idnumber = (int)(Math.random()*1000000)+1;
					Connection newCon = new Connection(socket, idnumber);
				}
				break;
			}
		}
		catch (IOException e) { System.out.println("Error while receiving from stream"); }
	}
}
