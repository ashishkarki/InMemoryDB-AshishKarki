package imdbEngine;

/**
 * @author ASHISH KARKI_
 *
 */
public class LevelInfo {

	private String levelName;
	private int levelNumber;
	
	public LevelInfo(int lvlNo) {
		this.levelNumber = lvlNo;
	}

	public String getLevelName() {
		return levelName + levelNumber;
	}
	
}
