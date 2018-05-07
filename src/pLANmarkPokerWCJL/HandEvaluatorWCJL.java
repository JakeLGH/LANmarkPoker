package pLANmarkPokerWCJL;

public class HandEvaluatorWCJL {				//class that evaluates hands by giving a primary hand score and secondary hand score
 
	private int theHandArray[];					//the array that holds the 7 cards of the community cards and the hole cards
	private int primaryHandScore; 				//primary hand score of the hand (1-9)
	private int secondaryHandScore;				//secondary hand score of the hand (specific to each type of hand)
	private boolean handEvaluatedBool;			//whether or not the 7 cards have been analyzed/ have a hand found
	
	public HandEvaluatorWCJL() 					//constructor
	{	
		theHandArray = new int[7];				//7 elements in the hand array
		primaryHandScore = 0;					//default PHS = 0
		secondaryHandScore = 0;					//default SHS = 0
		handEvaluatedBool = false;				//hand hasn't been analyzed yet
		
		for(int i = 0; i < 7; i++)				//each value in the array is -1 (not a real card)
		{
			theHandArray[i] = -1;
		}
	}
	
	public int givePlayerPHS(){					//returns the current PHS. Needs the hand to be analyzed for a correct score
		return primaryHandScore;
	}
	
	public int givePlayerSHS()					//returns the current SHS. Needs the hand to be analyzed for a correct score
	{	
		return secondaryHandScore;
	}
	
	public void emptyHand()						//empty the hand so that the evaluator can analyze a new set of 7 cards
	{
		handEvaluatedBool = false;				//the new set of cards have not been analyzed
 
		primaryHandScore = 0;					//PHS starts at 0
		secondaryHandScore = 0;					//SHS starts at 0
		
		for(int i = 0; i < 7; i++)				//each value in the array is -1 (not a real card)
		{
			theHandArray[i] = -1;
		}
	}
	
	public void inputHoleCards(int HC1, int HC2)										//input the hole cards into the array
	{
		theHandArray[0] = HC1;
		theHandArray[1] = HC2;
	}
	
	public void inputCommunityCards(int CC1, int CC2, int CC3, int CC4, int CC5)		//input the community cards into the array
	{
		theHandArray[2] = CC1;
		theHandArray[3] = CC2;
		theHandArray[4] = CC3;
		theHandArray[5] = CC4;
		theHandArray[6] = CC5;
	}
	
	
	public String getHandString(int externalPHS, double externalSHS)		//get a name string from a set of someone else's analyzed PHS and SHS 
	{																		//not necessarily currently held in handEvaluator object
		switch(externalPHS)													//switch on the PHS for type of hand
		{
			case(9):														//9 = straight flush
				if(externalSHS == 14)										//straight flush with SHS of 14 (A as end card) = royal flush
				{
					return "Royal Flush";
				}
				else														//else it's a normal straight flush
				{
					return "Straight Flush";
				}
			case(8):								
				return "Full House";										//8 = full house
			case(7):
				return "Four of a Kind";									//7 = 4 of a kind
			case(6):
				return "Full House";										//6 = full house
			case(5):
				return "Flush";												//5 = flush
			case(4):
				return "Straight";											//4 = straight
			case(3):
				return "Three of a Kind";									//3 = 3 of a kind
			case(2):
				return "Two Pair";											//2 = 2 Pair
			case(1):														//1 = 1 pair
				return ("Pair of " + DeckAndCardWCJL.getCardNameWithoutSuit((int)(externalSHS/1000000 - 2 )) + "s");	
				//the highest 2 digits of the SHS correspond with the numeric value of the pair of cards
			case(0):
				return "High Card";											//0 = high card
			default:
				return "";													//default = "", should not ever happen
		}
	}
	
	public String getHandString()						//get the name string from the class's current PHS and SHS
	{
		switch(primaryHandScore)						//switch on the PHS for type of hand
		{
			case(9):									//9 = straight flush
				if(secondaryHandScore == 14)			//straight flush with SHS of 14 (A as end card) = royal flush
				{
					return "Royal Flush";
				}
				else									//else it's a normal straight flush
				{
					return "Straight Flush";
				}
			case(8):
				return "Full House";					//8 = full house
			case(7):
				return "Four of a Kind";				//7 = 4 of a kind
			case(6):
				return "Full House";					//6 = full house
			case(5):
				return "Flush";							//5 = flush
			case(4):
				return "Straight";						//4 = straight
			case(3):
				return "Three of a Kind";				//3 = 3 of a kind
			case(2):
				return "Two of a Kind";					//2 = 2 Pair
			case(1):									//1 = 1 pair
				return ("Pair of " + DeckAndCardWCJL.getCardNameWithoutSuit((int)((secondaryHandScore/1000000) - 2 )) + "s");	
				//the highest 2 digits of the SHS correspond with the numeric value of the pair of cards
			case(0):
				return "High Card";						//0 = high card
			default:
				return "";								//default = "", should not ever happen
		}
	}
	
	public void evaluateHand()								//evaluate the hand algorithm, using other methods for each stage of the algorithm
	{														//after this method is concluded, the hand evaluator object will hold the analyzed PHS and SHS of the hand
		sortHandByNum(theHandArray);						//sort the hand from 2 to K to A (ascending order)

		if(handEvaluatedBool == false)						//if the hand type hasn't been found	
		{
			flushChecker(true);								//check for a straight flush
		}
				
		if(handEvaluatedBool == false)						//if the hand type still hasn't been found		
		{
			multipleChecker(7);								//check for a four of a kind
		}
		if(handEvaluatedBool == false)						//if the hand type still hasn't been found		
		{
			multipleChecker(6);								//check for a full house
		}
		
		if(handEvaluatedBool == false)						//if the hand type still hasn't been found			
		{
			flushChecker(false);							//check for a flush
		}
		
		if(handEvaluatedBool == false)						//if the hand type still hasn't been found						
		{
			straightChecker(theHandArray, false);			//check for a straight
		}
		
		if(handEvaluatedBool == false)						//if the hand type still hasn't been found						
		{
			multipleChecker(3);								//check for a three of a kind
		}
		
		if(handEvaluatedBool == false)						//if the hand type still hasn't been found		
		{
			multipleChecker(2);								//check for a two pair
 
		}
		
		if(handEvaluatedBool == false)						//if the hand type still hasn't been found			
		{
			multipleChecker(1);								//check for a pair
 
		}

		if(handEvaluatedBool == false) 						//if the hand type still hasn't been found		
		{
			
			primaryHandScore = 0;							//the hand type is a high card hand
			secondaryHandScoreAnalyzer(theHandArray,5);		//the SHS uses the SHSAnalyzer method, using 5 cards in the hand.
			handEvaluatedBool = true;						//the type of hand has been found.
		}
		
	}
	
	private void sortHandByNum(int a[])						//sort the hand array in ascending order numerically (2 to King to Ace)
	{
		int itemToInsert, j;								//the element value of the element to insert, the index of the items compared to the element to insert for insertion.
		boolean keepGoing;									//boolean for whether or not to continue running the sort
															//On kth pass, insert item k into its correct position among the first k items in the array
		for(int k = 1; k < a.length; k++)					//Go backwards through the list, looking for the slot to insert a[k]
		{
			itemToInsert = a[k];							//the element value used with comparisons to find where to insert a[k]. The first a[k] is a[1] (not a[0])
			j = k - 1;										//j starts off as the element to the left of a[k]
			keepGoing = true;								//keep going
			while((j >= 0) && keepGoing)					//while the element to insert is being compared to elements left of it:
			{
				if (DeckAndCardWCJL.getCardInt(itemToInsert) < DeckAndCardWCJL.getCardInt(a[j]))	//if the item to insert is less than a[j], the pass needs to continue
				{
					a[j + 1] = a[j]; 						//part of the shift right of elements needed for inserts. (makse the element to the right of a[j] = a [j]	
					j--;									//decrement j
					if(j == -1)								//special case for inserting an item at [0]
						a[0] = itemToInsert;				//insert the element to insert at [0]
				}
				else 										//Upon leaving loop, j + 1 is the index where itemToInsert belongs
				{
				keepGoing = false;
				a[j + 1] = itemToInsert;					//insert the element where it is greater than or equal to the element to the left of it
				}
			} 
		}	
		
	}
	
	private void multipleChecker(int typeOfMulti)									//check for a hand that involves multiples of a card (numerically), using the type of hand being checked for parameter
	{																				//UU means the digits that are variable, 00 are always 00 digits for that segment of the SHS analysis
		int theNumCounterArray[] = new int[13];										//counter array that records the quantity of each type of card numerically (ID = number, element value = quantity) 
		
		for(int p = 0; p < 13; p++)													//start off the counter array with 0 counts of each card
		{
			theNumCounterArray[p] = 0;
		}
		
		for(int s = 0; s < 7; s++)													//for each card in the hand array
		{
			int numAtCardAtHand1 = DeckAndCardWCJL.getCardInt(theHandArray[s]);		//get the numeric value of the current card
			theNumCounterArray[numAtCardAtHand1] ++;								//increment the quantity of the card at the numeric value of the card
		}
		
		int maxQuantityOfNumCards = 0;												//the most occurring card's number of occurrences
		int maxQuantityOfNumCardsID = 0;											//the most occurring card's ID
		int secondHighestQuantityOfNumCards = 0;									//the second most occurring card's number of occurrences
		int secondHighestQuantityOfNumCardsID = 0;									//the second most occurring card's ID
 
		for(int t = 0; t < 13; t++)													//for each element in the counterArray
		{
			if(theNumCounterArray[t] > maxQuantityOfNumCards)						//if the quantity is greater than the current most often occurring quantity
			{
				maxQuantityOfNumCards = theNumCounterArray[t];						//update the maxQuantityOfNumCards
				maxQuantityOfNumCardsID = t;										//update the maxQuantityOfNumCardsID
			}
		}
		
		for(int q = 0; q < 13; q++)													//after finding the maxQuantityOfNumCards and maxQuantityOfNumCardsID, 										
		{																			//find the second most occurring card's number of occurrences and its ID
			if((theNumCounterArray[q] > secondHighestQuantityOfNumCards)&&(q != maxQuantityOfNumCardsID))	//if the quantity is greater than the current secondHighestQuantityOfNumCards
			{																								//and that quantity is not maxQuantityOfNumCards
				secondHighestQuantityOfNumCards = theNumCounterArray[q];			//update the secondHighestQuantityOfNumCards
				secondHighestQuantityOfNumCardsID = q;								//update the secondHighestQuantityOfNumCardsID
			}
		}
		
		//--------------------------------------------------------------------------------------------------//		
		
		if((maxQuantityOfNumCards == 4)&&(typeOfMulti == 7))						//if there is a four of a kind and the algorithm is currently checking for a four of a kind
		{
			primaryHandScore = 7;													//PHS = 7
			
			secondaryHandScore += (maxQuantityOfNumCardsID + 2) * 100;				//SHS: 4 of a kind = UU,00 digits
																																	
			int fourOfAKindKicker = 0;												//getting kicker value for SHS
			
			for(int f = 12; f >= 0; f--)											//finding the greatest kicker value
			{
				if((theNumCounterArray[f] > 0)&&(maxQuantityOfNumCardsID != f))		//if there is at least one card and that card is not the 4 of a kind card,
				{																	//with 4 of a kind, it doesn't matter if there is also 3 of a kind or 2 of a kind elsewhere
					fourOfAKindKicker = f;											//the kicker has been found
					break;															//break out of the for loop early
				}
			}
			
			secondaryHandScore += fourOfAKindKicker + 2;		 					//SHS: kicker = UU digits
			handEvaluatedBool = true;												//the hand type has been found
		}
		
		//--------------------------------------------------------------------------------------------------//	
		
		if((maxQuantityOfNumCards == 3)&&(secondHighestQuantityOfNumCards == 2)&&(typeOfMulti == 6)) //if there is a full house and the algorithm is currently checking for a full house
		{
			primaryHandScore = 6;													//PHS = 6							
			secondaryHandScore += (maxQuantityOfNumCardsID + 2) * 100;				//SHS: three of a kind = UU,00 digits
			secondaryHandScore += secondHighestQuantityOfNumCardsID + 2;			//SHS: two of a kid = 	    UU digits
			handEvaluatedBool = true;												//the hand type has been found
		}
		
		//--------------------------------------------------------------------------------------------------//	
		
		if((maxQuantityOfNumCards == 3)&&(typeOfMulti == 3))						//if there is a three of a kind and the algorithm is currently checking for a three of a kind
		{
			primaryHandScore = 3;													//PHS = 3
			secondaryHandScore += (maxQuantityOfNumCardsID + 2) * 10000;			//SHS: three of a kind = UU,00,00 digits
			
			int threeOfAKindHighCardHigher = 0;										//the higher kicker 
			int threeOfAKindHighCardLower = 0;										//the lower kicker
			
			for(int thh = 12; thh >= 0; thh--)										//finding the higher kicker
			{
				if((theNumCounterArray[thh] > 0)&&(thh != maxQuantityOfNumCardsID))	//the higher quicker is the highest card that != the three of a kind card
				{
					threeOfAKindHighCardHigher = thh;								//get the higher kicker
					break;															//break out early
				}
			}
			
			for(int thl = 12; thl >= 0; thl--)										//finding the lower kicker
			{
				if((theNumCounterArray[thl] > 0)&&(thl != threeOfAKindHighCardHigher)&&(thl != maxQuantityOfNumCardsID)) 
				{																	//the lower kicker is the highest card that != the three of a kind card and != the lower kicker	
					threeOfAKindHighCardLower = thl;								//get the lower kicker
					break;															//break out early
				}
			}
			
			secondaryHandScore += (threeOfAKindHighCardHigher + 2) * 100;			//SHS: higher kicker = UU,00
			secondaryHandScore += threeOfAKindHighCardLower + 2;					//SHS: lower kicker  =    UU
 
			handEvaluatedBool = true;												//the hand type has been found
		}
		
		//--------------------------------------------------------------------------------------------------//	

		if((maxQuantityOfNumCards == 2)&&(secondHighestQuantityOfNumCards == 2)&&(typeOfMulti == 2)) //if there is a two pair and the algorithm is currently looking for a two pair
		{
			primaryHandScore = 2;													//PHS = 2
			secondaryHandScore += (maxQuantityOfNumCardsID + 2) * 10000;			//SHS: higher pair = UU,00,00
			secondaryHandScore += (secondHighestQuantityOfNumCardsID + 2) * 100;	//SHS: smaller pair =   UU,00
			
			int twoPairKicker = 0;													//the kicker for the two pair
			
			for(int tpk = 12; tpk >= 0; tpk--)										//finding the kicker
			{
				if((theNumCounterArray[tpk] > 0)&&(tpk != maxQuantityOfNumCardsID)&&(tpk != secondHighestQuantityOfNumCardsID)) //kicker = highest card != pair 1 card and != pair 2 card
				{
					twoPairKicker = tpk;											//getting the kicker
					break;															//break out early
				}
			}
			secondaryHandScore += twoPairKicker + 2;								//SHS: kicker = UU
			handEvaluatedBool = true;												//hand type has been found
		}
		
		//--------------------------------------------------------------------------------------------------//	

		if((maxQuantityOfNumCards == 2)&&(typeOfMulti == 1))						//if there is a pair and the algorithm is currently looking for a pair
		{
			primaryHandScore = 1;													//PHS = 1
			secondaryHandScore += (maxQuantityOfNumCardsID + 2) * 1000000;			//SHS: pair = UU,00,00,00
			
			int pairHighCardPrimary = 0;											//first kicker
			int pairHighCardSecondary = 0;											//second kicker
			int pairHighCardTertiary = 0;											//third kicker
 
			for(int pHP = 12; pHP >= 0; pHP--)										//finding first kicker
			{
				if((theNumCounterArray[pHP] > 0)&&(pHP != maxQuantityOfNumCardsID))	//first kicker = highest card that != pair card
				{
					pairHighCardPrimary = pHP;										//get the first kicker
					break;															//break out early
				}
			}
			
			for(int pHS = 12; pHS >= 0; pHS--)										//finding the second kicker
			{
				if((theNumCounterArray[pHS] > 0)&&(pHS != pairHighCardPrimary)&&(pHS != maxQuantityOfNumCardsID)) //second kicker = highest card that != pair card and != first kicker
				{
					pairHighCardSecondary = pHS;									//get the second kicker
					break;															//break out early
				}
			}
			
			for(int pHT = 12; pHT >= 0; pHT--)										//finding the third kicker
			{
				if((theNumCounterArray[pHT] > 0)&&(pHT != pairHighCardPrimary)&&(pHT != pairHighCardSecondary)&&(pHT != maxQuantityOfNumCardsID)) 
				{																	//third kicker = highest card that != pair card and != first kicker and != second kicker
					pairHighCardTertiary = pHT;										//get the third kicker
					break;															//break out early
				}
			}
			
			secondaryHandScore += (pairHighCardPrimary + 2) * 10000;				//SHS: first kicker  = UU,00,00
			secondaryHandScore += (pairHighCardSecondary + 2) * 100;				//SHS: second kicker =    UU,00
			secondaryHandScore += pairHighCardTertiary + 2;							//SHS: third kicker  =       UU
 
			handEvaluatedBool = true;												//the hand type has been found
		}
	}
	
	private void flushChecker(boolean checkForStraightFlushF)								//checks for a flush, and if bool parameter == true, check for a straight flush
	{
		int theSuitCounterArray[] = new int[4];												//counter array for suits (index = type of suit, element value = quantity)
		
		for(int p = 0; p < 4; p++)															//start the counter array off at 0 occurrences of each suit
		{
			theSuitCounterArray[p] = 0;
		}
		
		for(int s = 0; s < 7; s++)															//for each card in the hand array
		{
			int suitAtCardAtHand1 = DeckAndCardWCJL.getCardSuitInt(theHandArray[s]);		//get the suit of the card

			theSuitCounterArray[suitAtCardAtHand1] ++;										//update the suit counter array
		}
		
		int maxSuitQuantity = 0;															//the most occurring suit's # of occurrences. If it is >= 5, the second most # <= 2
		int maxSuitID = 0;																	//the most occurring suit's suit. Default to diamond
		
		for(int t = 0; t < 4; t++)															//getting the maxSuitQuantity
		{												
			if(theSuitCounterArray[t] > maxSuitQuantity) 									//if the current quantity > maxSuitQuantity
			{
				maxSuitQuantity = theSuitCounterArray[t];									//update the maxSuitQuantity
				maxSuitID = t;																//update the maxSuitID
			}
		}
		
		if(maxSuitQuantity >= 5)															//if there is a flush, check for a straight flush
		{
			int theFlushHandArray[] = new int[maxSuitQuantity];								//array of cards of the same suit as big as the number of the cards of the same suit
			int theFlushHandArrayCurrentIndex = 0;											//current index = 0
			
			for(int q = 0; q < 7; q++)														//for each card in the hand array
			{
				int suitAtCardAtHand2 = DeckAndCardWCJL.getCardSuitInt(theHandArray[q]);	//get the suit of the current hand array card
				if(suitAtCardAtHand2 == maxSuitID)											//if the suit of the current card == the most often occurring relevant suit,
				{
					 theFlushHandArray[theFlushHandArrayCurrentIndex] = theHandArray[q];	//put that card into the flush array
					 theFlushHandArrayCurrentIndex ++;										//update the index for the next card to be added to the flush array
				}
			}

			if(checkForStraightFlushF == true)												//if the algorithm is checking for a straight flush, 
			{																				//check the flush hand/ cards that are the same suit to see if they are a straight. 
				straightChecker(theFlushHandArray, true);									//true == check for a straight flush
			}																				
			else if(checkForStraightFlushF == false)										//else if the algorithm is just checking for a normal flush
			{
				handEvaluatedBool = true;													//the hand type has been found
				primaryHandScore = 5;														//PHS = 5
				
				secondaryHandScoreAnalyzer(theFlushHandArray, 5);							//SHS: each of the five cards is a kicker
			}
		}
	}
	
	public void straightChecker(int a[], boolean checkForStraightFlushS)	//requires the array to be sorted in numeric order. Checks the a[] array for a straight, 
	{																		//and if checkForStraightFlushS, that straight would be a straight flush
		boolean straightFound = false;										//whether or not a straight has been found
		
		//the following gets rid of duplicates (if the array is sorted in ascending numeric order)
		int straightStreak = 1;												//how many cards in a row are incremental. Default = 1
		
		int[] straightCompiler = new int[7];								//array of unique cards, max size 7 used card elements
		
		for(int i = 0; i < 7; i++)											//the element values are set so they cannot be incremental (-12 is very far from 0 and 12)
		{
			straightCompiler[i] = -12;
		}
		
		int sCIndex = 6;													//the index of adding elements to the straight compiler starts from the end and moves to the beginning
		
		//straightCompiler[6] = a[5]; 
		straightCompiler[6] = a[a.length - 1]; 								//the last element of the straight compiler is the last (and highest) element of the original array

		for(int i = a.length - 1; i >= 0; i--)								//for each element in the original array starting from the second last to the first
		{	
			if(DeckAndCardWCJL.getCardInt(a[i]) != DeckAndCardWCJL.getCardInt(straightCompiler[sCIndex]))	//if the current original element is not the same as an element in the straightComplier
			{
				sCIndex--;													//decrement the current index of the updating straightCompiler
				straightCompiler[sCIndex] = a[i];							//add the unique element into the straightCompiler
			}
		}
		
		//now checking for a straight
		int previousDifferentCard = straightCompiler[6];					//start the card checking off with the greatest card of the unique array list

		for(int j = 6; j >= 0; j--)											//from the top to the bottom of the straight compiler array
		{
			if(DeckAndCardWCJL.getCardInt(previousDifferentCard) - DeckAndCardWCJL.getCardInt(straightCompiler[j]) == 1) 			//if the cards are incremental, E.g. A - K = 1, 7 - 6 = 1
			{
				straightStreak++;											//increment the streak of incremental cards
				previousDifferentCard = straightCompiler[j];				//update the previous different card
				
				if(straightStreak == 5)										//if there is a straight, this is the highest straight
				{
					if(checkForStraightFlushS == true)						//if the algorithm is currently checking for a straight flush,
					{
						primaryHandScore = 9;								//PHS = 9
					}
					else if(checkForStraightFlushS == false)				//else if the algorithm is currently checking for a normal straight,
					{
						primaryHandScore = 4;								//PHS = 4
					}

					secondaryHandScore = DeckAndCardWCJL.getCardInt(straightCompiler[j+4]) + 2;		//SHS = the highest card of the straight (6 to 14)
					handEvaluatedBool = true;								//the hand type has been found
					straightFound = true;									//the straight has been found
					
					break;													//break out of the for loop, as any lower value straights need not be found
				}
			}
			else															//else if the adjacent cards are not incremental,  e.g. 5 - 2 = 3 is not incremental
			{
				straightStreak = 1;											//the streak of incremental cards is back to 1
				previousDifferentCard = straightCompiler[j];				//update the previous different card
			}
		}
		
		int aceToFiveStraightCards = 0;										//components of a straight from A to 5, used in the following code checking for a straight from A to 5
		
		for(int j = 6; j >= 0; j--)											//for each card in the straight compiler/ unique hand array
		{
			if((DeckAndCardWCJL.getCardInt(straightCompiler[j]) == 12)		//if the card is an ace,
			||((DeckAndCardWCJL.getCardInt(straightCompiler[j]) >= 0)&&(DeckAndCardWCJL.getCardInt(straightCompiler[j]) <= 3)))		//or the card is in the range of [2,5]
			{
				aceToFiveStraightCards++;									//the number of components for a straight from A to 5 increments
			}
		}
		
		if((aceToFiveStraightCards >= 5)&&(straightFound == false))			//if a stronger straight has not been found and there is a straight from A to 5,
		{			
			if(checkForStraightFlushS == true)								//if the algorithm is currently checking for a straight flush
			{
				primaryHandScore = 9;										//PHS = 9
			}
			else if(checkForStraightFlushS == false)						//else if the algorithm is currently checking for a normal straight
			{	
				primaryHandScore = 4;										//PHS = 4  
			}
			
			secondaryHandScore = 5; 										//SHS = 5
			handEvaluatedBool = true;										//the hand type has been found
		}
	}

	private void secondaryHandScoreAnalyzer(int a[], int consideredCardsQuantity)								//Analyzes the SHS with an array a[] and how many cards are kickers
	{
		int currentMultiplier = consideredCardsQuantity - 1;													//defines the maximum amount of digits used for the second hand score
																												//used as (card's numeric value) * 100 ^ currentMultiplier
		for(int i = a.length - 1; i > a.length - consideredCardsQuantity - 1 ; i--)
		{	
			secondaryHandScore += (DeckAndCardWCJL.getCardInt(a[i]) + 2)* Math.pow(100, currentMultiplier);		//SHS: (card numeric value) * 100 ^ currentMultiplier
			currentMultiplier--; 																				//move down two digits
																												//this ends up getting a number where each card gets its own digits. 
																												//Higher value cards are higher up in the SHS number 
		}	
	}
}
