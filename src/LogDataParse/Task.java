package LogDataParse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Task {
	
	
	//public static float ModifyPositionIfTapInX = 0;//914.76514f;
	//public static float ModifyPositionIfTapInY = 0;//595.663f;
	
	public static float ModifyPositionIfTapOutX = 0f;
	public static float ModifyPositionIfTapOutY = 0f;//56.0f;
	
	public Task(TaskSegment originData)
	{
		initialize(originData);
	}
	
	public JSONObject TaskHead;
	public JSONObject TaskTail;
	public List<AttemptSegment> AttemptList;
	
	public Boolean Corrupted = false;
	
	public Boolean Modified = false;
	
	public void ModifyPosition()
	{
		if(!Modified)
		{
			Modified = true;
			
			if(IsTapDownCorrect())
			{	
				SetOffset((float)this.GetTargetX(),(float)this.GetTargetY());
			}
			else
			{
				SetOffset(ModifyPositionIfTapOutX,ModifyPositionIfTapOutY);
			}
		}
		else
		{
			System.err.println("Duplicated Modify Position?");
		}
	}
	
	private void SetOffset(float X,float Y)
	{
		for(int i = 0;i<this.AttemptList.size();i++)
		{
			AttemptList.get(i).SetOffset(X, Y);
		}
	}
	
	public Boolean IsTapDownCorrect()
	{
		if(GetResult().equals("success"))
		{
			return true;
		}
		else
		{
			if(GetReason().equals("over_slop"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	public String GetTaskDescription()
	{
		JSONObject meta = TaskHead.getJSONObject("metadata");
		
		Long from = meta.getLong("initialSelection");
		Long to = meta.getLong("targetSelection");
		
		return from + "->" + to;
	}
	
	public String GetResult()
	{
		JSONObject meta = TaskTail.getJSONObject("metadata");
		
		String result = meta.getString("result");
		
		return result;
	}
	
	public String GetReason()
	{
		JSONObject meta = TaskTail.getJSONObject("metadata");
		
		String result = meta.getString("result");
		String reason = "";
		
		if(result.equals("fail"))
		{
			reason = meta.getString("reason");
		}
		
		return reason;
	}
	
	public double GetTargetX()
	{
		JSONObject meta = TaskHead.getJSONObject("metadata");
		
		return meta.getDouble("targetX");
	}
	
	public double GetTargetY()
	{
		JSONObject meta = TaskHead.getJSONObject("metadata");
		
		return meta.getDouble("targetY")+ModifyPositionIfTapOutY;
	}
	
	public double GetTaskBeginTime()
	{
		
		return TaskHead.getDouble("time");
	}
	
	public double GetTaskEndTime()
	{
		return TaskTail.getDouble("time");
	}
	
	public double GetTaskDuration()
	{
		return ((double)TaskTail.getDouble("time")-(double)TaskHead.getDouble("time"));
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
