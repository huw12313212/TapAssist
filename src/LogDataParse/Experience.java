package LogDataParse;

import java.util.ArrayList;
import java.util.List;

public class Experience 
{
	public static MultiTouchSelector multiTouchSelector = new MultiTouchSelector();
	
	public Experience(List<TaskSegment> originData)
	{
		taskList = new ArrayList<Task>();
		
		for(int i = 0 ; i < originData.size(); i++)
		{
			TaskSegment nowSegment = originData.get(i);
			
			taskList.add(new Task(nowSegment));
		}
	}
	
	public String ExportScrollingAttemptAnalysisAsCSV()
	{
		String data = "Task,Attempt,Task_Detail,begin_time(ms),end_time(ms)," +
				"touchBegin_x,touchBegin_y,max_x,max_y,min_x,min_y,ave_x," +
				"ave_y,SD_x,SD_y,x_end-begin,y_end-begin,Max_speed,Min_speed,Ave_speed," +
				"SD_speed,duration(ms),stroke_length,multitouch,V = stroke_length/time_duration\n";
		
		for(int taskNum = 0 ; taskNum < taskList.size(); taskNum++)
		{
			Task task = taskList.get(taskNum);
			List<AttemptSegment> attemptList = task.AttemptList;
			
			for(int attemptNum = 0 ; attemptNum < attemptList.size(); attemptNum++)
			{
				AttemptSegment nowAttempt = attemptList.get(attemptNum);
				MultiTouchSelector selector = multiTouchSelector;// new MultiTouchSelectorWithShortestDistance(task.GetTargetX(),task.GetTargetY());
				
				data +=(taskNum+1)+",";
				data +=(attemptNum+1)+",";
				//data +=task.GetTargetX()+",";
				//data +=task.GetTargetY()+",";
				data +=task.GetTaskDescription()+",";
				
				data +=(nowAttempt.getBeginTime() - taskList.get(0).GetTaskBeginTime())+",";
				data +=(nowAttempt.getEndTime()- taskList.get(0).GetTaskBeginTime())+",";
				
		
				
				data += nowAttempt.getBeginX(selector)+",";
				data += nowAttempt.getBeginY(selector)+",";
				data += nowAttempt.getMax(selector, "x")+",";
				data += nowAttempt.getMax(selector, "y")+",";
				data += nowAttempt.getMin(selector, "x")+",";
				data += nowAttempt.getMin(selector, "y")+",";
				data += nowAttempt.getMean(selector, "x")+",";
				data += nowAttempt.getMean(selector, "y")+",";
				data += nowAttempt.getSD(selector, "x")+",";
				data += nowAttempt.getSD(selector, "y")+",";
				data += nowAttempt.getDif(selector, "x")+",";
				data += nowAttempt.getDif(selector, "y")+",";
				data += nowAttempt.GetMaxSpeed(selector)+",";
				data += nowAttempt.GetMinSpeed(selector)+",";
				data += nowAttempt.GetAveSpeed(selector)+",";
				data += nowAttempt.GetSDSpeed(selector)+",";
				
				double duration =(nowAttempt.getEndTime()- nowAttempt.getBeginTime());
				data +=duration+",";
				
				double pathLength = nowAttempt.getPathLength(selector);
				data += pathLength+",";
				
				data += nowAttempt.getActionDownNumber()+ ",";
				
				data += (pathLength /(duration/1000)) + ",";
				
				data +="\n";
			}
		}
		
		return data;
	}
	
	public List<Task> taskList;
	
}
