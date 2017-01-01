import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class UserInterface extends Thread{

	private static UserInterface instance = null; 
	protected static BufferedReader console;
	protected static ArrayList<Connection> peers = new ArrayList<Connection>();
	protected static boolean portEstablished = false;
	private String argument;	
	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	private int TIMEOUT = 30000;
	
	private UserInterface()
	{
		console = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static UserInterface getInstance()
	{
		if(instance == null)
		{
			instance = new UserInterface();
		}
		return instance;
	}
	
	public void run() 
	{	
		while(true)
		{
			try
			{
				if((argument = console.readLine()) != null)
				{
					commandCenter(argument);
				}
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private void commandCenter(String command)
	{
		String[] arguments = command.split("\\s");
		int argsCount = arguments.length;
		String commandName = arguments[0];
		
		switch(commandName)
		{	
			case "connect" : 
			{
				if (argsCount != 2)
				{
					System.out.println("Wrong number of arguments. This command takes 1 argument in form of. \nconnect ip");
					return;
				}
				else
				{
					String ip = arguments[1];
					try
					{
						Socket socket = new Socket();
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
						try
						{
							socket.setSoTimeout(TIMEOUT);
							socket.connect(new InetSocketAddress(ip, Main.PORT));
							try 
							{
								// First handshake
								String handshake;
								out.println("Connect");
								
								// Second handshake
								handshake = in.readLine();
								if(handshake.equals("ACK0"))
								{
									int peerport = Integer.parseInt(in.readLine());
									int idnumber = Integer.parseInt(in.readLine());
									Connection newCon = new Connection(socket.getInetAddress(), peerport, idnumber);
									
									if(newCon.isConnected())
									{
										peers.add(newCon);
										out.println("ACK1");
										System.out.println("Connection established. ID number: "+idnumber);
									}
									else
									{
										out.println("NAK");
										System.out.println("Couldn't establish connection.");
									}
								}
								else if(handshake.equals("NAK"))
								{
									System.out.println("Peer declined connection");
								}
							}	
							catch (IOException e){	System.out.println("Error while connecting."); }
						}	
						catch (IOException e){ System.out.println("Could not connect."); }
					}	
					catch (IOException e){ System.out.println("Could not create socket."); }
				}
				break;
			}
			
			case "disconnect" :
			{
				if(argsCout == 2)
				{
					try
					{
						int conId = Integer.parseInt(arguments[1]);
						getConnection(conId);
						// Tu skoñczy³em
					}
					catch(NumberFormatException e) { System.out.println("No connection with this id number");
				}
				else
					System.out.println("Wrong number of arguments. This command takes 1 argument in form of. \ndisconnect id");
				break;
			}
			}
			
			case "ack" : 
			{
				System.out.println("What port number do you want to use for listening? Choose between 10001 and 60000.");
				boolean portEst = false;
				
				while(!portEst)
				{
					int portnumber = Integer.parseInt(console.readLine());
					if(portnumber < 10001 && portnumber > 60000)
						System.out.println("Choose between 10001 and 60000");
					else
					{
						portEst = true;
						HostListener.ackConnection("ACK", portnumber);
					}
				}
				break;
			}
			
			case "nak" :
			{
				HostListener.ackConnection("NAK", 0);
				break;
			}
			
			
			case "mylist" :
			{
				fileList.showFiles();
				break;
			}
			
			case "getlist" :
			{
				if(peer.lock == true)
					peer.getFileList();
				else 
					System.out.println("Couldn't get list from peer, because no connection was established");
				break;
			}
			
			case "timeout" :
			{
				try
				{
					int tmp = Integer.parseInt(arguments[1]);
				}
				catch(NumberFormatException e)
				{
					System.out.println("");
				}
			}
			case "push" :
			{
				if(Connection.lock == true && argsCount > 1)
				{
					ArrayList<File> files = new ArrayList<File>();
										
					for(int i = 1; i<argsCount; i++)
					{
						try
						{
							int tmp = Integer.parseInt(arguments[i]);
							if(fileList.getFile(tmp) != null)
								files.add(fileList.getFile(tmp));
							else
								System.out.println("No file with index: "+tmp);
						}
						catch (NumberFormatException e)
						{
							System.out.println("Argument: "+arguments[i]+" is not a number");
						}
					}
					File[] f = files.toArray(new File[files.size()]);
					peer.push(f);
				}
				if(argsCount < 2)
					System.out.println("Wrong number of arguments. This command takes 1 or more arguments in form of. \npush index...");
				break;
					
			}
			
			case "pull" :
			{
				if(Connection.lock == true && argsCount > 1)
				{
					ArrayList<Integer> files = new ArrayList<Integer>();
					
					for(int i = 1; i<argsCount; i++)
					{
						try
						{
							int tmp = Integer.parseInt(arguments[i]);
							files.add(tmp);
						}
						catch(NumberFormatException e)
						{
							System.out.println("Argument: "+arguments[i]+" is not a number");
						}
					}
					Integer[] f = files.toArray(new Integer[files.size()]);
					peer.pull(f);
				}
				if(argsCount < 2)
					System.out.println("Wrong number of arguments. This command takes 1 or more arguments in form of. \npull index...");
				break;
			}
			default :
			break;
		}
	}
	
	public Connection getConnection(int id)
	{
		for(Connection c : peers)
		{
			if(c.getId() == id)
				return c;
		}
		return null;
	}
}
