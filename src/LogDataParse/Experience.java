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
	
	public String ExportTappingAttemptAnalysisAsCSV()
	{
		String data = "Task,Attempt,targetX,targetY,begin_time(ms),end_time(ms),duration(ms),touchBegin_x,touchBegin_y,max_x,max_y,min_x,min_y,ave_x,ave_y,SD_x,SD_y,x_end-begin,y_end-begin,\n";
		
		for(int taskNum = 0 ; taskNum < taskList.size(); taskNum++)
		{
			Task task = taskList.get(taskNum);
			List<AttemptSegment> attemptList = task.AttemptList;
			
			for(int attemptNum = 0 ; attemptNum < attemptList.size(); attemptNum++)
			{
				AttemptSegment nowAttempt = attemptList.get(attemptNum);
				MultiTouchSelector selector = new MultiTouchSelectorWithShortestDistance(task.GetTargetX(),task.GetTargetY());
				
				data +=(taskNum+1)+",";
				data +=(attemptNum+1)+",";
				data +=task.GetTargetX()+",";
				data +=task.GetTargetY()+",";
				data +=(nowAttempt.getBeginTime() - taskList.get(0).GetTaskBeginTime())+",";
				data +=(nowAttempt.getEndTime()- taskList.get(0).GetTaskBeginTime())+",";
				data +=(nowAttempt.getEndTime()- nowAttempt.getBeginTime())+",";
				
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
				
				data +="\n";
			}
		}
		
		return data;
	}
	
	public List<Task> taskList;
	
}
