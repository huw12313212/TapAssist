package LogDataParse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class LogDataParse {
	
	enum SegmentState
	{
		findingStart,
		findingEnd,
	}
	
	public static List<TaskSegment> FindValidateSegment(List<TaskSegment> originList , boolean showLog)
	{
		List<TaskSegment> validateList = new ArrayList<TaskSegment>();
		List<TaskSegment> corruptList = new ArrayList<TaskSegment>();
		
		for(int i = 0;i <originList.size();i++)
		{
			
			TaskSegment seg = originList.get(i);
			
			if(seg.isValidate(showLog))
			{
				validateList.add(seg);
			}
			else
			{
				corruptList.add(seg);
			}
		}
		
		if(showLog)
		{
			System.out.println("origin:"+originList.size()+" validate:"+validateList.size());
		}
		
		return validateList;
	}
	
	public static List<TaskSegment> analysisTaskSegment(List<JSONObject> jsonList,boolean errMessage )
	{
		List<TaskSegment> taskSegmentList = new ArrayList<TaskSegment>();
		List<JSONObject> ignoreList = new ArrayList<JSONObject>();
		SegmentState state = SegmentState.findingStart;
		TaskSegment currentSegment = null;
		
		for(int i = 0 ; i < jsonList.size() ; i++)
		{
			JSONObject jsonObject = jsonList.get(i);
			String typeString = jsonObject.getString("logType");
	
			switch(state)
			{
			case findingStart:
				
				if(typeString.equals("task"))
				{
					String taskActionString = jsonObject.getString("taskAction");
					
					if(taskActionString.equals("start"))
					{
						currentSegment = new TaskSegment(new ArrayList<JSONObject>());
						currentSegment.JsonList.add(jsonObject);
						state = SegmentState.findingEnd;
					}
					else if (taskActionString.equals("end"))
					{
						if(errMessage)System.err.println("["+i+"]"+": start end pair failed : 1");
					}
					else
					{
						if(errMessage)System.err.println("taskActionString string is null!");
					}
					
				}
				else if(typeString.equals("touchEvent"))
				{
					ignoreList.add(jsonObject);
					
					if(errMessage)System.err.println("end <-> start : ignore");
				}
				else
				{
					if(errMessage)System.err.println("type string is null!");
				}
				
				break;
			case findingEnd:
				
				if(typeString.equals("task"))
				{
					String taskActionString = jsonObject.getString("taskAction");
					
					if(taskActionString.equals("start"))
					{
						if(errMessage)System.err.println("["+i+"]"+": start end pair failed : 2");
						
						ignoreList.addAll(currentSegment.JsonList);
						currentSegment = new TaskSegment(new ArrayList<JSONObject>());
						currentSegment.JsonList.add(jsonObject);
					}
					else if (taskActionString.equals("end"))
					{
						currentSegment.JsonList.add(jsonObject);
						taskSegmentList.add(currentSegment);
						currentSegment = null;
						state = SegmentState.findingStart;
					}
					else
					{
						if(errMessage)System.err.println("taskActionString string is null!");
					}
				}
				else if(typeString.equals("touchEvent"))
				{
					currentSegment.JsonList.add(jsonObject);
				}
				else
				{
					if(errMessage)System.err.println("type string is null!");
				}
				
				break;
			}
		}
		
		
		/*
		int originLength = jsonList.size();
		int ignoreLength = ignoreList.size();
		
		System.out.println("origin:"+originLength+" ignore:"+ignoreLength);*/
		
		
		return taskSegmentList;
	}

	public static List<JSONObject> parseFile(List<String> logLines)
	{
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();

		for(int i = 0; i < logLines.size(); i ++)
		{
			String line = logLines.get(i);
			JSONObject jsonObject = new JSONObject(line);
			jsonObject.put("line", i+1);
			jsonList.add(jsonObject);
		}
		
		return jsonList;
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
	    	data = reader.readLine();
	    }
	    
		
	    reader.close();
		
		return logLines;
	}
}
