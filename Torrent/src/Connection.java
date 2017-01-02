import java.util.*;
import java.io.*;
import java.net.*;


public class Connection {

	private InetAddress ip;
	private int port;
	private int idnumber;
	private int TIMEOUT = 30000;
	private Socket connectionSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Listener listener;
	
	// FILETRANSFER LIST
	private ArrayList<FileTransfer> transfers = new ArrayList<FileTransfer>();
	//	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	
	public Connection(InetAddress i, int p, int id) // When connecting
	{
		ip = i;
		port = p;
		idnumber = id;
		
		connectionSocket = new Socket();
		try
		{
			out = new PrintWriter(connectionSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			try
			{
				connectionSocket.setSoTimeout(TIMEOUT);
				connectionSocket.connect(new InetSocketAddress(ip, port));
				listener = new Listener(connectionSocket, out, in);
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
				listener = new Listener(connectionSocket, out, in);
				listener.start();
			}
			catch (IOException e) { System.out.println("Couldn't create streams."); }
		} 
		catch (IOException e) { System.out.println("Couldn't create socket."); }
	}
	
	public InetAddress getIp()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
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
			String response;
			
			while((response = in.readLine()) != null)
			{
				System.out.println(response);
			}
		} 
		catch (IOException e) { System.out.println("Couldn't get list."); }
	}
	
	
	public void push(File[] files)
	{
		try 
		{
			Socket socket = new Socket();
			socket.setSoTimeout(TIMEOUT);
			socket.connect(new InetSocketAddress(ip, port));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println("Push");
			out.println(files.length);
			
			for(int i = 0; i<files.length; i++)
			{
				out.println(files[i].getName());
				out.println(files[i].length());
			}
			
			String ans = in.readLine();
			String[] args = ans.split("\\s");
			
			if(args[0].equals("ACK"))
			{
				int transferport = Integer.parseInt(args[1]);
				
				for(int i = 0; i<files.length; i++)
				{
					FileTransfer ft = new FileTransfer(new Socket(ip, transferport+i), files[i], FileTransfer.command.PUSH);
					ft.start();
				}
				
			}
			else if(args[0].equals("NAK"))
			{
				System.out.println("Host decilend receiving file");
			}
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void pull(Integer[] files)
	{
		try
		{
			Socket socket = new Socket();
			socket.setSoTimeout(TIMEOUT);
			socket.connect(new InetSocketAddress(ip, port));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println("Pull");
			out.println(files.length);
			
			ArrayList<File> filesToRcv = new ArrayList<File>();
			
			for(int i = 0; i<files.length; i++)
			{
				out.println(files[i]);
				String response = in.readLine();
				if(response.equals("ACK")) 
				{
					String fname = in.readLine();
					filesToRcv.add(new File(fname));
				}
				else 
					System.out.println("There is no file with index: "+files[i]);
			}
			
			if(!filesToRcv.isEmpty())
			{
				int transferport = (int)(Math.random()*40000)+20000;
				
				out.println("ACK "+transferport);
				socket.close();
					
				for(int i = 0; i<filesToRcv.size(); i++)
				{
					FileTransfer ft = new FileTransfer(transferport+i, new File(Main.DIRPATH+"/"+filesToRcv.get(i)), FileTransfer.command.RECEIVE);
					ft.start();
				}
			}
			else 
			{
				out.println("NAK");
				System.out.println("None file index was correct.");
				socket.close();
			}
		}
		catch (IOException e)
		{
			
		}
	}
	
}









