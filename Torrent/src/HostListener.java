import java.io.*;
import java.net.*;
import java.util.*;

public class HostListener extends Thread{

	static int PORT;
	
	// TCP Socket
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private BufferedReader listenerIN = null;
	private PrintWriter listenerOUT = null;
	private static String answer;
	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	private static Connection peer = null;
	
	private static HostListener instance = null;
	private int number;
	private int TIMEOUT = 30000;
	
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
				clientSocket = serverSocket.accept();
				clientSocket.setSoTimeout(TIMEOUT);
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
			
			String handshake = listenerIN.readLine();
			
			switch(handshake)
			{
				case "Connect" :
				{
					System.out.println("Peer with IP: "+clientSocket.getInetAddress()+" is trying to connect. \nType ack to accept connection or nak to decline");
					
					synchronized (instance)
					{
						wait(15000);
					}
					
					if(answer == null)
					{
						System.out.println("User response timeout.");
						clientSocket.close();
					}
					
					else if(answer.equals("ACK"))
					{
						listenerOUT.println("ACK0");
						String ack = "";
						if((ack = listenerIN.readLine()).contains("ACK1"))
						{
							String[] args = ack.split("\\s");
							int port = Integer.parseInt(args[1]);
							int idnumber = Integer.parseInt(args[2]);
							UserInterface.peer = Connection.getInstance(clientSocket.getInetAddress().toString().substring(1), port, idnumber);
							peer = UserInterface.peer;
							peer.lock = true;
							clientSocket.close();
							System.out.println("Connection with host: "+clientSocket.getInetAddress().toString().substring(1)+" "
									+port+" with id number: "+idnumber+" established");
						}
					}
					else
					{
						System.out.println("Connection decliend");
						clientSocket.close();
					}
					break;
				}
				
				case "Disconnect" :
				{
					peer.lock = false;
					System.out.println("Peer has disconnected.");
					clientSocket.close();
					break;
				}
				
				case "Get List" :
				{
					listenerOUT.println(fileList.sendFiles());
					clientSocket.close();
					break;
				}
				
				case "Push" :
				{
					
					int filesCount = Integer.parseInt(listenerIN.readLine());
					String[] filesNames = new String[filesCount];
					
					System.out.println("Peer with IP: "+clientSocket.getInetAddress()+" wants to push a files: ");
					for(int i = 0; i<filesCount; i++)
					{
						String name = listenerIN.readLine();
						String size = listenerIN.readLine();
						filesNames[i] = name;
						System.out.println(name+" with size of "+size+" bytes.");
					}
					
					System.out.println("Type ack to accept or nak to decline");

					synchronized (instance)
					{
						wait(15000);
					}
					
					if(answer == null)
					{
						System.out.println("User response timeout.");
						clientSocket.close();
					}
					
					if(answer.equals("ACK"))
					{
						int transferport = (int)(Math.random()*40000)+20000;
						listenerOUT.println("ACK "+transferport);
						clientSocket.close();
						
						for(int i = 0; i<filesCount; i++)
						{
							FileTransfer ft = new FileTransfer(transferport+i, new File(Main.DIRPATH+"/"+filesNames[i]), FileTransfer.command.RECEIVE);
							ft.start();
						}
					}
					if(answer.equals("NAK"))
					{
						listenerOUT.println("NAK");
						clientSocket.close();
					}
					break;
				}
				
				default :
				{
					if(Integer.parseInt(handshake) == number)
					{
						
					}
					listenerOUT.println("NAK");
					clientSocket.close();
					break;
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
