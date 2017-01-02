import java.io.*;
import java.net.*;

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
		}
	}
}
