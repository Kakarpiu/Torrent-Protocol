import java.util.*;
import java.io.*;
import java.net.*;


public class Connection {

	private int TIMEOUT = 30000;
	
	private InetAddress ip;
	private int idnumber;
		
	private Socket connectionSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Listener listener = null;
	
	// FILETRANSFER LIST
	private ArrayList<FileTransfer> transfers = new ArrayList<FileTransfer>();
	
	public Connection(InetAddress i, int p, int id) // When connecting
	{
		ip = i;
		idnumber = id;
		
		connectionSocket = new Socket();
		try
		{
			out = new PrintWriter(connectionSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			try
			{
				connectionSocket.setSoTimeout(TIMEOUT);
				connectionSocket.connect(new InetSocketAddress(ip, p));
				listener = new Listener(connectionSocket, out, in, transfers);
				listener.start();
			}	
			catch (IOException e){ System.out.println("Could not connect."); }
		} 
		catch (IOException e){ System.out.println("Could not create streams."); }
	}
	
	public Connection(int p, int id) // When receiving
	{
		ServerSocket tmp;
		try
		{
			tmp = new ServerSocket(p);
			connectionSocket = tmp.accept();
			try
			{
				out = new PrintWriter(connectionSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				idnumber = id;
				listener = new Listener(connectionSocket, out, in, transfers);
				listener.start();
				try
				{
					tmp.close();
				}
				catch (IOException e) { System.out.println("Error while closing listining socket."); }
			}
			catch (IOException e) { System.out.println("Couldn't create streams."); }
		} 
		catch (IOException e) { System.out.println("Couldn't create socket."); }
	}
	
	public InetAddress getIp()
	{
		return ip;
	}
	
	public int getId()
	{
		return idnumber;
	}
	
	public boolean isConnected()
	{
		return connectionSocket.isConnected();
	}
	
	public void setTimeout(int i)
	{
		TIMEOUT = i*1000;
	}
	
	public void disconnect()
	{
		out.println("dis");
	}
	
	public void getFileList()
	{
		try 
		{
			out.println("Get List");
			String response = in.readLine();
			
			while(response != null)
				System.out.println(response);
		} 
		catch (IOException e) { System.out.println("Couldn't get list."); }
	}
	
	
	public void push(File file)
	{
		try 
		{
			out.println("Push");
			out.println(file.getName());
			out.println(FileList.getFileSize(file));
			
			String tmp = in.readLine();
			if(tmp.equals("ACK"))
			{
				int transferport = 60001;
				
				FileTransfer ft = new FileTransfer(new Socket(ip, transferport), file, FileTransfer.command.PUSH);
				transfers.add(ft);
				ft.start();
			}
			else if(tmp.equals("NAK"))
				System.out.println("Host declined receiving file.");
		} 
		catch (IOException e) { System.out.println("Stream error"); }
	}
	
	public void pull(String file)
	{
		try
		{
			out.println("Pull");
			out.println(file);
			
			String response = in.readLine();
			if(response.equals("ACK")) 
			{
				int transferport = 60001;
				FileTransfer ft = new FileTransfer(transferport, new File(Main.DIRPATH+"/"+file), FileTransfer.command.RECEIVE);
				transfers.add(ft);
				ft.start();

				out.println("ACK1");
			}
			else 
				System.out.println("There is no such file at the host.");
		}
		catch (IOException e) { System.out.println(); }
	}
	
}









