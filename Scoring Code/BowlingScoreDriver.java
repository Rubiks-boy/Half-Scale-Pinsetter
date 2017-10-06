import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class BowlingScoreDriver {

	public static void main(String[] args) {
		try
		{
			
			/*Cycle.startConnection();

			Cycle.setupScoring();
			Cycle.setupSerialData();*/
			ArrayList<String> names = new ArrayList<String>();
			names.add("zxcvbnm");
			names.add("qwerty");
			names.add("asdfjkl;");
			RunScoring g = new RunScoring(/*3, names*/);
			Scanner in = new Scanner(System.in);
			in.nextLine();
			g.displayMenu();
			g.addScore(10);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			boolean [] f2 = {false, true, false, true, true, false, false, false, false, false};
			//g.addScore(f2);
			//in.nextLine();
			boolean [] f3 = {true, true, true, true, true, false, false, false, false, false};

			//g.addScore(f3);
			in.nextLine();
			//g.addScore(10);
			//g.addScore(10);
			g.addScore(10);
			//in.nextLine();
			g.addScore(10);
			//in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			
			boolean [] arr = {false, false,true, true,false, false,false, false,false, false};
			g.addScore(arr);
			in.nextLine();
			for(int i = 0; i < arr.length; i++)
				arr[i] = true;
			g.addScore(arr);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(5);
			in.nextLine();
			g.addScore(5);
			in.nextLine();
			g.addScore(9);
			in.nextLine();
			g.addScore(1);
			in.nextLine();
			g.addScore(9);
			in.nextLine();
			g.addScore(1);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(0);
			in.nextLine();
			g.addScore(0);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(9);
			in.nextLine();
			g.addScore(1);
			in.nextLine();
			g.addScore(9);
			in.nextLine();
			g.addScore(1);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(8);
			in.nextLine();
			g.addScore(2);
			in.nextLine();
			g.addScore(10);
			in.nextLine();
			g.addScore(10);
			in.nextLine();

			g.addScore(8);
			in.nextLine();
			g.addScore(2);


			in.nextLine();
			g.addScore(2);
			in.nextLine();
			g.addScore(8);
			in.nextLine();
			g.addScore(10);
			for(int ij = 0; ij < 1000; ij++)
			{
				Random rand = new Random();
				boolean allFalse = true;
				boolean af2 = true;
				for(int i = 0; i < 10; i++)
				{
					boolean c = rand.nextBoolean();
					if(c)
					{
						if(allFalse)
						{
							
							if(af2)
							{
								arr[i] = false;
								af2 = false;
							}
								else
									allFalse = false;
						}
						arr[i] = c;


					}
					else
						arr[i] = c;
				}
				for(boolean c : arr)
					if(c)
						System.out.print("|");
					else System.out.print(".");
				System.out.println();
				in.nextLine();
				g.addScore(arr);
				if(!allFalse)
				{
					for(int i = 0; i < 10; i++)
					{
						if(arr[i])
						{
							arr[i] = rand.nextBoolean();
						}
					}
					for(boolean c : arr)
						if(c)
							System.out.print("|");
						else System.out.print(".");
					System.out.println();
					in.nextLine();
					g.addScore(arr);
				}
			}

			in.close();
			

			

		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
