package LogDataParse;

import org.json.JSONArray;
import org.json.JSONObject;

public class MultiTouchSelector {
	
	public JSONObject Select(JSONArray target)
	{
		return target.getJSONObject(0);
	}
}
