import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Listener extends Thread{
	
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	
	public Listener(Socket s, PrintWriter o, BufferedReader i)
	{
		socket = s;
		out = o;
		in = i;
	}
	
	public void run()
	{
		String argument;
		while(true)
		{
			try
			{
				if((argument = in.readLine()) != null)
					commandCenter(argument);
			}
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	private void commandCenter(String command)
	{
		String[] arguments = command.split("\\s");
		int argsCount = arguments.length;
		String commandName = arguments[0];
		
		switch(commandName)
		{	
			case "dis" :
			{
				try 
				{
					socket.close();
					System.out.println("Peer disconnected.");
				} catch (IOException e) { System.out.println("Error while disconnecting."); }
				break;
			}
			
			case "Get List" :
			{
				out.println(UserInterface.fileList.sendFiles());
				break;
			}
			
			case "Push" :
			{
				ArrayList<String> filesNames = new ArrayList<String>();
				
				int filesCount;
				try 
				{
					filesCount = Integer.parseInt(in.readLine());
					System.out.println("Peer with IP: "+socket.getInetAddress()+" is pushing "+filesCount+" fiels.");
					
					for(int i = 0; i<filesCount; i++)
					{
						String name = in.readLine();
						String size = in.readLine();
						System.out.println(name+" with size of "+size+" bytes. Type ack to accept or nak to decline");
						
						try
						{
							String tmp = UserInterface.console.readLine();
							
							if(tmp.equals("ACK"))
							{
								out.println("ACK");
								filesNames.add(name);
							}
							if(tmp.equals("NAK"))
								out.println("NAK");
						}
						catch(IOException e) { System.out.println("UserInterface exceptions."); }
					}
					
					int transferport = 60001;
					for(int i = 0; i<filesNames.size(); i++)
					{
						FileTransfer ft = new FileTransfer(transferport+i, new File(Main.DIRPATH+"/"+filesNames.get(i)), FileTransfer.command.RECEIVE);
						ft.start();
					}
				} 
				catch (IOException e) { System.out.println("File count stream exception."); }
				break;
			}
		}
	}
}
