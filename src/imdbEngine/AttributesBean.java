package imdbEngine;

import org.json.simple.JSONObject;

/**
 * @author ASHISH KARKI_
 * @Description Java Bean Class to store properties related to Engine
 */
public class AttributesBean {

	private JSONObject runningJSONObject = new JSONObject();

	private JSONObject permanentJSONObject = new JSONObject();

	// Setters and getters ///////////////////
	public JSONObject getRunningJSONObject() {
		return runningJSONObject;
	}

	public JSONObject getPermanentJSONObject() {
		return permanentJSONObject;
	}
}
