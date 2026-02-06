
# BetterPit
BetterPit is a 1.8.9 forge Minecraft mod aimed to help pit players with informations
on their game. It was made using OneConfig for GUI and HUD.
## Features 
All Toggleable
- ### Enemy Tracking :
  On join of a lobby or when an enemy joins your lobby, the mod will send a message in chat telling that the enemy is here. When the enemy leaves the lobby you will also receive a chat message.
  You will also see the enemies and their distance from you on the Enemies HUD.
  Managing your enemy list : can be done either by adding usernames to watchlist.txt in your config folder. Or by using the `"/watchlist" (or "/wl")` command, you can chose "add" or "remove" as the first argument and then   the username you want to add or remove.

  <img width="264" height="112" alt="image" src="https://github.com/user-attachments/assets/96a71f3f-2e67-41c1-89cb-7d0724d6b58a" />

- ### Dark Pants Tracking :
  On the Dark HUD you will see the players wearing dark pants their distance from you and the enchant on those dark pants. If the pants are tier 1 or fresh it will say the enchant is fresh. It might not show all the darks on the map since this feature can only detect players rendered on your client.

  <img width="568" height="111" alt="image" src="https://github.com/user-attachments/assets/5e46db23-39a3-4dcf-b1cb-e2d528b60e7f" />

- ### Denicker :
  The Denicker feature allows you to denick players in your lobby if they have attributes that make them denickable. When a player is denicked he will be added to a cache in your config so next time you see the nick the mod will quickly resolve the true name of the player, even if he doesn't have any attribute that make him denickable.

  <img width="266" height="87" alt="image" src="https://github.com/user-attachments/assets/ce141998-0111-44d9-abbf-a2934cca4b10" />

- ### Bounty Tracking :
  A HUD on which you can see the bountied players along with their distance from you. You can chose the minimum amount of the bounty for it to be shown on the HUD in the OneConfig GUI. Useful for bounty hunting.

- ### QuickMath Solver :
  This module allows you to solve the QuickMath automatically. You can chose how fast you want the Solver to be. The latency is randomized a little bit to not raise suspicions.
- ### 2d & 3d ESP :
  Draws 2d or 3d box around player entities. Enemies will be drawn in red and nicked players in a blue box.
- ### Right Click Pant Swap :
  Right clicking on a pant held in hands will fastly swap it automatically for you.
- ### Auto Pod :
  Swaps to auto pod pants in inventory or hotbar when health gets under the chosen value in config. 
- ### Diamond Pant Swap :
  Automatically swaps to diamond pants in inventory or hotbar when you are wearing mystic pants and you get venomed.
- ### Auto Bullet Time :
  Manually right clicking on a sword swaps to a bullet time sword in hotbar.
- ### AutoGhead :
  Swaps to a GHead and eats it when health gets under the chosen threshold in config, then swaps back.

## Building the Mod (In case you never built a mod in your life)
Open a command prompt and go in the BetterPit directory, then you build by using :
```./gradlew build.```
If you are using an IDE (like InteliJ or Eclipse...) make sure project sdk is java 8 and language level is 8 also make sure gradle jvm is using java 17.

