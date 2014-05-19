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
		int currentActiveTransactions = 0;

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
			// lineCommand = new StringBuilder(scanner.nextLine());
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
					/*JSONObject innerJsonObject = (JSONObject) attributes
							.getRunningJSONObject().get(
									"Level" + (currentLevelNo - 1));
					innerJsonObject.put(getLevelString(), currentJSONObject);
					attributes.getRunningJSONObject().put(
							"Level" + (currentLevelNo - 1), innerJsonObject);
*/
					/*
					 * Copy all the variable on the upper/parent level to this
					 */

				}
			}
			// SET command
			else if (parsedStr[0].equalsIgnoreCase("SET")) {
				System.out.println("setting");
				// parsedStr = lineCommand.toString().split(" ");

				// currentJSONObject = new JSONObject();
				if (firstRound) {
					currentLevelNo++;
					// currentJSONObject.put(parsedStr[1], parsedStr[2]);
					// attributes.getRunningJSONObject().put(getLevelString(),
					// currentJSONObject);
					firstRound = false;
				} else {
					// currentJSONObject.put(parsedStr[1], parsedStr[2]);
					// attributes.getRunningJSONObject().put(getLevelString(),
					// currentJSONObject);
				}
				// currentJSONObject.put(parsedStr[1], parsedStr[2]);
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
				Object resultObject = attributes.getRunningJSONObject().get(
						getLevelString());
			}
			//
			if (lineCommand.toString().equalsIgnoreCase("COMMIT")
					|| lineCommand.toString().equalsIgnoreCase("ROLLBACK")) {
				currentActiveTransactions--;

			}

		}

		// debug
		System.out.println("running json:"
				+ attributes.getRunningJSONObject().toString());
	}

	public String getLevelString() {
		return "Level" + currentLevelNo;
	}

	public static void main(String[] args) {
		IMDBEngine myEngine = new IMDBEngine();

		myEngine.readUserInputAndProcess();
	}
}
