import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class Main {
	
	public static void main(String[] args) {

		/** Set up these variables **/
		String AWS_ACCESS_KEY = "< INSERT YOUR KEY HERE >";
		String AWS_SECRET_KEY = "< INSERT YOUR KEY HERE >";
		String INPUT_FILE = "input.txt";
		String OUTPUT_FILE = "output.txt";
		/** End setup **/
		
		UrlInfo.setCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
		try	(
			BufferedReader br = new BufferedReader(
									new InputStreamReader(
										new FileInputStream(INPUT_FILE)));
				
			BufferedWriter out = new BufferedWriter(
					 				new FileWriter(OUTPUT_FILE));
		) {
			
			String site;
			while( (site = br.readLine()) != null)	{
				String result = UrlInfo.get(site);
				if(result != "")	{
					out.write("--------------------\n");
					out.write(result);
					out.write("\n");
				}
				else	{
					System.out.println("Failed to get ifno for " + site);
				}
			}
		}
		catch(Exception ex)	{
			ex.printStackTrace();
		}
	}
}
