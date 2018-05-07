package pLANmarkPokerWCJL;

import java.util.Random;												//for randomness
 
public class DeckAndCardWCJL {											//deck class that also handles the properties of cards
	
	private static int theDeckArray[];									//array for the deck: index = card ID, element value = quantity of card
	
	/* deck constructor. Initializes the whole deck with one of each card (card 1 to 52), with ID's of 0 to 51
	 * the cards are ordered so that each consecutive card is +1 in number and +1 in suit from the card before it
	 * numbers are ordered 2 to 10 to Jack to King to Ace (0 to 8 to 9 to 11 to 12)
	 * suits are ordered diamonds to spades (0 to 4)
	 * e.g. card ID/ array index:[0][1][2][3][4][5][6][7][ 8][9][10][11][12][13][14]
	 * 		card num:			 [2][3][4][5][6][7][8][9][10][J][ Q][ K][ A][ 2][ 3]
	 * 		card suit:			 [D][C][H][S][D][C][H][S][ D][C][ H][ S][ D][ C][ H]
	 */	
	
	public DeckAndCardWCJL()											//deck and card constructor
	{
		 theDeckArray = new int[52];									//create the deck array, 52 elements
		 	
		 for(int i = 0; i < 52; i++)									//set each element in the array to a default of 1 (quantity of card)
		 {
			 theDeckArray[i] = 1;
		 }
	}
	
	public void shuffleNewDeck()										//makes the deck have one of every card (card 0 to 51) again.
	{																	//refilling the deck is a more accurate description.
		 for(int i = 0; i < 52; i++)									//for each card in the deck, set it so that there is 1 card
		 {
			 theDeckArray[i] = 1;
		 }
	}
	
	private boolean isDeckEmpty()										//returns true if the deck is empty (cannot draw from it anymore)
	{
		for(int e = 0; e < 52; e++)										//for each element in the array,
		{
			if(theDeckArray[e] > 0)										//if a card exists,
			{
				return false;											//then the deck is not empty, therefore return false, the deck is not empty
			}
			
		}	
		return true;													//if there were no cards found in the whole deck, then the deck is indeed empty (true)
	}
	
	public int drawCard()												//returns a drawn card ID
	{
		int drawnCard;													//the drawn card

		if(isDeckEmpty() == false)										//if you can draw a card (cards in the deck)
		{
			do															//pick cards until the random card is equal to a card present in the deck
			{
				Random randomDraw = new Random();						//Random class generates a stream of pseudorandom numbers
				drawnCard = randomDraw.nextInt(52);						//populate the array with a pseudorandom number in the range of [0,52)
			
			}while(theDeckArray[drawnCard] == 0);						//repeat if the random card is not in the deck 
				
			theDeckArray[drawnCard] = 0;								//a random card has been drawn, so remove that card from the deck (quantity is now 0)
			return drawnCard;											//return that drawn card ID
		}
		else															//else if the deck is empty, return -1 (not a real card)		
		{
			return -1;						
		}
	}
	
	public static int getCardInt(int uNCardInt)							//get the number value of a card from its ID, used in game calculations
	{
		int numCardID = uNCardInt % 13;									//get the number value. E.g. 25 = 12, 26 = 0
																		//			           E.g. Ace = 12, 2 = 0
		return numCardID;												//return this card's numeric value
	}
	
	public static int getCardSuitInt(int uNCardSuiInt)					//get the suit's number value from the card ID, used in game calculations
	{
		int suitCardIntID = uNCardSuiInt % 4;							//get the number value E.g. 25 = 1, 26 = 2
																		//E.g. Ace of Clubs = 1, Two of Hearts = 2
		return suitCardIntID;											//return this card's numeric suit value
	}
	
	public static String getCardSuit(int uNCardSui)						//get the string suit of a number from its ID
	{
		int suitCardID = uNCardSui % 4;									//get the numeric suit value of the card ID
		
		switch(suitCardID)												//switch for the numeric suit value
		{
			case(0): return "Diamonds"; 								//0 = Diamonds
			case(1): return "Clubs"; 									//1 = Clubs
			case(2): return "Hearts"; 									//2 = Hearts
			case(3): return "Spades"; 									//3 = Spades
			default: return "";											//default return nothing, although this should be impossible
		}
	}
	
	
	public static String getCardNameWithoutSuit(int uNCardStrWOS)		//get the string name of the card without the suit from the card ID, used in stating the nature of a pair of cards
	{
		int nameCardWOSID = uNCardStrWOS % 13;							//get the numeric value of a card
		String nameCardWOSString = "";									//initialize the string
		
		switch(nameCardWOSID)											//switch on the numeric value of the card
		{
			case(0): nameCardWOSString = "Two";	break;					//0 = pair of twos
			case(1): nameCardWOSString = "Three";	break;				//1 = pair of threes
			case(2): nameCardWOSString = "Four";	break;				//2 = pair of fours
			case(3): nameCardWOSString = "Five";	break;				//3 = pair of fives
			case(4): nameCardWOSString = "Sixe";	break;				//4 = pair of (sixe + s) = pair of sixes
			case(5): nameCardWOSString = "Seven";	break;				//5 = pair of Sevens
			case(6): nameCardWOSString = "Eight";	break;				//6 = pair of Eights
			case(7): nameCardWOSString = "Nine";	break;				//7 = pair of Nines
			case(8): nameCardWOSString = "Ten";	break;					//8 = pair of Tens
			case(9): nameCardWOSString = "Jack";	break;				//9 = pair of Jacks
			case(10): nameCardWOSString = "Queen";	break;				//10 = pair of Queens
			case(11): nameCardWOSString = "King";	break;				//11 = pair of Kings
			case(12): nameCardWOSString = "Ace";	break;				//12 =  pair of Aces
		}
		return nameCardWOSString;										//return the string
	}
	
	public static String getCardName(int uNCardStr)						//get the full string name of a card ID
	{	
		int nameCardID = uNCardStr % 13;								//getting the numeric value (2 - A)
		String nameCardString = ""; 									//initialize the name string
		String suitCardString = getCardSuit(uNCardStr);					//get the card ID's suit string
		
		switch(nameCardID)												//switch on the numeric value of the string used for stating the card name
		{																	
			case(0): nameCardString = "Two";	break;					//0 = 2
			case(1): nameCardString = "Three";	break;					//1 = 3
			case(2): nameCardString = "Four";	break;					//2 = 4
			case(3): nameCardString = "Five";	break;					//3 = 5
			case(4): nameCardString = "Six";	break;					//4 = 6
			case(5): nameCardString = "Seven";	break;					//5	= 7
			case(6): nameCardString = "Eight";	break;					//6 = 8
			case(7): nameCardString = "Nine";	break;					//7 = 9
			case(8): nameCardString = "Ten";	break;					//8 = 10
			case(9): nameCardString = "Jack";	break;					//9 = J
			case(10): nameCardString = "Queen";	break;					//10 = Q
			case(11): nameCardString = "King";	break;					//11 = K
			case(12): nameCardString = "Ace";	break;					//12 = A
		}
		
		return(nameCardString + " of " + suitCardString);				//e.g. Card ID of 25 returns: Ace of Clubs
	}
} 	
