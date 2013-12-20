 /*
 * Known bugs: mirror will work outside of the school.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 2013-12-11
 * Updated by Christopher Lillthors.
 * Thanks to Viktor Kronvall @Considerate on github, (in the programming course) 
 * for showing me how multithreading
 * in java works!
 *
 *
 *The theme in this game is UNIX. There are questions based upon UNIX-commands.
 */
import java.util.*;
import java.io.*;

public class Game implements CommandListener {

    private Parser parser; 
    private Room currentRoom;
    private Room outside, computerlab,research,mainhall,magicdoor,hallway,exitroom;
    //To get answer from user.
    private BufferedReader buffer;
    //Time the running time.
    private Timer timer;
    private int keycounter;
    private int numberofquestions;
    private static int currentRoomId;
    private String command;
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        buildString();
        //current room ID
        currentRoomId = 1;
        //number of keys
        keycounter = 0;
        //Creates all the rooms in the game.
        this.createRooms();
        //This creates a new parser that can translate commands into actions.
        parser = new Parser();
        /*Create a parser listener, great if you have severall classes
        *that needs to talk to the command methods
        */
        parser.addEventListener(this);
        this.buffer = new BufferedReader(new InputStreamReader(System.in));
        //Create a clock.
        timer = new Timer();

        //Create a thread with a timer.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("\nWarning, only seconds left");
                System.out.print("> ");
            }
        } , (long) (9.5*60*1000)); //Will warn about 30 seconds before the program exits.

        //Create a thread with a timer.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Program will quit.
                System.exit(0);
            }
        } , (long) (10*60*1000)); //Will run for 10 minutes.
    }

    private void buildString() {
        command = "sh snow.sh";
    }

    public static int getCurrentRoomID() {
        return currentRoomId;
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        //create room variables.
        //private variables
        outside = new Room("You're outside the main building.",false,1);
        computerlab = new Room("You're in the computerlab",true,2);
        research = new Room("You're in the research room",false,3);
        mainhall = new Room("You're in the big hall",true,4);
        magicdoor = new Room("You're standing in front of a big black door",false,5);
        hallway = new Room("You're standing in the mainhall",true,6);
        //don't lock the exitroom, for this game to work.
        exitroom = new Room("You completed the game",false,7);
        //set current room. You start outside.
        currentRoom = outside;

        //set room exists.
        outside.setExit("north",hallway);
        hallway.setExit("north",mainhall);
        mainhall.setExit("south",hallway);
        mainhall.setExit("north",research);
        mainhall.setExit("west",computerlab);
        mainhall.setExit("east",magicdoor);
        magicdoor.setExit("south",mainhall);
        research.setExit("south",mainhall);
        computerlab.setExit("south",mainhall);
        magicdoor.setExit("north",exitroom);
    }
    //Pass the call to a well known method already written.
    public void commandRecieved(Command command) {
        processCommand(command);
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        //Create a new parser and put it in a new thread.
        Thread parserThread = new Thread(parser);
        //will run run method in Parser.java
        parserThread.start();
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the CS department of Denver university");
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        //This will be called everytime a command is entered.
        //It is called from parser.
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }

        else if (commandWord.equals("mirror")) {
            mirror();
        }

        else if (commandWord.equals("UNIX-time")) {
            System.out.println(System.currentTimeMillis() / 1000L);
        }
        
        // else command not recognised.
        return wantToQuit;
    }
    //If you have played Zelda before, then you will recognise this...
    private void mirror() {
        //change room.
        currentRoom = hallway;
        //update the current room ID!
        currentRoomId = hallway.getRoomId();
        System.out.println("You were teleported to the mainhall");
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("Number of keys: " + this.keycounter);
        parser.showCommands();
    }

    /** 
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) {
    try{
            if(!command.hasSecondWord()) {
                // if there is no second word, we don't know where to go...
                System.out.println("Go where?");
                return;
            }

            String direction = command.getSecondWord();

            // Try to leave current room.
            Room nextRoom = currentRoom.getExit(direction);

            if (nextRoom == null) {
                System.out.println("There is no door!");
            }
            else {
                //check if room is locked.
                boolean isLocked = nextRoom.isLocked();

                if(isLocked) {
                    System.out.println("The door is locked!");
                    Question question = nextRoom.generateQuestion();
                    numberofquestions = nextRoom.numberOfQuestions();
                    System.out.println("There is a riddle written on the door.");
                    System.out.println("Question: " + question.getQuestion());
                    System.out.print("> Input: ");
                    //read a whole line from user.
                    String rawinput = buffer.readLine();
                    //Trim the string.
                    String trimmed = rawinput.trim();
                    if (trimmed.equals(question.getAnswer())) {
                        nextRoom.setPassed();
                        System.out.println("You may proceed");
                        currentRoom = nextRoom;
                        currentRoomId = currentRoom.getRoomId();
                        this.keycounter++;
                        System.out.println("Number of keys: " + keycounter);
                    } else {
                        System.out.println("Wrong answer! The door is still locked.");
                    }    
                }
                else {
                    isFinalDoor(currentRoom,nextRoom);
                    currentRoomId = nextRoom.getRoomId();
                }
                System.out.println(currentRoom.getLongDescription());
            }
        }catch(IOException e) {
                System.out.println("Something bad happened.");
        }
    }
    //Is the game over yet?
    private void isFinalDoor(Room curr,Room next) {
        //7 is the id of the magic room.
        int id = next.getRoomId();
        System.out.println("Number of keys : " + this.keycounter);
        if (id == 7 && keycounter == numberofquestions) {
            System.out.println("You completed the game");
            System.exit(0);
        }
        else if (id == 7 && keycounter != numberofquestions) {
            //teleport yourself!
            mirror();
        }
        else {
            //go to next room.
            currentRoom = next;
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
