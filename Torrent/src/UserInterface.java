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
					System.out.println("Connection already established to "+Connection.getIp()+":"+Connection.getPort()+
							". Disconnect this connection to establish new");
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
				peer.getFileList();
				break;
			}
			
			case "push" :
			{
				if(Connection.lock == true)
				{
					if(argsCount != 2)
					{
						File tmp = fileList.getFile(arguments[1]);
						if(tmp != null)
							transfer("push", tmp);
						else
							System.out.println("No such file");
					}
				}
				else
					System.out.println("You can's push file because no connection is established.");
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
	
	public void transfer(String cmd, File file)
	{
		if(cmd.equals("push"))
			peer.push(file);
	}
	
}
