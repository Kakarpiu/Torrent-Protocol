import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class UserInterface extends Thread{

	private static UserInterface instance = null; 
	protected static BufferedReader console;
	protected static Connection peer;
	protected static boolean portEstablished = false;
	private String argument;	
	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	
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
				if(Connection.lock == true)
				{
					System.out.println("Connection already established to "+Connection.getIp()+":"+Connection.getPort()+
							". Disconnect this connection to establish new");
					return;
				}	
					
				else if (argsCount != 3)
				{
					System.out.println("Wrong number of arguments. This command takes 2 arguments in form of. \nIP PORT ");
					return;
				}
				else
					connect(arguments, argsCount); 
				break;
			}
			
			case "ack" : 
			{
				HostListener.ackConnection("ACK");
				break;
			}
			
			case "nak" :
			{
				HostListener.ackConnection("NAK");
				break;
			}
			
			case "disconnect" :
			{
				if(Connection.lock == true)
					peer.disconnect();
				else
					System.out.println("Couldn't disconnect from peer, because no connection was established");
				break;
			}
			
			case "mylist" :
			{
				fileList.showFiles();
				break;
			}
			
			case "getlist" :
			{
				if(Connection.lock == true)
					peer.getFileList();
				else 
					System.out.println("Couldn't get list from peer, because no connection was established");
				break;
			}
			
			case "push" :
			{
				if(Connection.lock == true)
				{
					int index = 0;
					File file = null;
					
					try
					{
						index = Integer.parseInt(arguments[1]);
						file = fileList.getFile(index);
						System.out.println(file.getName());
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
					}
				
					if(file != null)
						peer.push(file);
					else
						System.out.println("No such file");
				}
				else
					System.out.println("You can's push file because no connection is established.");
				break;
			}
			
			case "pull" :
			{
				break;
			}
		}
	}
	
	public void connect(String[] arguments, int argsCount)
	{		
		String ip = arguments[1];
		int port;
		try
		{
			port = Integer.parseInt(arguments[2]);
			peer = Connection.getInstance(ip, port);
			peer.connect();
		}
		catch (NumberFormatException e ) 
		{ 
			System.out.println("Port number is not Integer!"); 
			return;
		}
	
	}
}
