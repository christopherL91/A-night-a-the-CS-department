import java.util.*;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 2011.08.08
 */

public class Room 
{
    //variable to hold a description of the room.
    private String description;
    //direction and room.
    private HashMap<String, Room> exits;// stores exits of this room.
    private Random random;
    //Holds true/false if room is locked.
    private boolean isLocked;
    //hold the number of questions
    private int numberofquestions;
    //holds the room id
    private int roomid;

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String description, boolean locked,int id) {
        //set the current description to room.
        this.isLocked = locked;
        this.description = description;
        this.roomid = id;
        //create a hashmap over the availible exits.
        exits = new HashMap<>();
    }

    public void setPassed() {
        this.isLocked = false;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public Question generateQuestion() {       
        return randomQuestion();
    }

    private Question randomQuestion() {

        Question[] questions = {
            new Question("The greatest editor of all times","emacs"),
            new Question("Count the number of lines in file1","cat file1 | wc -l"),
            new Question("Update timestamp on file1","touch file1")
              // new Question("Pipe output from command1 to command2",
           // "command1 | command2"),
            
           // new Question("Remove the file log.txt without confirmation",
           // "rm -rf log.txt"),

           // new Question("Redirect the content of file1 to /dev/null", 
           // "cat file1 > /dev/null"),
        }; 
        random = new Random();
        this.numberofquestions = questions.length;
        int number = random.nextInt(questions.length);
        Question question = questions[number];
        return question;
    }

    public int numberOfQuestions() {
        return this.numberofquestions;
    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        //make exit.
        exits.put(direction, neighbor);
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "\n" + description + "\n" + getExitString();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    public int getRoomId() {
        return roomid;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }
}

