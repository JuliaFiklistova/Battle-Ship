# Battle Ship

# Program launch

This program is divided into 2 parts: client and server. To compile programs use mvn package. For the program to work fully, you must first run the BattleShipServer-1.0 file in the command line using the java -jar BattleShipServer-1.0.jar command. After that, on a separate command line, run the BattleShipClient-1.0 file using the java -jar BattleShipClient-1.0.jar command.

# User manual
After starting the program, a menu will be displayed on the screen, in which you can choose: connect, change ip or port or exit.

After choosing a connection, you will be asked to: log in, sign in or exit.

After log in or sign in you will be asked to: create a game, connect to an existing game or view rating table.

When choosing to create a game, you will need to arange the ships and then you will expect an opponent to connect.

When choosing to connect to an existing game, you will need to arange the ships amd then you will connect to a player who is waiting for an opponent. If there are no existing games, the corresponding message will be displayed on the screen and you will be taken to the main menu.

When choosing view rating table, you will see a rating table.

The player who created the game makes a first move. The player will need to enter the coordinates where he wants to shot. If player has missed, then second player makes next shot.

The game ends if one of the players shot down oll the ships of second plsyer. After the end of the game, players will be moved to the main menu.
