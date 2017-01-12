import java.io.*;
import java.net.*;

public class ClientConnection extends Thread{

	Socket socket = null;
	BufferedReader in = null;
	PrintWriter out = null;
	
	FileList list = FileList.getInstance(Client.DIRPATH);
	
	Thread suspendConnection = new Thread()
	{
		public void run()
		{
			while(socket.isConnected()){}
			System.out.println("Disconnected from server");
			this.interrupt();
		}
	};
	
	Thread listener = new Thread()
	{
		public void run()
		{
			String resp;
			while(true)
			{
				try 
				{
					resp = in.readLine();
					receive(resp);
				} 
				catch (IOException e) { System.out.println("Listener error."); }
			}
		}
	};
	
	public ClientConnection(Socket s) // When connecting
	{
		socket = s;
		try
		{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println("Connect");
			
			out.println(list.showFiles());
			out.println("END");
			
			System.out.println("Connection established.");
			this.start();
		}	
		catch (IOException e){ System.out.println("Could not create streams."); }
	}

	public void run()
	{
		suspendConnection.start();
		listener.start();
		while(true)
		{
			String s = Client.console.nextLine();
			String[] arguments = s.split("\\s");
			consoleRecv(arguments);
		}
	}
	
	public void consoleRecv(String[] command)
	{
		switch (command[0])
		{
			case "getlist" :
			{
				out.println("GetList");
				break;
			}
			
			case "mylist" :
			{
				list.printList();
			}
			
			case "push" :
			{
				File file = list.getFile(command[1]);
				if(file == null) break;
				
				out.println("Push");	
				out.println(file.getName());
				out.println(command[2]);
				break;
			}
		}
	}	
	
	public void receive(String command)
	{
		switch (command)
		{
			case "Sendinglist" :
			{
				StringBuffer s = new StringBuffer();
				
				try 
				{
					String resp;
					while(!(resp = in.readLine()).equals("END"))
						s.append(resp);
				}
				catch (IOException e) { System.out.println("Receiving Exception"); }
				
				String[] formatdata = s.toString().split(":");
				
				for(int i = 0; i<formatdata.length; i++)
				{
					if(i % 3 == 0)
						System.out.printf("%-60s", formatdata[i]);
					if(i % 3 == 1)
						System.out.printf("%-11s", formatdata[i]);
					if(i % 3 == 2)
						System.out.printf("%-32s\n", formatdata[i]);
				}
			}
			
			case "StartPushing" :
			{
				try 
				{
					String filename = in.readLine();
					String hostip = in.readLine();
					String hostport = in.readLine();
					
					int port = Integer.parseInt(hostport);
					
					Socket so = new Socket(new SocketInetAddress(), Client.Serverport);
					PrintWriter out = new PrintWriter(so.getOutputStream(), true);
					
					out.println("PushFile");
					out.println(filename);
					out.println(id);
					
					FileTransfer push = new FileTransfer(so, list.getFile(filename),FileTransfer.command.PUSH);
					push.start();
				}
				catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
