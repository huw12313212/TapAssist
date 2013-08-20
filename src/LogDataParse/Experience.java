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
	
	public List<Double> getAllDistTapping(String key)
	{
		List<Double> result = new ArrayList<Double>();
		
		for(int taskNum = 0 ; taskNum < taskList.size(); taskNum++)
		{
			Task task = taskList.get(taskNum);
			List<AttemptSegment> attemptList = task.AttemptList;
			
			for(int attemptNum = 0 ; attemptNum < attemptList.size(); attemptNum++)
			{
				AttemptSegment nowAttempt = attemptList.get(attemptNum);
				MultiTouchSelector selector =  new MultiTouchSelectorWithShortestDistance(task.GetTargetX(),task.GetTargetY());
				
				result.add(nowAttempt.getDif(selector, key));
			}
		}
		return result;
	}
	
	public List<Double> getAllDistScroll(String key)
	{
		List<Double> result = new ArrayList<Double>();
		
		for(int taskNum = 0 ; taskNum < taskList.size(); taskNum++)
		{
			Task task = taskList.get(taskNum);
			List<AttemptSegment> attemptList = task.AttemptList;
			
			for(int attemptNum = 0 ; attemptNum < attemptList.size(); attemptNum++)
			{
				AttemptSegment nowAttempt = attemptList.get(attemptNum);
				MultiTouchSelector selector =  multiTouchSelector;
				
				result.add(nowAttempt.getDif(selector, key));
			}
		}
		return result;
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
	
	
	public int GetTaskSuccessTimes()
	{
		int times = 0;

		for(int taskNum = 0 ; taskNum < taskList.size(); taskNum++)
		{
			Task task = taskList.get(taskNum);
			if(task.GetResult().equals("success"))
			{
				times ++;
			}
		}
		
		return times;
	}
	
	public List<Task> GetAllFailedTask()
	{
		List<Task> allTask = new ArrayList<Task>();

		for(int taskNum = 0 ; taskNum < taskList.size(); taskNum++)
		{
			Task task = taskList.get(taskNum);
			if(task.GetResult().equals("fail"))
			{
				allTask.add(task);
			}
		}
		
		return allTask;
	}
	
	
	public int GetTotalTaskTimes()
	{
		return taskList.size();
	}
	
	public List<Task> GetAllFailedByReason(String reason)
	{
		List<Task> allTask = new ArrayList<Task>();

		for(int taskNum = 0 ; taskNum < taskList.size(); taskNum++)
		{
			Task task = taskList.get(taskNum);
			if(task.GetResult().equals("fail"))
			{
				if(task.GetReason().equals(reason))
				{
				allTask.add(task);
				}
			}
		}
		
		return allTask;
	}
	
	public String ExportTappingAttemptAnalysisAsCSV()
	{
		String data = "Task,Attempt,result,reason,TargetX,TargetY,begin_time(ms),end_time(ms)," +
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
				MultiTouchSelector selector = new MultiTouchSelectorWithShortestDistance(task.GetTargetX(),task.GetTargetY());
				
				data +=(taskNum+1)+",";
				data +=(attemptNum+1)+",";
				data +=task.GetResult()+",";
				data +=task.GetReason()+",";
				
				data +=task.GetTargetX()+",";
				data +=task.GetTargetY()+",";
				
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
		data +="\n";
		
		
		
		List<Task> failed = this.GetAllFailedTask();
		List<Task> missed = this.GetAllFailedByReason("miss");
		List<Task> overslop = this.GetAllFailedByReason("over_slop");
		
		if(this.GetTotalTaskTimes()>0)
		{
		data += "Total Task,"+ this.GetTotalTaskTimes()+",";
		data += "Success,"+ this.GetTaskSuccessTimes()+",";
		data += "Fail," + failed.size()+",";
		data += "Sucess Ratio," + (this.GetTaskSuccessTimes()/(double)this.GetTotalTaskTimes())+",\n";
		data += ",,,,"+"missd:,"+missed.size()+"\n";
		data += ",,,,"+"overslop,"+overslop.size();
		}
		
		return data;
	}
	
	public List<Task> taskList;
	
}
