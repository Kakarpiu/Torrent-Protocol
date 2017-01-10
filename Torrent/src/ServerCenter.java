

public class ServerCenter {

	public static void main(String[] args) {
		
		try
		{
			int port = Integer.parseInt(args[0]);
			
			ServerHostListener sl = ServerHostListener.getInstance(port);
			sl.start();
		}
		catch(NumberFormatException e) { System.out.println("Port is not an Integer number"); System.exit(0); }
	}

}
