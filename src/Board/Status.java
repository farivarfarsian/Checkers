package Board;

//Creating an enum for Status to have better readability (like typedef)
public enum Status 
{
    EMPTY(0),
    NORMAL_PIECE_RED(1),
    NORMAL_PIECE_WHITE(2),
    KING_PIECE_RED(3),
    KING_PIECE_WHITE(4);

    private int Value;

    Status(int value) 
    {
        this.Value = value;
    }

    public int GetValue() 
    {
        return Value;
    }

    //Convert int to Status if it's necessary
    public static Status FromIntToStatus(int Value) 
    {
        for (Status Status : values()) 
        {
            if (Status.Value == Value) 
            {
                return Status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + Value);
    }
}