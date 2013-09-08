package LogDataParse;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MultiTouchSelector {
	
	public JSONObject Select(JSONArray target)
	{
		return target.getJSONObject(0);
	}
	
	public JSONObject Select(List<JSONObject> target)
	{
		return target.get(0);
	}
}
