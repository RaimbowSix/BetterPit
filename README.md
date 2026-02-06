
# BetterPit
BetterPit is a 1.8.9 forge Minecraft mod aimed to help pit players with informations
on their game. It was made using OneConfig for GUI and HUD.
## Features
- ### Enemy Tracking :
  On join of a lobby or when an enemy joins your lobby, the mod will send a message in chat telling that the enemy is here. When the enemy leaves the lobby you will also receive a chat message.
  You will also see the enemies and their distance from you on the Enemies HUD.
  Managing your enemy list : can be done either by adding usernames to watchlist.txt in your config folder. Or by using the "/watchlist" (or "/wl") command, you can chose "add" or "remove" as the first argument and then   the username you want to add or remove.

<img width="264" height="112" alt="image" src="https://github.com/user-attachments/assets/96a71f3f-2e67-41c1-89cb-7d0724d6b58a" />

- ### Dark Pants Tracking :
  On the Dark HUD you will see the players wearing dark pants their distance from you and the enchant on those dark pants. If the pants are tier 1 or fresh it will say the enchant is fresh. It might not show all the darks on the map since this feature can only detect players rendered on your client.

<img width="568" height="111" alt="image" src="https://github.com/user-attachments/assets/5e46db23-39a3-4dcf-b1cb-e2d528b60e7f" />

- ### Nicked Players Tracking :
  As of now I did not make a denicker tho I started by adding an HUD on which you can see every nicked players in your lobby.

<img width="266" height="87" alt="image" src="https://github.com/user-attachments/assets/ce141998-0111-44d9-abbf-a2934cca4b10" />

## Building the Mod (In case you never built a mod in your life)
Open a command and go in the BetterPit directory, then you build by using ./gradlew build.
If you are using an IDE (like InteliJ or Eclipse...) make sure project sdk is java 8 and language level is 8 also make sure gradle jvm is using java 17.
## Future Updates
- A working denicker
- QuickMath solver
- Venom timer
- Mid overlay and Exfil points overlay
- Pants swapper
- Lobby swapper
  and more...
