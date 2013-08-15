package LogDataParse;

import java.util.List;

import org.json.JSONObject;

public class TaskSegment 
{
	public TaskSegment(List<JSONObject> segmentList)
	{
		JsonList = segmentList;
	}
	
	public JSONObject getHead()
	{
		return JsonList.get(0);
	}
	
	public JSONObject getTail()
	{
		int TailIndex = JsonList.size()-1;
		return JsonList.get(TailIndex);
	}
	
	
	
	public boolean isValidate(boolean showLog)
	{
		
		JSONObject head = getHead();
		JSONObject tail = getTail();
		
		if(!head.getString("logType").equals("task"))
		{
			if(showLog)System.out.println("head log type error");
			
			return false;
		}
		
		if(!tail.getString("logType").equals("task"))
		{
			if(showLog)System.out.println("tail log type error");
			
			return false;
		}
		
		
		if(head.getLong("taskNum") != tail.getLong("taskNum"))
		{
			if(showLog)System.out.println("["+head.getInt("line")+"]<->["+tail.getInt("line")+"]:num"+head.getLong("taskNum")+" != num"+tail.getLong("taskNum")+"");
			
			return false;
		}
		
		if(!head.getString("taskType").equals( tail.getString("taskType")))
		{
			
			if(!head.getString("taskType").contains(tail.getString("taskType")))
			{
				if(showLog)System.out.println("type  error :"+head.getString("taskType")+":"+tail.getString("taskType"));
			
				return false;
			}
		}
		//System.out.println("type true");
		return true;
	}
	
	public List<JSONObject> JsonList;
}
