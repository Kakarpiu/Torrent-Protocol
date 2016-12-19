import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;


public class UserInterface extends Thread{

	private static UserInterface instance = null; 
	protected static BufferedReader console;
	private static Connection peer;
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
			case "CONNECT" : 
			{
				connect(arguments, argsCount); 
				break;
			}
			case "ACK" : 
			{
				HostListener.ackConnection("ACK");
				break;
			}
			case "DISCONNECT" :
			{
				peer.disconnect();
				break;
			}
			case "FILELIST" :
			{
				fileList.showFiles();
			}
			case "GETLIST" :
			{
				
			}
				
		}
	}
	
	public void connect(String[] arguments, int argsCount)
	{
		if(Connection.lock == true)
		{
			System.out.println("Connection already established to "+peer.getIp()+":"+peer.getPort()+". Disconnect this connection to establish new");
			return;
		}
		else
		{
			if (argsCount != 3)
			{
				System.out.println("Wrong number of arguments. This command takes 2 arguments in form of. \nIP PORT ");
				return;
			}
			
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
	
	public void disconnect()
	{
		if(Connection.lock == true)
		{
			peer.disconnect();
		}
		else
			System.out.println("Couldn't disconnect from peer, because no connection was established");
	}
	
}
