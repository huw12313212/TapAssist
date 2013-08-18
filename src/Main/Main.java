package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import LogDataParse.Experience;
import LogDataParse.LogDataParse;
import LogDataParse.TaskSegment;

public class Main {

	  public static void main(String args[]) throws Exception 
	  {
		  File file = new File("./Content/F5*1*24_2013_08_14_11_34_44.txt");
		  
		  List<String> strList = LogDataParse.getFileStrings(file);
		  
		  List<JSONObject> jsonList = LogDataParse.parseFile(strList);
		  
		  List<JSONObject> overlapFreeJsonList = LogDataParse.EliminateOverlapLog(jsonList, false);
		  
		  List<TaskSegment> SegmentList = LogDataParse.analysisTaskSegment(overlapFreeJsonList,false);
		  
		  List<TaskSegment> ValidateSegmentList = LogDataParse.FindValidateSegment(SegmentList, false);
		  
		  List<TaskSegment> TappingList = new ArrayList<TaskSegment>();
		  List<TaskSegment> ScrollingList = new ArrayList<TaskSegment>();
		  LogDataParse.SplitTwoTask(ValidateSegmentList, TappingList, ScrollingList);
		  
		  System.out.println("[Phase1] ParseFile :" + Detail(strList,jsonList));
		  System.out.println("[Phase2] EliminateOverlapLog :" + Detail(jsonList,overlapFreeJsonList));
		  System.out.println("[Phase3] analysisTaskSegment :" + Detail(overlapFreeJsonList,SegmentList));
		  System.out.println("[Phase4] ValidateSegmentList :" + Detail(SegmentList,ValidateSegmentList));
		  System.out.println("[Phase5] Split : Tap("+TappingList.size()+")" +" Scroll("+ScrollingList.size()+")");
		  
		  Experience TappingExperience = new Experience(TappingList);
		  Experience ScrollingExperience = new Experience(ScrollingList);
		  
		  
		  String result = ScrollingExperience.ExportScrollingAttemptAnalysisAsCSV();
		 
		  SaveAsFile(result,"./Result/F5_slop24_scroll.csv");
		  
		 
	  }
	  
	  public static void SaveAsFile(String data,String filePath)
	  {
		  File file = new File(filePath);
		  
		  FileWriter fw;
		  
	  
		try 
		{
			fw = new FileWriter(file);
			fw.write(data);
			fw.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  

	  }
	  
	  public static String Detail(List before,List after)
	  {
		  int beforeSize = before.size();
		  int afterSize = after.size();
		  
		  String result = "before:"+beforeSize+" after:"+afterSize;
		  
		  return result;
	  }
}
