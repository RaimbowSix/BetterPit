
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

  <img width="454" height="110" alt="546429588-ba8b36be-2309-4bfd-91d7-555c0ab9e7cc" src="https://github.com/user-attachments/assets/6f845219-08e0-4739-b597-a33c6aaa5520" />

- ### Bounty Tracking :
  A HUD on which you can see the bountied players along with their distance from you. You can chose the minimum amount of the bounty for it to be shown on the HUD in the OneConfig GUI. Useful for bounty hunting.

  <img width="390" height="133" alt="546430325-eeabfa65-284e-48dd-9f44-912fd0405362" src="https://github.com/user-attachments/assets/732e5e2f-9c91-45dc-a9c3-603501461462" />

- ### QuickMath Solver :
  This module allows you to solve the QuickMath automatically. You can chose how fast you want the Solver to be. The latency is randomized a little bit to not raise suspicions.
- ### 2d & 3d ESP :
  Draws 2d or 3d box around player entities. Enemies will be drawn in red and nicked players in a blue box.

  <img width="862" height="472" alt="546429926-8bba6f47-a190-4435-94b5-a98505265369" src="https://github.com/user-attachments/assets/ae850b22-496e-4fcf-a304-38fc93b111bc" />

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

