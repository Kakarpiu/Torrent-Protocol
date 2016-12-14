import java.io.*;
import java.net.*;

public class HostListener extends Thread{

	private int port;
	
	// TCP Socket
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private BufferedReader listenerIN = null;
	private PrintWriter listenerOUT = null;
	
	private static HostListener instance = null;
	
	private HostListener(int port)
	{
		this.port = port;
		try 
		{
			serverSocket = new ServerSocket(port);
			UserInterface.portEstablished = true;
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
				System.out.println("Im in run");
				clientSocket = serverSocket.accept();
				receive();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void receive()
	{
		try
		{
			System.out.println("I'm in receive");
			listenerIN = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			listenerOUT = new PrintWriter(clientSocket.getOutputStream(), true);
			
			String handshake;
			while( (handshake = listenerIN.readLine()) != null)
			{
				System.out.println(handshake);
				System.out.println(Connection.lock);
				if((handshake == "I want to connect") && (Connection.lock == false))	
				{
					System.out.println("Peer with IP: "+clientSocket.getInetAddress()+" is trying to connect. \nType ACK to accept connection.");
					String answer = UserInterface.console.readLine();
					listenerOUT.println(answer);
					if(answer == "ACK")
					{
						Connection.lock = true;
					}
					else
					{
						listenerOUT.println("NAK");
						clientSocket.close();
					}
				}
				else
				{
					listenerOUT.println("NAK");
					clientSocket.close();
				}
			}
		}
		
		catch (IOException e) 
		{
			System.out.println();;
		}
		
		
	
	}
}
