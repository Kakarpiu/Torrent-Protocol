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
			System.out.println("Host Listens on port number: "+port);
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
				finally
				{
					clientSocket.close();
				}
			}
			catch (IOException e) { System.out.println("Could not create socket."); }	
			
		}	
	}
	
	public static void ackConnection(String a, int p)
	{
		ack = a;
		ackport = p;
		
		synchronized (instance)
		{
			instance.notify();
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
					System.out.println("Peer with IP: "+socket.getInetAddress()+" is trying to connect. \nType ack to accept connection or nak to decline");
					
					synchronized (instance)
					{
						try 
						{
							wait(15000);
						} catch (InterruptedException e) { System.out.println("Error while waiting for response from user"); }
					}
					
					if(ack == null)
					{
						System.out.println("User response timeout.");
						socket.close();
					}
					
					if(ack.equals("ACK"))
					{
						ack = null;
						int portnumber = ackport;
						ackport = 0;
						
						int idnumber = (int)(Math.random()*1000000)+1;
						Connection newCon = new Connection(portnumber, idnumber);
						out.println("ACK0");
						out.println(portnumber);
						out.println(idnumber);

						if(in.readLine().equals("ACK1"))
						{
							UserInterface.peers.add(newCon);
							System.out.println("Connection established");
						}
						else
							System.out.println("Couldn't establish connection");
					}
					else
					{
						System.out.println("Connection decliend");
					}
					break;
				}
			}
		}
		catch (IOException e) { System.out.println("Error while receiving from stream"); }
	}
}
