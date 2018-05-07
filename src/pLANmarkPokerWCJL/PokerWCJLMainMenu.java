package pLANmarkPokerWCJL;
 
import java.io.File;												//for files
import java.util.Scanner; 											//for user inputs
 
public class PokerWCJLMainMenu {									//main menu class. Run this to play the game
 
private static Scanner input;										//for inputs
 
	public static void main(String[] args) {						//main method

		//the startup text.
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");	//start marker
		System.out.println("Welcome to LANmark Poker!");																			//title
		System.out.println("This version of Poker is no-limit, using a modified version of the dead button rule,");					//a bit of explanation about the rules of this version of Poker
		System.out.println("because this game does not use the dealer button.");
		System.out.println("In the pre-flop round, the first player to play will be the one clockwise to the big blind. ");
		System.out.println("After the flop, the first player to play will be the one with the small blind, which is equivalent");
		System.out.println("to being clockwise of the dealer button, if this game had one. Also, the turn order is clockwise.");
		System.out.println("This version of Poker is no-limit, using a modified version of the dead button rule,");
		System.out.println("Once playing the game, a visualization of the Poker table can be unminimized, although");
		System.out.println("the game is mainly played in the Java console.");
		System.out.println("Also, please do not cheat by scrolling up the console or screen-peaking to see other");
		System.out.println("player's information.");
		System.out.println("Finally, we, the makers of LANmark Poker, do not condone real life gambling,");
		System.out.println("especially illegal gambling.");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");	//end marker
 
		File pokerTableFile;										//a file
		
		System.out.println("\nEnter 1 to start a new game, and 2 to load a game.");		//user prompt for if they want to start or load a new game		
		int newSaveOrLoad = PlayerWCJL.acquireUserIntInput(1, 2);	//get the user prompt in the range of [1,2]
		
		String userFileName = "defaultFileNamePoker";				//the default name for the file is "defaultFileNamePoker"
		PokerTableWCJL pokerTableObj;								//new poker table object
		int smallBlindSetting = 0;									//the default blind setting is no blind/ 0 chips
		
		do															//repeat until a new table has been successfully created with a new save file or a loaded save file
		{
			if(newSaveOrLoad == 1)									//if the user has chosen to save 
			{
				System.out.println("Please enter the name of the new game save file: ");	//user prompt for the name of the new save
			}
			else													//else if saveOrLoad == 2; if the user has chosen to load a game
			{
				System.out.println("Please enter the name of the game file to load, or \"*\" to start a new game save: ");	//user prompt for the name of the file to load or "*" to switch to making a new save file
			}																												//note: * is an illegal symbol to name a file with
			
			input = new Scanner(System.in);							//for inputs
			userFileName = input.nextLine();						//get the user's file name for the new save file or the file to load
			
			pokerTableFile = new File(userFileName + ".dat");		//creates a data file with the user-defined name.
 
			if(newSaveOrLoad == 1)									//if the user is saving
			{
				pokerTableObj = new PokerTableWCJL(0, pokerTableFile, false);		//try to construct a table with the parameters of: small blind value: 0 chips (not really 
																					//relevant here), the file defined by the user, and false for starting a new game
																					//this is not the real Poker table, this is only to test out whether or not the user-defined file can be used
																					//if the table creation is successful, wasFileCreationSuccessful() will return true, and false if it is not successful.
			}
			else													//else if the user is loading a pre-existing save file
			{	
				pokerTableObj = new PokerTableWCJL(5, pokerTableFile, true);		//actually create the Poker table using the parameters of: small blind value: 5 chips (not really relevant here,
																					//will be replaced by the small blind value of the save file, the file defined by the user, and true for loading an old game
			}
	
			if(userFileName.equals("*"))							//if the user entered "*" and they were trying to load a file, then that file would not be loaded because "*" is
																	//an illegal symbol in file names. Then, the user would then be prompted for file names for a new game						
			{														//this option to switch from loading to creating a new game is relevant because there might 
																	//not be any files that can be loaded, so the user must switch to creating a new file
				newSaveOrLoad = 1;									//it will now be as if the user pressed 1 to start a new game in the beginning
			}
		}while(pokerTableObj.wasFileCreationSuccessful() != true); 	//unsuccessfully creating the Poker table will lead to the method returning false, causing a repeat in the do while loop
																	//successfully creating the Poker table will end the do while loop because pokerTableObj.wasFileCreationSuccessful() will return true
		
		if(newSaveOrLoad == 1)										//if the user is starting a new game, get certain parameters for the table and add/define players
		{
			pokerTableFile.delete(); 								//delete the table that was used to test whether or not the user-defined file would be capable of serving as a new save file
 
			pokerTableFile = new File(userFileName + ".dat");		//creates a data file named by the user
 
			System.out.println("Enter the small blind value. This can be 0 chips, which is effectively playing with no blinds.");	//user prompt for the small blind value of the table
			smallBlindSetting = PlayerWCJL.acquireUserIntInput(0, 1073741820);				//the small blind cannot be negative. The small blind can be from 0 chips to the rounded (highest possible int divided by two)
																							//this makes the big blind be 2147483640 chips at its most, which is still a legal/ usable int
			pokerTableObj = new PokerTableWCJL(smallBlindSetting, pokerTableFile, false);	//create the Poker Table with the parameters of: the user defined small blind value, 
																							//the user-defined and previously tested save file, and false for starting a new game
 
			System.out.println("Enter how many players will be playing. (2-12)");			//user prompt for the number of players that will be playing, in a range of 2 players to 12 players
			int tableSizeSetting = PlayerWCJL.acquireUserIntInput(2, 12);					//int for how many players there will be
			
			for(int i = 0; i < tableSizeSetting; i ++)				//repeat for each player to be added to the table
			{
				String playerNameSetting;							//the user-defined name for each player
				do													//do while the user has not entered in a valid name
				{
					System.out.println("Enter the username for player " + (i + 1) + ". (Max.20 characters)");	//user prompt for a player's name (max 20 characters to fit the GUI)
					playerNameSetting = input.nextLine();														//get the user-defined name
				
					if(playerNameSetting.length() > 20)				//if the user-defined name is too long,
					{
						System.out.println("This name is too long. Please attempt another name.");	//tell the user that the name they entered was too long
					}
				}while(playerNameSetting.length() > 20);			//repeat if the name is longer than 20 characters
					
				System.out.println("Enter the starting chip count for " + playerNameSetting + ".");			//user prompt for the initial chip count for the current player, with their name successfully defined by the user
				int startingChipCountSetting = PlayerWCJL.acquireUserIntInput(1, 178956970);				//get the chip count, in the range of: [(at least 1 chip),(the highest int possible / 12 possible players) rounded down]
				
				PlayerWCJL playerToAdd = new PlayerWCJL(startingChipCountSetting, playerNameSetting, i); 	//create a new player with the user-defined parameters of their initial chip count, 
																											//their name, and the program defined original index, based on order of existence
				pokerTableObj.addPlayer(playerToAdd);				//add this new player to the table
			}
			pokerTableObj.startPokerTableBlindSettings();			//initialize the table's small and big blind locations (need to do this now because the settings use the size of the 
																	//arraylist of the players, so only now that all of the players are in the game can the blind locations be defined)
		}

		//the table has now been initialized, whether from a new file or based on the data from the loaded file.
		if(pokerTableObj.getWinnerFound() == true)					//if the file loaded already has a winner
		{
			pokerTableObj.announceWinner();							//announce the winner
		}
		else														//else if the game can still be played/ there is no winner yet
		{
			do														//repeatedly play round of poker until a winner is found
			{
				pokerTableObj.playHandRound();						//play a hand of poker
			}while(pokerTableObj.getWinnerFound() == false);		//repeat if the winner has not been found
			
			pokerTableObj.announceWinner();							//once a winner has been found, announce the winner
		}
	}
}