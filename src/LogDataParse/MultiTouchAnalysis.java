package LogDataParse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import Main.Main;

public class MultiTouchAnalysis {
	
	static public void Analysis(List<Experience> tappingExperiences)
	{
		Map<String,List<Experience>> experiences_map = SplitId(tappingExperiences);
		Map<String,List<TapAttemptInfo>> tapAttemptsInfo_map = GetAllTapAttempt(experiences_map);
		
		
		String Analysis = "name,pureMiss,pureOverslop,miss&overslop,success \n";
		
		for(String name : tapAttemptsInfo_map.keySet())
		{
			List<TapAttemptInfo> infos = tapAttemptsInfo_map.get(name);
			String result = AnalysisPersonMultiTouchData(name,infos);
			
			String path = Main.MultiTouchDir + name+"_multitouch.csv";
			
			
			
			Main.SaveAsFile(result,path);
			System.out.println(path);
		}
		
		for(String name :experiences_map.keySet() )
		{
			List<Experience> elist = experiences_map.get(name);
			
			Experience e = elist.get(0);
			
			Analysis += name+","+e._PureMiss+","+e._PureOverSlop+","+e._OverslopAndMiss+","+e._Success+",\n";
		}
		
		Main.SaveAsFile(Analysis,Main.MultiTouchDir +"analysis.csv");
	}
	
	
	static private String AnalysisPersonMultiTouchData(String name,List<TapAttemptInfo> infos)
	{
		String result = "";
		
		List<TapAttemptInfo> multiTouchInfos = GetAllMultitouchInfo(infos);
		
		result += "#"+name+" total,"+infos.size()+",multiTouch,"+multiTouchInfos.size()+",ratio,"+((double)multiTouchInfos.size())/infos.size()+"\n";
		result += "\n";
		result += "TargetX,TargetY,TouchCount,IntentX,IntentY,LeftMost,TopMost,RightMost,BottomMost,DownPositions\n";
		
		int LeftMostCount = 0;
		int TopMostCount = 0;
		int RightMostCount = 0;
		int BottomMostCount = 0;
		
		
		for(TapAttemptInfo info : multiTouchInfos)
		{
			MultiTouchSelectorWithShortestDistance selector = new MultiTouchSelectorWithShortestDistance(info.targetX,info.targetY);
			JSONObject intent =selector.Select(info.pointers);
			
			result += info.targetX + ",";
			result += info.targetY + ",";
			result += info.multiTouchCount + ",";
			result += intent.getDouble("x")+",";
			result += intent.getDouble("y")+",";
			
			
			double mostLeft = getMinimum(info.pointers,"x");
			double mostRight = getMaximum(info.pointers,"x");
			double mostTop = getMinimum(info.pointers,"y");
			double mostBottom = getMaximum(info.pointers,"y");
			
			
			Boolean b_mostLeft =  (intent.getDouble("x")==mostLeft);
			Boolean b_mostTop = (intent.getDouble("y")==mostTop);
			Boolean b_mostRight = (intent.getDouble("x")==mostRight);
			Boolean b_mostBottom = (intent.getDouble("y")==mostBottom);
			
			result += b_mostLeft + ",";
			result += b_mostTop +",";
			result += b_mostRight+",";
			result += b_mostBottom+",";
			
			if(b_mostLeft)
			{
				LeftMostCount ++;
			}
			
			if(b_mostTop)
			{
				TopMostCount ++;
			}
			
			if(b_mostRight)
			{
				RightMostCount ++;
			}
			
			if(b_mostBottom)
			{
				BottomMostCount ++;
			}
			
			for(JSONObject pointer : info.pointers)
			{
				result += "x:"+pointer.getDouble("x")+","+"y:"+pointer.getDouble("y")+",";
			}
			result += "\n";
		}
		result += "\n";
		
		result += "TouchCountAve," + getAveMultiTouchCount(multiTouchInfos)+"\n";
		
		result += "LeftMost,"+LeftMostCount+",";
		result += "TopMost,"+TopMostCount+",";
		result += "RightMost,"+RightMostCount+",";
		result += "BottomMost,"+BottomMostCount+",\n";
		
		result += "LeftMost(%),"+((double)LeftMostCount)/multiTouchInfos.size()+",";
		result += "TopMost(%),"+((double)TopMostCount)/multiTouchInfos.size()+",";
		result += "RightMost(%),"+((double)RightMostCount)/multiTouchInfos.size()+",";
		result += "BottomMost(%),"+((double)BottomMostCount)/multiTouchInfos.size()+",\n";
		
		
		return result;
	}
	
	static private double getMinimum(List<JSONObject> Pointers,String tag)
	{
		double result = Pointers.get(0).getDouble(tag);
		
		for(JSONObject obj : Pointers)
		{
			if(obj.getDouble(tag) < result)
			{
				result = obj.getDouble(tag);
			}
		}
		
		return result;
	}
	
	static private double getMaximum(List<JSONObject> Pointers,String tag)
	{
		double result = Pointers.get(0).getDouble(tag);
		
		for(JSONObject obj : Pointers)
		{
			if(obj.getDouble(tag) > result)
			{
				result = obj.getDouble(tag);
			}
		}
		
		return result;
	}
	
	
	
	static private double getAveMultiTouchCount(List<TapAttemptInfo> infos)
	{
		double sum = 0;
		double count = infos.size();
		
		for(TapAttemptInfo info : infos)
		{
			sum += info.multiTouchCount;
		}
		
		double ave = sum/count;
		
		return ave;
	}
	
	static private List<TapAttemptInfo> GetAllMultitouchInfo(List<TapAttemptInfo> list)
	{
		List<TapAttemptInfo> multiTouchList = new ArrayList<TapAttemptInfo>();
		
		for(TapAttemptInfo info : list)
		{
			if(info.multiTouchCount>1)
			{
				multiTouchList.add(info);
			}
		}
		
		return multiTouchList;
	}
	
	  static private Map<String,List<Experience>> SplitId(List<Experience> tappingExperiences)
	  {
		  Map<String,List<Experience>> map = new HashMap<String,List<Experience>>();
		  
		  //Split Identical Name ID
		  for(int i = 0; i < tappingExperiences.size();i++)
		  {
			 Experience nowExperience = tappingExperiences.get(i);
			 String id = nowExperience.GetNameId();
			 
			 TryToAdd(map,id,nowExperience);
			 
			 TryToAdd(map,"all",nowExperience);
		  }
		  
		  return map;
	  }
	  
	  static private void TryToAdd(Map<String,List<Experience>> map,String id,Experience nowExperience)
	  {
		  if( map.containsKey(id))
			{
				List<Experience> id_Experiences = map.get(id);
				
				id_Experiences.add(nowExperience);
			}
			else
			{
				List<Experience> id_Experiences = new ArrayList<Experience>();
				map.put(id, id_Experiences);
				
				id_Experiences.add(nowExperience);
			}
	  }
	  
	  static private Map<String,List<TapAttemptInfo>> GetAllTapAttempt(Map<String,List<Experience>> experiences_map)
	  {
		  Map<String,List<TapAttemptInfo>> attempts_map = new HashMap<String,List<TapAttemptInfo>>();
		  
		  for(String id : experiences_map.keySet())
		  {
			  List<Experience > experiences =experiences_map.get(id);
			  
			  List<TapAttemptInfo> infoList = new ArrayList<TapAttemptInfo>();
			  attempts_map.put(id, infoList);
			  
			  for(Experience exp : experiences)
			  {
				  infoList.addAll(exp.GetAllTapAttemptInfo());
			  }
		  }
		  
		  return attempts_map;
	  }
	  
}
