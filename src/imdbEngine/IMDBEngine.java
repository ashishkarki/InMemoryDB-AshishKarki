package imdbEngine;

import java.util.Scanner;

import org.json.simple.JSONObject;

/**
 * @author ASHISH KARKI_
 * 
 */
public class IMDBEngine {

	private AttributesBean attributes;

	/*
	 * Read input until user terminates the transactions by typing in "END"
	 */
	Scanner scanner;

	/**
	 * A single line of command input from the user
	 */
	private StringBuilder lineCommand;

	public IMDBEngine() {
		this.attributes = new AttributesBean();

		this.scanner = new Scanner(System.in);
	}

	public void readUserInputAndProcess() {
		// a new transaction is started
		int currentActiveTransactions = 0;
		// current level name + number
		String currentLevelName;
		// current level number
		int currentLevelNo = 0;
		// parent level of current level
		int parentLevelNo = 0;
		boolean firstRound = true;

		// Parsed/Split string from user input
		String[] parsedStr;

		JSONObject currentJSONObject = new JSONObject();

		System.out
				.println("Please enter commands, one line at a time. Enter END to quit.");
		lineCommand = new StringBuilder();

		while (!lineCommand.toString().equalsIgnoreCase("END")) {
			System.out.println("Its not the end");
			firstRound = false;
			lineCommand = new StringBuilder(scanner.nextLine());

			// BEGIN new transaction
			if (lineCommand.toString().equalsIgnoreCase("BEGIN")) {
				currentActiveTransactions++;
				currentLevelNo++;
				currentJSONObject = new JSONObject();

				/*
				 * A new transaction is started but not committed, so do not
				 * write to the permanent JSON yet
				 */
				if (firstRound) {
					attributes.getRunningJSONObject().put(
							"Level" + currentLevelNo, currentJSONObject);
				} else {
					JSONObject innerJsonObject = (JSONObject) attributes
							.getRunningJSONObject().get(
									"Level" + (currentLevelNo - 1));
					innerJsonObject.put("Level" + currentLevelNo,
							currentJSONObject);
					attributes.getRunningJSONObject().put(
							"Level" + (currentLevelNo - 1), innerJsonObject);
				}
			}
			// SET command
			else if (lineCommand.toString().equalsIgnoreCase("SET")) {
				parsedStr = lineCommand.toString().split(" ");

				if (firstRound) {
					currentLevelNo++;
					currentJSONObject = new JSONObject();
					currentJSONObject.put(parsedStr[1], parsedStr[2]);
					attributes.getRunningJSONObject().put(
							"Level" + currentLevelNo, currentJSONObject);
				} else {
					currentJSONObject.put(parsedStr[1], parsedStr[2]);
					attributes.getRunningJSONObject().put(
							"Level" + currentLevelNo, currentJSONObject);
				}
			}
			// GET command
			else if (lineCommand.toString().equalsIgnoreCase("GET")) {
				parsedStr = lineCommand.toString().split(" ");
			}
			//
			if (lineCommand.toString().equalsIgnoreCase("COMMIT")
					|| lineCommand.toString().equalsIgnoreCase("ROLLBACK")) {
				currentActiveTransactions--;

			}

		}
	}
	
	public static void main(String[] args){
		IMDBEngine myEngine = new IMDBEngine();
		
		myEngine.readUserInputAndProcess();
	}
}
