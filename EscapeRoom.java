import java.util.Scanner;

public class EscapeRoom
{
    public static void main(String[] args) 
    {      
        System.out.println("Welcome to EscapeRoom!");
        System.out.println("Get to the other side of the room, avoiding walls and invisible traps,");
        System.out.println("pick up all the prizes.\n");

        GameGUI game = new GameGUI();
        game.createBoard();

        int m = 60;  // size of one move = 1 grid space
        int score = 0;

        String[] validCommands = { "right", "left", "up", "down",
                                   "r", "l", "u", "d",
                                   "w", "a", "s", 
                                   "jump", "jr", "jumpleft", "jl",
                                   "jumpup", "ju", "jumpdown", "jd",
                                   "pickup", "p",
                                   "findtrap", "trapvictim", "detrap",
                                   "replay", "help", "?", 
                                   "quit", "q" };

        boolean play = true;

        while (play)
        {
            System.out.print("Enter command: ");
            String input = UserInput.getValidInput(validCommands);

            int moveScore = 0;

            switch (input.toLowerCase()) 
            {
                // RIGHT
                case "right": case "r": case "d":
                    moveScore = game.movePlayer(m, 0);
                    if (moveScore < 0) System.out.println("Failed move to the right!");
                    break;

                // LEFT
                case "left": case "l": case "a":
                    moveScore = game.movePlayer(-m, 0);
                    if (moveScore < 0) System.out.println("Failed move to the left!");
                    break;

                // UP
                case "up": case "u": case "w":
                    moveScore = game.movePlayer(0, -m);
                    if (moveScore < 0) System.out.println("Failed move up!");
                    break;

                // DOWN
                case "down": case "s":
                    moveScore = game.movePlayer(0, m);
                    if (moveScore < 0) System.out.println("Failed move down!");
                    break;

                // JUMP RIGHT
                case "jump": case "jr":
                    moveScore = game.movePlayer(m*2, 0);
                    if (moveScore < 0) System.out.println("Failed jump to the right!");
                    break;

                // JUMP LEFT
                case "jumpleft": case "jl":
                    moveScore = game.movePlayer(-m*2, 0);
                    if (moveScore < 0) System.out.println("Failed jump to the left!");
                    break;

                // JUMP UP
                case "jumpup": case "ju":
                    moveScore = game.movePlayer(0, -m*2);
                    if (moveScore < 0) System.out.println("Failed jump up!");
                    break;

                // JUMP DOWN
                case "jumpdown": case "jd":
                    moveScore = game.movePlayer(0, m*2);
                    if (moveScore < 0) System.out.println("Failed jump down!");
                    break;

                // PICKUP PRIZE
                case "pickup": case "p":
                    moveScore = game.pickupPrize();
                    break;

                // FIND TRAP
                case "findtrap":
                    game.findTrap();
                    break;

                // TRAP VICTIM
                case "trapvictim":
                    moveScore = game.trapVictim();
                    break;

                // DETRAP
                case "detrap":
                    game.detrap();
                    break;

                // REPLAY
                case "replay":
                    moveScore = game.replay();
                    if (moveScore > 0) System.out.println("ReplayFinish! Bonus awarded.");
                    else System.out.println("Fail Finish: penalty applied.");
                    break;

                // HELP
                case "help": case "?":
                    System.out.println("Commands:");
                    System.out.println("Move: w/a/s/d or up/left/down/right");
                    System.out.println("Jump: jump/jr/jumpleft/jl/jumpup/ju/jumpdown/jd");
                    System.out.println("Pickup prize: pickup or p");
                    System.out.println("Traps: findtrap, trapvictim, detrap");
                    System.out.println("Replay: replay");
                    System.out.println("Quit: quit or q");
                    break;

                // QUIT
                case "quit": case "q":
                    play = false;
                    System.out.println("Quitting game...");
                    break;

                default:
                    System.out.println("Command not implemented yet.");
                    break;
            }

            score += moveScore;
            System.out.println("Current score: " + score);
        }

        int endScore = game.endGame(); // Finish / Fail Finish
        if (endScore > 0) System.out.println("You finished the game! Bonus awarded.");
        else System.out.println("You quit too soon! Penalty applied.");

        score += endScore;
        System.out.println("Final score = " + score);
        System.out.println("Steps taken = " + game.getSteps());
    }
}
