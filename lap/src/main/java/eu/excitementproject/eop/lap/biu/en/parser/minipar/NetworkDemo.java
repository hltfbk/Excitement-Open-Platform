package eu.excitementproject.eop.lap.biu.en.parser.minipar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A demo client of {@link MiniparServer}.
 * 
 * @author Asher Stern
 *
 */
public class NetworkDemo
{
	public static void f(String[] args) throws Exception
	{
		if (args.length < 1) throw new Exception("args");
		System.out.println("minipar server demo:");
		BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter sentences. To stop type \"stop\"");
		String line = stdinReader.readLine();
		boolean stop = false;
		if (line != null) if (line.equals("stop")) stop=true;
		while (!stop)
		{
			Socket clientSocket = new Socket(args[0], MiniparServer.DEFAULT_MINIPAR_SERVER_PORT);
			PrintWriter pwOut = new PrintWriter(clientSocket.getOutputStream());
			pwOut.println(line);
			pwOut.flush();
			BufferedReader brIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String miniparLine = brIn.readLine();
			while (miniparLine != null)
			{
				System.out.println(miniparLine);
				miniparLine = brIn.readLine();
			}
			pwOut.close();
			brIn.close();
			clientSocket.close();
			
			line = stdinReader.readLine();
			if (line != null) if (line.equals("stop")) stop=true;
		}
		

		
		
	}

	public static void main(String[] args)
	{
		try{
			f(args);
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
