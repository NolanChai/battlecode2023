# Scuffed Battlecode 2023

Stuff for Battlecode 2023 after starting to work on it 1.5 days before the final sprint qual, watching lecs at 2.5x speed, and mashing some stuff together at 3 am. Courtesy to Battlecode devs for a good chunk of this lmao

### Project Structure

- `README.md`
    This file.
- `build.gradle`
    The Gradle build file used to build and run players.
- `src/`
    Player source code.
- `test/`
    Player test code.
- `client/`
    Contains the client. The proper executable can be found in this folder (don't move this!)
- `build/`
    Contains compiled player code and other artifacts of the build process. Can be safely ignored.
- `matches/`
    The output folder for match files.
- `maps/`
    The default folder for custom maps.
- `gradlew`, `gradlew.bat`
    The Unix (OS X/Linux) and Windows versions, respectively, of the Gradle wrapper. These are nifty scripts that you can execute in a terminal to run the Gradle build tasks of this project. If you aren't planning to do command line development, these can be safely ignored.
- `gradle/`
    Contains files used by the Gradle wrapper scripts. Can be safely ignored.

### How to get started

just load in eclipse and build gradle; update from tasks and build from client, do `./gradlew run -PteamA=<botname1> -PteamB=<botname2>`

### Useful Commands

- `./gradlew build`
    Compiles your player
- `./gradlew run`
    Runs a game with the settings in gradle.properties
- `./gradlew update`
    Update configurations for the latest version -- run this often
- `./gradlew zipForSubmit`
    Create a submittable zip file
- `./gradlew tasks`
    See what else you can do!
