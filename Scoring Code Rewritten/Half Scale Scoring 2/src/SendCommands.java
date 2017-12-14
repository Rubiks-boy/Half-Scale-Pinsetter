
/**
 * This class sends all commands based on the action
 * you want to perform.
 * @author Adam Walker
 */
public class SendCommands 
{
	/**
	 * Sends commands for each cycle possible to perform.
	 */
	public static class PerformCycle
	{
		/**
		 * Send command for arduino to set up 10 new pins.
		 */
		public static void resetCycle() 
		{
			Serial.sendCmd("C0");
			System.out.println("CMD SEND: C0: CYCLE Reset");
		}//end newSet()

		/**
		 * Send command for arduino to pick up pins, clear
		 * knocked over pins, and set down remaining pins.
		 */
		public static void respotCycle() 
		{
			Serial.sendCmd("C1");
			System.out.println("CMD SEND: C1: CYCLE Respot");
		}//end standingPins()

		/**
		 * Send command for arduino to perform gutter cycle.
		 */
		public static void gutterCycle()
		{
			Serial.sendCmd("C2");
			System.out.println("CMD SEND: C2: CYCLE Gutter");
		}
		
		/**
		 * Send command to set custom set of pins
		 * @param pins - "1" if pin should be set, "0" if not
		 */
		public static void resetCycle(boolean[] pins)
		{
			String pinStr = new String();
			for(boolean pin : pins)
			{
				if(pin)
					pinStr += "1";
				else pinStr += "0";
			}//end for

			String cmd = "C3" + pinStr;
			
			Serial.sendCmd(cmd);
		}//end resetCycle()
	}//end PerformCycle
	
	/**
	 * Sends commands that change machine states.
	 */
	public static class MachineState
	{
		/**
		 * Tells the arduino to change the pinsetter power state.
		 * @param args - power state. "1" for on, "0" for off
		 */
		public static void changePowState(boolean state)//S
		{
			String cmd = "P" + (state ? "1" : "0");
			Serial.sendCmd(cmd);
		}//end changePowState()

		/**
		 * Tells the arduino to change the pinsetter light state.
		 * @param args - light state. "1" for on, "0" for off
		 */
		public static void changeLightState(boolean state) //S
		{
			String cmd = "L" + (state ? "1" : "0");
			Serial.sendCmd(cmd);
		}//end changeLightState()
	}//end ChangeMachineState
}//end SendCommands
