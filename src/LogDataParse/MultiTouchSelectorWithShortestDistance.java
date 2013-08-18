package LogDataParse;

import org.json.JSONArray;
import org.json.JSONObject;

public class MultiTouchSelectorWithShortestDistance extends MultiTouchSelector{
	public double targetX;
	public double targetY;
	
	public MultiTouchSelectorWithShortestDistance(double l,double m)
	{
		targetX = l;
		targetY = m;
	}
	


	@Override
	public JSONObject Select(JSONArray target)
	{
		JSONObject shortest = target.getJSONObject(0);
		
		//int index = 0;
		
		for(int i =0; i < target.length();i++)
		{
			if(GetDistance(shortest.getDouble("x"),shortest.getDouble("y"))>GetDistance(target.getJSONObject(i).getDouble("x"),target.getJSONObject(i).getDouble("y")))
			{
				shortest = target.getJSONObject(i);
				//index = i;
			}
		}
		
		//System.out.println("shortest:"+index+ " from:"+target.length());
		
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
