# Sonic Skillz
Jennifer Nino Tapia (jninotap), Noëll Cousins (ncousin2), Maliha Islam (mislam8), Catherine Li (cli75)

## How to Build and Run
**Running online**
1. Visit "https://sonicskillz.herokuapp.com/" and ensure cookies are allowed.

**Running locally**
1. Go to the `term-project-cli75-jninotap-mislam8-ncousin2-1` directory (the directory 
containing `pom.xml`).
2. In the Constants class, ensure `REDIRECT_URI = REDIRECT_URI_LOCAL` (rather than `REDIRECT_URI = REDIRECT_URI_HEROKU`).
3. In the websockets.js file, ensure WebSockets are using `ws://` (rather than `wss://`).
4. (Optional) Run `mvn clean`.
5. Run `mvn package`. This will compile the program and run unit tests. Run `mvn compile` if you want to compile without running unit tests.
6. Run `./run` to start the program.
7. Visit "http://localhost:4567" in a browser window. Ensure cookies are allowed.

**Updating deployed version [development]**
If this is your first time deploying this app on this machine, ensure you have the Heroku cli installed and are logged in, then run `heroku git:remote -a sonicskillz` to add the remote Heroku repository, and follow the remaining instructions.
1. Complete local changes to be added to the update.
2. In the Constants class, replace `REDIRECT_URI = REDIRECT_URI_LOCAL` with `REDIRECT_URI = REDIRECT_URI_HEROKU`.
3. In the websockets.js file, replace `ws://` with  `wss://`.
4. (Optional) Run `mvn clean install`.
5. Run `git add .`, then `git commit -m "[message here]"`, then either `git push heroku [branchname]:master` if on a 
branch or `git push heroku master` if on master branch. This will push all local changes to the master branch on Heroku,
compile the new program, and deploy the latest version.
6. Visit "https://sonicskillz.herokuapp.com/" in a browser window. Ensure cookies are allowed.

## Game Instructions
* All players need to have a Spotify account to play this game. Any account type is fine, free or premium. We will be using Spotify data to give you customized songs.
* At the start of the game, the host chooses the number of songs per player and level of difficulty of the game. We will generate a customized playlist based on your favorite songs and artists.
* During their turn, a player has thirty seconds to guess the name of the song playing. You get only one guess, so make it count! No worries, small typos are okay! You also do not need to worry about items that would generally be included in parentheses in the title.
* A player gets a point if they guess the right song and no points if they get it wrong. Waiting for the timer to time out won't work either; if a player runs out of time, they won't get a point. The person with the most points by the end wins the game.
* If a player exits the game (clicks on the "Home" icon) while the game is running, the game will prompt the user to confirm exit and the game will be closed; everyone in that game session will be redirected to the home page.
* Max number of players per session is currently capped at 6.

## Checkstyle Appeals
* Track.java, line 23 "preview_url": due to JSON to Java object conversion, this instance variable must match JSON property name
* Track.java, line 29 "duration_ms": due to JSON to Java object conversion, this instance variable must match JSON property name

## Project Intro
My friends and I really love playing the "guess the song game" but we usually have a hard time finding good playlists to use because if we choose a random playlist on Spotify, there are usually songs no one knows and if it's one of our playlists, then one of us will have an unfair advantage. 

**What problems that ideas is attempting to solve and how your project will solve them:**
The current problems when playing “Guess the song” game is that the folks playing need to decide on a playlist that everyone knows songs from. The other problem that comes up is you need a moderator (which means during each round, someone will need to sit out). The reason you need a moderator is because while people are guessing, someone needs to determine whether the guesses are correct or not. 

**Main parts of the project:**
 * main interface for both games
 * uses Spotify data to create playlists reflecting musical interests of players
     * We need to figure out a way to choose songs from peoples listening history. This algorithm will also need to consider the cases where there are no songs in common. This is important because we want to choose music that is diverse and spans across the users interests.
 * Authenticating Spotify login and user information
     * We need to figure out a way to get peoples Spotify information to pull listening history
 * Use a lyrics API to find common words for the song association game
     * We need to find songs contained within the Spotify playlist containing a specific word or set of words. This will involve extensive data processing and a lyrics website API that can connect our project to reliable lyrics for the songs in the playlists.
 * Clear, interactive front-end design
     * A frontend that has clear navigation. This is important because the web app is what is replacing the moderators place. This way, everyone can play the game!  
 
**Challenges:**
 * Figuring out the best songs to use (so figuring out a way to evaluate and compare songs quantitatively). 
 * Creating a UI that is easy to navigate and will let us play music from the browser (with out having us provide the audio files)

**Potential Survery Questions:**
 * Have you ever played the song association game or guess the song game? 
 * If there was an interface for this, what would that look like? 
 * What would be the first thing you’d want to see? 
 * How would you want it to be structured (you determine the number of songs, what songs, etc)

**Mentor TA:** Mina Rhee, mina_rhee@brown.edu
