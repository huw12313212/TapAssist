package Main;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import LogDataParse.LogDataParse;
import LogDataParse.TaskSegment;

public class Main {

	  public static void main(String args[]) throws Exception 
	  {
		  File file = new File("./Content/F5*1*24_2013_08_14_11_34_44.txt");
		  
		  List<String> strList = LogDataParse.getFileStrings(file);
		  
		  List<JSONObject> jsonList = LogDataParse.parseFile(strList);
		  
		  List<TaskSegment> SegmentList = LogDataParse.analysisTaskSegment(jsonList,false);
		  
		  List<TaskSegment> ValidateSegmentList = LogDataParse.FindValidateSegment(SegmentList, true);
		  
		  
		  
	  }
}
