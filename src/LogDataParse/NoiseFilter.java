package LogDataParse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class NoiseFilter {
	
	public List<Integer> TapNoises = new ArrayList<Integer>();
	public List<Integer> ScrollNoises = new ArrayList<Integer>();
	
	public void FilterOutTap(Experience experience)
	{
		System.out.print("Tap Noise Filter : ");
		
		for(int i = TapNoises.size()-1;i>=0;i--)
		{
			int targetNum = TapNoises.get(i);
			
			Task removed = experience.taskList.remove(targetNum-1);
			
			if(removed == null)
			{
				System.err.println("Noise Filter Error!");
			}
			
			System.out.print(targetNum+",");
		}
		System.out.print("   ");
	}
	
	public void FilterOutScroll(Experience experience)
	{
		System.out.print("Scroll Noise Filter : ");
		
		for(int i = ScrollNoises.size()-1;i>=0;i--)
		{
			int targetNum = ScrollNoises.get(i);
			
			Task removed = experience.taskList.remove(targetNum-1);
			
			if(removed == null)
			{
				System.err.println("Noise Filter Error!");
			}
			
			System.out.print(targetNum+",");
		}
		System.out.print("   ");
	}
	
	public NoiseFilter(File filterFile)
	{
		String all = "";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filterFile));
			
			
			String nowLine = reader.readLine();
			
			while(nowLine!=null)
			{
				all += nowLine;
				
				nowLine = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		JSONObject obj = new JSONObject(all);
		JSONArray tapNoises = obj.getJSONArray("tapNoises");
		JSONArray scrollNoises = obj.getJSONArray("scrollNoises");
		
		for(int i = 0; i< tapNoises.length() ; i ++)
		{
			TapNoises.add(tapNoises.getInt(i));
		}
		
		for(int i = 0; i< scrollNoises.length() ; i ++)
		{
			ScrollNoises.add(scrollNoises.getInt(i));
		}
	}

}
