import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port;
		UserInterface ui = UserInterface.getInstance();
		HostListener hl = null;
		
		while(!UserInterface.portEstablished)
		{
			System.out.println("What port do you want to use for listening? Choose between 10 000 and 60 000");
			try 
			{
				port = Integer.parseInt(ui.console.readLine());
				if(port >= 10000 && port <=60000 )
				{
					hl = HostListener.getInstance(port);
					UserInterface.portEstablished = true;
				}
				else
					System.out.println("Choose port number between 10 000 and 60 000");
			} 
			catch (NumberFormatException e) { System.out.println("Number Format Exception."); }
			catch (IOException e) { System.out.println("I/O Exception"); }
		}
		
		Runnable[] freds = {ui, hl};
		
		for(Runnable r : freds)
		{
			r.run();
		}
	}

}
