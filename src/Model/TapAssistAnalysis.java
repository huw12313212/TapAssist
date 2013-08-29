package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import LogDataParse.Experience;
import Model.TapAssistDataEntry.DistancePolicy;

public class TapAssistAnalysis {
	

	//DifXY
	ArrayList<TapAssistDataEntry> tapDistX = new ArrayList<TapAssistDataEntry>();
	ArrayList<TapAssistDataEntry> tapDistY = new ArrayList<TapAssistDataEntry>();
	ArrayList<TapAssistDataEntry> tapDistXY = new ArrayList<TapAssistDataEntry>();

	
	ArrayList<TapAssistDataEntry> scrollX = new ArrayList<TapAssistDataEntry>();
	ArrayList<TapAssistDataEntry> scrollY = new ArrayList<TapAssistDataEntry>();
	ArrayList<TapAssistDataEntry> scrollXY = new ArrayList<TapAssistDataEntry>();
	
	
	ArrayList<Float> tapDistXCDF = new ArrayList<Float>();
	ArrayList<Float> tapDistYCDF = new ArrayList<Float>();
	ArrayList<Float> tapDistXYCDF = new ArrayList<Float>();
	
	ArrayList<Float> scrollXCDF = new ArrayList<Float>();
	ArrayList<Float> scrollYCDF = new ArrayList<Float>();
	ArrayList<Float> scrollXYCDF = new ArrayList<Float>();
	//DifXY
	
	//path
	ArrayList<TapAssistDataEntry> TapPath = new ArrayList<TapAssistDataEntry>();
	ArrayList<TapAssistDataEntry> ScrollPath = new ArrayList<TapAssistDataEntry>();
	
	ArrayList<Float> TapPathCDF = new ArrayList<Float>();
	ArrayList<Float> ScrollPathCDF = new ArrayList<Float>();

	
	
	public TapAssistAnalysis(Experience tap,Experience scroll)
	{
		List<Double> tapX = tap.getAllDistTapping("x");
		List<Double> tapY = tap.getAllDistTapping("y");
		
		List<Double> scrollx = scroll.getAllDistScroll("x");
		List<Double> scrolly = scroll.getAllDistScroll("y");
		
		
		List<Double> pathTap = tap.getAllMaxDifTapping();
		List<Double> pathScroll = scroll.getAllMaxDifScroll();
		
		
		if(tapX.size()==0 || scrollx.size()==0)
		{
			return;
		}
		
		for(int i = 0; i < pathTap.size() ; i++)
		{
			TapPath.add(new TapAssistDataEntry(pathTap.get(i).floatValue(),pathTap.get(i).floatValue(),DistancePolicy.XOnly));
		}
		
		
		for(int i = 0; i < pathScroll.size() ; i++)
		{
			ScrollPath.add(new TapAssistDataEntry(pathScroll.get(i).floatValue(),pathScroll.get(i).floatValue(),DistancePolicy.XOnly));
		}
		
		
		
		
		for(int i = 0;i<tapX.size();i++)
		{
			tapDistX.add(new TapAssistDataEntry(tapX.get(i).floatValue(),tapY.get(i).floatValue(),DistancePolicy.XOnly));
			tapDistY.add(new TapAssistDataEntry(tapX.get(i).floatValue(),tapY.get(i).floatValue(),DistancePolicy.YOnly));
			tapDistXY.add(new TapAssistDataEntry(tapX.get(i).floatValue(),tapY.get(i).floatValue(),DistancePolicy.Dist));
		}
		
		for(int i = 0 ; i < scrollx.size();i++)
		{
			scrollX.add(new TapAssistDataEntry(scrollx.get(i).floatValue(),scrolly.get(i).floatValue(),DistancePolicy.XOnly));
			scrollY.add(new TapAssistDataEntry(scrollx.get(i).floatValue(),scrolly.get(i).floatValue(),DistancePolicy.YOnly));
			scrollXY.add(new TapAssistDataEntry(scrollx.get(i).floatValue(),scrolly.get(i).floatValue(),DistancePolicy.Dist));
		}
		
		
		Collections.sort(tapDistX, new TapDistanceComparator());
		Collections.sort(tapDistY, new TapDistanceComparator());
		Collections.sort(tapDistXY, new TapDistanceComparator());
		Collections.sort(scrollX, new TapDistanceComparator());
		Collections.sort(scrollY, new TapDistanceComparator());
		Collections.sort(scrollXY, new TapDistanceComparator());
		
		Collections.sort(TapPath, new TapDistanceComparator());
		Collections.sort(ScrollPath, new TapDistanceComparator());
		
		float maximum = getMaximun();
		
	    tapDistXCDF = GetCDF(tapDistX,maximum);
	    tapDistYCDF = GetCDF(tapDistY,maximum);
	    tapDistXYCDF = GetCDF(tapDistXY,maximum);
	    
	    scrollXCDF = GetCDF(scrollX,maximum);
	    scrollYCDF = GetCDF(scrollY,maximum);
	    scrollXYCDF = GetCDF(scrollXY,maximum);
	    
	    float maximum2 = getMaximum2();
	    TapPathCDF = GetCDF(TapPath,maximum2);
	    ScrollPathCDF = GetCDF(ScrollPath,maximum2);
	    
	}
	
	public String getCDF()
	{

		if(tapDistX.size()==0 || scrollX.size()==0)
		{
			return "CDF failed";
		}
		
		String data = "CDF,dist,tapX,tapY,tapXY,scrollX,scrollY,scrollXY\n";
		
		for(int i = 0 ; i < tapDistXCDF.size();i++)
		{
			data += ","+i+","+tapDistXCDF.get(i)+","+tapDistYCDF.get(i)+","+tapDistXYCDF.get(i)+","+scrollXCDF.get(i)+","+scrollYCDF.get(i)+","+scrollXYCDF.get(i)+"\n";
		}
		
		return data;
	}
	
	public String getPathCDF()
	{
		if(TapPath.size()==0 || ScrollPath.size()==0)
		{
			return "CDF failed";
		}
		
		String data = "CDF,dist,tapMaxDif,scrollMaxDif,,CorrectRatio,tap%,scroll%,(Tap+Scroll)%\n";
		
		for(int i = 0 ; i < TapPathCDF.size();i++)
		{
			data += ","+i+","+TapPathCDF.get(i)+","+ScrollPathCDF.get(i)+",,,"+TapPathCDF.get(i)+","+(1-ScrollPathCDF.get(i))+","+((1-ScrollPathCDF.get(i))+TapPathCDF.get(i))+"\n";
		}
		
		
		
		return data;
	}
	
	public float getMaximum2()
	{
		float result = this.TapPath.get(TapPath.size()-1).getDistance();
		
		float temp = 0;
		
		temp = ScrollPath.get(ScrollPath.size()-1).getDistance();
		
		if(temp>result)result = temp;
		
		return result;
		
	}
	
	public float getMaximun()
	{
		float result = tapDistX.get(tapDistX.size()-1).getDistance();
		
		float temp = 0;
		
		temp = tapDistY.get(tapDistY.size()-1).getDistance();
		
		if(temp>result)result = temp;
		
		temp = tapDistXY.get(tapDistXY.size()-1).getDistance();
		
		if(temp>result)result = temp;
		
		temp = scrollX.get(scrollX.size()-1).getDistance();
		
		if(temp>result)result = temp;
		
		temp = scrollY.get(scrollY.size()-1).getDistance();
		
		if(temp>result)result = temp;
		
		
		temp = scrollXY.get(scrollXY.size()-1).getDistance();
		
		if(temp>result)result = temp;
		
		return result;
	}
	
	
	
	public static ArrayList<Float> CDFtoPDF(ArrayList<Float> CDF)
	{
		ArrayList<Float> PDF = new ArrayList<Float>();
		
		PDF.add(CDF.get(0));
		
		for(int i=1;i<CDF.size();i++)
		{
			PDF.add(CDF.get(i)-CDF.get(i-1));
		}
		return PDF;
	}
	
	
	
	public static ArrayList<Float> GetCDF (ArrayList<TapAssistDataEntry> distList,float max)
	{
		ArrayList<Float> CDFList = new ArrayList<Float>();
		
		int nowIndex = 0;
		
		for(int i = 0; i < max ; i++)
		{
			
			while(nowIndex<distList.size()&&distList.get(nowIndex).getDistance()<=i)
			{
				nowIndex++;
			}
			
			CDFList.add(nowIndex/(float)distList.size());
			
		}
		
		CDFList.add(1.0f);
		return CDFList;

	}
	
	
	public ArrayList<Float> GetPDF (ArrayList<TapAssistDataEntry> distList,float max,boolean percentage)
	{
		ArrayList<Float> PDFList = new ArrayList<Float>();
		
		int nowIndex = 0;
		
		int lastIndex = 0;
		
		
		for(int i = 0; i < max ; i++)
		{
			
			while(nowIndex<distList.size()&&distList.get(nowIndex).getDistance()<=i)
			{
				nowIndex++;
			}
			
			
			
			if(percentage)
			{
				PDFList.add((nowIndex-lastIndex)/(float)distList.size());
			}
			else
			{
				PDFList.add((float)(nowIndex-lastIndex));
			}
			
			lastIndex = nowIndex;
			
		}
		
		
		if(percentage)
		{
			PDFList.add((distList.size()-lastIndex)/(float)distList.size());
		}
		else
		{
			PDFList.add((float)(distList.size()-lastIndex));
		}
		
		return PDFList;

	}
	
}
