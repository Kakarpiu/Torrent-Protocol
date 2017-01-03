import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Listener extends Thread{
	
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	private ArrayList<FileTransfer> transfers = null;
	
	public Listener(Socket s, PrintWriter o, BufferedReader i,  ArrayList t)
	{
		socket = s;
		out = o;
		in = i;
		transfers = t;
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
				out.println(UserInterface.fileList.showFiles());
				break;
			}
			
			case "Push" :
			{
				try 
				{
					String filename = in.readLine();
					String filesize = in.readLine();
					System.out.println("Peer with IP: "+socket.getInetAddress()+" is pushing "+filename+" "+filesize);
					
					try
					{
						String tmp = UserInterface.console.readLine();
						if(tmp.equals("ACK"))
						{
							out.println("ACK");
							int transferport = 60001;
							
							FileTransfer ft = new FileTransfer(transferport, new File(Main.DIRPATH+"/"+filename), FileTransfer.command.RECEIVE);
							transfers.add(ft);
							ft.start();
						}
						if(tmp.equals("NAK"))
							out.println("NAK");
					}
					catch(IOException e) { System.out.println("Error while reading from console."); }
				}
				catch (IOException e) { System.out.println("Stream exception."); }
				break;
			}
			
			case "Pull" :
			{
				try
				{
					String filename = in.readLine();
					File file = UserInterface.fileList.getFile(filename);
					if(file != null)
					{
						out.println("ACK");
						if(in.readLine().equals("ACK1"))
						{
							int transferport = 60001;
							
							FileTransfer ft = new FileTransfer(new Socket(socket.getInetAddress(), transferport), file, FileTransfer.command.PUSH);
							transfers.add(ft);
							ft.start();
						}
					}
					else
						out.println("NAK");
				}
				catch (IOException e) { System.out.println(); }
				break;
			}
		}
	}
}
