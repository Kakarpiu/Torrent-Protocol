import java.io.*;
import java.net.*;

public class HostListener extends Thread{

	private int port;
	
	// TCP Socket
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private BufferedReader listenerIN = null;
	private PrintWriter listenerOUT = null;
	private static String answer;
	
	private static HostListener instance = null;
	
	private HostListener(int port)
	{
		this.port = port;
		try 
		{
			serverSocket = new ServerSocket(port);
			UserInterface.portEstablished = true;
			this.setName("HostListener");
			System.out.println("Host Listens on port number: "+port);
		} 
		catch (IOException e)
		{
			System.out.println("Can't create socket on a given port");
			UserInterface.portEstablished = false;
		}
	}
	
	public static HostListener getInstance(int port)
	{
		if(instance == null)
			instance = new HostListener(port);
		
		return instance;
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			try 
			{
				clientSocket = serverSocket.accept();
				receive();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void ackConnection(String a)
	{
		answer = a;
		synchronized (instance)
		{
			instance.notify();
		}
	}
	
	public void receive()
	{
		try
		{
			listenerIN = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			listenerOUT = new PrintWriter(clientSocket.getOutputStream(), true);
			
			String handshake;
			while( (handshake = listenerIN.readLine()) != null)
			{
				if((handshake.equals("Connect")) && (Connection.lock == false))	
				{
					System.out.println("Peer with IP: "+clientSocket.getInetAddress()+" is trying to connect. \nType ACK to accept connection or NAK to decline");
					
					synchronized (instance)
					{
						wait();
					}
					
					if(answer.equals("ACK"))
					{
						Connection.lock = true;
						System.out.println("Connection established");
						listenerOUT.println("ACK");
						clientSocket.close();
						return;
					}
				}
				else if(handshake.equals("Disconnect"))
				{
					Connection.lock = false;
					System.out.println("Peer has disconnected.");
					clientSocket.close();
					return;
				}
				else
				{
					listenerOUT.println("NAK");
					clientSocket.close();
					return;
				}
			}
		}
		
		catch (IOException e) 
		{
			System.out.println("Could not recive connection.");
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
	}
}
