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
	
	
	public static void SplitTwoTask(List<TaskSegment> orginData,List<TaskSegment> tappingList,List<TaskSegment> scrollingList)
	{
		for(int i = 0 ; i < orginData.size() ; i++)
		{
			TaskSegment currentTask = orginData.get(i);
			TaskSegment.taskType type = currentTask.GetTaskType();
			
			if(type == TaskSegment.taskType.tap)
			{
				tappingList.add(currentTask);
			}
			else if(type == TaskSegment.taskType.scroll)
			{
				scrollingList.add(currentTask);
			}
			else
			{
				System.err.println("undefined type");
			}
		}
	}
	
	public static List<JSONObject> EliminateOverlapLog(List<JSONObject> originData,boolean showLog)
	{
		List<JSONObject> result = new ArrayList<JSONObject>();
		
		JSONObject PreviousValidate = null;
		
		for(int i = 0 ;i <originData.size();i++)
		{
			JSONObject nowObject = originData.get(i);
			String logType = nowObject.getString("logType");
			
			if(logType.equals("touchEvent"))
			{
				result.add(nowObject);
			}
			else if(logType.equals("task"))
			{
				if(isValidate(PreviousValidate,nowObject,showLog))
				{
					result.add(nowObject);
					PreviousValidate = nowObject;
				}
				else
				{
					
				}
				
			}
			else
			{
				System.err.println("unmanaged case");
			}
		}
		
		
		
		return result;
	}
	
	private static boolean isValidate(JSONObject previous,JSONObject next,boolean showLog)
	{
		int previous_taskNumber;
		String previous_taskAction;
		
		int next_taskNumber;
		String next_taskAction;
		
		next_taskNumber = (int)next.getLong("taskNum");
		next_taskAction = next.getString("taskAction");
		
		if(next_taskNumber == 0 && next_taskAction.equals("start"))
		{
			return true;
		}
		
		if(previous == null)
		{
			if(showLog)
			{
				System.out.println("["+next.getInt("line")+"] ignore by not found start");
			}
			return false;
		}
		else
		{
			previous_taskNumber = (int)previous.getLong("taskNum");
			previous_taskAction = previous.getString("taskAction");
			
			if(previous_taskAction.equals("start")&&next_taskAction.equals("end"))
			{
				if(previous_taskNumber == next_taskNumber)
				{
					return true;
				}
				else
				{
					if(showLog)
					{
						System.out.println("["+next.getInt("line")+"] ignore by overlap");
					}
					return false;
				}
			}
			else if(previous_taskAction.equals("end")&&next_taskAction.equals("start"))
			{
				if((previous_taskNumber+1) == next_taskNumber)
				{
					return true;
				}
				else
				{
					if(showLog)
					{
						System.out.println("["+next.getInt("line")+"] ignore by overlap");
					}
					return false;
				}
			}
			else
			{
				if(showLog)
				{
					System.out.println("["+next.getInt("line")+"] ignore by overlap");
				}
				return false;
			}
			
		}

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
			int num = jsonObject.getInt("line");
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
						if(errMessage)System.err.println("["+num+"]"+": start end pair failed : 1");
					}
					else
					{
						if(errMessage)System.err.println("taskActionString string is null!");
					}
					
				}
				else if(typeString.equals("touchEvent"))
				{
					ignoreList.add(jsonObject);
					
					if(errMessage)System.err.println("["+num+"]"+"end <-> start : ignore");
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
		
		return taskSegmentList;
	}

	public static List<JSONObject> parseFile(List<String> logLines)
	{
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();

		for(int i = 0; i < logLines.size(); i ++)
		{
			String line = logLines.get(i);
			//System.out.println(i+1);
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
