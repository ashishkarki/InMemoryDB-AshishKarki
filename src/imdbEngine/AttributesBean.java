package imdbEngine;

import org.json.simple.JSONObject;

/**
 * @author ASHISH KARKI_
 * @Description Java Bean Class to store properties related to Engine
 */
public class AttributesBean {
	/**
	 * Running Cache into which all operations are written into and read from.
	 */
	private JSONObject runningJSONObject = new JSONObject();

	/**
	 * Called so because this is not altered too many times. Used only in
	 * Commit, Rollback and Unset operations.
	 */
	private JSONObject permanentJSONObject = new JSONObject();

	/**
	 * current level number
	 */
	private int currentLevelNo = 0;

	// Setters and getters ///////////////////
	public JSONObject getRunningJSONObject() {
		return runningJSONObject;
	}

	public JSONObject getPermanentJSONObject() {
		return permanentJSONObject;
	}

	public int getCurrentLevelNo() {
		return currentLevelNo;
	}

	public void incrementCurrentLevelNo() {
		this.currentLevelNo += 1;
	}
	
	public void decrementCurrentLevelNo() {
		this.currentLevelNo -= 1;
	}
}
