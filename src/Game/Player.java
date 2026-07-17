package Game;

public class Player 
{
    Player(int Color)
    {
        this.Id = "Default";
        this.Color = Color;
    }
    Player(String Id, int Color)
    {
        this.Id = Id;
        this.Color = Color;
    }
    public void SetPlayerID(String Id)
    {
        this.Id = Id;
    }
    public String GetPlayerID()
    {
        return this.Id;
    }
    public int GetPlayerColor()
    {
        return this.Color;
    }

    private String Id;
    private int Color; // 0 is for Red, 1 is for White
}
