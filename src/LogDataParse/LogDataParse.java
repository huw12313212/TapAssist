package LogDataParse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class LogDataParse {

	public static List<JSONObject> ParseFile(File file) throws Exception
	{
		ArrayList<JSONObject> ParseFile = new ArrayList<JSONObject>();
		
		
		List<String> logLines = getFileStrings(file);
		
		
	
		
		
		return ParseFile;
	}
	
	public static List<String> getFileStrings(File file) throws Exception
	{
		ArrayList<String> logLines = new ArrayList<String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String data = null;

		data = reader.readLine();

	    while(data != null)
	    {
	    	logLines.add(data);
	    	reader.readLine();
	    }
	    
		
		
		
		return logLines;
	}
}
