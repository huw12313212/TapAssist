package LogDataParse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Task {
	
	public Task(TaskSegment originData)
	{
		initialize(originData);
	}
	
	public JSONObject TaskHead;
	public JSONObject TaskTail;
	public List<AttemptSegment> AttemptList;
	
	public Boolean Corrupted = false;
	
	
	
	public String GetTaskDescription()
	{
		JSONObject meta = TaskHead.getJSONObject("metadata");
		
		Long from = meta.getLong("initialSelection");
		Long to = meta.getLong("targetSelection");
		
		return from + "->" + to;
	}
	
	public double GetTargetX()
	{
		JSONObject meta = TaskHead.getJSONObject("metadata");
		
		return meta.getDouble("targetX");
	}
	
	public double GetTargetY()
	{
		JSONObject meta = TaskHead.getJSONObject("metadata");
		
		return meta.getDouble("targetY");
	}
	
	public double GetTaskBeginTime()
	{
		return TaskHead.getDouble("time");
	}
	
	public void initialize(TaskSegment originData)
	{
		TaskHead = originData.getHead();
		TaskTail = originData.getTail();
		
		List<JSONObject> TouchEventList = new ArrayList<JSONObject>();
		TouchEventList.addAll(originData.JsonList);
		TouchEventList.remove(TaskHead);
		TouchEventList.remove(TaskTail);
		
		
		if(TouchEventList.size() == 0)
		{
			System.err.println("["+TaskHead.getInt("line")+"] Corrupted");
			Corrupted = true;
		}
		
		AttemptList = AnalysisAttemptSegment(TouchEventList);
		AttemptList = ValidateAttemptSegment(AttemptList);
		
	}
	
	public static List<AttemptSegment> ValidateAttemptSegment(List<AttemptSegment> origin)
	{
		List<AttemptSegment> result = new ArrayList<AttemptSegment>();
		
		for(int i =0 ; i < origin.size(); i ++)
		{
			AttemptSegment nowSegment = origin.get(i);
			
			if(nowSegment.isValidate())
			{
				result.add(nowSegment);
			}
			else
			{
				System.out.println("["+nowSegment.getHead().getInt("line")+"<->"+nowSegment.getTail().getInt("line")+"]bad attempt event");
			}
		}
		
		return result;
	}
	
	public static List<AttemptSegment> AnalysisAttemptSegment(List<JSONObject> touchEventList)
	{
		List<AttemptSegment> attemptList = new ArrayList<AttemptSegment>();
		
		AttemptSegment nowAttempSegment = new AttemptSegment();
		
		for(int i = 0; i < touchEventList.size();i++)
		{
			JSONObject nowTouchEvent = touchEventList.get(i);
			JSONArray array = nowTouchEvent.getJSONArray("pointers");
			
			nowAttempSegment.TouchEventList.add(nowTouchEvent);
			
			if(array.length()==1)
			{
				String Action = nowTouchEvent.getString("action");
				if(Action.contains("UP"))
				{
					attemptList.add(nowAttempSegment);
					nowAttempSegment = new AttemptSegment();
				}
			}
		}
		
		if(nowAttempSegment.TouchEventList.size()!=0)
		{
			attemptList.add(nowAttempSegment);
		}
		
		return attemptList;
	}
	

	
	
	
	
}
