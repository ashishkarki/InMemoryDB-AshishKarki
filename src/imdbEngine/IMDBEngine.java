package imdbEngine;

import java.util.Scanner;

import org.json.simple.JSONObject;

/**
 * @author ASHISH KARKI_
 * 
 * @Description The engine gets input from user and completes the data and
 *              transaction operations.
 */
public class IMDBEngine {

	/**
	 * Major attributes required in the engine.
	 */
	private AttributesBean attributes;

	/**
	 * Read input until user terminates the transactions by typing in "END"
	 */
	Scanner scanner;

	/**
	 * current level number
	 */
	// private int currentLevelNo = 0;

	/**
	 * Constructor
	 */
	public IMDBEngine() {
		this.attributes = new AttributesBean();

		this.scanner = new Scanner(System.in);
	}

	/**
	 * @Description Read input from user (one line at a time) and process them
	 */
	@SuppressWarnings("unchecked")
	public void readUserInputAndProcess() {
		// All transactions started with the BEGIN command
		int currentActiveTransactions = 0;

		// Level from which the latest UNSET command removed a variable
		int variableUnsetLevel = 0;

		// Is it the first time any operation/transaction is being performed
		boolean firstRound = true;

		// Parsed/Split string from user input
		String[] parsedStr = { "n", "n", "n" };

		// The object where we perform our operations
		JSONObject currentJSONObject = new JSONObject();

		System.out
				.println("Please enter commands, one line at a time. Enter END to quit.");

		while (!parsedStr[0].equalsIgnoreCase("END")) {
			parsedStr = scanner.nextLine().toString().split(" ");

			// BEGIN command
			if (parsedStr[0].equalsIgnoreCase("BEGIN")) {
				currentActiveTransactions++;
				attributes.incrementCurrentLevelNo();
				currentJSONObject = new JSONObject();

				/*
				 * If this is first round, then create a new Level within JSON
				 * Object
				 */
				if (firstRound) {
					firstRound = false;
				}
				attributes.getRunningJSONObject().put(getLevelString(),
						currentJSONObject);
			}
			// SET command
			else if (parsedStr[0].equalsIgnoreCase("SET")) {
				/*
				 * If this is first round, then create a new Level within JSON
				 * Object
				 */
				if (firstRound) {
					attributes.incrementCurrentLevelNo();
					;
					firstRound = false;
				}

				currentJSONObject = (JSONObject) attributes
						.getRunningJSONObject().get(getLevelString());
				if (null == currentJSONObject) {
					currentJSONObject = new JSONObject();
				}

				// SET a 20
				currentJSONObject.put(parsedStr[1], parsedStr[2]);
				attributes.getRunningJSONObject().put(getLevelString(),
						currentJSONObject);
			}
			// GET command
			else if (parsedStr[0].equalsIgnoreCase("GET")) {
				String variableToGet = parsedStr[1];
				JSONObject resultObject = null;
				String resultString = null;

				/*
				 * If an UNSET command was issued recently, we don't search
				 * beyond that level for a variable.
				 */
				for (int i = attributes.getCurrentLevelNo(); i > 0
						&& i >= variableUnsetLevel; i--) {
					resultObject = (JSONObject) attributes
							.getRunningJSONObject().get("Level" + i);
					if (null != resultObject
							&& resultObject.containsKey(variableToGet)) {
						resultString = (String) resultObject.get(variableToGet);
						break;
					}
				}
				System.out.println(resultString);
			}
			// NUMEQUALTO command
			else if (parsedStr[0].equalsIgnoreCase("NUMEQUALTO")) {
				String valueToCompare = parsedStr[1];
				JSONObject resultObject = null;
				int numEqualTo = 0;

				/*
				 * Start from current level and count the variable-instances of
				 * specified value in increasingly higher levels.
				 */
				for (int i = attributes.getCurrentLevelNo(); i > 0; i--) {
					resultObject = (JSONObject) attributes
							.getRunningJSONObject().get("Level" + i);
					if (null != resultObject
							&& resultObject.containsValue(valueToCompare)) {
						numEqualTo++;
					}
				}
				System.out.println(numEqualTo);
			}
			// UNSET command
			else if (parsedStr[0].equalsIgnoreCase("UNSET")) {
				String variableToUnset = parsedStr[1];

				JSONObject resultObject = null;
				JSONObject backUpOfResultObject = null;

				/*
				 * Start from current level and search the variable to UNSET in
				 * increasingly higher levels. During a GET command, we do not
				 * move beyond the level where the most recent UNSET operation
				 * was performed.
				 */
				for (int i = attributes.getCurrentLevelNo(); i > 0; i--) {
					resultObject = (JSONObject) attributes
							.getRunningJSONObject().get("Level" + i);
					if (null != resultObject
							&& resultObject.containsKey(variableToUnset)) {
						// copy to another JSONObject cache
						attributes.getPermanentJSONObject().clear();
						attributes.getPermanentJSONObject().putAll(
								attributes.getRunningJSONObject());

						// now remove the object
						backUpOfResultObject = new JSONObject(resultObject);
						resultObject.remove(variableToUnset);
						variableUnsetLevel = i;

						// put back the removed object in permanent JSON
						// temporarily
						attributes.getPermanentJSONObject().put("Level" + i,
								backUpOfResultObject);
						break;
					}
				}
			}
			// ROLLBACK command: delete current block and its operations
			else if (parsedStr[0].equalsIgnoreCase("ROLLBACK")) {
				currentActiveTransactions--;
				if (currentActiveTransactions <= 0) {
					System.out.println("NO TRANSACTION");
					continue;
				}

				// first restore running JSON from permanent JSON cache
				attributes.getRunningJSONObject().putAll(
						attributes.getPermanentJSONObject());

				// Now delete current block
				attributes.getRunningJSONObject().remove(getLevelString());
				attributes.decrementCurrentLevelNo();
			}
			// COMMIT command: commit all operations starting at the top level
			else if (parsedStr[0].equalsIgnoreCase("COMMIT")) {
				currentActiveTransactions--;
				if (currentActiveTransactions <= 0) {
					System.out.println("NO TRANSACTION");
					continue;
				}

				/*
				 * During commit, re-write all the value pairs from Running
				 * Cache to Permanent Cache.
				 */
				JSONObject resultJSONObject = new JSONObject();
				for (int i = 1; i <= attributes.getCurrentLevelNo(); i++) {
					System.out.println("elements:"
							+ ((JSONObject) attributes.getRunningJSONObject()
									.get("Level" + i)).entrySet());
					resultJSONObject.putAll(((JSONObject) attributes
							.getRunningJSONObject().get("Level" + i)));
				}

				// copy JSON to permanent cache
				attributes.getPermanentJSONObject().clear();
				attributes.getPermanentJSONObject().put("Level1",
						resultJSONObject);

				// finally reset Running cache with freshly committed info
				attributes.getRunningJSONObject().clear();
				attributes.getRunningJSONObject().putAll(
						attributes.getPermanentJSONObject());
			} else if (parsedStr[0].equalsIgnoreCase("D")) {
				// debug
				System.out.println("running json:"
						+ attributes.getRunningJSONObject().toString());
			}

		}

	}

	/**
	 * @return level string which forms the key for a new object in our Running
	 *         Cache.
	 */
	public String getLevelString() {
		return "Level" + attributes.getCurrentLevelNo();
	}

	/**
	 * @param args
	 * @Description For testing purpose
	 */
	public static void main(String[] args) {
		IMDBEngine myEngine = new IMDBEngine();

		myEngine.readUserInputAndProcess();
	}
}
