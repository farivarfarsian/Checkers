package Game;

public class Player 
{
    Player(String Id, int Color)
    {
        this.Id = Id;
        this.Color = Color;
    }
    public String GetPlayerID()
    {
        return this.Id;
    }
    public int GetPlayerColor()
    {
        return this.Color;
    }

    private final String Id;
    private int Color; // 0 is for Red, 1 is for White
}
