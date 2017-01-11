import java.io.*;
import java.net.*;

public class ClientConnection extends Thread{

	Socket socket = null;
	BufferedReader in = null;
	PrintWriter out = null;
	
	FileList list = FileList.getInstance(Client.DIRPATH);
	
	public ClientConnection(Socket s) // When connecting
	{
		socket = s;
		try
		{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println(list.showFiles());
			out.println("END");
			System.out.println("Connection established.");
			this.start();
		}	
		catch (IOException e){ System.out.println("Could not create streams."); }
	}

	public void run()
	{
		while(true)
		{
			
		}
	}
	
	public void receive(String command)
	{
		switch (command)
		{
			case "SendingList" :
			{
				String s;
				try 
				{
					while(!(s = in.readLine()).equals("END"))
						System.out.println(s);
				} 
				catch (IOException e) { System.out.println("Error while sending list"); }
				break;
			}
			
			case "Push" :
			{
				try 
				{
					String filename = in.readLine();
					String filesize = in.readLine();
					System.out.println("Peer with ID: "+idnumber+" is pushing "+filename+" "+filesize);
					
					FileTransfer ft = new FileTransfer(new File(Main.DIRPATH+"/"+filename), FileTransfer.command.RECEIVE);
					ft.start();
				}
				catch (IOException e) { System.out.println("Stream exception."); }
				break;
			}
			
		}
	}	
}
