# Modern-Art-Board-Game
## Running the game
The best way to run the game is by running the Jar file. It will be the most up to date working version, although it will usually have some debugging info printing. If you would like to run the current WIP version of the game, usually the bin files are up to date, but a better idea would be to compile the source yourself and the Main class has the main method.

## Notes about this project
This is a research project about devloping AI for a board game that is not as deterministic as a game like chess or connect 4. With that being said, the game may be slightly incomplete and is not ment for human interaction at this time. A little more detail about it being incomplete is that it may not account for all of the edge cases properly, the game itself works well enough where AIs can be tested. Support for genetic AIs has been removed because I don't see where they could go at this point in time.

# About the players
The players in the player package are snapshots of different ideas. They will be kept around but probably no longer updated. I will be using them to judge how well the future AIs act. The only exceptions to this are the Random and Human players. The AIs themselves are really snapshots of ideas that were implemented over time. That being said, to reduce repeated code, the AIs are children of their previous versions.

## Human
This is exactly what is sounds like, it is a pass through so that humans can play the game. (only 1 allowed)

## Random
This AI makes random moves and is just used as a consistant baseline.

## Reactive
The Reactive AI gets a bit smarter by trying to predict the end values of paintings based off of how they are doing in the given state of the game. It will also try to end the season as fast as it can and will only bid up to half the expected value of the card.

## Memory
Memory AI uses a different value that it will bid up to. ((n-1)/n)\**expected value*, where n = player count, for all players except the best other player.

## Basic Predictive
This AI now disregards the best other player and now considers cards that other players have won in its prediction of the prices of cards (to predict what other players will play).

## Basic Predictive AI V2
V2 decides on an order it wants to try to force the artist to be in at the end of the season. So it will bid and play cards based on that.

## High Roller
This extention of V2 changes its bidding style. It will always bid the most its willing on the first round so that other players may not have the chance because bidding higher is a bad idea.

## Merchant
This AI is based off of the Memory AI, but it dosent really use much of its properties. It will only sell the highest valued cards and will not buy anything.

---

## Currently WIP
Working on the High Roller's and Merchant's children AIs. JavaDoc is mostly up to date, but the newer AIs are not done and the PlayerView is not complete.
