package Game;

import Board.Board;
import UI.UI;

public class Game
{
    public Game()
    {
        //Setting Up the Game Env
        players = new Player[2];
        b = new Board();
        b.SetupBoard();

        players[0] = new Player("Farivar", 0); //Red
        players[1] = new Player("Farsian", 1); //White

        currentPlayer = players[0]; //It's red's turn to play first

        this.UI = new UI(this);
    }
    public boolean Move(int X, int Y, int Dest_X, int Dest_Y) 
    {
        if (b.Move(X, Y, Dest_X, Dest_Y, this.currentPlayer) == true) 
        {
            if (b.GetIsMidJump() == false) 
            {
                // Change Player
                if (currentPlayer.GetPlayerColor() == 0) 
                {
                    currentPlayer = players[1];
                } 
                else 
                {
                    currentPlayer = players[0];
                }
            }
            return true;
        } 
        else 
        {
            return false; 
        }
    }
    public void Run()
    {
        this.UI.Update();
    }
    public boolean IsGameOver()
    {
        return !b.GetCurrentPlayerHasAnyMove();
    }

    public Player GetCurrentPlayer()
    {
        return currentPlayer;
    }
    public Board GetBoard()
    {
        return b;
    }
    public Player[] GetPlayers()
    {
        return players;
    }

    private final Board b;
    private final Player[] players;
    private Player currentPlayer;

    private final UI UI;
}