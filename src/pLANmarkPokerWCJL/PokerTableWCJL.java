package pLANmarkPokerWCJL;
 
import java.awt.BorderLayout;									//import java.awt elements
import java.awt.Color;											//(for the Abstract Windows Toolkit, used to create GUI objects)
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Dimension;
 
import java.io.File;											//import java.io elements
import java.io.FileInputStream;									//for files and exceptions related to them
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;										//import java.util elements, for ArrayLists
import java.util.Scanner;										//for scanning user inputs
 
import javax.swing.ImageIcon;									//import java.swing elements
import javax.swing.JButton;										//(for program components to create GUI components)
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
  
public class PokerTableWCJL {														//Poker Table class: facilitates playing Poker between players
													
	private ArrayList<PlayerWCJL> playerList = new ArrayList<PlayerWCJL>(); 		//ArrayList of players
	private ArrayList<PlayerWCJL> spectatorList = new ArrayList<PlayerWCJL>(); 		//ArrayList of players that will never decrease even if the players are eliminated
	private ArrayList<PotSliceWCJL> potCake = new ArrayList<PotSliceWCJL>();		//ArrayList of slices/layers of the pot, using the Pot Slice-Cake model
	private static Scanner input;													//for inputs
 
	private DeckAndCardWCJL pokerDeck = new DeckAndCardWCJL();						//deck and card analyzer
	private boolean winnerFound;													//boolean to check for winner: true if a winner is found, false if no won has won yet
	
	private File pokerSaveFile;														//the file that the Poker Table's information is loaded from (if it is being loaded) and saved to 
	private boolean successfulFileCreateOrLoad;										//whether or not the poker table was created successfully with a user-defined new file or file to load
	
	//using dead button rule (sometimes there will be a dead small blind)
	private int smallBlindLocation;													//location of small blind
	private int bigBlindLocation;													//location of big blind
	private int smallBlindValue;													//small blind value
	private int bigBlindValue;														//big blind value
		
	private int highestPossibleBet;													//highest bet possible, which is the value of the second highest chip count in the game
	private boolean potOpened;														//boolean to check for open pot

	private int numOfNotFoldedPlayers;												//number of not folded players
	private int numOfNotAllInPlayers;												//number of not all-in players
	private boolean breakOut;														//boolean to signify whether or not to break out of the loop of asking each player for their action. True to break out, false to remain looping				
	private int checksInARow;														//how many players have checked in a row. When all of the players that can make choices have checked, the game will advance to the next round
	
	private int communityCard1;														//flop: the first community card
	private int communityCard2;														//flop: the second community card
	private int communityCard3;														//flop: the third community card
	private int communityCard4; 													//the turn: the fourth community card
	private int communityCard5;														//the river: the fifth community card
	
	private JFrame frame = new JFrame("pokerTableGFrame");;							//the frame of the GUI
	
	public PokerTableWCJL(int uSmallBlindSetting, File userPokerSaveFile, boolean newFalseOrLoadTrueSetting) {	//constructor for the Poker table, using the parameters of: the small blind value, the file used to save to 
																												//and/or load from, and the boolean where it is false for new file, true for loading a pre-existing file 
		bigBlindValue = uSmallBlindSetting * 2;										//the big blind value is twice that of the small blind value
		smallBlindValue = uSmallBlindSetting;										//the small blind value is as the user set it to be

		highestPossibleBet = 0;														//the highest possible bet is by default set to 0, although this will be quickly replaced by the actual 
																					//highestPossibleBet (the second highest chip count in the game)	
		potOpened = false;															//the pot has not been opened yet; there are 0 chips in the pot
		breakOut = false;															//the breakOut setting is default set to false
		checksInARow = 0;															//0 players have checked in a row currently		
		winnerFound = false;														//a winner has not yet been found
		
		if(newFalseOrLoadTrueSetting == false)										//try to create a new game based on the user parameter/choice
		{
			if(userPokerSaveFile.exists())											//cannot create a new game if the save file already exists
			{
				System.out.println("Error: Cannot create a new game file with this name, as there is already a file with this name.");	//error message
				successfulFileCreateOrLoad = false;									//the file has not been successfully created, so the pokerTable object creating class should try to make the table again with a different file name
			}
			else if(!userPokerSaveFile.exists())									//else if the file does not already exist
			{
				try																	//try creating the file
				{
					userPokerSaveFile.createNewFile();								//create the new file		
					
					//for this game of poker, the location of the first blinds is predetermined if it is a new game
					pokerSaveFile = userPokerSaveFile;								//set the file to be used for saving to be the new user-defined file
					smallBlindLocation = 1;											//the small blind location is by default set to the "second" player, the one clockwise of the 
																					//non-existent dealer button, which starts as the "first" player, (the one in the top left corner)
					bigBlindLocation = rotationLocationNumber(2);					//in case there are only 2 players, the "dealer" also gets the big blind. (doesn't actually set the big 
																					//blind location, as the players have not been added yet. Requires the startBlindSettings method																		
					successfulFileCreateOrLoad = true;								//if the program has reached this point without error, then the file has been successfully created.
					System.out.println("File Created.");							//state that the file has been created successfully 	
				}
				catch (IOException e)												//if you cannot create the file, there is an IOException exception
				{
					successfulFileCreateOrLoad = false; 							//the file has not been successfully created, so the pokerTable object creating class should try to make the table again with a different file name
					System.out.println("File could not be created.");				//in the case of this exception, display an error message
					System.out.println("IOException:	" + e.getMessage());	
				}
			}
		}
		else if(newFalseOrLoadTrueSetting == true)									//else try to load a new game if the user has decided to load a file
		{
			if(!userPokerSaveFile.exists())											//if the user-defined file doesn't exist, then it cannot be loaded
			{
				System.out.println("Error: Save file does not exist.");				//error message
				successfulFileCreateOrLoad = false; 								//the file has not been successfully created, so the pokerTable object creating class should try to load the table again with a different file name
			}
			else if(userPokerSaveFile.exists())										//else if the file does exist
			{
				try																	//try reading the file to get the information to load/ recreate the poker table
				{					
					FileInputStream in = new FileInputStream(userPokerSaveFile);	//a new file input stream
					ObjectInputStream readPokerTable = new ObjectInputStream(in);	//creates a reading stream from the object stream in		
 
					smallBlindValue = (int)readPokerTable.readInt();				//read the value/price of the small blind
					bigBlindValue = (int)readPokerTable.readInt();					//read the value/price of the big blind (constant)
					smallBlindLocation = (int)readPokerTable.readInt();				//read the location of the small blind
					bigBlindLocation = (int)readPokerTable.readInt();				//read the location of the big blind			 
					int numberOfSpectators = (int)readPokerTable.readInt();			//read the number of players that were playing in the beginning
					
					playerList.clear();												//make sure that there are no players in the playerList
					spectatorList.clear();											//make sure that there are no players in the spectatorList
					
					PlayerWCJL playerStreamed;										//the player/spectator to be streamed and added
					for(int i = 0; i < numberOfSpectators; i++)						//for each of the spectators/players in the file, add the players/spectators to the spectatorList
					{					
						playerStreamed = (PlayerWCJL)readPokerTable.readObject();	//read the player/spectator object			
						spectatorList.add(playerStreamed);							//add the player/spectator object to the spectatorList
					}
 
					for(int i = 0; i < spectatorList.size(); i++)					//create the player list from the still playing players in the spectator list
					{
						if(spectatorList.get(i).getIsEliminatedBool() != true)		//if the player is not eliminated
						{
							playerList.add(spectatorList.get(i));					//add that player to the playerList
						}
					}
					
					readPokerTable.close();											//close stream
					pokerSaveFile = userPokerSaveFile;								//set the file to be used for saving to be the user-defined file, the same file that the game was loaded from
	 
					//checking to see if the data from the file will actually yield a proper Poker table
					if(spectatorList.size() > 0)									//if there were any spectators loaded from the file
					{
						successfulFileCreateOrLoad = true;							//then the Poker table was successfully created, so set this to true
					}
					else															//else if there were no spectators successfully loaded from the file, then the Poker Table cannot be created properly
					{
						successfulFileCreateOrLoad = false;							//the file was not successfully created, so set this to false
					}
					
					if(playerList.size() == 1)										//if there is only one player remaining that can actually play the game, then that player is the last man standing, and therefore the winner
					{
						winnerFound = true;											//the winner has been found, so set this to true
					}
				}
				catch(FileNotFoundException e)										//exception handling for the file not being found
				{
					successfulFileCreateOrLoad = false;								//the file was not successfully created, so set this to false
					System.out.println("File could not be found.");					//display the error message
					System.err.println("FileNotFoundException: " + e.getMessage());
				}
				catch(IOException e)												//exception handling for IOException 
				{
					successfulFileCreateOrLoad = false;								//the file was not successfully created, so set this to false
					System.out.println("Problem with input/output.");				//display the error message
					System.err.println("IOException: " + e.getMessage());
					System.out.println("This save file is empty, flawed, or otherwise unusable.");
				}	
				catch(ClassNotFoundException e)										//exception handling for the class not being found
				{
					successfulFileCreateOrLoad = false; 							//the file was not successfully created, so set this to false
					System.out.println("Class could not be used to cast object.");	//display the error message
					System.err.println("ClassNotFoundException " + e.getMessage());
				}
			}
		}
	}
	
	public boolean wasFileCreationSuccessful()		//return true if the Poker table was successfully created, false it was not
	{
		return successfulFileCreateOrLoad;
	}
	
	public boolean getWinnerFound()					//return true if a winner has been found/ there is a last player standing
	{
		return winnerFound;
	}
	
	public void announceWinner()													//announce the whole game winner on screen (requires a winner to actually be present in order to be accurate)
	{
		System.out.println(playerList.get(0).getPName() + " has won the game!");	//state that the last player left in the playerList has won the game
	}
	
	public void updatePotOpened()		//update the status of whether or not the pot has been opened
	{
		if(potCake.size() > 0)			//if there is anything in the pot
		{
			potOpened = true;			//then the pot is indeed opened
		}
		else							//else if there is nothing in the pot
		{
			potOpened = false;			//then the pot is indeed not opened
		}
	}
	
	public int getPotTotal()																	//return the total value of how many chips are in the pot
	{
		int totalPot = 0;																		//the tally of the chips in the pot starts at 0 chips
		
		for(int i = 0; i < potCake.size(); i++)													//for each slice in the pot cake
		{
			totalPot += potCake.get(i).getPotSegment() * potCake.get(i).getPotKeyFrequency();	//add the pot segment of that slice * the number of players that put chips into that slice to get the total chip value of that slice
		}
		
		return totalPot;																		//return the total amount of chips in the pot
	}
	
	public int getLargestBet()										//get the largest bet made by a single player in a hand; the height/ highest pot key of the pot cake
	{
		if(potCake.size() == 0)										//if there is nothing in the pot, then the largest bet made in the hand is 0 chips
		{
			return 0;												//return 0 chip bet
		}
		else														//else if there are chips in the pot
		{
			return potCake.get(potCake.size() - 1).getPotKey();		//get the pot key/height of the greatest/highest slice of the pot
		}
	}
	
	public int getHighestPossibleBet()												//get the highest possible bet, which is the second highest chip count in the game, and set chipCountShadows. 
	{																				//the second highest chip count is the highest possible bet because anything greater cannot be won by anyone. the second highest chip 
																					//count shadow can be the same as the highest chip count shadow if two or more players have the same chip count shadow
		int highestChipShadow = 0;													//the greatest amount of chips in the game before any betting has occured in the hand
		int secondHighestChipShadow = 0;											//the second greatest amount of chips in the game before any betting has occured in the hand
		int highestChipShadowID = 0;												//the index of the player with the most chips is default 0
		
		//maximizing/getting the highestChipShadow and setting each player's chip count shadow
		for(int i = 0; i < playerList.size(); i++)									//for each player in the game
		{
			playerList.get(i).setChipCountShadow();									//set the current player's chip count shadow to be their current chip count

			if(playerList.get(i).getChipCountShadow() > highestChipShadow)			//if the current player's chip count shadow is higher than the current highestChipShadow
			{
				highestChipShadow = playerList.get(i).getChipCountShadow();			//update the highestChipShadow to be the chip count shadow of the current player
				highestChipShadowID = i;											//update the index of the highestChipShadow player
			}
		}
		
		//maximizing/getting the secondHighestChipShadow
		for(int i = 0; i < playerList.size(); i++)									//for each player playing
		{
			if(i != highestChipShadowID)											//if this isn't the player with the highest chip count
			{
				if((playerList.get(i).getChipCountShadow() > secondHighestChipShadow)&&(playerList.get(i).getChipCountShadow() <= highestChipShadow))	//if the current player's chip count shadow is higher than the current
				{																																		//secondHighestChipShadow and less than or equal to the highest chip shadow							
					secondHighestChipShadow = playerList.get(i).getChipCountShadow();	//update the highestChipShadow to be the chip count shadow of the current player
				}
			}
		}

		if(secondHighestChipShadow == 0)											//if the second highest ChipShadow == highestChipShadow, then the secondHighestChipShadow will have remained 0 chips
		{
			secondHighestChipShadow = highestChipShadow;							//therefore, set the secondHighestChipShadow to equal the highestChipShadow, because that is the reality
		}																			//this allows these players, tied in chip count, to contest the same amount of chips
		
		return secondHighestChipShadow;												//return the second highest chip count shadow in the game as the highest bet a player can make
	}
	
	public void startPokerTableBlindSettings()				//set the big blind location of the table. Necessary when the table is a new table. and this method can only be used after all of the 
	{														//players have been added to the game, because the size of the arrayList of players is used in determining the location of the big blind 
		//the small blind location does not need to be set here because the small blind always begins at the "second" player, and there is always at least 2 players in the beginning of a new game
		bigBlindLocation = rotationLocationNumber(2);		//in case there are only 2 players, the "dealer" also gets the big blind.
	}
	
	public int rotationLocationNumber(int initialLocation)		//take in a theoretical index and return an index that factors in the size of the playerList and the circular nature of the players
	{
		int newLocation;										//the new index that will be returned
		
		if(initialLocation > playerList.size() - 1)				//if the theoretical index parameter is greater than/outside of the bounds of the player list
		{																		
			newLocation = initialLocation - playerList.size();	//then that index refers to circling back around to the beginning of the player list
			return newLocation;									//return this new index
		}
		else if(initialLocation < 0)							//else if the theoretical index parameter is 1 less than/outside of the bounds of the player list from the negative
		{
			newLocation = playerList.size() - 1; 				//then that index refers to circling back around to the top of the player list
			return newLocation; 								//return this new location
		}
		else													//else if the theoretical index parameter is a perfectly legal index
		{		
			return initialLocation;								//return the index, unchanged
		}	
	}
	
	public int rotationSpectatorLocationNumber(int initialLocationSpectator)	//take in a theoretical index and return an index that factors in the size of the spectatorList and the circular nature of the spectators
	{
		int newLocationSpect; 													//the new index that will be returned
		
		if(initialLocationSpectator > spectatorList.size() - 1)					//if the theoretical index parameter is greater than/outside of the bounds of the spectator list
		{
			newLocationSpect = initialLocationSpectator - spectatorList.size(); //then that index refers to circling back around to the beginning of the spectator list
			return newLocationSpect; 											//return this new index
		}
		else if(initialLocationSpectator < 0) 									//else if the theoretical index parameter is 1 less than/outside of the bounds of the spectator list from the negative
		{
			newLocationSpect = spectatorList.size() - 1;  						//then that index refers to circling back around to the top of the spectator list
			return newLocationSpect; 											//return this new location 
		}
		else 																	//else if the theoretical index parameter is a perfectly legal index
		{			 
			return initialLocationSpectator; 									//return the index, unchanged
		}	
	}

	public void addPlayer(PlayerWCJL ugaPlayer)		//add the player in the parameter to the table
	{
		spectatorList.add(ugaPlayer);				//add the player to the playing player list
		playerList.add(ugaPlayer);					//add the player to the spectator list, which is essentially an immortal version/copy of the player list
	}
	
	//updatePotCake will add or insert a slice using the parameter of the key of the new slice and the previous key of the betting player, and it does not give the player a key
	public void updatePotCake(int userPotKeyPurchase, int userLastPotKeyPurchase)	//userPotKeyPurchase should not be greater than the second highest chip count in the game
	{	
		if(potCake.size() > 0)														//if the pot has been opened/ already has slices
		{
			//checking for when the slice being created matches a pre-existing slice
			for(int i = 0; i < potCake.size(); i++)									//check each slice of the pot cake
			{
				if(potCake.get(i).getPotKey() == userPotKeyPurchase)				//if a slice's key matches the key of the player purchase,
				{				
					for(int s = i; s >= 0; s--)										//for the matching slice and each slice below it until the the last slice that the player bet into is reached
					{
						if(potCake.get(s).getPotKey()>userLastPotKeyPurchase)		//if the current slice is a slice that the player has not already bet into with their previous pot purchase
						{						
							potCake.get(s).incrementPotKeyFrequency();				//then increment their pot frequency, as there are chips being put into that slice
						}
					}
				}
			}	
			
			//covering insertions/additions to the pot cake
			if(userPotKeyPurchase > (potCake.get(potCake.size() - 1)).getPotKey())	//if the current biggest slice is less than the slice being created, then add the new slice to the top of the cake
			{	
				int newPotSliceSegment = userPotKeyPurchase - (potCake.get(potCake.size() - 1)).getPotKey();	//the pot segment of the new slice is the key/height of the new slice subtract the previous highest slice/key

				for(int s = potCake.size() - 1; s >= 0; s--)						//for each slice below the new slice being added to the top of the pot cake until the the last slice that the player bet into is reached
				{
					if(potCake.get(s).getPotKey()>userLastPotKeyPurchase)			//if the current slice is a slice that the player has not already bet into with their previous pot purchase
					{
						potCake.get(s).incrementPotKeyFrequency();					//then increment their pot frequency, as there are chips being put into that slice
					}
				}
				PotSliceWCJL newPotSlice = new PotSliceWCJL(userPotKeyPurchase, newPotSliceSegment);			//create the new slice with the player's key and the calculated pot segment
				
				potCake.add(newPotSlice);											//add this new slice to the cake	
			}
			else if(userPotKeyPurchase < potCake.get(0).getPotKey())				//if the current smallest slice is greater than the slice being created, then insert that slice into the bottom of the cake
			{
				PotSliceWCJL newPotSlice = new PotSliceWCJL(userPotKeyPurchase, userPotKeyPurchase);			//the new pot slice has the player's key as its key and the player's key as its pot segment, as there are no pot slices below it currently
				potCake.add(0, newPotSlice);										//insert the slice as the new slice
				(potCake.get(1)).updatePotSegmentFromInsertion(userPotKeyPurchase);	//now update the pot segment of the slice above the new slice.  
				
				for(int i = 0; i < potCake.get(1).getPotKeyFrequency(); i++)		//the new inserted slice has a frequency of (1 + the same frequency as the slice above it) 
				{																	//the "1" of this calculation came from the slice being constructed
					potCake.get(0).incrementPotKeyFrequency();						//this is the "+ the same frequency as the old bottom slice" of this calculation
				}	
			}
			else																	//insertions in the middle of the cake
			{		
				int indexToInsert = -10000;											//initialize the index of where the slice will be inserted as an unusable number. This number will later be changed to a real index of insertion if an insertion should be made
				
				for(int i = 0; i < potCake.size() - 1; i++)							//checking each slice of the cake
				{
					if((potCake.get(i).getPotKey() < userPotKeyPurchase)&&(userPotKeyPurchase < potCake.get(i + 1).getPotKey()))	//(potSlice.get(i)).getKey < userPotKeyPurchase < (potSlice.get(i + 1)).getKey; if this is the slice where the potKey				
					{																												//is less than the slice above it and greater than the slice below it, then this is where the slice should be inserted.
						indexToInsert = i + 1;										//set the index of insertion
					}
				}
 
				if(indexToInsert >= 0)												//if you can insert the slice (indexToInsert was changed from -10000 to an i + 1)
				{				
					for(int s = indexToInsert - 1; s >= 0; s--)						//for each slice below the one being inserted until the the last slice that the player bet into is reached
					{
						if(potCake.get(s).getPotKey() > userLastPotKeyPurchase)		//if the current slice is a slice that the player has not already bet into with their previous pot purchase
						{
							potCake.get(s).incrementPotKeyFrequency();				//then increment their pot frequency, as there are chips being put into that slice
						}
					}			
					
					int newPotSliceSegment = userPotKeyPurchase - potCake.get(indexToInsert - 1).getPotKey();	//the inserted pot slice segment is equal to the player's key/ bet subtract the key of the slice below it				
					potCake.get(indexToInsert).updatePotSegmentFromInsertion(newPotSliceSegment);				//update the pot segment of the slice above the inserted slice using the recently calculated pot segment of the slice to insert
					PotSliceWCJL newPotSlice = new PotSliceWCJL(userPotKeyPurchase, newPotSliceSegment);		//now actually create the slice to insert using the player's key/bet and the recently calculated pot segment
					
					potCake.add(indexToInsert, newPotSlice);													//insert this new pot slice 
					
					for(int i = 0; i < potCake.get(indexToInsert + 1).getPotKeyFrequency(); i++)				//the new inserted slice has a frequency of (1 + the same frequency as the slice above it) 
					{																							//the "1" of this calculation came from the slice being constructed
						potCake.get(indexToInsert).incrementPotKeyFrequency(); 		//this is the "+ the same frequency as the old bottom slice" of this calculation
					}				
				}
			}	
		}	
		else																		//else if the user is creating the first slice
		{		
			PotSliceWCJL newPotSlice = new PotSliceWCJL(userPotKeyPurchase, userPotKeyPurchase);				//create this new slice. It's key is the user-defined key, and its segment is the key, because there are no other slices currently
			potCake.add(newPotSlice);												//add this new pot slice/layer to the pot cake
		}	
	}
	
	//only available to a player if they are not folded or all-in
	//prerequisite: the player is not folded and is not all-in
	private void giveUserChoices(boolean mPotOpenedBool, PlayerWCJL uCurrentPlayerTurn)		//prompt the current player with choices that they can make. Uses the parameters of whether or not the pot has been opened and the current player
	{	
		int userChoice;																		//the player's choice input
 
		if((mPotOpenedBool == false))														//if the pot has 0 chips in it, then the player can check, bet, or fold
		{
			System.out.println("Enter 1 to check, 2 to bet, or 3 to fold");					//player/user prompt
			userChoice = PlayerWCJL.acquireUserIntInput(1, 3);								//get the player/user's choice
			
			switch(userChoice)																//switch of the user/player's choice
			{
				case(1): 																	//if the user checked
					System.out.println(uCurrentPlayerTurn.getPName() + " has checked.");	//state that the player has checked
					checksInARow ++;														//another player has checked
					break;																	//break out of the switch
				
				case(2):																	//if the user bets
					updatePotCake(uCurrentPlayerTurn.bet(highestPossibleBet), uCurrentPlayerTurn.getPreviousPlayerPotKey());	
					//add/insert the slice using the user/player-defined bet, confined by the highestPossibleBet or their chip count shadow, whichever is lower, and also using the current player's last pot key purchase for incrementing pot frequency.  
				
					if(uCurrentPlayerTurn.getChipCount() == 0)								//if the current player went all in with their bet
					{
						numOfNotAllInPlayers --;											//decrement the number of players that are not all in
					}	
					checksInARow = 0;														//by betting, the number of players that have checked in a row is 0
					break;																	//break out of the switch
				
				case(3):																	//if the player folds
					uCurrentPlayerTurn.fold();												//the current player folds
					
					numOfNotFoldedPlayers --;												//the number of players that have not folded decrements
					numOfNotAllInPlayers --;												//the number of players that can still make choices in this hand decrements
					break;																	//break out of the switch
			}
		}
		//else the pot has been opened and the current player can check or raise
		else if((uCurrentPlayerTurn.getPlayerPotKey() == potCake.get(potCake.size() - 1).getPotKey())&&(uCurrentPlayerTurn.getChipCountShadow() > potCake.get(potCake.size() - 1).getPotKey())&&(uCurrentPlayerTurn.getPlayerPotKey() < highestPossibleBet))	
		{	//else if(the current player's bet is the same as the highest bet of the hand) && (the current player has more chips left to raise with) && (the highest possible bet has not been made yet)	
			System.out.println("Enter 1 to check, 2 to raise, or 3 to fold");				//user prompt of choices
			userChoice = PlayerWCJL.acquireUserIntInput(1, 3);								//get the user/player choice in the range of [1,3]
			
			switch(userChoice)																//switch for the user/player's choice
			{
				case(1): 																	//if the player checked
					System.out.println(uCurrentPlayerTurn.getPName() + " has checked.");	//state that they have checked
					checksInARow ++;														//another player has checked
					break;																	//break out of the switch
				
				case(2):																	//if the user raises	
					updatePotCake(uCurrentPlayerTurn.raise(potCake.get(potCake.size() - 1).getPotKey(), highestPossibleBet), uCurrentPlayerTurn.getPreviousPlayerPotKey()); 
					//add/insert the slice using the user/player-defined raise, confined by the highestPossibleBet or their chip count shadow, whichever is lower, and also using the current player's last pot key purchase for incrementing pot frequency.  
					
					checksInARow = 0;														//by raising, the number of players that have checked in a row is 0
					if(uCurrentPlayerTurn.getChipCount() == 0)								//if the player went all in with their raise
					{
						numOfNotAllInPlayers --;											//then the number of players that are not all in decrements
					}
					break;																	//break out of the switch
			
				case(3):
					numOfNotFoldedPlayers --; 												//the number of players that have not folded decrements
					numOfNotAllInPlayers --; 												//the number of players that can still make choices in this hand decrements
					
					uCurrentPlayerTurn.fold();												//the current player folds
					break;																	//break out of the switch
			}
		}
		//else the pot has been opened, and the user can call or raise
		else if((uCurrentPlayerTurn.getPlayerPotKey() < potCake.get(potCake.size() - 1).getPotKey())&&(uCurrentPlayerTurn.getChipCountShadow() > potCake.get(potCake.size() - 1).getPotKey())&&(potCake.get(potCake.size() - 1).getPotKey() < highestPossibleBet))		
		{//else if(the current player's bet is less the highest bet of the hand) && (the current player has more chips left to call with) && (the highest possible bet has not been made yet)	
			System.out.println("Enter 1 to call, 2 to raise, or 3 to fold");				//user/player prompt of choices 
			userChoice = PlayerWCJL.acquireUserIntInput(1, 3);								//get the user/player input in the range of [1/3]
 
			switch(userChoice)																//switch of the player/user's input
			{
				case(1):																	//if the player calls
					updatePotCake(uCurrentPlayerTurn.call(potCake.get(potCake.size() - 1).getPotKey()), uCurrentPlayerTurn.getPreviousPlayerPotKey());	//add/insert the slice using the chips that can be called, and also using
																																				//the current player's last pot key purchase for incrementing pot frequency.  
					checksInARow = 0;														//by calling, the number of players that have checked in a row is 0
			
					if(uCurrentPlayerTurn.getChipCount() == 0)								//if the current player went all in with their call
					{
						numOfNotAllInPlayers --;											//decrement the number of players that are not all in
					}
					break;																	//break out of the switch
			
				case(2):																	//if the current player raises			
					checksInARow = 0;														//by raising, the number of players that have checked in a row is 0

					updatePotCake(uCurrentPlayerTurn.raise(potCake.get(potCake.size() - 1).getPotKey(), highestPossibleBet), uCurrentPlayerTurn.getPreviousPlayerPotKey()); 
					//add/insert the slice using the user/player-defined raise, confined by the highestPossibleBet or their chip count shadow, whichever is lower, and also using the current player's last pot key purchase for incrementing pot frequency.  
					
					if(uCurrentPlayerTurn.getChipCount() == 0)								//if the current player went all in with their raise
					{	
						numOfNotAllInPlayers --;											//decrement the number of not all in players
					}			
					break;																	//break out of the switch
			
				case(3):																	//if the current player folds
					numOfNotFoldedPlayers --;												//the number of players that have not folded decrements
					numOfNotAllInPlayers --;												//the number of players that can still make choices in this hand decrements
					
					uCurrentPlayerTurn.fold(); 												//the current player folds
					break;																	//break out of the switch
			}
		}		
		//if the user can only call or fold (calling would make the player all in),(or it is impossible to raise because the maximum bet has already been made)
		else if((uCurrentPlayerTurn.getPlayerPotKey() < potCake.get(potCake.size() - 1).getPotKey())&&((uCurrentPlayerTurn.getChipCountShadow() <= potCake.get(potCake.size() - 1).getPotKey())||(potCake.get(potCake.size() - 1).getPotKey() == highestPossibleBet))) 
		{	//else if(the current player's bet is less the highest bet of the hand) && (the current player would have no chips left if they called)		
			//or (the maximum possible bet has been made)		
			System.out.println("Enter 1 to call or 2 to fold");								//user/player prompt for choices
			userChoice = PlayerWCJL.acquireUserIntInput(1, 2);								//get the user/player choice in the range of [1,2]
 
			switch(userChoice)																//switch for the user/player's input
			{
				case(1): 																	//if the player calls
					updatePotCake(uCurrentPlayerTurn.call(potCake.get(potCake.size() - 1).getPotKey()), uCurrentPlayerTurn.getPreviousPlayerPotKey());  //add/insert the slice using the chips that can be called, and also using
																																						//the current player's last pot key purchase for incrementing pot frequency.  
					checksInARow = 0;														//by calling, the number of players that have checked in a row is 0
					if(uCurrentPlayerTurn.getChipCount() == 0)								//if the current player went all in with their call
					{
						numOfNotAllInPlayers --; 											//decrement the number of players that are not all in
					}
					break; 																	//break out of the switch
				case(2): 																	//if the current player folds
					numOfNotFoldedPlayers --;  												//the number of players that have not folded decrements
					numOfNotAllInPlayers --; 												//the number of players that can still make choices in this hand decrements
					
					uCurrentPlayerTurn.fold();   											//the current player folds
					break; 																	//break out of the switch
			}
		}
		else if(uCurrentPlayerTurn.getPlayerPotKey() >= highestPossibleBet)					//else if the highest possible bet has been made, and the current user/player still has chips, then the user/player can check or fold
		{
			System.out.println("Enter 1 to check or 2 to fold");							//user/player prompt for choices
			userChoice = PlayerWCJL.acquireUserIntInput(1, 2);								//get the user/player input in the range of [1,2]
			
			switch(userChoice)																//switch for the user/player input
			{
				case(1): 																	//if the current player checked
					System.out.println(uCurrentPlayerTurn.getPName() + " has checked.");	//state that the user has checked
					checksInARow ++;														//the number of players that have checked in a row increases
					break;																	//break out of the switch
				case(2):																	//if the player folds
					numOfNotFoldedPlayers --; 												//the number of players that have not folded decrements
					numOfNotAllInPlayers --; 												//the number of players that can still make choices in this hand decrements
					
					uCurrentPlayerTurn.fold();  											//the current player folds			
					break; 																	//break out of the switch
			}		
		}
	}
	
	public void distributePot() 													//distribute the pot to the players. Also concludes the current hand round and readies it for the next hand round 
	{//for cases where there is more than 1 player not folded, the list of players needs to be sorted from lowest hand strength to highest hand strength
		
		System.out.println("The pot is currently: " + getPotTotal() + " chips.");	//state what the total pot is before it is distributed

		//assign point gates for each segment of cake
		for(int i = playerList.size() - 1; i >= 0; i--)								//for each player, starting from the highest to the lowest hand strength player 
		{
			if(playerList.get(i).getFoldedBool() == false)							//if the player did not fold
			{
				for(int c = 0; c < potCake.size(); c++)								//for each layer of the cake
				{
					if(potCake.get(c).getPotKey() <= playerList.get(i).getPlayerPotKey())			//if the player bet into that amount of chips
					{
						potCake.get(c).updatePotPointGate(playerList.get(i).getpPrimaryHandScore(), playerList.get(i).getpSecondaryHandScore());			//try to update the point gate using the current player's hand scores
					}
				}	
			}
		}
		
		//figure out how many players tied and update the pot slices' frequency of pot key winners
		for(int i = playerList.size() - 1; i >= 0; i--)								//for each player, starting from the highest to the lowest hand strength player 
		{
			if(playerList.get(i).getFoldedBool() == false)							//if the player did not fold
			{
				for(int c = 0; c < potCake.size(); c++)								//for each layer of the cake
				{
					if((potCake.get(c).passesPotPointGate(playerList.get(i).getpPrimaryHandScore(), playerList.get(i).getpSecondaryHandScore()) == true)	//if the current player's hand score surpasses the current slice's point gate
						&&(potCake.get(c).getPotKey() <= playerList.get(i).getPlayerPotKey()))		//and the player bet into that amount of chips
					{
						potCake.get(c).incrementPotKeyFrequencyWinners();			//then increment the amount of players who can win that slice of pot
					}
				}
			}
		}
		
		//distribute the pot
		for(int i = playerList.size() - 1; i >= 0; i--)								//for each player, starting from the highest to the lowest hand strength player 
		{
			if(playerList.get(i).getFoldedBool() == false)							//if the player did not fold
			{
				for(int c = 0; c < potCake.size(); c++)								//for each layer of the cake
				{
					if((potCake.get(c).passesPotPointGate(playerList.get(i).getpPrimaryHandScore(), playerList.get(i).getpSecondaryHandScore()) == true)	//if the current player's hand score surpasses the current slice's point gate
						&&(potCake.get(c).getPotKey() <= playerList.get(i).getPlayerPotKey())) 		//and the player bet into that amount of chips
					{
						int chipsWonSegment = (potCake.get(c).getPotSegment() * potCake.get(c).getPotKeyFrequency()) / potCake.get(c).getPotKeyFrequencyWinners();
						//the current player wins: (the number of chips in the segment * the number of times those chips were put into the pot) / the number of players who tie in points for that segment
						
						playerList.get(i).collectPot(chipsWonSegment);				//give the chips that the player won to the player
						potCake.get(c).denyRefunds();								//set it so that this slice of the pot cake cannot give refunds, as the slice's chip contents have already been won and distributed away
					}
				}
			}
		}
		
		//refund the pot for players, which happens when folded players had put in more money than the amount of money that the winning players won,  
		for(int c = 0; c < potCake.size(); c++)										//for each layer of the cake
		{
			if(potCake.get(c).getPotKeyFrequencyRefunds() > 0)						//if players can be refunded for the pot
			{
				for(int i = 0; i < playerList.size(); i ++)							//for each player
				{
					if(playerList.get(i).getPlayerPotKey() >= potCake.get(c).getPotKey())			//if the player can get refunds for betting as much as or more than this slice key, refund the player for this segment
					{
						playerList.get(i).collectPot(potCake.get(c).getPotSegment());				//give the player back the chip segments that they bet and were not won  
					}
				}
			}
		}
		
		//the pot has been fully distributed, so now the pot needs to be emptied
		potCake.clear();
			
		//reset everything for the next playHandRound
		//sort the player list based on their original order
		PlayerWCJL itemToInsertP; 													//the element value of the element to insert
		int jP;																		//the index of the items compared to the element to insert for insertion.
		boolean keepGoingP;															//boolean for whether or not to continue running the sort pass
															
		for(int k = 1; k < playerList.size(); k++)									//for each k, take the element at k and insert it in between the elements to the left of it (or leave a[k] be in some cases).
		{
			itemToInsertP = playerList.get(k);										//the element value used with comparisons to find where to insert a[k]. The first a[k] is a[1] (not a[0])
			jP = k - 1;																//j starts off as the element to the left of a[k]
			keepGoingP = true;														//keep going
			while((jP >= 0) && keepGoingP)											//while the element to insert is being compared to elements left of it:
			{				
				if (itemToInsertP.getOriginalIndex() < playerList.get(jP).getOriginalIndex())		//if the item to insert is less than a[j], the pass needs to continue
				{
					playerList.set(jP + 1, playerList.get(jP)); 					//part of the shift right of elements needed for inserts. (makse the element to the right of a[j] = a [j]	
					jP--;															//decrement j
					if(jP == -1)													//if the element needs to be inserted when j == -1 (item to insert < a[0]):
						playerList.set(0, itemToInsertP);
				}
				else 																//if itemToInsert >= a[j], the location that the element should be inserted is found: a[j+1]
				{
					keepGoingP = false;												//stop the while loop of inserting the element			
					playerList.set(jP+1, itemToInsertP); 							//insert the element where it is greater than or equal to the element to the left of it
				}
			} 
		}
 
		//now move/update the blinds and eliminate players
		int numberOfBlindDecrements = 0;											//the number of times that the big blind location index has to increment to account for players being eliminated
 
		for(int i = playerList.size() - 1; i >= 0; i--)								//from the last player to the first player based on their original order (important to be in this order because of the shift left of elements after a removal)
		{		
			if(playerList.get(i).getChipCount() <= 0)								//if the player has no money after the pot was distributed, they are eliminated
			{
				if(i <= bigBlindLocation )											//if the player was located behind/ counterclockwise from the big blind,
				{
					numberOfBlindDecrements++;										//then the big blind index has to decrement to account for this removed player
				}
				playerList.get(i).eliminatePlayer();								//eliminate the player
				spectatorList.get(playerList.get(i).getOriginalIndex()).eliminatePlayer();			//mark the player in the spectator list as being eliminated
		
				playerList.remove(i);												//remove the player from the list of players 
			}
		}
 
 		//determine the location of the big blind, which moves clockwise and must always be present
		for(int i = 0; i < numberOfBlindDecrements; i++)							//for each decrement in the index of the big blind, determined above
		{
			bigBlindLocation = rotationLocationNumber(bigBlindLocation - 1);		//decrement the index of the big blind
		}

		bigBlindLocation = rotationLocationNumber(bigBlindLocation + 1);			//now move the big blind 1 player clockwise to get the actual current big blind location
 
		//smallBlindLocation = rotationLocationNumber(bigBlindLocation--);	//the small blind will be behind the big blind
		if(playerList.get(rotationLocationNumber(bigBlindLocation - 1)).getOriginalIndex() != rotationSpectatorLocationNumber(playerList.get(bigBlindLocation).getOriginalIndex() - 1))
		{	//if (the player to the right of the big blind)'s index does not match the index of the chair to the right of the big blind 
			//if the chair to the right of the big blind is empty, then the small blind is dead for this hand, and the first player to act will be the big blind
			smallBlindValue = 0;													//the small blind is a dead small blind, and so it costs no chips (it is as if the empty chair counterclockwise of the big blind has the small blind)
			smallBlindLocation = bigBlindLocation;									//the small blind will move to the big blind location, and it will be a dead small blind (handled in the GUI section)
		}
		else																		//else the small blind is posted to the right of the big blind.
		{
			smallBlindLocation = rotationLocationNumber(bigBlindLocation - 1);		//the small blind is counterclockwise to the big blind
			smallBlindValue = bigBlindValue / 2;									//give the small blind its price, which is half of the big blind
		}

		//now reset all of the players for the next hand round
		for(int i = 0; i < playerList.size(); i++)									//for each player not eliminated/ still playing
		{	
			playerList.get(i).resetPlayer();										//reset the player
		}
			
		//now that the hand round has essentially concluded, the game can be saved
		System.out.println("Enter \"S\" to save, and anything else to continue without saving.");	//user prompt to save. If the user does not save here, then they must wait until the next hand round concludes to save
		if(PlayerWCJL.charButton("S") == true)										//if the user enters in a one-character string where the character is an "s" or an "S"
		{
			saveGame(pokerSaveFile);												//then the user has chosen to save, so save the game to the save file 
		}
		
		System.out.println("Enter \"Q\" to quit, and anything else to continue playing.");			//user prompt to quit if the user wishes
		if(PlayerWCJL.charButton("Q") == true)										//if the user enters in a one-character string where the character is a "q" or a "Q"
		{
			System.out.println("Quitting...");										//then the user is quitting
			System.exit(0);															//exit the program
		}	
		
		if(playerList.size() == 1)													//if there is only one player left playing/ a last player standing
		{
			winnerFound = true;														//then a winner has been found
		}
	}
	
	
	public void playPreFlopRound()																					//play the pre-flop round, before the flop. Identical to the playRound() method  
	{																												//except that the first player to play will be the one clockwise of the big blind 
		System.out.println("___________________________________________________________________________________________________");	//mark the start of a new round

		checksInARow = 0;																							//the number of players that have checked in a row is 0. When all of the players that can check have checked, the round will end
		breakOut = false;																							//whether or not to break out of the loop is by default set to false. This will be true when all of the players that can check have checked
 
		do																											//do while breakout == false, which means repeatedly give players choices until all of the players that can check have checked
		{
			System.out.println("The pot is currently: " + getPotTotal() + " chips.");								//display the current pot

			if(breakOut == false)																					//if the round is still going on; if all of the players that can check have not checked yet
			{
				for(int i = rotationLocationNumber(bigBlindLocation + 1); i < playerList.size(); i++)				//starting from the player clockwise of the big blind, to the "end" of the table. Part of the cycle of cycling through players
				{
					printALotOFSpaces();																			//let the current player at the computer switch off to let the next player play

					createAndShowGUI(i);																			//update the graphics (player at i is currently playing)
					
					updatePotOpened();																				//update whether or not the pot has been opened yet
					
					System.out.println("The pot is currently: " + getPotTotal() + " chips.");						//state the current pot
					System.out.println("The highest bet of this hand so far is " + getLargestBet() + " chips.");	//state the current highest bet of the hand; the bet that must be surpassed to raise
					System.out.println("The highest bet that " + playerList.get(i).getPName() + " has made in this hand so far is " + playerList.get(i).getPlayerPotKey() + " chips.");	//state the current player's highest bet
 
			
					System.out.println("It is currently " + playerList.get(i).getPName() + " 's turn.");			//state which player's current turn it is
					System.out.println((playerList.get(i)).getPName() + " has " + playerList.get(i).getChipCount() + " chips.");	//state how many chips the current player has
					//state the current hole cards of the current player
					System.out.println((playerList.get(i)).getPName() + " has a " + DeckAndCardWCJL.getCardName((playerList.get(i)).getHoleCard1())  + " and a " + DeckAndCardWCJL.getCardName((playerList.get(i)).getHoleCard2()) + ".");	
					System.out.println(playerList.get(i).getPName() + " All in status: " + playerList.get(i).getAllInOrNotBool() + ".");	//state whether or not the current player is all in		
					System.out.println(playerList.get(i).getPName() + " Folded status: " + playerList.get(i).getFoldedBool() + ".");//state whether or not the current player is folded
			
					if((playerList.get(i).getAllInOrNotBool() == false)&&(playerList.get(i).getFoldedBool() == false))				//if the player is not all in and has not folded
					{
						if(numOfNotFoldedPlayers == 1)																//if the current player is the last one to not fold
						{	
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold.");	//state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true;																		//the round is finished, so the round can end
							break;																					//break out of the for loop of players
						}
						else																						//else if there are still players playing
						{
							giveUserChoices(potOpened, playerList.get(i));											//if the current player is not all in or folded, give them choices based on whether or not the pot has been opened

							if((checksInARow == numOfNotFoldedPlayers)||(checksInARow == numOfNotAllInPlayers))		//after the player takes his turn, if all of the players that can check have checked
							{//if((the number of players that have checked in a row is equal to the number of players that haven't folded) or (the number of players that have checked in a row is equal to the number of players that aren't all-in))
								breakOut = true;																	//then all of the players that can check have checked, so the round is finished
								break;																				//break out of the for loop of players
							}
						}
					}
					else if((playerList.get(i).getAllInOrNotBool() == true)&&(playerList.get(i).getFoldedBool() == false))			//else if the player is all in
					{			
						if(numOfNotAllInPlayers == 0)																//if everyone that can be all in is all in
						{
							breakOut = true;																		//then the round is over
						}
						if(numOfNotFoldedPlayers == 1)																//if the current player is the last one to not fold
						{	
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold."); 	//state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true; 																		//the round is finished, so the round can end
							break; 																					//break out of the for loop of players
						}
					}
				}
			}
			else																									//else if the round is over
			{
				printALotOFSpaces();																				//let the current player at the computer switch off to let the next player play
			}

			if(breakOut == false) 																					//if the round is still going on; if all of the players that can check have not checked yet
			{
				for(int i =  0; i < rotationLocationNumber(bigBlindLocation + 1); i++) 								//starting from the first player, to the player clockwise of the big blind. Part of the cycle of cycling through players
				{
					printALotOFSpaces(); 																			//let the current player at the computer switch off to let the next player play
			
					createAndShowGUI(i);																			//update the graphics (player at i is currently playing)
 
					updatePotOpened();																				//update whether or not the pot has been opened yet
					
					System.out.println("The pot is currently: " + getPotTotal() + " chips."); 						//state the current pot
					System.out.println("The highest bet of this hand so far is " + getLargestBet() + " chips."); 	//state the current highest bet of the hand; the bet that must be surpassed to raise
					System.out.println("The highest bet that " + playerList.get(i).getPName() + " has made in this hand so far is " + playerList.get(i).getPlayerPotKey() + " chips.");  //state the current player's highest bet
			
					System.out.println("It is currently " + playerList.get(i).getPName() + " 's turn."); 			//state which player's current turn it is	
					System.out.println((playerList.get(i)).getPName() + " has " + playerList.get(i).getChipCount() + " chips.");	//state how many chips the current player has	
					//state the current hole cards of the current player
					System.out.println((playerList.get(i)).getPName() + " has a " + DeckAndCardWCJL.getCardName((playerList.get(i)).getHoleCard1())  + " and a " + DeckAndCardWCJL.getCardName((playerList.get(i)).getHoleCard2()) + "."); 			
			
					if((playerList.get(i).getAllInOrNotBool() == false)&&(playerList.get(i).getFoldedBool() == false))				//if the player is not all in and has not folded
					{
						if(numOfNotFoldedPlayers == 1) 																//if the current player is the last one to not fold
						{	
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold.");	//state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true;																		 //the round is finished, so the round can end
							break; 																				 	//break out of the for loop of players
						}
						else  																						//else if there are still players playing
						{
							giveUserChoices(potOpened, playerList.get(i));											//if the current player is not all in or folded, give them choices based on whether or not the pot has been opened
							if((checksInARow == numOfNotFoldedPlayers)||(checksInARow == numOfNotAllInPlayers)) 	//after the player takes his turn, if all of the players that can check have checked
							{//if((the number of players that have checked in a row is equal to the number of players that haven't folded) or (the number of players that have checked in a row is equal to the number of players that aren't all-in))
								breakOut = true; 																	//then all of the players that can check have checked, so the round is finished
								break; 																				//break out of the for loop of players
							}
						}	
					}	
					else if((playerList.get(i).getAllInOrNotBool() == true)&&(playerList.get(i).getFoldedBool() == false))			//else if the player is all in
					{	
						if(numOfNotAllInPlayers == 0) 																//if everyone that can be all in is all in
						{
							breakOut = true;																		//then the round is over
						} 
						if(numOfNotFoldedPlayers == 1) 																//if the current player is the last one to not fold
						{	 
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold."); //state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true; 																		//the round is finished, so the round can end
							break;																					//break out of the for loop of players
						}
					}
				}
			}
			else 																									//else if the round is over
			{
				printALotOFSpaces(); 																				//let the current player at the computer switch off to let the next player play
			}	
		
		}while(breakOut == false);																					//repeat while all of the players that can check have not yet checked
		
		printALotOFSpaces();																						//let the current player at the computer switch off to let the next player play
	}
	
	
	public void playRound()																//play a post-flop round. Basically identical to the playPreFlopRound() method except that the first player to play will be the one with the small blind
	{
		System.out.println("___________________________________________________________________________________________________"); //mark the start of a new round

		checksInARow = 0; 																//the number of players that have checked in a row is 0. When all of the players that can check have checked, the round will end
		breakOut = false;																//whether or not to break out of the loop is by default set to false. This will be true when all of the players that can check have checked
 
		do 																				//do while breakout == false, which means repeatedly give players choices until all of the players that can check have checked
		{
			System.out.println("The pot is currently: " + getPotTotal() + " chips."); 												//display the current pot
			System.out.println("The highest bet of this hand so far is " + getLargestBet() + " chips.");							//display the highest bet of the hand so far
		
			if(breakOut == false) 														//if the round is still going on; if all of the players that can check have not checked yet
			{
				for(int i = rotationLocationNumber(smallBlindLocation); i < playerList.size(); i++) 								//starting from the player at the small blind, to the "end" of the table. Part of the cycle of cycling through players
				{
					printALotOFSpaces(); 												//let the current player at the computer switch off to let the next player play
					
					createAndShowGUI(i);												//update the graphics (player at i is currently playing)
					
					updatePotOpened();													//update whether or not the pot has been opened yet
					
					System.out.println("The pot is currently: " + getPotTotal() + " chips."); 										//state the current pot
					System.out.println("The highest bet of this hand so far is " + getLargestBet() + " chips."); 					//state the current highest bet of the hand; the bet that must be surpassed to raise
					System.out.println("The highest bet that " + playerList.get(i).getPName() + " has made in this hand so far is " + playerList.get(i).getPlayerPotKey() + " chips.");  //state the current player's highest bet
	
					System.out.println("It is currently " + playerList.get(i).getPName() + " 's turn.");  							//state which player's current turn it is
					System.out.println((playerList.get(i)).getPName() + " has " + playerList.get(i).getChipCount() + " chips.");	//state how many chips the current player has
					//state the current hole cards of the current player
					System.out.println((playerList.get(i)).getPName() + " has a " + DeckAndCardWCJL.getCardName((playerList.get(i)).getHoleCard1())  + " and a " + DeckAndCardWCJL.getCardName((playerList.get(i)).getHoleCard2()) + "."); 
				
					if((playerList.get(i).getAllInOrNotBool() == false)&&(playerList.get(i).getFoldedBool() == false))				//if the player is not all in and has not folded
					{
						if(numOfNotFoldedPlayers == 1) 									//if the current player is the last one to not fold
						{	
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold."); 	//state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true; 											//the round is finished, so the round can end
							break; 														//break out of the for loop of players
						}
						else 															//else if there are still players playing
						{
							giveUserChoices(potOpened, playerList.get(i)); 				//if the current player is not all in or folded, give them choices based on whether or not the pot has been opened
							if((checksInARow == numOfNotFoldedPlayers)||(checksInARow == numOfNotAllInPlayers)) 					//after the player takes his turn, if all of the players that can check have checked
							{//if((the number of players that have checked in a row is equal to the number of players that haven't folded) or (the number of players that have checked in a row is equal to the number of players that aren't all-in))
								breakOut = true; 										//then all of the players that can check have checked, so the round is finished
								break;  												//break out of the for loop of players
							}
						}
					}
					else if((playerList.get(i).getAllInOrNotBool() == true)&&(playerList.get(i).getFoldedBool() == false)) 			//else if the player is all in
					{			
						if(numOfNotAllInPlayers == 0) 									//if everyone that can be all in is all in
						{
							breakOut = true; 										 	//then the round is over
						}
						if(numOfNotFoldedPlayers == 1) 									//if the current player is the last one to not fold
						{	
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold."); 	//state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true;  											//the round is finished, so the round can end
							break; 														//break out of the for loop of players
						}
					}		
				}
			}
			else 																		//else if the round is over
			{
				printALotOFSpaces();													//let the current player at the computer switch off to let the next player play
			}

			if(breakOut == false) 														//if the round is still going on; if all of the players that can check have not checked yet
			{
				for(int i =  0; i < rotationLocationNumber(smallBlindLocation); i++)	//starting from the first player, to the player with the small blind. Part of the cycle of cycling through players
				{
					printALotOFSpaces();												//let the current player at the computer switch off to let the next player play
					
					createAndShowGUI(i);												//update the graphics (player at i is currently playing)
	 
					updatePotOpened();													//update whether or not the pot has been opened yet
					
					System.out.println("The pot is currently: " + getPotTotal() + " chips."); 										//state the current pot
					System.out.println("The highest bet of this hand so far is " + getLargestBet() + " chips."); 					//state the current highest bet of the hand; the bet that must be surpassed to raise
					System.out.println("The highest bet that " + playerList.get(i).getPName() + " has made in this hand so far is " + playerList.get(i).getPlayerPotKey() + " chips."); //state the current player's highest bet	
					
					System.out.println("It is currently " + playerList.get(i).getPName() + " 's turn."); 							//state which player's current turn it is
					System.out.println((playerList.get(i)).getPName() + " has " + playerList.get(i).getChipCount() + " chips."); 	//state how many chips the current player has
					//state the current hole cards of the current player
					System.out.println((playerList.get(i)).getPName() + " has a " + pokerDeck.getCardName((playerList.get(i)).getHoleCard1())  + " and a " + pokerDeck.getCardName((playerList.get(i)).getHoleCard2()) + "."); 	
				
					if((playerList.get(i).getAllInOrNotBool() == false)&&(playerList.get(i).getFoldedBool() == false))				//if the player is not all in and has not folded
					{
						if(numOfNotFoldedPlayers == 1) 									//if the current player is the last one to not fold
						{	
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold."); 	//state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true;  											//the round is finished, so the round can end
							break;  													//break out of the for loop of players
						}
						else 															//else if there are still players playing
						{
							giveUserChoices(potOpened, playerList.get(i));  			//if the current player is not all in or folded, give them choices based on whether or not the pot has been opened
							if((checksInARow == numOfNotFoldedPlayers)||(checksInARow == numOfNotAllInPlayers)) //after the player takes his turn, if all of the players that can check have checked
							{//if((the number of players that have checked in a row is equal to the number of players that haven't folded) or (the number of players that have checked in a row is equal to the number of players that aren't all-in))
								breakOut = true; 										//then all of the players that can check have checked, so the round is finished
								break;													//break out of the for loop of players
							}
						}
					}
					else if((playerList.get(i).getAllInOrNotBool() == true)&&(playerList.get(i).getFoldedBool() == false)) 			//else if the player is all in
					{				
						if(numOfNotAllInPlayers == 0) 									//if everyone that can be all in is all in
						{
							breakOut = true; 											//then the round is over
						}
						if(numOfNotFoldedPlayers == 1)  								//if the current player is the last one to not fold
						{	
							System.out.println(playerList.get(i).getPName() + " has won the pot as the last person to not fold."); 	//state that the current player has won the pot/ hand by being the last one not to fold
							breakOut = true; 											//the round is finished, so the round can end
							break;														//break out of the for loop of players
						}
					}
				}
			}
			else 																		//else if the round is over
			{
				printALotOFSpaces();													//let the current player at the computer switch off to let the next player play
			}
	 
		}while(breakOut == false); 														//repeat while all of the players that can check have not yet checked
		
		printALotOFSpaces(); 															//let the current player at the computer switch off to let the next player play	
	}
	
	
	public void playBlinds()											//causes the players with the blinds pay them
	{
		if(bigBlindValue >= 2)											//if there are blinds worth anything in this game
		{
			updatePotCake(playerList.get(smallBlindLocation).subtractBlind(smallBlindValue, 1), playerList.get(smallBlindLocation).getPreviousPlayerPotKey()); 	//add/insert the slice using whatever the player payed for the small blind, 
																																								//using the current player's last pot key purchase for incrementing pot frequency.  
			updatePotCake(playerList.get(bigBlindLocation).subtractBlind(bigBlindValue, 2), playerList.get(bigBlindLocation).getPreviousPlayerPotKey());		//add/insert the slice using whatever the player payed for the big blind, 
																																								//using the current player's last pot key purchase for incrementing pot frequency.  
			if(playerList.get(smallBlindLocation).getChipCount() == 0)	//if the player paying the small blind went all in by paying the small blind
			{
				numOfNotAllInPlayers --;								//decrement the number of players that are not all in
			}
		
			if(playerList.get(bigBlindLocation).getChipCount() == 0)	//if the player paying the big blind went all in by paying the big blind
			{
				numOfNotAllInPlayers --;								//decrement the number of players that are not all in
			}
		}
		updatePotOpened();												//update whether or not the pot has been opened
	}
	
	
	public void playHandRound()																	//play a hand of Poker (involves dealing the cards, posting the blinds, the pre-flop round,
	{																							//the flop round, the turn, the river, the showdown, the distribution of the pot, ending the round)
		System.out.println("The pot is currently: " + getPotTotal() + " chips.");				//state the current pot

 		communityCard1 = -10;																	//the first community card is set by default to a non-real card, turns into a face down card for the GUI
  		communityCard2 = -10;																	//the second community card is set by default to a non-real card, turns into a face down card for the GUI
  		communityCard3 = -10;																	//the third community card is set by default to a non-real card, turns into a face down card for the GUI
 		communityCard4 = -10;																	//the fourth community card is set by default to a non-real card, turns into a face down card for the GUI
		communityCard5 = -10;																	//the fifth community card is set by default to a non-real card, turns into a face down card for the GUI
		
		HandEvaluatorWCJL currentHandEv = new HandEvaluatorWCJL();								//new hand evaluator object
		
		pokerDeck.shuffleNewDeck();																//shuffle the deck
		
		highestPossibleBet = getHighestPossibleBet();											//set the highest possible bet 
		
		numOfNotFoldedPlayers = playerList.size();												//set the number of players that are not folded yet to be the amount of players still alive/playing
		numOfNotAllInPlayers = playerList.size();												//set the number of players that are not all in yet to be the amount of players still alive/playing
		updatePotOpened();																		//update whether or not the pot has been opened

		playBlinds();																			//play the blinds
		
		createAndShowGUI(-1);																	//update the graphics (no one is currently playing)
		System.out.println("The pot is currently: " + getPotTotal() + " chips.");				//state the current pot

		for(int i = rotationLocationNumber(bigBlindLocation + 1); i < playerList.size(); i++)	//everyone draws their hole cards starting from from the player clockwise of the big blind
		{																						//to the player at the "end" of the table. Part of the cycle of cycling through the players
			int pokTabHolCard1 = pokerDeck.drawCard();											//get/draw the first hole card
			int pokTabHolCard2 = pokerDeck.drawCard();											//get/draw the second hole card
			
			(playerList.get(i)).setHoleCards(pokTabHolCard1, pokTabHolCard2);					//set the hole cards of the current player	
		}
		
		for(int i =  0; i < rotationLocationNumber(bigBlindLocation + 1); i++)					//everyone draws their hole cards now from from the first player to the player with the big blind. Finishes the cycle of cycling through the players
		{
			int pokTabHolCard1 = pokerDeck.drawCard();											//get/draw the first hole card
			int pokTabHolCard2 = pokerDeck.drawCard(); 											//get/draw the second hole card
			
			(playerList.get(i)).setHoleCards(pokTabHolCard1, pokTabHolCard2); 					//set the hole cards of the current player	
		}
		
		//preflop round starts with the player to the left of the big blind
		System.out.println(playerList.get(rotationLocationNumber(bigBlindLocation + 1)).getPName() + ": will be the first player to take an action. Please enter anything to continue.");		//state who starts first
		input = new Scanner(System.in);															//for inputs
		String anythingPrompt = input.nextLine();												//let the user enter anything. anythingPrompt is never used; getting it just signifies that the next player wishes to advance the game

		playPreFlopRound();																		//play the pre-flop round
			
		//all subsequent rounds start with the player to the left of the dealer
		if(numOfNotFoldedPlayers == 1)															//if there is only one player left that hasn't folded, then they automatically win the round.
		{	
			distributePot();																	//as a result, distribute the pot
		}
		else																					//else if more rounds need to be played, play more rounds
		{
			//flop round: Here is the flop
			communityCard1 = pokerDeck.drawCard();												//draw the first community card
			communityCard2 = pokerDeck.drawCard();												//draw the second community card
			communityCard3 = pokerDeck.drawCard();												//draw the third community card
			
			//state the community cards of the flop			
			System.out.println("Flop: Community Cards: " + DeckAndCardWCJL.getCardName(communityCard1) + ", " + DeckAndCardWCJL.getCardName(communityCard2) + ", and " + DeckAndCardWCJL.getCardName(communityCard3));		
			createAndShowGUI(-1);																//update the graphics (no one is currently playing)
 
			System.out.println("Next player: Please enter anything to play your turn.");		//user prompt. Used to stop the program to let the user see the flop cards and then advance when they want	 
			input = new Scanner(System.in);														//for  inputs
			String anythingPrompt2 = input.nextLine();											//let the user enter anything. anythingPrompt2 is never used; getting it just signifies that the next player wishes to advance the game
			
			playRound();																		//play the flop round
			
			if(numOfNotFoldedPlayers == 1)														//if there is only one player left that hasn't folded, then they automatically win the round.
			{	
				distributePot(); 																//as a result, distribute the pot
			}
			else 																				//else if more rounds need to be played, play more rounds
			{
				communityCard4 = pokerDeck.drawCard();											//draw the turn card; the fourth community card
				
				//state the already drawn community cards
				System.out.println("Flop: Community Cards: " + DeckAndCardWCJL.getCardName(communityCard1) + ", " + DeckAndCardWCJL.getCardName(communityCard2) + ", and " + DeckAndCardWCJL.getCardName(communityCard3));	
				System.out.println("The Turn: " + DeckAndCardWCJL.getCardName(communityCard4) + "." );										//state the turn card				
				createAndShowGUI(-1);															//update the graphics (no one is currently playing)
 
				System.out.println("Next player: Please enter anything to play your turn.");	//user prompt. Used to stop the program to let the user see the flop cards and then advance when they want	
				input = new Scanner(System.in);	 												//for  inputs
				String anythingPrompt3 = input.nextLine(); 										//let the user enter anything. anythingPrompt3 is never used; getting it just signifies that the next player wishes to advance the game
					
				playRound();																	//play the turn round	
					
				if(numOfNotFoldedPlayers == 1)													//if there is only one player left that hasn't folded, then they automatically win the round.
				{	
					distributePot();															//as a result, distribute the pot
				}
				else 																			//else if more rounds need to be played, play more rounds
				{
					communityCard5 = pokerDeck.drawCard();										//draw the river; the fifth community card
					
					//state the already drawn community cards
					System.out.println("Flop: Community Cards: " + DeckAndCardWCJL.getCardName(communityCard1) + ", " + DeckAndCardWCJL.getCardName(communityCard2) + ", and " + DeckAndCardWCJL.getCardName(communityCard3));	
					System.out.println("The Turn: " + DeckAndCardWCJL.getCardName(communityCard4) + "." );									//state the turn card
					System.out.println("The River: " + DeckAndCardWCJL.getCardName(communityCard5) + "." );									//state the river card
					
					createAndShowGUI(-1);														//update the graphics (no one is currently playing)
 
					System.out.println("Next player: Please enter anything to play your turn.");//user prompt. Used to stop the program to let the user see the flop cards and then advance when they want
					input = new Scanner(System.in);	 											//for  inputs
					String anythingPrompt4 = input.nextLine(); 									//let the user enter anything. anythingPrompt4 is never used; getting it just signifies that the next player wishes to advance the game
 
					playRound();																//play the river round
				
					//with the river round concluded, now it is time for the showdown and the pot distribution
					for(int i = 0; i < playerList.size(); i++)									//turn order doesn't matter in this segment, which is evaluating everyone's hands
					{
						currentHandEv.emptyHand();												//empty the hand evaluator's cards
						currentHandEv.inputCommunityCards(communityCard1, communityCard2, communityCard3, communityCard4, communityCard5);	//input the community cards into the hand evaluator
						currentHandEv.inputHoleCards(playerList.get(i).getHoleCard1(), playerList.get(i).getHoleCard2());					//input the hole cards of the current player into the hand evalutaor
						currentHandEv.evaluateHand();											//evaluate the hand of the current player
						
						playerList.get(i).setpPrimaryHandScore(currentHandEv.givePlayerPHS());	//with the hand analyzed, give the current player their primary hand score
						playerList.get(i).setpSecondaryHandScore(currentHandEv.givePlayerSHS());//with the hand analyzed, give the current player their secondary hand score
					}

					//sort the players from lowest score to highest score
					PlayerWCJL itemToInsert; 													//the element value of the element to insert
					int j;																		//the index of the items compared to the element to insert for insertion.
					boolean keepGoing;															//boolean for whether or not to continue running the sort
																		
					for(int k = 1; k < playerList.size(); k++)									//for each k, take the element at k and insert it in between the elements to the left of it (or leave a[k] be in some cases).
					{
						itemToInsert = playerList.get(k);										//the element value used with comparisons to find where to insert a[k]. The first a[k] is a[1] (not a[0])
						j = k - 1;																//j starts off as the element to the left of a[k]
						keepGoing = true;														//keep going
						while((j >= 0) && keepGoing)											//while the element to insert is being compared to elements left of it:
						{
							if (itemToInsert.isLowerThanOtherPlayerScoreBool((playerList.get(j)).getpPrimaryHandScore(), (playerList.get(j)).getpSecondaryHandScore()) == true)	//if the item to insert is less than a[j], the pass needs to continue
							{
								playerList.set(j + 1, playerList.get(j)); 						//part of the shift right of elements needed for inserts. (makse the element to the right of a[j] = a [j]	
								j--;															//decrement j
								if(j == -1)														//if the element needs to be inserted when j == -1 (item to insert < a[0]):
									playerList.set(0, itemToInsert);							//insert the element
							}
							else 																//if itemToInsert >= a[j], the location that the element should be inserted is found: a[j+1]
							{
								keepGoing = false;												//the insertion pass will end
								playerList.set(j+1, itemToInsert);								//insert the element where it is greater than or equal to the element to the left of it
							}
						} 
					}
					
					for(int i = 0; i < playerList.size(); i++)									//for each player still playing
					{
						//state the current player's hole cards
						System.out.println(playerList.get(i).getPName() + "'s Hole Cards: " + DeckAndCardWCJL.getCardName(playerList.get(i).getHoleCard1()) + ", " + DeckAndCardWCJL.getCardName(playerList.get(i).getHoleCard2()));		
						System.out.println(playerList.get(i).getPName() + " has a " + currentHandEv.getHandString(playerList.get(i).getpPrimaryHandScore(), playerList.get(i).getpSecondaryHandScore()));	//state what type of hand the player has
					}
					createAndShowGUI(100);														//update the graphics (showdown setting is an int parameter of 100, far beyond the index 
																								//of a player. This also temporarily disables the blinds and makes all of cards face up)

					distributePot();															//distribute the pot			
				}
			}
		}
	}
	
	
	public void saveGame(File pokerTableFileToSave)										//store the Poker Table information in a file
	{
		for(int i = 0; i < playerList.size(); i++)										//for each player still alive
		{
			spectatorList.set(playerList.get(i).getOriginalIndex(), playerList.get(i));	//update the spectator list so that the players still alive are copied to the spectator list in their original positions
		}

		try 																			//try to write the information of the Poker table to the save file
		{
			FileOutputStream outClear = new FileOutputStream(pokerTableFileToSave);		//Output file stream for the File object. Does not append; it overwrites
			outClear.write(("").getBytes());											//write nothing
			outClear.close();															//close the stream
			
			FileOutputStream out = new FileOutputStream(pokerTableFileToSave);			//Output file stream for the File object. Does not append; it overwrites
			ObjectOutputStream writePokerTable = new ObjectOutputStream(out);			//creates a writing stream of the object stream out
 
			writePokerTable.writeInt(smallBlindValue);									//write the current value/price of the small blind
			writePokerTable.writeInt(bigBlindValue);									//write the current value/price of the big blind (constant)
			writePokerTable.writeInt(smallBlindLocation);								//write the current location of the small blind
			writePokerTable.writeInt(bigBlindLocation);									//write the current location of the big blind
			writePokerTable.writeInt(spectatorList.size());								//write the number of players that were playing in the beginning
				
			for(Object specPlayerStreamed: spectatorList)								//for each player in the list of spectators/players	
			{
				writePokerTable.writeObject(specPlayerStreamed);						//write that account to the file
			}	
			
			writePokerTable.close();													//close write stream

			System.out.println("Game Saved.");
		}
		catch(FileNotFoundException e)													//exception handling for the file not being found
		{
			System.out.println("File could not be found.");								//display the error message
			System.err.println("FileNotFoundException: " + e.getMessage());
 
		}
		catch(IOException e)															//exception handling for IOException 
		{
			System.out.println("Problem with input/output.");							//display the error message
			System.err.println("IOException: " + e.getMessage());
		}
	}
	
	
	public void printALotOFSpaces()								//use to let the current player at the computer switch off to let the next player play.
	{
		createAndShowGUI(-1);									//update the graphics (no one is currently playing)
 
		for(int i = 0; i < 50; i++)								//the information of the last player is hidden by printing many blank spaces (50 in this case) to shunt that information all the way upwards
		{
			System.out.println("");								//print a blank line
		}
		for(int i = 0; i < playerList.size(); i++)				//for each player in the game
		{
			System.out.println(playerList.get(i).toString());	//print their name and their chip count, in order to get a console snapshot of the current board state
		}
		
		System.out.println("Current player: Please look away from the screen, and then get the next player to sit down to play.");		//tell the current player at the computer switch off to let the next player play.
		System.out.println("Next player: Please enter anything to play your turn.");													//user prompt for anything to be entered to advance the game
		input = new Scanner(System.in);							//for inputs
		String anythingPromptMethod = input.nextLine();			//let the user enter anything. anythingPromptMethod is never used; getting it just signifies that the next player wishes to advance the game
		
		for(int i = 0; i < 50; i++)								//the user prompt is hidden by printing many blank spaces (50 in this case) to shunt that information all the way upwards
		{
			System.out.println("");								//print a blank line
		}	
	}

    private void createAndShowGUI(int currentPlayerIndexStage2)															//create and show the GUI, using the parameter of: the index of the player who is currently playing, 
    {																													//or a negative number for no player currently playing, or 100 for the showdown
    										
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);																//set what happens when the frame is closed. (the program will exit) 
      JLabel emptyLabel = new JLabel("");																				//new empty label
      emptyLabel.setPreferredSize(new Dimension(1280, 720));															//set size for frame
      frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);														//add the empty panel to the frame					
      frame.pack();																										//pack the frame; size the frame so that its contents are at their preferred size
      frame.setVisible(true);																							//make the frame visible
	        
      //Display the window.  
      JPanel tpanel = new JPanel();																						//new panel created
      tpanel.setBackground(new Color(128, 0, 128));																		//set background color
      frame.add(tpanel, BorderLayout.CENTER);																			//add panel to frame
      tpanel.setLayout(null);																							//absolute positioning will be used
		
      for(int i = 0; i < playerList.size(); i++)																		//for each player still playing
      {
    	  switch(playerList.get(i).getOriginalIndex())																	//user the players' original position for updating the graphics 
    	  {																												//positioning: 0 is the top left corner, then as the index approaches 12, the position moves clockwise
    	  //for each case, the (playerList.get(i).getOriginalIndex()) int is for player (playerList.get(i).getOriginalIndex() + 1)
    	  //the comments for each position are essentially the same, so refer back to this position's comments to explain the other positions' code
	    	  case(0):																									//position 0 is for player 1
	    		
	    		JLabel folded1 = new JLabel("");																		//new JLabel for this position's folded (red x) symbol
	    	  	folded1.setVerticalAlignment(SwingConstants.TOP);														//aligned to the top
				folded1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png"));	//the icon
				folded1.setBounds(0, 0, 200, 201);																		//the bounds of the image/label
				tpanel.add(folded1);																					//add the image/label to the panel
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded1.setVisible(false);																			//then do not show the folded (red x) symbol.
				}				
					
				JLabel player1 = new JLabel(playerList.get(i).getPName());												//player name label
				player1.setVerticalAlignment(SwingConstants.TOP);														//set vertical alignment
				player1.setHorizontalAlignment(SwingConstants.LEFT);													//set horizontal alignment
				player1.setForeground(Color.WHITE);																		//set the foreground colour
				player1.setBounds(0, 138, 256, 14);																		//set the positional bounds
				tpanel.add(player1);																					//add this label to the panel
	 
				JLabel chip1 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());							//label that shows the chip count of a player
				chip1.setVerticalAlignment(SwingConstants.TOP);															//vertical alignment
				chip1.setHorizontalAlignment(SwingConstants.LEFT);														//horizontal alignment
				chip1.setForeground(Color.WHITE);																		//set the foreground colour
				chip1.setBounds(0, 152, 256, 14);																		//set the positional bounds
				tpanel.add(chip1);																						//add this label to the panel
	 
				JLabel largeBet1 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());		//label that shows the largest bet of this hand of a player
				largeBet1.setVerticalAlignment(SwingConstants.TOP);														//vertical alignment
				largeBet1.setHorizontalAlignment(SwingConstants.LEFT);													//horizontal alignment
				largeBet1.setForeground(Color.WHITE);																	//set the foreground colour
				largeBet1.setBounds(0, 168, 256, 14);																	//set the positional bounds
				tpanel.add(largeBet1);																					//add this label to the panel
 
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind1 = new JLabel("");																//small blind button visual
					smallBlind1.setVerticalAlignment(SwingConstants.BOTTOM);											//set the vertical alignment
					smallBlind1.setBounds(185, 0, 46, 58);																//set the positional bounds
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{																									//therefore, show the dead small blind button instead of the normal small blind button
						smallBlind1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));			//get the icon from a file
					}
					else
					{
						smallBlind1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));		//get the icon from a file
					}		
					tpanel.add(smallBlind1);																			//add the small blind button, dead or alive, to the panel
				}
																														//add this picture to the panel
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind1 = new JLabel("");																	//big blind button visual
					bigBlind1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));					//get the icon from a file
					bigBlind1.setBounds(185, 58, 46, 47);																//set the positional bounds
					tpanel.add(bigBlind1);																				//add this picture to the panel
				}
				
				JLabel player1Card1 = new JLabel("");																	//label for the first card of the current player
				player1Card1.setBounds(0, 0, 93, 139);																	//positional image bounds
				tpanel.add(player1Card1);																				//add the card label to the panel
 
				JLabel player1Card2 = new JLabel("");																	//label for the second card of the current player
				player1Card2.setBounds(92, 0, 93, 139);																	//positional image bounds
				tpanel.add(player1Card2);																				//add the card label to the panel
				
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{																										//then the player's cards are face up
					player1Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player1Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying1 = new JLabel("");															//currently playing button
					currentlyPlaying1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));	//locate the art asset
					currentlyPlaying1.setBounds(185, 105, 46, 47);														//set the positional bounds
					tpanel.add(currentlyPlaying1);																		//add this label to the panel
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player1Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card face up
					player1Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else																									//else the player is not currently playing and the cards are face down.
				{
					player1Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));				//face down graphics for the cards
					player1Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;																									//break out of the switch
	
			case(1):																									//position 1 is for player 2
			
				JLabel folded2 = new JLabel("");
				folded2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded2.setVerticalAlignment(SwingConstants.TOP);
				folded2.setBounds(256, 0, 200, 201);
				tpanel.add(folded2);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded2.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
		 
				JLabel player2 = new JLabel(playerList.get(i).getPName());
				player2.setVerticalAlignment(SwingConstants.TOP);
				player2.setHorizontalAlignment(SwingConstants.LEFT);
				player2.setForeground(Color.WHITE);
				player2.setBounds(256, 138, 256, 14);
				tpanel.add(player2);
		 
				JLabel chip2 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip2.setVerticalAlignment(SwingConstants.TOP);
				chip2.setHorizontalAlignment(SwingConstants.LEFT);
				chip2.setForeground(Color.WHITE);
				chip2.setBounds(256, 152, 256, 14);
				tpanel.add(chip2);
		 
				JLabel largeBet2 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet2.setVerticalAlignment(SwingConstants.TOP);
				largeBet2.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet2.setForeground(Color.WHITE);
				largeBet2.setBounds(256, 168, 256, 14);
				tpanel.add(largeBet2);	 
				
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind2 = new JLabel("");
					smallBlind2.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind2.setBounds(441, 0, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{
						smallBlind2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));		 
					}					
					tpanel.add(smallBlind2);
				}
				
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind2 = new JLabel("");
					bigBlind2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind2.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind2.setBounds(441, 58, 46, 47);
					tpanel.add(bigBlind2);
				}	 
				
				JLabel player2Card1 = new JLabel("");
				player2Card1.setBounds(256, 0, 93, 139);
				tpanel.add(player2Card1);
		 
				JLabel player2Card2 = new JLabel("");
				player2Card2.setBounds(349, 0, 93, 139);
				tpanel.add(player2Card2);		
		 
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{																										//then the player's cards are face up
					player2Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player2Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying2 = new JLabel("");
					currentlyPlaying2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying2.setBounds(441, 105, 46, 47);
					tpanel.add(currentlyPlaying2);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player2Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player2Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player2Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));	 
					player2Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
				
			case(2):																									//position 2 is for player 3
				
				JLabel folded3 = new JLabel("");
				folded3.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded3.setVerticalAlignment(SwingConstants.TOP);
				folded3.setBounds(512, 0, 200, 201);
				tpanel.add(folded3);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded3.setVisible(false);																			//then do not show the folded (red x) symbol.	
				}
				
				JLabel player3 = new JLabel(playerList.get(i).getPName());
				player3.setVerticalAlignment(SwingConstants.TOP);
				player3.setHorizontalAlignment(SwingConstants.LEFT);
				player3.setForeground(Color.WHITE);
				player3.setBounds(512, 138, 256, 14);
				tpanel.add(player3);
 
				JLabel chip3 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip3.setVerticalAlignment(SwingConstants.TOP);
				chip3.setHorizontalAlignment(SwingConstants.LEFT);
				chip3.setForeground(Color.WHITE);
				chip3.setBounds(512, 152, 256, 14);
				tpanel.add(chip3);		
				
				JLabel largeBet3 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet3.setVerticalAlignment(SwingConstants.TOP);
				largeBet3.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet3.setForeground(Color.WHITE);
				largeBet3.setBounds(512, 168, 256, 14);
				tpanel.add(largeBet3);
				
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind3 = new JLabel("");
					smallBlind3.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind3.setBounds(697, 0, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{																									//therefore, show the dead small blind button instead of the normal small blind button
						smallBlind3.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));			//get the icon from a file
					}
					else
					{
						smallBlind3.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));		//get the icon from a file
					}
					tpanel.add(smallBlind3);
				}
				
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind3 = new JLabel("");
					bigBlind3.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind3.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind3.setBounds(697, 58, 46, 47);
					tpanel.add(bigBlind3);
				}
				
				JLabel player3Card1 = new JLabel("");
				player3Card1.setBounds(512, 0, 93, 139);
				tpanel.add(player3Card1);
 
				JLabel player3Card2 = new JLabel("");
				player3Card2.setBounds(605, 0, 93, 139);
				tpanel.add(player3Card2);
				
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player3Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player3Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying3 = new JLabel("");
					currentlyPlaying3.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying3.setBounds(697, 105, 46, 47);
					tpanel.add(currentlyPlaying3);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player3Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player3Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player3Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player3Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
				
			case(3):																									//position 3 is for player 4
				
				JLabel folded4 = new JLabel("");
				folded4.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded4.setVerticalAlignment(SwingConstants.TOP);
				folded4.setBounds(768, 0, 200, 201);
				tpanel.add(folded4);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded4.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player4 = new JLabel(playerList.get(i).getPName());
				player4.setVerticalAlignment(SwingConstants.TOP);
				player4.setHorizontalAlignment(SwingConstants.LEFT);
				player4.setForeground(Color.WHITE);
				player4.setBounds(768, 138, 256, 14);
				tpanel.add(player4);
	 
				JLabel chip4 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip4.setVerticalAlignment(SwingConstants.TOP);
				chip4.setHorizontalAlignment(SwingConstants.LEFT);
				chip4.setForeground(Color.WHITE);
				chip4.setBounds(768, 152, 256, 14);
				tpanel.add(chip4);
	 
				JLabel largeBet4 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey()	);
				largeBet4.setVerticalAlignment(SwingConstants.TOP);
				largeBet4.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet4.setForeground(Color.WHITE);
				largeBet4.setBounds(768, 168, 256, 14);
				tpanel.add(largeBet4);
			
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind4 = new JLabel("");
					smallBlind4.setVerticalAlignment(SwingConstants.BOTTOM);
					if(smallBlindValue == 0)
					{
						smallBlind4.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind4.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
					}
					smallBlind4.setBounds(953, 0, 46, 58);
					tpanel.add(smallBlind4);
				}
	
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind4 = new JLabel("");
					bigBlind4.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind4.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind4.setBounds(953, 58, 46, 47);
					tpanel.add(bigBlind4);
				}
		
				JLabel player4Card1 = new JLabel("");
				player4Card1.setBounds(768, 0, 93, 139);
				tpanel.add(player4Card1);
				
				JLabel player4Card2 = new JLabel("");
				player4Card2.setBounds(861, 0, 93, 139);
				tpanel.add(player4Card2);
			
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player4Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player4Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying4 = new JLabel("");
					currentlyPlaying4.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying4.setBounds(953, 105, 46, 47);
					tpanel.add(currentlyPlaying4);	
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player4Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player4Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player4Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));	 
					player4Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}	
				break;
				
			case(4):																									//position 4 is for player 5
				
				JLabel folded5 = new JLabel("");
				folded5.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded5.setVerticalAlignment(SwingConstants.TOP);
				folded5.setBounds(1024, 0, 200, 201);
				tpanel.add(folded5);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded5.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player5 = new JLabel(playerList.get(i).getPName());
				player5.setVerticalAlignment(SwingConstants.TOP);
				player5.setHorizontalAlignment(SwingConstants.LEFT);
				player5.setForeground(Color.WHITE);
				player5.setBounds(1024, 138, 256, 14);
				tpanel.add(player5);
	 
				JLabel chip5 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip5.setVerticalAlignment(SwingConstants.TOP);
				chip5.setHorizontalAlignment(SwingConstants.LEFT);
				chip5.setForeground(Color.WHITE);
				chip5.setBounds(1024, 152, 256, 14);
				tpanel.add(chip5);
	 
				JLabel largeBet5 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet5.setVerticalAlignment(SwingConstants.TOP);
				largeBet5.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet5.setForeground(Color.WHITE);
				largeBet5.setBounds(1024, 168, 256, 14);
				tpanel.add(largeBet5);
	 
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind5 = new JLabel("");
					smallBlind5.setVerticalAlignment(SwingConstants.BOTTOM);
	 
					if(smallBlindValue == 0)
					{
						smallBlind5.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind5.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
					}
					smallBlind5.setBounds(1209, 0, 46, 58);
					tpanel.add(smallBlind5);
				}
				
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind5 = new JLabel("");
					bigBlind5.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind5.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind5.setBounds(1209, 58, 46, 47);
					tpanel.add(bigBlind5);
				}
	 
				JLabel player5Card1 = new JLabel("");
				player5Card1.setBounds(1024, 0, 93, 139);
				tpanel.add(player5Card1);
	 
				JLabel player5Card2 = new JLabel("");
				player5Card2.setBounds(1117, 0, 93, 139);
				tpanel.add(player5Card2);
			
				if(i == currentPlayerIndexStage2)
				{
					player5Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));
					player5Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
			
					JLabel currentlyPlaying5 = new JLabel("");
					currentlyPlaying5.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying5.setBounds(1209, 105, 46, 47);
					tpanel.add(currentlyPlaying5);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player5Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player5Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player5Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player5Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
				
			case(5):																									//position 5 is for player 6
				
				JLabel folded6 = new JLabel("");
				folded6.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded6.setVerticalAlignment(SwingConstants.TOP);
				folded6.setBounds(1024, 240, 200, 201);
				tpanel.add(folded6);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded6.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player6 = new JLabel(playerList.get(i).getPName());
				player6.setVerticalAlignment(SwingConstants.TOP);
				player6.setHorizontalAlignment(SwingConstants.LEFT);
				player6.setForeground(Color.WHITE);
				player6.setBounds(1024, 378, 256, 14);
				tpanel.add(player6);
	 
				JLabel chip6 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip6.setVerticalAlignment(SwingConstants.TOP);
				chip6.setHorizontalAlignment(SwingConstants.LEFT);
				chip6.setForeground(Color.WHITE);
				chip6.setBounds(1024, 390, 256, 14);
				tpanel.add(chip6);
	 
				JLabel largeBet6 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet6.setVerticalAlignment(SwingConstants.TOP);
				largeBet6.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet6.setForeground(Color.WHITE);
				largeBet6.setBounds(1024, 403, 256, 14);
				tpanel.add(largeBet6);
 
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind6 = new JLabel("");
					smallBlind6.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind6.setBounds(1209, 240, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{
						smallBlind6.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind6.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
					}
					tpanel.add(smallBlind6);
				}
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind6 = new JLabel("");
					bigBlind6.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind6.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind6.setBounds(1209, 298, 46, 47);
					tpanel.add(bigBlind6);
				}
				
				JLabel player6Card1 = new JLabel("");
				player6Card1.setBounds(1024, 240, 93, 139);
				tpanel.add(player6Card1);
	 
				JLabel player6Card2 = new JLabel("");
				player6Card2.setBounds(1117, 240, 93, 139);
				tpanel.add(player6Card2);
			
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player6Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player6Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying6 = new JLabel("");
					currentlyPlaying6.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying6.setBounds(1209, 345, 46, 47);
					tpanel.add(currentlyPlaying6);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player6Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player6Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player6Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player6Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;	
			
			case(6):																									//position 6 is for player 7
				
				JLabel folded7 = new JLabel("");
				folded7.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded7.setVerticalAlignment(SwingConstants.TOP);
				folded7.setBounds(1024, 452, 200, 201);
				tpanel.add(folded7);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded7.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player7 = new JLabel(playerList.get(i).getPName());
				player7.setVerticalAlignment(SwingConstants.TOP);
				player7.setHorizontalAlignment(SwingConstants.LEFT);
				player7.setForeground(Color.WHITE);
				player7.setBounds(1024, 591, 256, 14);
				tpanel.add(player7);
	 
				JLabel chip7 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip7.setVerticalAlignment(SwingConstants.TOP);
				chip7.setHorizontalAlignment(SwingConstants.LEFT);
				chip7.setForeground(Color.WHITE);
				chip7.setBounds(1024, 605, 256, 14);
				tpanel.add(chip7);
	 
				JLabel largeBet7 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet7.setVerticalAlignment(SwingConstants.TOP);
				largeBet7.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet7.setForeground(Color.WHITE);
				largeBet7.setBounds(1024, 619, 256, 14);
				tpanel.add(largeBet7);
 
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind7 = new JLabel("");
					smallBlind7.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind7.setBounds(1209, 452, 46, 58);
					if(smallBlindValue == 0)
					{
						smallBlind7.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
		 
					}
					else
					{
						smallBlind7.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
		 
					}
		 
					tpanel.add(smallBlind7);
				}
				
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind7 = new JLabel("");
					bigBlind7.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind7.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind7.setBounds(1209, 510, 46, 47);
					tpanel.add(bigBlind7);
				}
				
				JLabel player7Card1 = new JLabel("");
				player7Card1.setBounds(1024, 452, 93, 139);
				tpanel.add(player7Card1);
	 
				JLabel player7Card2 = new JLabel("");
				player7Card2.setBounds(1117, 452, 93, 139);
				tpanel.add(player7Card2);
					
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player7Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player7Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying7 = new JLabel("");
					currentlyPlaying7.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying7.setBounds(1209, 557, 46, 47);
					tpanel.add(currentlyPlaying7);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player7Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player7Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player7Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player7Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
			
			case(7):																									//position 7 is for player 8
				
				JLabel folded8 = new JLabel("");
				folded8.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded8.setVerticalAlignment(SwingConstants.TOP);
				folded8.setBounds(768, 452, 200, 201);
				tpanel.add(folded8);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded8.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player8 = new JLabel(playerList.get(i).getPName());
				player8.setVerticalAlignment(SwingConstants.TOP);
				player8.setHorizontalAlignment(SwingConstants.LEFT);
				player8.setForeground(Color.WHITE);
				player8.setBounds(768, 591, 256, 14);
				tpanel.add(player8);
	 
				JLabel chip8 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip8.setVerticalAlignment(SwingConstants.TOP);
				chip8.setHorizontalAlignment(SwingConstants.LEFT);
				chip8.setForeground(Color.WHITE);
				chip8.setBounds(768, 605, 256, 14);
				tpanel.add(chip8);
	 
				JLabel largeBet8 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet8.setVerticalAlignment(SwingConstants.TOP);
				largeBet8.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet8.setForeground(Color.WHITE);
				largeBet8.setBounds(768, 619, 256, 14);
				tpanel.add(largeBet8);
				
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind8 = new JLabel("");
					smallBlind8.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind8.setBounds(953, 452, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{	
						smallBlind8.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png")); 
					}
					else
					{
						smallBlind8.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));	 
					}
					tpanel.add(smallBlind8);
				}
				
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind8 = new JLabel("");
					bigBlind8.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind8.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind8.setBounds(953, 510, 46, 47);
					tpanel.add(bigBlind8);
				}
				
				JLabel player8Card1 = new JLabel("");
				player8Card1.setBounds(768, 452, 93, 139);
				tpanel.add(player8Card1);
	 
				JLabel player8Card2 = new JLabel("");
				player8Card2.setBounds(861, 452, 93, 139);
				tpanel.add(player8Card2);
				
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player8Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player8Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying8 = new JLabel("");
					currentlyPlaying8.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying8.setBounds(953, 557, 46, 47);
					tpanel.add(currentlyPlaying8);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player8Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player8Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player8Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player8Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
				
			case(8):																									//position 8 is for player 9
				
				JLabel folded9 = new JLabel("");
				folded9.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded9.setVerticalAlignment(SwingConstants.TOP);
				folded9.setBounds(512, 452, 200, 201);
				tpanel.add(folded9);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded9.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player9 = new JLabel(playerList.get(i).getPName());
				player9.setVerticalAlignment(SwingConstants.TOP);
				player9.setHorizontalAlignment(SwingConstants.LEFT);
				player9.setForeground(Color.WHITE);
				player9.setBounds(512, 591, 256, 14);
				tpanel.add(player9);
	 
				JLabel chip9 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip9.setVerticalAlignment(SwingConstants.TOP);
				chip9.setHorizontalAlignment(SwingConstants.LEFT);
				chip9.setForeground(Color.WHITE);
				chip9.setBounds(512, 605, 256, 14);
				tpanel.add(chip9);
	 
				JLabel largeBet9 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet9.setVerticalAlignment(SwingConstants.TOP);
				largeBet9.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet9.setForeground(Color.WHITE);
				largeBet9.setBounds(512, 619, 256, 14);
				tpanel.add(largeBet9);
				
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind9 = new JLabel("");
					smallBlind9.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind9.setBounds(697, 452, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{	
						smallBlind9.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind9.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
					}
					tpanel.add(smallBlind9);
				}
			
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{ 
					JLabel bigBlind9 = new JLabel("");
					bigBlind9.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png")); 					//new JLabel for this position's folded (red x) symbol
					bigBlind9.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind9.setBounds(697, 510, 46, 47);
					tpanel.add(bigBlind9);
				}
				
				JLabel player9Card1 = new JLabel("");
				player9Card1.setBounds(512, 452, 93, 139);
				tpanel.add(player9Card1);
				
				JLabel player9Card2 = new JLabel("");
				player9Card2.setBounds(605, 452, 93, 139);
				tpanel.add(player9Card2);
				
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player9Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player9Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying9 = new JLabel("");
					currentlyPlaying9.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying9.setBounds(697, 557, 46, 47);
					tpanel.add(currentlyPlaying9);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player9Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player9Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player9Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));	 
					player9Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
			
			case(9):																									//position 9 is for player 10
			
				JLabel folded10 = new JLabel("");
				folded10.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded10.setVerticalAlignment(SwingConstants.TOP);
				folded10.setBounds(256, 452, 200, 201);
				tpanel.add(folded10);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded10.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player10 = new JLabel(playerList.get(i).getPName());
				player10.setVerticalAlignment(SwingConstants.TOP);
				player10.setHorizontalAlignment(SwingConstants.LEFT);
				player10.setForeground(Color.WHITE);
				player10.setBounds(256, 591, 256, 14);
				tpanel.add(player10);
	 
				JLabel chip10 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip10.setVerticalAlignment(SwingConstants.TOP);
				chip10.setHorizontalAlignment(SwingConstants.LEFT);
				chip10.setForeground(Color.WHITE);
				chip10.setBounds(256, 605, 256, 14);
				tpanel.add(chip10);
	 
				JLabel largeBet10 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet10.setVerticalAlignment(SwingConstants.TOP);
				largeBet10.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet10.setForeground(Color.WHITE);
				largeBet10.setBounds(256, 619, 256, 14);
				tpanel.add(largeBet10);
				
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind10 = new JLabel("");
					smallBlind10.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind10.setBounds(441, 452, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{
						smallBlind10.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind10.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
					}
					tpanel.add(smallBlind10);
				}
 
				//add this picture to the panel
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind10 = new JLabel("");
					bigBlind10.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind10.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind10.setBounds(441, 510, 46, 47);
					tpanel.add(bigBlind10);
				}

				JLabel player10Card1 = new JLabel("");
				player10Card1.setBounds(256, 452, 93, 139);
				tpanel.add(player10Card1);
				
				JLabel player10Card2 = new JLabel("");
				player10Card2.setBounds(349, 452, 93, 139);
				tpanel.add(player10Card2);
				
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player10Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player10Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying10 = new JLabel("");
					currentlyPlaying10.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying10.setBounds(441, 557, 46, 47);
					tpanel.add(currentlyPlaying10);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player10Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player10Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player10Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player10Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
			
			case(10):																									//position 10 is for player 11
				
				JLabel folded11 = new JLabel("");
				folded11.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded11.setVerticalAlignment(SwingConstants.TOP);
				folded11.setBounds(0, 452, 200, 201);
				tpanel.add(folded11);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded11.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
					
				JLabel player11 = new JLabel(playerList.get(i).getPName());
				player11.setVerticalAlignment(SwingConstants.TOP);
				player11.setHorizontalAlignment(SwingConstants.LEFT);
				player11.setForeground(Color.WHITE);
				player11.setBounds(0, 591, 256, 14);
				tpanel.add(player11);
	 
				JLabel chip11 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip11.setVerticalAlignment(SwingConstants.TOP);
				chip11.setHorizontalAlignment(SwingConstants.LEFT);
				chip11.setForeground(Color.WHITE);
				chip11.setBounds(0, 605, 256, 14);
				tpanel.add(chip11);
	 
				JLabel largeBet11 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet11.setVerticalAlignment(SwingConstants.TOP);
				largeBet11.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet11.setForeground(Color.WHITE);
				largeBet11.setBounds(0, 619, 256, 14);
				tpanel.add(largeBet11);
	 
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind11 = new JLabel("");
					smallBlind11.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind11.setBounds(185, 452, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{
						smallBlind11.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind11.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
					}
					tpanel.add(smallBlind11);
				}
				
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind11 = new JLabel("");
					bigBlind11.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind11.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind11.setBounds(185, 510, 46, 47);
					tpanel.add(bigBlind11);
				}	
				JLabel player11Card1 = new JLabel("");
				player11Card1.setBounds(0, 452, 93, 139);
				tpanel.add(player11Card1);
				
				JLabel player11Card2 = new JLabel("");
				player11Card2.setBounds(92, 452, 93, 139);
				tpanel.add(player11Card2);
				
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player11Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player11Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying11 = new JLabel("");
					currentlyPlaying11.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png"));
					currentlyPlaying11.setBounds(185, 557, 46, 47);
					tpanel.add(currentlyPlaying11);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player11Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player11Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player11Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player11Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
			
			case(11):																									//position 11 is for player 12
				
				JLabel folded12 = new JLabel("");
				folded12.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadXPokerWCJL.png")); 							//new JLabel for this position's folded (red x) symbol
				folded12.setVerticalAlignment(SwingConstants.TOP);
				folded12.setBounds(0, 240, 200, 201);
				tpanel.add(folded12);
				if(playerList.get(i).getFoldedBool() == false)															//if the player is not folded,
				{
					folded12.setVisible(false);																			//then do not show the folded (red x) symbol.
				}
				
				JLabel player12 = new JLabel(playerList.get(i).getPName());
				player12.setVerticalAlignment(SwingConstants.TOP);
				player12.setHorizontalAlignment(SwingConstants.LEFT);
				player12.setForeground(Color.WHITE);
				player12.setBounds(0, 378, 256, 14);
				tpanel.add(player12);
	 
				JLabel chip12 = new JLabel(" Chip Count: " + playerList.get(i).getChipCount());
				chip12.setVerticalAlignment(SwingConstants.TOP);
				chip12.setHorizontalAlignment(SwingConstants.LEFT);
				chip12.setForeground(Color.WHITE);
				chip12.setBounds(0, 392, 256, 14);
				tpanel.add(chip12);
	 
				JLabel largeBet12 = new JLabel(" Largest Bet This Hand: " + playerList.get(i).getPlayerPotKey());
				largeBet12.setVerticalAlignment(SwingConstants.TOP);
				largeBet12.setHorizontalAlignment(SwingConstants.LEFT);
				largeBet12.setForeground(Color.WHITE);
				largeBet12.setBounds(0, 408, 256, 14);
				tpanel.add(largeBet12);
	 
				if((i == smallBlindLocation)&&(currentPlayerIndexStage2 != 100))										//if this is the location of the small blind and it is not the showdown, then show the small blind button
				{
					JLabel smallBlind12 = new JLabel("");
					smallBlind12.setVerticalAlignment(SwingConstants.BOTTOM);
					smallBlind12.setBounds(185, 240, 46, 58);
					if(smallBlindValue == 0)																			//if the small blind costs nothing, then it is either a dead small blind or there effectively is no small blind
					{
						smallBlind12.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/DeadSmallBlindButton.png"));
					}
					else
					{
						smallBlind12.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/SmallBlindButtonPokerWCJL.png"));
					}
					tpanel.add(smallBlind12);
				}
			
				if((i == bigBlindLocation)&&(currentPlayerIndexStage2 != 100))											//if this is the location of the big blind and it is not the showdown, then show the big blind button
				{
					JLabel bigBlind12 = new JLabel("");
					bigBlind12.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/BigBlindButtonWCJL.png"));
					bigBlind12.setHorizontalAlignment(SwingConstants.LEFT);
					bigBlind12.setBounds(185, 298, 46, 47);
					tpanel.add(bigBlind12);
				}
				JLabel player12Card1 = new JLabel("");
				player12Card1.setBounds(0, 240, 93, 139);
				tpanel.add(player12Card1);
	 
				JLabel player12Card2 = new JLabel("");
				player12Card2.setBounds(92, 240, 93, 139);
				tpanel.add(player12Card2);
				
				if(i == currentPlayerIndexStage2)																		//if the currently playing player is the current player of this stage of the graphics creation
				{
					player12Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player12Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
					
					JLabel currentlyPlaying12 = new JLabel("");
					currentlyPlaying12.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/CurrentlyPlayingButtonWCJL.png")); 
					currentlyPlaying12.setBounds(185, 345, 46, 47);
					tpanel.add(currentlyPlaying12);
				}
				else if(currentPlayerIndexStage2 == 100)																//else if it is the showdown
				{
					player12Card1.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard1())));				//use the current player's hole cards as parameters to show the art for that card
					player12Card2.setIcon(new ImageIcon(showCardPath(playerList.get(i).getHoleCard2())));
				}
				else
				{
					player12Card1.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
					player12Card2.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png"));
				}
				break;
			}
		}
		tpanel.repaint();																								//repaint the panel in order to update the GUI visuals properly
		
		JPanel newTable = new JPanel();																					//new panel for the table for the centre section, where the community cards are
		newTable.setBackground(new Color(0, 128, 0));																	//set background color (green)
		newTable.setLayout(null);																						//set null layout
		newTable.setBounds(246, 201, 768, 240);																			//set panel bounds
		tpanel.add(newTable);																							//add to previous panel

		JLabel midCardBack = new JLabel("");																			//card for community card 1
		midCardBack.setBounds(10, 11, 93, 139);																			//set the positional bounds 
		newTable.add(midCardBack);																						//add the visual card to the center table
		midCardBack.setIcon(new ImageIcon(showCardPath(communityCard1)));												//set the icon to be face up or face down based on the card ID, which is negative (not a real card) if the card hasn't been drawn yet
 
		JLabel midCardBack2 = new JLabel(""); 																			//card for community card 2
		midCardBack2.setBounds(113, 11, 93, 139);																		//set the positional bounds 
		newTable.add(midCardBack2);																						//add the visual card to the center table
		midCardBack2.setIcon(new ImageIcon(showCardPath(communityCard2)));												//set the icon to be face up or face down based on the card ID, which is negative (not a real card) if the card hasn't been drawn yet
		
		JLabel midCardBack3 = new JLabel("");																			//card for community card 3
		midCardBack3.setBounds(216, 11, 93, 139);																		//set the positional bounds 
		newTable.add(midCardBack3);																						//add the visual card to the center table
		midCardBack3.setIcon(new ImageIcon(showCardPath(communityCard3)));												//set the icon to be face up or face down based on the card ID, which is negative (not a real card) if the card hasn't been drawn yet
		
		JLabel midCardBack4 = new JLabel("");																			//card for community card 4
		midCardBack4.setBounds(319, 11, 93, 139);																		//set the positional bounds 
		newTable.add(midCardBack4);																						//add the visual card to the center table
		midCardBack4.setIcon(new ImageIcon(showCardPath(communityCard4)));												//set the icon to be face up or face down based on the card ID, which is negative (not a real card) if the card hasn't been drawn yet
		
		JLabel midCardBack5 = new JLabel("");																			//card for community card 5
		midCardBack5.setBounds(422, 11, 93, 139);																		//set the positional bounds 
		newTable.add(midCardBack5);																						//add the visual card to the center table
		midCardBack5.setIcon(new ImageIcon(showCardPath(communityCard5)));												//set the icon to be face up or face down based on the card ID, which is negative (not a real card) if the card hasn't been drawn yet
 
		JLabel lblNewLabel = new JLabel("This is merely a visualization of the game. Use the console to play the game.");//label to display that game is played in console and that this is the GUI
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));														//set the font
		lblNewLabel.setBounds(10, 178, 500, 38);																		//set the positional bounds
		newTable.add(lblNewLabel);																						//add this new label to the table panel
		
		JLabel potLabel = new JLabel("Pot: " + getPotTotal());															//new label to display the pot
		potLabel.setFont(new Font("Tahoma", Font.BOLD, 14));															//set the fond
		potLabel.setBounds(630, 11, 132, 24);																			//set the positional bounds
		newTable.add(potLabel);																							//add this label to the table panel
 
		JLabel potPic = new JLabel("");																					//new empty label
		potPic.setVerticalAlignment(SwingConstants.TOP);																//set vertical alignment of label
		potPic.setIcon(new ImageIcon("src/pLANmarkPokerWCJL/PotPicturePokerWCJL.png"));									//set icon of label to specific image path
		potPic.setBounds(518, 11, 250, 79);																				//set size and location of label
		newTable.add(potPic);																							//add to the panel
 
		JLabel lblNewLabel_1 = new JLabel("Game uses the dead button");													//new labels to display information about the game
		lblNewLabel_1.setBounds(525, 90, 230, 20);																		//set the positional bounds
		newTable.add(lblNewLabel_1);																					//add this label to the table
 
		JLabel lblPlayerXHas = new JLabel("rule. Keep in mind: ");														//new labels to display information about the game
		lblPlayerXHas.setBounds(525, 111, 230, 20);																		//set the positional bounds
		newTable.add(lblPlayerXHas);																					//add this label to the table
 
		JLabel label = new JLabel("raise to does not mean");															//new labels to display information about the game
		label.setBounds(525, 136, 230, 20);																				//set the positional bounds
		newTable.add(label);																							//add this label to the table
 
		JLabel lblFromTheBig = new JLabel("raise by. Warning:");														//new labels to display information about the game
		lblFromTheBig.setBounds(525, 157, 230, 20);																		//set the positional bounds
		newTable.add(lblFromTheBig);																					//add this label to the table
 
		JLabel lblTheCurrentHighest = new JLabel("closing any of these");												//new labels to display information about the game
		lblTheCurrentHighest.setBounds(525, 190, 230, 20);																//set the positional bounds
		newTable.add(lblTheCurrentHighest);																				//add this label to the table
 
		JLabel lblX = new JLabel("windows will stop the game.");														//new labels to display information about the game
		lblX.setBounds(525, 211, 230, 20);																				//set the positional bounds
		newTable.add(lblX);																								//add this label to the table
		
		newTable.repaint();																								//repaint the new table to make sure that the GUI properly updates
    }
 
    //IMPORTANT: The art assets must be in the "pLANmarkPokerWCJL" folder, and that "pLANmarkPokerWCJL" folder must be in the "src" folder for this method and the GUI to display the graphics properly
    public String showCardPath(int cardIDPng)									//method to get the path of the art asset for a certain card ID parameter
	{
		switch(cardIDPng)														//switch for the card ID
		{
		  //case(card ID): return (path of the card art for the card that corresponds with the card ID);
			case(0): return ("src/pLANmarkPokerWCJL/0_2_of_Diamonds.png");		
			case(1): return ("src/pLANmarkPokerWCJL/1_3_of_Clubs.png");
			case(2): return ("src/pLANmarkPokerWCJL/2_4_of_Hearts.png");
			case(3): return ("src/pLANmarkPokerWCJL/3_5_of_Spades.png");
			case(4): return ("src/pLANmarkPokerWCJL/4_6_of_Diamonds.png");
			case(5): return ("src/pLANmarkPokerWCJL/5_7_of_Clubs.png");
			case(6): return ("src/pLANmarkPokerWCJL/6_8_of_Hearts.png");
			case(7): return ("src/pLANmarkPokerWCJL/7_9_of_Spades.png");
			case(8): return ("src/pLANmarkPokerWCJL/8_10_of_Diamonds.png");
			case(9): return ("src/pLANmarkPokerWCJL/9_J_of_Clubs.png");
			case(10): return ("src/pLANmarkPokerWCJL/10_Q_of_Hearts.png");
			case(11): return ("src/pLANmarkPokerWCJL/11_K_of_Spades.png");
			case(12): return ("src/pLANmarkPokerWCJL/12_A_of_Diamonds.png");
			case(13): return ("src/pLANmarkPokerWCJL/13_2_of_Clubs.png");
			case(14): return ("src/pLANmarkPokerWCJL/14_3_of_Hearts.png");
			case(15): return ("src/pLANmarkPokerWCJL/15_4_of_Spades.png");
			case(16): return ("src/pLANmarkPokerWCJL/16_5_of_Diamonds.png");
			case(17): return ("src/pLANmarkPokerWCJL/17_6_of_Clubs.png");
			case(18): return ("src/pLANmarkPokerWCJL/18_7_of_Hearts.png");
			case(19): return ("src/pLANmarkPokerWCJL/19_8_of_Spades.png");
			case(20): return ("src/pLANmarkPokerWCJL/20_9_of_Diamonds.png");
			case(21): return ("src/pLANmarkPokerWCJL/21_10_of_Clubs.png");
			case(22): return ("src/pLANmarkPokerWCJL/22_J_of_Hearts.png");
			case(23): return ("src/pLANmarkPokerWCJL/23_Q_of_Spades.png");
			case(24): return ("src/pLANmarkPokerWCJL/24_K_of_Diamonds.png");
			case(25): return ("src/pLANmarkPokerWCJL/25_A_of_Clubs.png");
			case(26): return ("src/pLANmarkPokerWCJL/26_2_of_Hearts.png");
			case(27): return ("src/pLANmarkPokerWCJL/27_3_of_Spades.png");
			case(28): return ("src/pLANmarkPokerWCJL/28_4_of_Diamonds.png");
			case(29): return ("src/pLANmarkPokerWCJL/29_5_of_Clubs.png");
			case(30): return ("src/pLANmarkPokerWCJL/30_6_of_Hearts.png");
			case(31): return ("src/pLANmarkPokerWCJL/31_7_of_Spades.png");
			case(32): return ("src/pLANmarkPokerWCJL/32_8_of_Diamonds.png");
			case(33): return ("src/pLANmarkPokerWCJL/33_9_of_Clubs.png");
			case(34): return ("src/pLANmarkPokerWCJL/34_10_of_Hearts.png");
			case(35): return ("src/pLANmarkPokerWCJL/35_J_of_Spades.png");
			case(36): return ("src/pLANmarkPokerWCJL/36_Q_of_Diamonds.png");
			case(37): return ("src/pLANmarkPokerWCJL/37_K_of_Clubs.png");
			case(38): return ("src/pLANmarkPokerWCJL/38_A_of_Hearts.png");
			case(39): return ("src/pLANmarkPokerWCJL/39_2_of_Spades.png");
			case(40): return ("src/pLANmarkPokerWCJL/40_3_of_Diamonds.png");
			case(41): return ("src/pLANmarkPokerWCJL/41_4_of_Clubs.png");
			case(42): return ("src/pLANmarkPokerWCJL/42_5_of_Hearts.png");
			case(43): return ("src/pLANmarkPokerWCJL/43_6_of_Spades.png");
			case(44): return ("src/pLANmarkPokerWCJL/44_7_of_Diamonds.png");
			case(45): return ("src/pLANmarkPokerWCJL/45_8_of_Clubs.png");
			case(46): return ("src/pLANmarkPokerWCJL/46_9_of_Hearts.png");
			case(47): return ("src/pLANmarkPokerWCJL/47_10_of_Spades.png");
			case(48): return ("src/pLANmarkPokerWCJL/48_J_of_Diamonds.png");
			case(49): return ("src/pLANmarkPokerWCJL/49_Q_of_Clubs.png");
			case(50): return ("src/pLANmarkPokerWCJL/50_K_of_Hearts.png");
			case(51): return ("src/pLANmarkPokerWCJL/51_A_of_Spades.png");
			default: return("src/pLANmarkPokerWCJL/POKER CARD BACK SMALL.png");	//default: the card is face down
		}
	}
}
