package pLANmarkPokerWCJL;

import java.io.*;													//for files
import java.util.Scanner;											//for user inputs
 
public class PlayerWCJL implements Serializable {					//player class that can be serialized for saving to a file
	
	private static Scanner input;									//for inputs
	
	//variables that are changed no times or once after being initialized
	private String playerName; 										//the player's name
	private boolean isEliminatedBool;								//whether or not the player is eliminated
	private final int originalIndex;								//the player's original index in the arrayList of players, which helps to define a player's location on the board

	//variables that can change many times after being initialized
	private int chipCount;											//how many chips the player has
	private int chipCountShadow;									//the player's chip count before a hand

	private int holeCard1;											//the player's first hole card
	private int holeCard2;											//the player's second hole card
	
	private int pPrimaryHandScore;									//the player's primary hand score
	private double pSecondaryHandScore;								//the player's secondary hand score
	
	private boolean foldedBool;										//whether or not the player has folded
	private boolean isAllInBool;									//whether or not the player is all in
	
	private int playerPotKey;										//the player's pot key/ highest bet made currently in a hand
	private int previousPlayerPotKey;								//the player's previous highest bet made currently in a hand
	
	public PlayerWCJL(int chipCounti, String uPName, int ogIndex)	//constructor, using the user-defined parameter of the initial chip count, the user-defined 
	{																//player name, and the program-defined parameter of the original index of the player						
		playerName = uPName;										//set the player's name according to the parameter
		chipCount = chipCounti;										//set the initial chip count according to the parameter
		chipCountShadow = chipCount;								//set the initial chip count shadow to be the initial chip count
		originalIndex = ogIndex;									//set the player's initial index according to the parameter
		
		previousPlayerPotKey = 0;									//the player starts off not having made any bets yet
		pPrimaryHandScore = 0;										//the player starts with no real hand score as of yet
		pSecondaryHandScore = 0;									//the player starts with no real secondary hand score as of yet
		foldedBool = false;											//the player starts off not being folded yet
		isAllInBool = false;										//the player starts off not being all in yet
		isEliminatedBool = false;									//the player starts off not being eliminated yet
	}

	public void resetPlayer()										//ready/ reset the player for a new hand/ major round of the game
	{
		chipCountShadow = chipCount;								//reset the chip count shadow to be the new chip count
		playerPotKey = 0;											//reset the player's highest bet of this hand to be no bet
		previousPlayerPotKey = 0;									//reset the player's previous bet of this hand to be no bet
		
		holeCard1 = -1;												//the player doesn't have their hole cards yet (-1 is not a real card)
		holeCard2 = -1;												//the player doesn't have their hole cards yet (-1 is not a real card)
		pPrimaryHandScore = 0;										//reset the player's PHS, as they have no cards or hand yet
		pSecondaryHandScore = 0;									//reset the player's SHS, as they have no cards or hand yet
		
		foldedBool = false;											//reset the player's folded status, as they have not folded yet
		isAllInBool = false; 										//reset the player's all in status, as they have not went all-in yet
	}
	
	public String getPName()										//return the name of this player
	{		
		return playerName;
	}
	
	public int getChipCount() 										//return the chip count of this player
	{		
		return chipCount;
	}
	
	public int getChipCountShadow()									//return the chip count shadow of this player
	{
		return chipCountShadow;
	}
	
	public void setChipCountShadow() 								//set the chip count shadow of this player to be the current chip count
	{		
		chipCountShadow = chipCount;
	}
 
	public int getOriginalIndex()									//return the original index of this player
	{
		return originalIndex;
	}
	
	public boolean getFoldedBool()									//return whether or not this player is currently folded
	{
		return foldedBool;											//true if folded, false if not
	}
	
	public void setHoleCards(int usgHCard1, int usgHCard2)			//set the hole cards of this player
	{
		holeCard1 = usgHCard1;										//set the first hole card
		holeCard2 = usgHCard2;										//set the second hold card
	}
	
	public int getHoleCard1()										//return the first hole card of this player
	{
		return holeCard1;
	}
	public int getHoleCard2()										//return the second hole card of this player
	{
		return holeCard2;
	}
	
	public int getpPrimaryHandScore() 								//return the primary hand score of this player
	{		
		return pPrimaryHandScore;
	}
		
	public double getpSecondaryHandScore()							//return the secondary hand score of this player
	{
		return pSecondaryHandScore;
	}
	
	public void setpPrimaryHandScore(int gspPHS)					//set the primary hand score of this player (analyzed and given by elsewhere in the program)
	{
		pPrimaryHandScore = gspPHS;
	}
	
	public void setpSecondaryHandScore(int gspSHS)					//set the secondary hand score of this player (analyzed and given by elsewhere in the program)
	{
		pSecondaryHandScore = gspSHS;
	}
	
	public boolean getAllInOrNotBool()								//return whether or not a player is all-in
	{
		return isAllInBool;
	}
	
	public void collectPot(int chipsWon)							//collect chips from the pot, with the parameter of how many chips the player is getting
	{
		chipCount += chipsWon;										//increment the chip count
	}
	
	public int getPlayerPotKey()									//return the player's current pot key
	{
		return playerPotKey;
	}
	
	public int getPreviousPlayerPotKey()							//return the previous player pot key
	{
		return previousPlayerPotKey;
	}
	
	public void eliminatePlayer()									//try to eliminate the player if the player needs to be eliminated
	{
		if(chipCount <= 0)											//if the player has no chips left
		{
			isEliminatedBool = true;								//set that it is true that the player is eliminated.
		}
	}
	
	public boolean getIsEliminatedBool()							//return whether or not the player is eliminated.
	{
		return isEliminatedBool;
	}
	
	public String toString()										 //display the player's name and chip count
	{						
		return(playerName + "'s current chip count is: $" + chipCount);
	}
	
	public boolean isLowerThanOtherPlayerScoreBool(int otherPlayerPHS, double otherPlayerSHS)	//compare two player's hand scores
	{																							//true if the other player's hand scores are higher than the current player's
		if(otherPlayerPHS > pPrimaryHandScore)													//if the other player has a better hand type
		{	
			return true;																		//return true
		}
		else if((otherPlayerPHS == pPrimaryHandScore)&&(otherPlayerSHS > pSecondaryHandScore))	//else if the other player has a better hand of the same type
		{
			return true;																		//return true
		}
		else																					//else return false, the current player has a better hand score
		{
			return false;
		}
	}

	//methods that are player options/ interactions for playing the game: fold, bet, raise, call, pay the blinds. Checking needs no method because checking doesn't really do anything
	public void fold()														//player action: fold
	{
		foldedBool = true;													//set the bool to true, the player is now folded
	}
		
	public int bet(int highestBetBound)										//method to bet. Only available if the pot hasn't been opened yet (if there are no blinds/ blinds of 0 chips.)
	{																		//uses the parameter of the highest possible bet for the table
		int betAmount;														//how much the player bets
		int betUpperBound;													//the maximum bet that the player can make
		if(chipCountShadow > highestBetBound)								//if the player's chip count before betting is greater than the highest possible bet for the table
		{
			betUpperBound = highestBetBound;								//the maximum bet the player can make is the highest bet of the table
		}
		else																//else if the player's chip count before betting is less than the highest possible bet of the table
		{
			betUpperBound = chipCountShadow;								//the maximum bet the player can make is all of their chips/ going all-in
		}
		
		System.out.println("How much do you bet?");							//player prompt
		betAmount = acquireUserIntInput(0, betUpperBound);					//the player can enter an input in the range of [0, highestBetBound] as their bet
															
		chipCount = chipCountShadow - betAmount;							//update the current chip count as the player's chip count before betting for this hand - how much the player bet
		if(chipCount == 0)													//if the player went all-in/ has no chips after betting
		{
			System.out.println(playerName + " has went all-in with " + betAmount + " chips.");	//state that the player has went in and how much they bet
			isAllInBool = true;												//the player is now all in
		}
		else																//else if the player still has some chips left
		{
			System.out.println(playerName + " has bet " + betAmount + " chips.");	//state how much that the player has bet
		}
		
		previousPlayerPotKey = playerPotKey;								//update the player's previous bet as the current bet, which is right about to be changed
		playerPotKey = betAmount;											//update the player's current bet as the bet they just made

		return betAmount;													//return how much the player bet
	}

	public int raise(int currentHighestBetRN, int highestRaisePossible)		//requires the game table to only offer this option if the player can in fact raise
	{																		//uses the parameters of the current highest bet to surpass and the highest possible bet for the whole board
		int raiseUpperBound;												//the maximum number of chips that the player can raise to
		if(chipCountShadow > highestRaisePossible)							//if the player has more chips than the highest raise possible (the second highest chip count)
		{
			raiseUpperBound = highestRaisePossible;							//set the upper limit of the raise amount to the highest bet possible
		}
		else																//else if the player's chip count before betting is less than the highest possible bet for the table			
		{
			raiseUpperBound = chipCountShadow;								//the highest that the player can raise to is the amount of chips the player had for the hand
		}
		
		int raiseAmount = 0;												//how much the player has raised to
		System.out.println("What do you raise to? (note: raise to, not raise by)");	//user prompt
		raiseAmount = acquireUserIntInput(currentHighestBetRN + 1, raiseUpperBound);//get the raise as an int in the range of [the highest current bet + 1, the max bet possible]
																							
		previousPlayerPotKey = playerPotKey; 								//update the player's previous key/ previous bet
		playerPotKey = raiseAmount;											//set the player's current highest bet to what they just raised to
		chipCount = chipCountShadow - raiseAmount;							//subtract how much the player raised to from the chips the player had before betting to get the current chip count

		if(chipCount == 0)													//if the player went all-in with their raise
		{
			System.out.println(playerName + " has went all-in with " + raiseAmount + " chips.");	//state that the player has went all in and state how much the player raised to
			isAllInBool = true;												//the player is all-in
		}	
		else																//else if the player is not all in
		{
			System.out.println(playerName + " has raised to " + raiseAmount + " chips.");	//state how much the player raised to
		}
		
		return raiseAmount;													//return how much the player raised to
	}
	
	public int call(int currentBettoCall)									//the player calls the current bet. If the player cannot fully call, 
	{																		//then they go all in with whatever chips they have
		int chipsManagedToCall = 0;											//how many chips the player managed to call

		if(chipCountShadow - currentBettoCall <= 0)							//if the player went all in with their call or could not match the full bet
		{
			System.out.println(playerName + " has went all-in.");			//state that the player has went all-in
			
			chipsManagedToCall = chipCountShadow;							//set the amount of chips that the player puts into the pot as all of the player's chips
			
			previousPlayerPotKey = playerPotKey;							//update the player's previous bet as the current bet, which is right about to be changed
			playerPotKey = chipsManagedToCall;								//update the player's current bet
			
			chipCount = 0;													//set the player's chip count to 0, as they are now all in
			isAllInBool = true;												//the player is now all in
		}
		else 																//else if the player still has chips left over after calling
		{
			chipsManagedToCall = currentBettoCall;							//the player will call the bet
			previousPlayerPotKey = playerPotKey;							//update the player's previous bet as the current bet, which is right about to be changed
			playerPotKey = chipsManagedToCall;								//update the player's current bet
			chipCount = chipCountShadow - chipsManagedToCall;				//update the player's chip count as the chips the player had before betting - the chips that the player called
		}

		return chipsManagedToCall;											//return the amount of chips that the player managed to call with
	}
	
	public int subtractBlind(int blindValue, int blindSetting)				//make the player pay a blind, using the parameters of how much the blind is worth 
	{																		//and what kind of blind it is. blindSetting 1 = small blind, blindSetting 2 = big blind
		int chipsManagedToBlind = blindValue;								//set the amount of chips that player pays to equal the blind value
		
		if((chipCount - blindValue) <= 0)									//if the player would go all in by paying the blind
		{
			chipsManagedToBlind = chipCount;								//set the amount of chips that the player pays as the player's chip count
			System.out.println(playerName + " has went all-in.");			//state the player has went all in
			isAllInBool = true;												//the player is now all in
		}
		
		chipCount -= chipsManagedToBlind;									//subtract the chips from the blind from the chip count
		previousPlayerPotKey = playerPotKey;								//update the player's previous bet as the current bet, which is right about to be changed
 
		playerPotKey = chipCountShadow - chipCount;							//update the player's current bet as how much they had before paying the blind - how much they payed from the blind
		
		if(blindSetting == 1)												//if the player paid a small blind
		{
			System.out.println(playerName + " has put in " + chipsManagedToBlind + " chips from the small blind.");	//state that player paid the small blind
		}
		else																//if the player paid a big blind
		{
			System.out.println(playerName + " has put in " + chipsManagedToBlind + " chips from the big blind.");	//state that the player paid the big blind
		}
		
		return chipsManagedToBlind;											//return how much the player managed to pay from the blind
	}
	//end of the methods for a player's decisions in playing the game
 
	public static boolean charButton(String charStringToMatch)				//return true if the player enters in a string where the first character  
	{																		//is the same as the parameter, which needs to be a one-character string
		input = new Scanner(System.in);										//for console inputs
		String userStringAction;											//the user's input as a string
		userStringAction = input.nextLine();								//get the user's input as a string
		
		if(userStringAction.length() > 0)									//if the user actually entered in something
		{
			if((userStringAction.substring(0,1).toUpperCase().equals(charStringToMatch.toUpperCase()))&&(userStringAction.length() == 1))	//if the first character of the input string matches the parameter 
			{																																//to match and the string is only one character in length
																																			//case doesn't matter
				return true;												//return true
			}
			else															//else if the user's input's first character doesn't match
			{
				return false;												//return false
			}
		}
		else																//else if the user's input string is basically nothing 
		{																	//like if they just entered nothing/ pressed the enter key
			return false;													//return false, they didn't match the character
		}
	}
 
	public static int acquireUserIntInput(int bottomRange, int topRange)	//method for getting and returning user int input in the range of [bottomRange,topRange]
	{	
		while(true)															//repeat until the user inputs a proper input (an int in the defined parameters)
		{
			try																//try user inputs and catch improper inputs (not ints)
			{
				input = new Scanner(System.in);								//for inputs
				int userAction;												//user's input						
				userAction = input.nextInt();								//getting the user's input 
				
				if((userAction >= bottomRange)&&(userAction <= topRange))	//if the user's int is such that bottomRange <= user's int <= topRange
				{
					return userAction;										//return the user's int
				}
				else														//else if the user entered an int that is outside of the defined range
				{
					System.out.println("Error: improper input.");			//print out an error message
					System.out.println("Please enter a proper input.");
					System.out.println("");
				}
			}
			catch(java.util.InputMismatchException z)						//catch improper inputs that aren't ints
			{
				System.out.println("Error: improper input.");				//print out an error message
				System.out.println("Please enter a proper input.");
				System.out.println("");
			}
		}
	}
}