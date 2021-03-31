package General;

import java.io.BufferedReader;
import java.io.IOException;

public class StreamPrinter extends Thread{
	
	private BufferedReader reader;
	
	public StreamPrinter(BufferedReader reader)
	{
		this.reader = reader;
	}
	
	public void run()
	{
		String line;
		try {
			while ((line = reader.readLine()) != null)
			{
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println("Errore durante la lettura dallo stream");
		}
		
		try {
			reader.close();
		} catch (IOException e) {
		}
	}
}
