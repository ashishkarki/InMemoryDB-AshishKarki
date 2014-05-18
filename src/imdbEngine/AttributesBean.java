package imdbEngine;

import org.json.simple.JSONObject;

/**
 * @author ASHISH KARKI_
 * @Description Java Bean Class to store properties related to Engine
 */
public class AttributesBean {

	JSONObject runningJSONObject = new JSONObject();

	JSONObject permanentJSONObject = new JSONObject();

	// Setters and getters ///////////////////
	public JSONObject getRunningJSONObject() {
		return runningJSONObject;
	}

	public JSONObject getPermanentJSONObject() {
		return permanentJSONObject;
	}
}
