import java.util.*;
import java.io.*;
import java.net.*;


public class Connection {
	
	private static Connection instance = null;	
	private static String ip;
	private static int port;
	private static int idnumber;
	protected static boolean lock = false;
	private int TIMEOUT = 30000;
		
	// FILETRANSFER LIST
	protected static ArrayList<FileTransfer> transfers = new ArrayList<FileTransfer>();
	//	private static FileList fileList = FileList.getInstance(Main.DIRPATH);
	
	private Connection(String i, int p, int id)
	{
		ip = i;
		port = p;
		idnumber = id;
	}
	
	public static Connection getInstance(String ip, int port, int idnumber)
	{
		if(instance == null)
			instance = new Connection(ip, port, idnumber);
		return instance;
	}
	
	public static String getIp()
	{
		return ip;
	}
	
	public static int getPort()
	{
		return port;
	}	
	
	public void setTimeout(int i)
	{
		TIMEOUT = i*1000;
	}
	
	public void connect()
	{
		try 
		{
			Socket socket = new Socket();
			socket.setSoTimeout(TIMEOUT);
			socket.connect(new InetSocketAddress(ip, port));
			PrintWriter TCP_out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader TCP_input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
					
			String handshake;
			TCP_out.println("Connect");
			try 
			{
				handshake = TCP_input.readLine();
					if(handshake.contains("ACK0"))
				{
					TCP_out.println("ACK1 "+HostListener.PORT+" "+idnumber);
					lock = true;
					System.out.println("Connection established. ID number: "+idnumber);
				}
				else
				{
					System.out.println("Can't establish connection");
				}
				TCP_out.close();
				TCP_input.close();
				socket.close();
			} 
			catch (IOException e) 
			{	
				System.out.println("Socket closed."); 
				TCP_out.close();
				TCP_input.close();
				socket.close();
			}
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void disconnect()
	{
		try
		{
			Socket socket = new Socket();
			socket.setSoTimeout(TIMEOUT);
			socket.connect(new InetSocketAddress(ip, port));
			PrintWriter TCP_out = new PrintWriter(socket.getOutputStream(), true);
			TCP_out.println("Disconnect");
			socket.close();
			lock = false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void getFileList()
	{
		try 
		{
			Socket socket = new Socket();
			socket.setSoTimeout(TIMEOUT);
			socket.connect(new InetSocketAddress(ip, port));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println("Get List");
			String response;
			while((response = in.readLine()) != null)
			{
				System.out.println(response);
			}
			socket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
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
				System.out.println(files[i]);
				out.println(files[i]);
				String response = in.readLine();
				if(response.equals("ACK")) 
				{
					System.out.println("Acknowledged ? in connection");
					String fname = in.readLine();
					filesToRcv.add(new File(fname));
					System.out.println("Added file "+fname+" in connection");
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









