package LogDataParse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AttemptSegment {
	
	public List<JSONObject> TouchEventList = new ArrayList<JSONObject>();
	
	public boolean isValidate()
	{
		JSONObject head = getHead();
		
		if(!head.getString("action").contains("DOWN"))
		{
			return false;
		}
		
		return true;
	}
	
	public double getDif(MultiTouchSelector selector,String key)
	{
		double result = 0;
		
		JSONArray headPointers = this.getHead().getJSONArray("pointers");
		JSONArray tailPointers = this.getTail().getJSONArray("pointers");
		
		JSONObject headPointer = selector.Select(headPointers);
		JSONObject tailPointer = selector.Select(tailPointers);
		
		double initValue = headPointer.getDouble(key);
		double endValue = tailPointer.getDouble(key);
		
		result = endValue - initValue;
		
		return result;
	}
	
	public double getSD(MultiTouchSelector selector,String key)
	{
		List<JSONObject> allPointers = getSelectedPointer(selector);
		
		double sumSquare = 0;
		
		for(int i = 0; i < allPointers.size(); i++)
		{
			double data = allPointers.get(i).getDouble(key);
			sumSquare+= (data*data);
			
		}
		
		double SquareMean = sumSquare / allPointers.size();
		double Mean = getMean(selector,key);
		double MeanSqure = Mean*Mean;
		
		double result = Math.sqrt(SquareMean - MeanSqure);
	
		
		
		return result;
	}
	
	public double getMean(MultiTouchSelector selector,String key)
	{
		List<JSONObject> allPointers = getSelectedPointer(selector);
		
		double sum = 0;
		
		for(int i = 0; i < allPointers.size(); i++)
		{
			sum+=allPointers.get(i).getDouble(key);
		}
		
		double mean = sum / allPointers.size();
		
		return mean;
	}
	
	public double getMax(MultiTouchSelector selector,String key)
	{
		List<JSONObject> allPointers = getSelectedPointer(selector);
		
		double MaxX = allPointers.get(0).getDouble(key);
		
		for(int i = 0 ; i < allPointers.size() ; i ++)
		{
			JSONObject nowPointer = allPointers.get(i);
			double newX = nowPointer.getDouble(key);
			
			if(MaxX<newX)
			{
				MaxX = newX;
			}
			
		}
		
		return MaxX;
	}
	
	public double getMin(MultiTouchSelector selector,String key)
	{
		List<JSONObject> allPointers = getSelectedPointer(selector);
		
		double MinX = allPointers.get(0).getDouble(key);
		
		for(int i = 0 ; i < allPointers.size() ; i ++)
		{
			JSONObject nowPointer = allPointers.get(i);
			double newX = nowPointer.getDouble(key);
			
			if(MinX>newX)
			{
				MinX = newX;
			}
			
		}
		
		return MinX;
	}
	
	public List<JSONObject> getSelectedPointer(MultiTouchSelector selector)
	{
		List<JSONObject> result = new ArrayList<JSONObject>();
		
		for(int i = 0 ; i < TouchEventList.size(); i ++)
		{
			JSONObject nowTouchEvent = TouchEventList.get(i);
			JSONArray pointers = nowTouchEvent.getJSONArray("pointers");
			JSONObject point = selector.Select(pointers);
			
			result.add(point);
		}
		
		return result;
	}
	
	public double getBeginX(MultiTouchSelector getter)
	{
		JSONObject head = getHead();
		JSONObject targetPointer = GetPointer(head,getter);
		
		return targetPointer.getDouble("x");
	}
	
	public double getBeginY(MultiTouchSelector getter)
	{
		JSONObject head = getHead();
		JSONObject targetPointer = GetPointer(head,getter);
		
		return targetPointer.getDouble("y");
	}
	
	
	public static JSONObject GetPointer(JSONObject touchEvent,MultiTouchSelector getter)
	{
		JSONArray pointers = touchEvent.getJSONArray("pointers");
		JSONObject target = getter.Select(pointers);
		
		return target;
	}

	
	public double getBeginTime()
	{
		JSONObject head = getHead();
		return head.getDouble("time");
	}
	
	public double getEndTime()
	{
		JSONObject tail = getTail();
		return tail.getDouble("time");
	}
	
	public JSONObject getHead()
	{
		return TouchEventList.get(0);
	}
	
	public JSONObject getTail()
	{
		int TailIndex = TouchEventList.size() - 1;
		return TouchEventList.get(TailIndex);
	}
	

}
