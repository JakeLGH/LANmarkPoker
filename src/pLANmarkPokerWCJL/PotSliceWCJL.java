package pLANmarkPokerWCJL;

public class PotSliceWCJL {												//Class for managing the Poker pot. Each object is a segment/ layer of the pot
	
	private int potSegment;												//the chip value of the pot in this segment 
	private int potKey;													//the chip value of the whole pot up to this slice
	private int potKeyFrequency;										//how many players put chips into this segment of the pot
	private int potKeyFrequencyWinners;									//how many players can lay claim to winning this segment of the pot (more than 1 winner means that there was a tie)
	private int potKeyFrequencyRefunds;									//how many players can lay claim to getting their chips back from this segment if no one else won the segment with a Poker Hand
	private int potPointGatePrimary;									//the primary hand score that players must tie in order to lay claim to this segment
	private double potPointGateSecondary;								//the secondary hand score that players must tie in order to lay claim to this segment
	
	/*	E.g. total pot: 1300 chips
	 * p.1's|p.2's|				 p.1 bets 500, p.2 calls, 
	 * bet: |bet: |
	 * [500]|[500]|				 potKey: 500, potKeyFrequncy: 2, potSegment: 500, potKeyFrequencyRefunds: 2    ~2 players bet at least 500 chips
	 * --------------------------
	 * p.1's|p.2's|p.3's| 		 p.3 calls and goes all-in with his 200 chips, segmenting the pot
	 * bet: |bet: |bet: |		 
	 * [300]|[300]|	    |		 potKey: 500, potKeyFrequency: 2, potSegment: 300, potKeyFrequencyRefunds: 2	~2 players bet at least 500 chips
	 * [200]|[200]|[200]| 		 potKey: 200, potKeyFrequency: 3, potSegment: 200, potKeyFrequencyRefunds: 3	~3 players bet at least 200 chips
	 * -------------------------
	 * p.1's|p.2's|p.3's|p.4's|  p.4 calls and goes all-in with his 100 chips,
	 * bet: |bet: |bet: |bet: |		 
	 * [300]|[300]|		|     |	 potKey: 500, potKeyFrequency: 2, potSegment: 300, potKeyFrequencyRefunds: 2	~2 players bet at least 500 chips
	 * [100]|[100]|[100]|	  |  potKey: 200, potKeyFrequency: 3, potSegment: 100, potKeyFrequencyRefunds: 3	~3 players bet at least 200 chips
	 * [100]|[100]|[100]|[100]|  potKey: 100, potKeyFrequency: 4, potSegment: 100, potKeyFrequencyRefunds: 4 	~4 players bet at least 100 chips
	 * -------------------------
	 * p.1's|p.2's|p.3's|p.4's|  p.1 and p.2 fold. The hands are revealed. p.4 has a better hand than p.3.
	 * bet: |bet: |bet: |bet: |		 
	 * [300]|[300]|		|     |	 potKey: 500, potKeyFrequency: 2, potSegment: 300, potKeyFrequencyRefunds: 2	~2 players bet at least 500 chips, potPointGatePrimary: -1 ,potPointGateSecondary: -1
	 * [100]|[100]|[100]|	  |  potKey: 200, potKeyFrequency: 3, potSegment: 100, potKeyFrequencyRefunds: 3	~3 players bet at least 200 chips, potPointGatePrimary: p.3's Primary Hand Score ,potPointGateSecondary: p.3's Secondary Hand Score
	 * [100]|[100]|[100]|[100]|  potKey: 100, potKeyFrequency: 4, potSegment: 100, potKeyFrequencyRefunds: 4 	~4 players bet at least 100 chips, potPointGatePrimary: p.4's Primary Hand Score ,potPointGateSecondary: p.4's Secondary Hand Score
	 * -------------------------
	 * p.1's|p.2's|p.3's|p.4's|  p.4's key of 100 and his PHS and SHS get him potKey 100's potSegment * potKeyFrequency = 100 * 4 = 400 chips.
	 * bet: |bet: |bet: |bet: |	 p.3's key of 200 and his PHS and SHS don't let him get the 400 chips of pot key 100, as the point gates of key 100 are higher than p.3's hand score.
	 * [300]|[300]|		|     |	 p.3's key of 200 and his PHS and SHS get him potKey 200's potSegment * potKeyFrequency = 100 * 3 = 300 chips. 
	 * [100]|[100]|[100]|	  |  because no one won the pot key 500 chips, p.1 and p.2 each get pot key 500 refunded:
	 * [100]|[100]|[100]|[100]|  p.1 and p.2 each get back potSegment; p.1 and p.2 each get back 300 chips, which are chips that haven't been won
	 */
	
	public PotSliceWCJL(int uPotKey, int uPotSegment)					//constructor, using parameters of the key and the pot segment
	{
		potKey = uPotKey;												//the key of this pot slice
		potSegment = uPotSegment;										//the segment of this pot slice
		potKeyFrequency = 1;											//each slice begins with at least one person having put that amount of money in
		potKeyFrequencyWinners = 0;										//each slice begins with no winners of that slice, (not yet).
		potKeyFrequencyRefunds = 1;										//each slice begins with at least one person having put that amount of money in
		potPointGatePrimary = -1;										//each slice begins with a secondary point gate that any hand can surpass
		potPointGateSecondary = -1;										//each slice begins with a secondary point gate that any hand can surpass
	}
	
	public boolean passesPotPointGate(int playerPHS, double playerSHS)	//true if player's hand strength >= point gate
	{																
		if(playerPHS > potPointGatePrimary)								//if the player's primary hand score is greater than the current point gate
		{
			return true;												//return true
		}
		else if((playerPHS == potPointGatePrimary)&&(playerSHS >= potPointGateSecondary))	//if the player's primary hand score is the same but their secondary hand score is greater than the current points gate 
		{
			return true;												//return true
		}
		else															//else if the player's hand score is less than the current point gate 
		{
			return false;												//return false
		}
	}
	
	public void updatePotPointGate(int attemptedPotGateUsurperPrimary, double attemptedPotGateUsurperSecondary)		//try to update/ increase the current point gate with a set of new hand scores (from a player)
	{
		if(attemptedPotGateUsurperPrimary > potPointGatePrimary)		//if the new (player's) primary hand score is higher than the current point gate
		{
			potPointGatePrimary = attemptedPotGateUsurperPrimary;		//update the gate's primary hand score to be the new, higher primary hand score
			potPointGateSecondary = attemptedPotGateUsurperSecondary;	//update the gate's secondary hand score to be the new, higher secondary hand score
		}
		else if((attemptedPotGateUsurperPrimary == potPointGatePrimary)&&(attemptedPotGateUsurperSecondary > potPointGateSecondary))	//else if the new (player's) primary hand score is the same 
		{																																//but their secondary hand score is higher than the current point gate
			potPointGateSecondary = attemptedPotGateUsurperSecondary;	//update the gate's secondary hand score to be the new, higher secondary hand score
		}
	}
	
	public void incrementPotKeyFrequency()								//increment the pot key frequency of this slice because another player
	{																	//put chips in that include this slice in the total of the bet
		potKeyFrequency ++;												//increment potKeyFrequnecy 
		potKeyFrequencyRefunds ++;										//increment potKeyRefunds
	}
	
	public void denyRefunds()											//if a player won this slice of the pot, no player may get refunded of their chips
	{
		potKeyFrequencyRefunds = 0;										//set the amount of people that can have this slice refunded to 0
	}
	
	public void updatePotSegmentFromInsertion(int uPotSegmentOfInsert)	//when another slice is inserted into the cake in between two pre-existing slices,
	{
		potSegment -= uPotSegmentOfInsert; 								//the slice above it loses some potsegment to the newly inserted slice
	}
	
	public int getPotSegment()											//return the pot segment of this pot slice
	{
		return potSegment;
	}
	
	public int getPotKey()												//return the key value of this pot slice
	{
		return potKey;
	}
	
	public int getPotKeyFrequency()										//return the number of people who put money into this pot slice for pot distribution
	{
		return potKeyFrequency;			
	}
	
	public int getPotKeyFrequencyWinners()								//return the number of people who can win this pot slice for pot distribution and ties
	{
		return potKeyFrequencyWinners; 
	}
	
	public int getPotKeyFrequencyRefunds()								//return the number of people who put money into this pot slice	for refunding
	{
		return potKeyFrequencyRefunds; 
	}

	public void incrementPotKeyFrequencyWinners()						//increment the number of people who can win this pot slice, including tied players
	{
		potKeyFrequencyWinners ++;
	}
}
