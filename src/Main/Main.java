package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import LogDataParse.Experience;
import LogDataParse.LogDataParse;
import LogDataParse.MultiTouchAnalysis;
import LogDataParse.NoiseFilter;
import LogDataParse.TaskSegment;
import Model.TapAssistAnalysis;

public class Main {

	 public static String ContentDir = "./Content/";
	 public static String ResultDir = "./Result/";
	 public static String MultiTouchDir = "./MultiTouch/";
	 
	 public static List<Experience> TappingExperiences = new ArrayList<Experience>();
	
	  public static void main(String args[]) throws Exception 
	  {
		  File f = new File(ContentDir);
		  
		  File[] list = f.listFiles();
		  
		  for(int i = 0;i<list.length;i++)
		  {
			  String path = list[i].getAbsolutePath();
			  
			  if(path.endsWith(".txt"))
			  {
			  System.out.println(path);
			  ProcessFile(path);
			  }
		  }
		  
		  MultiTouchAnalysis.Analysis(TappingExperiences);
		  //Map<String,List<Experience>> map = MultiTouchAnalysis.SplitId(TappingExperiences);
	  }
	  
	
	
	  
	  public static void ProcessFile(String filePath)throws Exception 
	  {
		  File file = new File(filePath);
		  
		  List<String> strList = LogDataParse.getFileStrings(file);
		  
		  List<JSONObject> jsonList = LogDataParse.parseFile(strList);
		  
		  List<JSONObject> overlapFreeJsonList = LogDataParse.EliminateOverlapLog(jsonList, false);
		  
		  List<TaskSegment> SegmentList = LogDataParse.analysisTaskSegment(overlapFreeJsonList,false);
		  
		  List<TaskSegment> ValidateSegmentList = LogDataParse.FindValidateSegment(SegmentList, false);
		  
		  List<TaskSegment> TappingList = new ArrayList<TaskSegment>();
		  List<TaskSegment> ScrollingList = new ArrayList<TaskSegment>();
		  LogDataParse.SplitTwoTask(ValidateSegmentList, TappingList, ScrollingList);
		  
		  System.out.println("[File] ParseFile :" + filePath);
		  System.out.println("[Phase1] ParseFile :" + Detail(strList,jsonList));
		  System.out.println("[Phase2] EliminateOverlapLog :" + Detail(jsonList,overlapFreeJsonList));
		  System.out.println("[Phase3] analysisTaskSegment :" + Detail(overlapFreeJsonList,SegmentList));
		  System.out.println("[Phase4] ValidateSegmentList :" + Detail(SegmentList,ValidateSegmentList));
		  System.out.println("[Phase5] Split : Tap("+TappingList.size()+")" +" Scroll("+ScrollingList.size()+")");
		  
		
		  
		  Experience TappingExperience = new Experience(TappingList);
		  Experience ScrollingExperience = new Experience(ScrollingList);
		  
		  ScrollingExperience.RemoveTargets(ScrollingExperience.GetAllTimeOutList());
		  
		  TappingExperience.SetFilePath(filePath);
		  ScrollingExperience.SetFilePath(filePath);
		  
		  
		  TappingExperiences.add(TappingExperience);
		 // TappingExperience.ModifyPosition();
		 // System.out.println("[Phase6] Tap Experience ModifyPosition");
		  
		  
		  String filterPath = filePath.replace(".txt", "_filter.json");
		  File filterFile = new File(filterPath);
		  if(filterFile.exists())
		  {
			  NoiseFilter noisfilter = new NoiseFilter(filterFile);
			  
			  System.out.print("[Phase7] ");
			  noisfilter.FilterOutTap(TappingExperience);
			  noisfilter.FilterOutScroll(ScrollingExperience);
			  System.out.println("");
		  }
		  
		  
		  System.out.println("------");
		  
		  String result_Scroll = ScrollingExperience.ExportScrollingAttemptAnalysisAsCSV();
		  
		  String result_Tap = TappingExperience.ExportTappingAttemptAnalysisAsCSV();
		 
		  SaveAsFile(result_Scroll,ResultDir + file.getName().replace(".txt","_scroll.csv"));
		  SaveAsFile(result_Tap,ResultDir + file.getName().replace(".txt","_tap.csv"));
		  
		  
		  TapAssistAnalysis analysis = new TapAssistAnalysis(TappingExperience,ScrollingExperience);
		  String CDFResult = analysis.getCDF();
		  
		  SaveAsFile(CDFResult,ResultDir + file.getName().replace(".txt","_CDF.csv"));
		  
		  String pathCDFResult = analysis.getPathCDF();
		  SaveAsFile(pathCDFResult,ResultDir + file.getName().replace(".txt","_maxDif_CDF.csv"));
		  
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
