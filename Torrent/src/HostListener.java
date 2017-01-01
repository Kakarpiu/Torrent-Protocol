import java.io.*;
import java.net.*;
import java.util.*;

public class HostListener extends Thread{

	static int PORT;
	
	// TCP Socket
	private ServerSocket serverSocket = null;
	private static String answer;
	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	private static ArrayList<Connection> peer = null;
	
	private static HostListener instance = null;
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
				Socket clientSocket = serverSocket.accept();
				clientSocket.setSoTimeout(TIMEOUT);
				try 
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));;
					PrintWriter out =  new PrintWriter(clientSocket.getOutputStream(), true);
					receive(clientSocket, in, out);
				}
				catch (IOException e) 
				{
					System.out.println("Could not create stream.");
				}
				finally
				{
					clientSocket.close();
				}
			}
			catch (IOException e) 
			{
				System.out.println("Could not create socket.");
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
	
	public void receive(Socket socket, BufferedReader in, PrintWriter out)
	{
		// First handshake
		String handshake = in.readLine();
		
		switch(handshake)
		{
			case "Connect" :
			{
				System.out.println("Peer with IP: "+socket.getInetAddress()+" is trying to connect. \nType ack to accept connection or nak to decline");
				
				synchronized (instance)
				{
					wait(15000);
				}
				
				if(answer == null)
				{
					System.out.println("User response timeout.");
					socket.close();
				}
				
				if(answer.equals("ACK"))
				{
					answer = null;
					System.out.println("What port number do you want to use for listening? Choose between 10001 and 60000 ");
					try
					{
						int portnumber = Integer.parseInt(UserInterface.console.readLine());
						if(portnumber < 10001 && portnumber > 60000)
							System.out.println("Choose between 10001 and 60000");
						else
						{
							int idnumber = (int)(Math.random()*1000000);
							Connection newCon = new Connection(portnumber, idnumber);
							out.println("ACK0");
							out.println(portnumber);
							out.println(idnumber);
							// Tu skoñczy³em sprawdz nadawnie portu
						}
					}
					catch(NumberFormatException e) { System.out.println("Input is not a integer number."); }
				}
				else
				{
					System.out.println("Connection decliend");
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
					ArrayList<String> filesNames = new ArrayList<String>();
					
					for(int i = 0; i<filesCount; i++)
					{
						String name = listenerIN.readLine();
						String size = listenerIN.readLine();
						System.out.println("Peer with IP: "+clientSocket.getInetAddress()+" wants to push file: "+name+" with size of "+size+" bytes. Type ack to accept or nak to decline");
						synchronized (instance)
						{
							wait(15000);
						}
						
						if(answer == null)
						{
							System.out.println("User response timeout. File declined");
						}
						
						if(answer.equals("ACK"))
						{
							filesNames.add(name);
						}
						if(answer.equals("NAK"))
						{
							listenerOUT.println("NAK");
						}
					}
					
					int transferport = (int)(Math.random()*40000)+20000;
					listenerOUT.println("ACK "+transferport);
					clientSocket.close();
						
					for(int i = 0; i<filesNames.size(); i++)
					{
						FileTransfer ft = new FileTransfer(transferport+i, new File(Main.DIRPATH+"/"+filesNames.get(i)), FileTransfer.command.RECEIVE);
						ft.start();
					}
					break;
				}
				
				case "Pull" :
				{
					int filesCount = Integer.parseInt(listenerIN.readLine());
					System.out.println(filesCount);
					ArrayList<File> filesIndx = new ArrayList<File>();
							
					for(int i = 0; i<filesCount; i++)
					{
						int tmp = Integer.parseInt(listenerIN.readLine());
						if(fileList.getFile(tmp) != null)
						{
							filesIndx.add(fileList.getFile(tmp));
							listenerOUT.println("ACK");
							listenerOUT.println(fileList.getFile(tmp).getName());
						}
						else
						{
							listenerOUT.println("NAK");
						}
					}
					
					String response = listenerIN.readLine();
					
					if(response.contains("ACK"))
					{
						String[] args = response.split("\\s");
						int transferport = Integer.parseInt(args[1]);
						clientSocket.close();
						
						for(int i = 0; i<filesIndx.size(); i++)
						{
							FileTransfer ft = new FileTransfer(new Socket(Connection.getIp(), transferport+i), filesIndx.get(i), FileTransfer.command.PUSH);
							ft.start();
						}
					}
					else
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
