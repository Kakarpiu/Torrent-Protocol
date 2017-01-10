package Server;

import java.util.*;
import java.io.*;
import java.net.*;


public class Connection extends Thread{
	
	private InetAddress ip;
	private int connectionPort;
	private int idnumber;
	
	private Socket connectionSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	// FILETRANSFER LIST
	private StringBuffer list = new StringBuffer();
	
	public Connection(Socket s) // When connecting
	{
		connectionSocket = s;
		ip = connectionSocket.getInetAddress();
		try
		{
			out = new PrintWriter(connectionSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			idnumber = Integer.parseInt(in.readLine());
			out.println("ACK");
			System.out.println("Connection with IP: "+ip.toString()+" established. ID number: "+idnumber);
			UserInterface.peers.add(this);
			this.start();
		}	
		catch (IOException e){ System.out.println("Could not create streams."); }
		catch (NumberFormatException e) { System.out.println("Could not establish connection"); }
	}
	
	public Connection(Socket s, int id) // When receiving
	{
		connectionSocket = s;
		idnumber = id;	
		try
		{
			ip = connectionSocket.getInetAddress();
			out = new PrintWriter(connectionSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			out.println(id);
			if(in.readLine().equals("ACK"))
			{
				System.out.println("Connection with IP: "+ip.toString()+" established. ID number: "+idnumber);
				UserInterface.peers.add(this);
				this.start();
			}
		}
		catch (IOException e) { System.out.println("Couldn't create streams."); }
	}
	
	public void run()
	{
		while(true)
		{
			try 
			{
				String command = in.readLine();
				receive(command);
			} catch (IOException | NullPointerException e) { System.out.println("Host "+idnumber+" disconnected"); UserInterface.peers.remove(this); this.interrupt(); break; }			
		}
	}	
	
	public void receive(String command)
	{
		switch (command)
		{
			case "Push" :
			{
				try 
				{
					String filename = in.readLine();
					String filesize = in.readLine();
					System.out.println("Peer with ID: "+idnumber+" is pushing "+filename+" "+filesize);
					
					
					int transferport = 60001;	
					System.out.println("port established "+transferport);
					
					FileTransfer ft = new FileTransfer(transferport, new File(Main.DIRPATH+"/"+filename), FileTransfer.command.RECEIVE, this);
					transfers.add(ft);
					ft.start();
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
						int transferport = 60001;	
								
						FileTransfer ft = new FileTransfer(new Socket(connectionSocket.getInetAddress(), transferport), file, FileTransfer.command.PUSH, this);
						transfers.add(ft);
						ft.start();
					}
					else
					{
						out.println("Nofile");
					}
				}
				catch (IOException e) { System.out.println(); }
				break;
			}
			
			case "Nofile" :
			{
				System.out.println("No such file.");
			}
			
			case "GetList" :
			{
				String list = UserInterface.fileList.showFiles();
				out.println("SendList");
				out.println(list);
				out.println("END");
				break;
			}
			
			case "SendList" :
			{
				String s;
				try 
				{
					while(!(s = in.readLine()).equals("END"))
						list.append(s+"\n");
					synchronized(this)
					{
						notify();
					}
				} 
				catch (IOException e) { System.out.println("Error while sending list"); }
				break;
			}
			
			case "Disconnect" :
			{
				try 
				{
					connectionSocket.close();
					System.out.println("Peer disconnected.");
				} catch (IOException e) { System.out.println("Error while disconnecting."); }
				break;
			}
			
		}
	}
	
	public InetAddress getIp()
	{
		return ip;
	}
	
	public int getID()
	{
		return idnumber;
	}
	
	public boolean isConnected()
	{
		return connectionSocket.isConnected();
	}
	
	public void disconnect()
	{
		out.println("Disconnect");
	}
	
	public void getFileList()
	{
		list = new StringBuffer();
		out.println("GetList");
		synchronized (this)
		{
			try 
			{
				wait(15000);
			} 
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	public void printList()
	{
		System.out.println("List from host: "+idnumber);
		String[] formatdata = list.toString().split(",");
		
		for(int i = 0; i<formatdata.length; i++)
		{
			if(i % 3 == 0)
				System.out.printf("%-60s", formatdata[i]);
			if(i % 3 == 1)
				System.out.printf("%-11s", formatdata[i]);
			if(i % 3 == 2)
				System.out.printf("%-32s\n", formatdata[i]);
		}
		System.out.println();
	}
	
	
	public void push(File file)
	{
		try 
		{
			out.println("Push");
			out.println(file.getName());
			out.println(FileList.getFileSize(file));
			
			int transferport = 60001;
			
			FileTransfer ft = new FileTransfer(new Socket(ip, transferport), file, FileTransfer.command.PUSH, this);
			transfers.add(ft);
			ft.start();
			
		} 
		catch (IOException e) { System.out.println("Stream error"); }
	}
	
	public void pull(String file)
	{
		out.println("Pull");
		out.println(file);
		
		FileTransfer ft = new FileTransfer(60001, new File(Main.DIRPATH+"/"+file), FileTransfer.command.RECEIVE, this);
		transfers.add(ft);
		ft.start();
	}
}










