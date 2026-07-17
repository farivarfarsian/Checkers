package Board;

public class Tile 
{
    Tile(int X, int Y, int Color, Status Status)
    {
        this.Position_X = X;
        this.Position_Y = Y;
        this.color = Color;
        this.status = Status;
    }
    Tile(int X, int Y, int Color)
    {
        this.Position_X = X;
        this.Position_Y = Y;
        this.color = Color;
        this.status = Status.EMPTY;
    }
    public void SetPosition_X(int X)
    {
        this.Position_X = X;
    }
    public void SetPosition_Y(int Y)
    {
        this.Position_Y = Y;
    }
    public void SetStatus(Status Status)
    {
        this.status = Status;
    }
    public void SetColor(int Color)
    {
        this.color = Color;
    }
    public int GetPosition_X()
    {
        return Position_X;
    }
    public int GetPosition_Y()
    {
        return Position_Y;
    }
    public Status GetStatus()
    {
        return status;
    }
    public int GetColor()
    {
        return color;
    }
    private int color; //Color 0 is green, Color 1 is white
    private Status status; // Empty = 0, Normal Piece Red = 1, Normal Piece White = 2, King Piece Red = 3, King Piece Red = 4, Other values are invalid
    private int Position_X , Position_Y;
}
