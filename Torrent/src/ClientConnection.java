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
			String s = Client.console.nextLine();
			String[] arguments = s.split("\\s");
			receive(arguments);
		}
	}
	
	public void receive(String[] command)
	{
		switch (command[0])
		{
			case "getlist" :
			{
				out.println("GetList");
				StringBuffer s = new StringBuffer();
			
				s.append(in.readLine());
				String[] formatdata = s.toString().split("|");
				
				for(int i = 0; i<formatdata.length; i++)
				{
					if(i % 3 == 0)
						System.out.printf("%-60s", formatdata[i]);
					if(i % 3 == 1)
						System.out.printf("%-11s", formatdata[i]);
					if(i % 3 == 2)
						System.out.printf("%-32s\n", formatdata[i]);
				}
				break;
			}
			
			case "push" :
			{
				try 
				{
					list.getFile(filename)
					out.println("Push");
					out.println(file.getName());
					out.println(FileList.getFileSize(file));
					
					int transferport = 60001;
						
					FileTransfer ft = new FileTransfer(new Socket(ip, transferport), file, FileTransfer.command.PUSH);
					ft.start();
					
				} 
				catch (IOException e) { System.out.println("Stream error"); }
			}
			
		}
	}	
}
