import java.io.*;
import java.net.*;

public class HostListener extends Thread{

	static int PORT;
	
	// TCP Socket
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private BufferedReader listenerIN = null;
	private PrintWriter listenerOUT = null;
	private static String answer;
	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	
	private static HostListener instance = null;
	
	private HostListener(int port)
	{
		PORT = port;
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
				clientSocket.setSoTimeout(15000);
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
					System.out.println("Peer with IP: "+clientSocket.getInetAddress()+" is trying to connect. \nType ack to accept connection or anything else to decline");
					
					synchronized (instance)
					{
						wait();
					}
					
					if(answer.equals("ACK"))
					{
						listenerOUT.println("ACK0");
						String ack = "";
						if((ack = listenerIN.readLine()).contains("ACK1"))
						{
							String[] args = ack.split("\\s");
							int port = Integer.parseInt(args[1]);
							System.out.println(clientSocket.getInetAddress().toString().substring(1)+" "+port);
							UserInterface.peer = Connection.getInstance(clientSocket.getInetAddress().toString().substring(1), port);
							Connection.lock = true;
							clientSocket.close();
							System.out.println("Connection established");
							return;
						}
					}
					else
					{
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
				
				else if(handshake.equals("Get List"))
				{
					listenerOUT.println(fileList.sendFiles());
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
