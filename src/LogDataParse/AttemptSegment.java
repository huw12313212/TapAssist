package LogDataParse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AttemptSegment {
	
	public List<JSONObject> TouchEventList = new ArrayList<JSONObject>();
	
	public double MaxDif(MultiTouchSelector getter)
	{
		double FirstX = this.getBeginX(getter);
		double FirstY = this.getBeginY(getter);
		
		double mostDif = 0;
		
		for(int i = 0;i<TouchEventList.size();i++)
		{
			JSONObject jsonData = TouchEventList.get(i);
			JSONArray pointers = jsonData.getJSONArray("pointers");
			JSONObject target = getter.Select(pointers);
			
			double targetX = target.getDouble("x");
			double targetY = target.getDouble("y");
			
			double dif = findDif(targetX,targetY,FirstX,FirstY);
			
			
			if(mostDif<dif)mostDif=dif;
			
		}
		return mostDif;
	}
	
	public double findDif(double x,double y,double x2,double y2)
	{
		double difX = x-x2;
		double difY = y-y2;
		
		double distance = Math.sqrt((difX*difX)+(difY*difY));
		
		return distance;
	}
	
	public boolean isValidate()
	{
		JSONObject head = getHead();
		
		if(!head.getString("action").contains("DOWN"))
		{
			return false;
		}
		
		return true;
	}
	
	public int getActionDownNumber()
	{
		int sum = 0;
		for(int i = 0; i < TouchEventList.size();i++)
		{
			if(TouchEventList.get(i).getString("action").contains("DOWN"))
			{
				sum ++;
			}
		}
		
		return sum;
	}
	
	public double getPathLength(MultiTouchSelector selector)
	{
		double sum = 0;
		
		for(int i = 1;i < TouchEventList.size();i++)
		{
			JSONArray previousPointers = TouchEventList.get(i-1).getJSONArray("pointers"); 
			JSONArray nowPointers = TouchEventList.get(i).getJSONArray("pointers");
			
			JSONObject previousPointer = selector.Select(previousPointers);
			JSONObject nowPointer = selector.Select(nowPointers);
			
			double x1 = previousPointer.getDouble("x");
			double y1 = previousPointer.getDouble("y");
			double x2 = nowPointer.getDouble("x");
			double y2 = nowPointer.getDouble("y");
			
			double difX = x2 - x1;
			double difY = y2 - y1;
			double Distance = Math.sqrt((difX * difX) + (difY*difY));
			
			sum += Distance;
		}
		
		return sum;
	}
	
	public String GetMinSpeed(MultiTouchSelector selector)
	{
		 List<Double> SpeedList = getSpeedList(selector);
		 
		 if(SpeedList.size()==0)
		 {
			 return "NaN";
		 }
		 else
		 {
			 double min = SpeedList.get(0);
			 
			 for(int i = 0;i < SpeedList.size();i++)
			 {
				 double current =SpeedList.get(i);
				 
				 if(current < min)
				 {
					 min = current;
				 }
			 }
			 
			 return min+"";
		 }
	}
	
	public String GetMaxSpeed(MultiTouchSelector selector)
	{
		 List<Double> SpeedList = getSpeedList(selector);
		 
		 if(SpeedList.size()==0)
		 {
			 return "NaN";
		 }
		 else
		 {
			 double max = SpeedList.get(0);
			 
			 for(int i = 0;i < SpeedList.size();i++)
			 {
				 double current =SpeedList.get(i);
				 
				 if(current > max)
				 {
					 max = current;
				 }
			 }
			 
			 return max+"";
		 }
	}
	
	public String GetAveSpeed(MultiTouchSelector selector)
	{
		 List<Double> SpeedList = getSpeedList(selector);
		 
		 if(SpeedList.size()==0)
		 {
			 return "NaN";
		 }
		 else
		 {
			 double sum = 0;
			 
			 for(int i=0;i<SpeedList.size();i++)
			 {
				 sum += SpeedList.get(i);
			 }
			 
			 double mean = sum/SpeedList.size();
			 
			 return mean+"";
		 }
	}
	
	public String GetSDSpeed(MultiTouchSelector selector)
	{
		List<Double> SpeedList = getSpeedList(selector);
		 
		 if(SpeedList.size()==0)
		 {
			 return "NaN";
		 }
		 else
		 {
			 double sumSquare = 0;
			// System.out.println("---");
			 for(int i=0;i<SpeedList.size();i++)
			 {
				 double data = SpeedList.get(i);
				 
				 //System.out.println(data);
				 sumSquare += data*data;
			 }
			 
			 double SquareMean = sumSquare/SpeedList.size();
			 
			 double Mean = Double.valueOf(GetAveSpeed(selector));
			 
			 double MeanSquare = Mean*Mean;
			 
			 double sd = Math.sqrt(SquareMean - MeanSquare);
			 
			// System.out.println("result"+sd);
			 
			 return sd+"";
		 }
	}
	
	
	
	public List<Double> getSpeedList(MultiTouchSelector selector)
	{
		List<Double> speedList = new ArrayList<Double>();
		//System.out.println(Distance);
		for(int i = 1;i < TouchEventList.size();i++)
		{
			JSONArray previousPointers = TouchEventList.get(i-1).getJSONArray("pointers"); 
			JSONArray nowPointers = TouchEventList.get(i).getJSONArray("pointers");
			
			JSONObject previousPointer = selector.Select(previousPointers);
			JSONObject nowPointer = selector.Select(nowPointers);
			
			double x1 = previousPointer.getDouble("x");
			double y1 = previousPointer.getDouble("y");
			double x2 = nowPointer.getDouble("x");
			double y2 = nowPointer.getDouble("y");
			
			double difX = x2 - x1;
			double difY = y2 - y1;
			double Distance = Math.sqrt((difX * difX) + (difY*difY));
			
			//System.out.println(Distance);
			double TimeDif = (TouchEventList.get(i).getDouble("time") - TouchEventList.get(i-1).getDouble("time"))/1000;
			
			//here is weird
			if(Distance != 0)
			{
				speedList.add(Distance/TimeDif);
			}
			
		}
		
		return speedList;
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
	
	public double getEnd(MultiTouchSelector selector,String key)
	{
		double result = 0;
		
		JSONArray tailPointers = this.getTail().getJSONArray("pointers");
		JSONObject tailPointer = selector.Select(tailPointers);
		double endValue = tailPointer.getDouble(key);
		
		result = endValue;
		
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
	
	
	public void SetOffset(float X,float Y)
	{
		for(int i = 0 ; i < TouchEventList.size(); i ++)
		{
			JSONObject nowTouchEvent = TouchEventList.get(i);
			JSONArray pointers = nowTouchEvent.getJSONArray("pointers");
			
			for(int j = 0;  j<pointers.length() ; j++ )
			{
				JSONObject point = pointers.getJSONObject(j);
				
				Double x = point.getDouble("x");
				Double y = point.getDouble("y");
				
				point.remove("x");
				point.remove("y");
				
				x += X;
				y += Y;
				
				point.put("x", x);
				point.put("y", y);
			}
		}
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
