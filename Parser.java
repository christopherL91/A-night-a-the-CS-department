/*
* Thanks to Viktor Kronvall for showing me how multithreading
* in java works!
*/

import java.util.Scanner;
import java.util.ArrayList;

//This is the interface that Game.java communicates with.
//commandRecieved is an abstract method, and Game.java must implement it.
interface CommandListener {
    public void commandRecieved(Command command);
}

/*

This class is part of the "A night at the CS department!"
Parser is an object that reads input from the user, and passes it to the Game
class. It is multithreaded to there's no blocking in this game.

 * 
 * @author  Michael Klling and David J. Barnes
 * @version 2011.08.08
 * Updated by Christopher Lillthors
 * 2013-12-19
 *
 *Runnable is implemented because of the multithreading.
 *Runnable is an interface, the method run() must be implemented by the class
 *that uses it!
 */

public class Parser implements Runnable
{
    private CommandWords commands;  // holds all valid command words
    private Scanner reader;         // source of command input
    //Holds all the Command listerners. In this program there's only one 
    //listener.
    private ArrayList<CommandListener> listeners;

    /**
     * Create a parser to read from the terminal window.
     */
    public Parser() 
    {
        commands = new CommandWords();
        reader = new Scanner(System.in);
        listeners =  new ArrayList<>();
    }

    public void addEventListener(CommandListener listener) {
        this.listeners.add(listener);
    }



    /**
     * @return The next command from the user.
     */
    public Command getCommand(String inputLine) 
    {
        String word1 = null;
        String word2 = null;

        // Find up to two words on the line.
        Scanner tokenizer = new Scanner(inputLine);
        if(tokenizer.hasNext()) {
            word1 = tokenizer.next();      // get first word
            if(tokenizer.hasNext()) {
                word2 = tokenizer.next();      // get second word
                // note: we just ignore the rest of the input line.
            }
        }

        // Now check whether this word is known. If so, create a command
        // with it. If not, create a "null" command (for unknown command).
        
        if(commands.isCommand(word1)) {
            return new Command(word1, word2);
        }
        else {
            return new Command(null, word2); 
        }
    }

    public void run() {
        while(true) {
            System.out.print("> ");     // print prompt
            String inputLine = reader.nextLine();
            Command command = getCommand(inputLine);

            for (CommandListener tmp : listeners) {
                tmp.commandRecieved(command);
            }
        }
    }

    /**
     * Print out a list of valid command words.
     */
    public void showCommands()
    {
        commands.showAll();
    }
}
