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
	
}
