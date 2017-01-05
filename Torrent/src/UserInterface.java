import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class UserInterface extends Thread{

	private static UserInterface instance = null; 
	protected static BufferedReader console;
	protected static ArrayList<Connection> peers = new ArrayList<Connection>();
	protected static boolean portEstablished = false;
	protected static FileList fileList = FileList.getInstance(Main.DIRPATH);
	
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
		String argument;
		while(true)
		{
			try
			{
				if((argument = console.readLine()) != null)
					commandCenter(argument);
			}
			catch (IOException e) {	e.printStackTrace(); }
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
				if (argsCount != 3)
				{
					System.out.println("Wrong number of arguments. This command takes 1 argument in form of. \nconnect ip port");
					return;
				}
				else
				{
					try
					{
						String ip = arguments[1];
						int port = Integer.parseInt(arguments[2]);
						try
						{
							Socket socket = new Socket();
							socket.connect(new InetSocketAddress(ip, port));
							try
							{
								PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
								BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								
									
								String handshake;
								out.println("Connect");
									
								Connection newCon = new Connection(socket);
							}	
							catch (IOException e){ System.out.println("Could not create socket.");  }
						}	
						catch (IOException e){ System.out.println("Error while connecting. Check host address."); }
					}
					catch (NumberFormatException e) { System.out.println("Port is not a Integer number."); }
				}
				break;
			}
			
			case "disconnect" :
			{
				if(argsCount == 2)
				{
					try
					{
						int conId = Integer.parseInt(arguments[1]);
						Connection c = getConnection(conId);
						if(c != null)
						{
							c.disconnect();
							peers.remove(c);
						}
						else
							System.out.println("No connection with number: "+conId);
					}
					catch(NumberFormatException e) { System.out.println("No connection with this id number"); }
				}
				else
					System.out.println("Wrong number of arguments. This command takes 1 argument in form of. \ndisconnect id");
				break;
			}

			case "getconns" :
			{
				String list = "";
				for(Connection c : peers)
				{
					list += "IP: "+c.getIp().toString()+" number: "+c.getID()+"\n";
				}
				System.out.println(list);
				break;
			}
			
			case "mylist" :
			{
				System.out.println(fileList.showFiles());
				break;
			}
			
			case "getlist" :
			{
				for(Connection c : peers)
				{
					c.getFileList();
				}
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
				if(argsCount > 1)
				{
					StringBuffer filename = new StringBuffer();
					for(int i = 1; i<arguments.length; i++)
					{
						filename.append(arguments[i]);
					}
					File file = fileList.getFile(filename.toString());
					System.out.println(filename.toString());
					if(file != null)
					{
						System.out.println("What host do you want to send files to?");
						String temp;
						try 
						{
							temp = console.readLine();
							try
							{
								int conId = Integer.parseInt(temp);
								Connection c = getConnection(conId);
								if(c != null)
									c.push(file);
								else
									System.out.println("No connection with number: "+conId);
							}
							catch(NumberFormatException e) { System.out.println("Argument: "+temp+" is not a number"); }
						} 
						catch (IOException e1) { System.out.println("Couldn't read from UserInterface."); }
					}
					else
						System.out.println("No such file");				
				}
				if(argsCount < 2)
					System.out.println("Wrong number of arguments. This command takes 1 or more arguments in form of. \npush file.");
				break;	
			}
			
			case "pull" :
			{
				if(argsCount > 1)
				{
					StringBuffer filename = new StringBuffer();
					filename.append(arguments[1]);
					for(int i = 2; i<arguments.length; i++)
					{
						filename.append(" "+arguments[i]);
					}
					System.out.println(filename.toString());
					System.out.println("What host do you want to pull files from?");
					String temp;
					try
					{
						temp = console.readLine();
						try
						{
							int conId = Integer.parseInt(temp);
							Connection c = getConnection(conId);
							if(c != null)
								c.pull(filename.toString());
							else
								System.out.println("No connection with number: "+conId);
						}
						catch(NumberFormatException e) { System.out.println("Argument: "+temp+" is not a number"); }
					} 
					catch (IOException e1) { System.out.println("Couldn't read from UserInterface."); }
					
				}
				if(argsCount < 2)
					System.out.println("Wrong number of arguments. This command takes 1 or more arguments in form of. \npull file.");
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
			if(c.getID() == id)
				return c;
		}
		return null;
	}
}
