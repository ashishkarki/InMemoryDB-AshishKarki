package imdbEngine;

import java.util.Scanner;

import org.json.simple.JSONObject;

/**
 * @author ASHISH KARKI_
 * 
 */
public class IMDBEngine {

	private AttributesBean attributes;

	/**
	 * Read input until user terminates the transactions by typing in "END"
	 */
	Scanner scanner;

	/**
	 * A single line of command input from the user
	 */
	private StringBuilder lineCommand;

	/**
	 * current level number
	 */
	int currentLevelNo = 0;

	public IMDBEngine() {
		this.attributes = new AttributesBean();

		this.scanner = new Scanner(System.in);
	}

	public void readUserInputAndProcess() {
		// a new transaction is started

		@SuppressWarnings("unused")
		int currentActiveTransactions = 0;

		// Level from which the latest UNSET command removed a variable
		int variableUnsetLevel = 0;

		// parent level of current level
		boolean firstRound = true;

		// Parsed/Split string from user input
		String[] parsedStr = { "n", "n", "n" };

		JSONObject currentJSONObject = new JSONObject();

		System.out
				.println("Please enter commands, one line at a time. Enter END to quit.");
		lineCommand = new StringBuilder();

		while (!parsedStr[0].equalsIgnoreCase("END")) {
			System.out.println("Its not the end");
			parsedStr = scanner.nextLine().toString().split(" ");

			// BEGIN new transaction
			if (parsedStr[0].equalsIgnoreCase("BEGIN")) {
				currentActiveTransactions++;
				currentLevelNo++;
				currentJSONObject = new JSONObject();

				/*
				 * A new transaction is started but not committed, so do not
				 * write to the permanent JSON yet
				 */
				if (firstRound) {
					attributes.getRunningJSONObject().put(getLevelString(),
							currentJSONObject);
					firstRound = false;
				} else {
				}
			}
			// SET command
			else if (parsedStr[0].equalsIgnoreCase("SET")) {
				System.out.println("setting");

				if (firstRound) {
					currentLevelNo++;
					firstRound = false;
				} else {
				}
				currentJSONObject = (JSONObject) attributes
						.getRunningJSONObject().get(getLevelString());
				if (null == currentJSONObject) {
					currentJSONObject = new JSONObject();
				}
				currentJSONObject.put(parsedStr[1], parsedStr[2]);
				attributes.getRunningJSONObject().put(getLevelString(),
						currentJSONObject);
			}
			// GET command
			else if (parsedStr[0].equalsIgnoreCase("GET")) {
				String variableToGet = parsedStr[1];
				JSONObject resultObject = null;
				String resultString = null;

				for (int i = currentLevelNo; i > 0 && i >= variableUnsetLevel; i--) {
					resultObject = (JSONObject) attributes
							.getRunningJSONObject().get("Level" + i);
					if (null != resultObject
							&& resultObject.containsKey(variableToGet)) {
						resultString = (String) resultObject.get(variableToGet);
						break;
					}
				}
				System.out.println("result of GET:" + resultString);
			}
			// NUMEQUALTO command
			else if (parsedStr[0].equalsIgnoreCase("NUMEQUALTO")) {
				String valueToCompare = parsedStr[1];
				JSONObject resultObject = null;
				int numEqualTo = 0;
				for (int i = currentLevelNo; i > 0; i--) {
					resultObject = (JSONObject) attributes
							.getRunningJSONObject().get("Level" + i);
					if (null != resultObject
							&& resultObject.containsValue(valueToCompare)) {
						numEqualTo++;
					}
				}
				System.out.println("Result of NUMEQUALTO: " + valueToCompare
						+ " is: " + numEqualTo);
			}
			// UNSET command
			else if (parsedStr[0].equalsIgnoreCase("UNSET")) {
				String variableToUnset = parsedStr[1];

				JSONObject resultObject = null;
				JSONObject backUpOfResultObject = null;
				// resultObject.remove(variableToUnset);

				for (int i = currentLevelNo; i > 0; i--) {
					resultObject = (JSONObject) attributes
							.getRunningJSONObject().get("Level" + i);
					if (null != resultObject
							&& resultObject.containsKey(variableToUnset)) {
						// copy to another json cache
						attributes.getPermanentJSONObject().clear();
						attributes.getPermanentJSONObject().putAll(
								attributes.getRunningJSONObject());

						// now remove the object
						backUpOfResultObject = new JSONObject(resultObject);
						resultObject.remove(variableToUnset);
						variableUnsetLevel = i;
						System.out.println("Removed variable "
								+ variableToUnset + " from level:" + i);

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

				// first restore running json from permanent json cache
				attributes.getRunningJSONObject().putAll(
						attributes.getPermanentJSONObject());

				attributes.getRunningJSONObject().remove(getLevelString());

				currentLevelNo--;
			}
			// COMMIT command: commit all operations starting at the top level
			else if (parsedStr[0].equalsIgnoreCase("COMMIT")) {
				currentActiveTransactions--;
				if (currentActiveTransactions <= 0) {
					System.out.println("NO TRANSACTION");
					continue;
				}

				JSONObject resultJSONObject = new JSONObject();
				for (int i = 1; i <= currentLevelNo; i++) {
					System.out.println("elements:"
							+ ((JSONObject) attributes.getRunningJSONObject()
									.get("Level" + i)).entrySet());
					resultJSONObject.putAll(((JSONObject) attributes
							.getRunningJSONObject().get("Level" + i)));
				}

				// copy json to another cache
				attributes.getPermanentJSONObject().clear();
				attributes.getPermanentJSONObject().put("Level1",
						resultJSONObject);

				// finally reset running json with freshly committed info
				attributes.getRunningJSONObject().clear();
				attributes.getRunningJSONObject().putAll(
						attributes.getPermanentJSONObject());
			} else if (parsedStr[0].equalsIgnoreCase("D")) {
				// debug
				System.out.println("running json:"
						+ attributes.getRunningJSONObject().toString());
			} else if (parsedStr[0].equalsIgnoreCase("D2")) {
				// debug
				System.out.println("permanent json:"
						+ attributes.getPermanentJSONObject().toString());
			}

		}

	}

	public String getLevelString() {
		System.out.println("Current level no is:" + currentLevelNo);
		return "Level" + currentLevelNo;
	}

	public static void main(String[] args) {
		IMDBEngine myEngine = new IMDBEngine();

		myEngine.readUserInputAndProcess();
	}
}
