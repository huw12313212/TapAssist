package LogDataParse;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MultiTouchSelectorWithShortestDistance extends MultiTouchSelector{
	public double targetX;
	public double targetY;
	
	public MultiTouchSelectorWithShortestDistance(double l,double m)
	{
		targetX = l+33;
		targetY = m+33;
	}
	
	@Override
	public JSONObject Select(List<JSONObject> target)
	{
		JSONObject shortest = target.get(0);
		
		
		for(int i =0; i < target.size();i++)
		{
			if(GetDistance(shortest.getDouble("x"),shortest.getDouble("y"))>GetDistance(target.get(i).getDouble("x"),target.get(i).getDouble("y")))
			{
				shortest = target.get(i);
			}
		}
	
		return shortest;
	}


	@Override
	public JSONObject Select(JSONArray target)
	{
		JSONObject shortest = target.getJSONObject(0);
		
		
		for(int i =0; i < target.length();i++)
		{
			if(GetDistance(shortest.getDouble("x"),shortest.getDouble("y"))>GetDistance(target.getJSONObject(i).getDouble("x"),target.getJSONObject(i).getDouble("y")))
			{
				shortest = target.getJSONObject(i);
				//index = i;
			}
		}
		
		if(target.length()>1)
		{
		//System.out.println("shortest:"+index+ " from:"+target.length());
		}
		
		return shortest;
		//return ;
	}
	
	public double GetDistance(double nowX,double nowY)
	{
		double DistanceX = nowX - targetX;
		double DistanceY = nowY - targetY;
		
		double distSquare = (DistanceX*DistanceX) + (DistanceY*DistanceY);
		
		return Math.sqrt(distSquare);
	}
}
