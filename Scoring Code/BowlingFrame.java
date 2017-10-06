import java.util.ArrayList;

/**
 * This interface is for any bowling frame's common information.
 * @author Adam Walker P5
 */
public interface BowlingFrame {
	/**Maximum score (in a strike) / number of pins*/
	public final static int MAX_SCORE = 10;
	/**Number of potential throws*/
	public final static int NUM_SCORES = 2;
	/**Sets the first ball score*/
	public abstract void setFirstBall(int score) throws Exception;
	/**Sets the second ball score*/
	public abstract void setSecondBall(int score) throws Exception;
	/**
	 * Gets the first ball's score
	 * @return first ball score
	 */
	public abstract int getFirstBall();
	/**
	 * Gets the second ball's score
	 * @return second ball score
	 */
	public abstract int getSecondBall();
	/**
	 * Calculates the total of the game thus far
	 * @param prevFrameTot - total as of the previous frame
	 * @return int with total score
	 */
	public abstract int calcTot(int prevFrameTot);
	/**
	 * Gets the scores of the shots thrown in the frame thus far
	 * @return ArrayList<Character> with the scores of the frame
	 */
	public abstract ArrayList<Character> getScores();
}//end BowlingFrame