package Board;

import Game.Player;
import UI.SoundManager;

public class Board
{
    public Board()
    {
        this.board = new Tile[8][8];
    }
    public void SetupBoard()
    {
        for (int r = 0; r < 8; r++) 
        {
            for (int c = 0; c < 8; c++) 
            {
                if (r < 3 && (r + c) % 2 != 0) 
                {   
                    board[r][c] = new Tile(r, c, 0, Status.NORMAL_PIECE_WHITE);
                } 
                else if (r > 4 && (r + c) % 2 != 0) 
                {
                    board[r][c] = new Tile(r, c, 0, Status.NORMAL_PIECE_RED);
                } 
                else 
                {
                    // Logic Behind it is that when r and c are both even or odd, the (r + c) is even so we assign White
                    // if it's for exmaple r is even and c is odd, then (r + c) is odd so we assign Green
                    int TileColor = ((r + c) % 2 == 0) ? 1 : 0; 
                    board[r][c] = new Tile(r, c, TileColor, Status.EMPTY);
                }
            }
        }
    }
    public boolean Move(int X, int Y, int Dest_X, int Dest_Y, Player CurrentPlayer)
    {
        // Algorithm For Move
        // 0-Check Position Validation
        // 1-Check for Player's turn
        // 2-Check Tile Kind at (X,Y)
        // 3-Check for Normal Move
        // 4-Is Capture Forced
        // 5-Check for Capture Move
        // 6-King Promotion Check
        // 7-Double Jump Validation

        int ActivePlayerColor = CurrentPlayer.GetPlayerColor();

        //0-Check for Valid Input
        if (!IsValidCoordinate(X, Y) || !IsValidCoordinate(Dest_X, Dest_Y)) 
        {
            //Debug Retired Code: System.out.println("Invalid coordinates.");
            return false;
        }

        Status Start_statustile = GetTile(X, Y).GetStatus();
        Status Dest_statustile = GetTile(Dest_X, Dest_Y).GetStatus();

        // 1-Check for Player's turn
        if ((ActivePlayerColor == 0 && (Start_statustile != Status.NORMAL_PIECE_RED && Start_statustile != Status.KING_PIECE_RED)) 
            ||
            (ActivePlayerColor == 1 && (Start_statustile != Status.NORMAL_PIECE_WHITE && Start_statustile != Status.KING_PIECE_WHITE))) 
        {
            //Debug Retired Code: System.out.println("Player: " + CurrentPlayer.GetPlayerID() + ", It's not your turn");
            return false;
        }

        // 2-Check Tile Kind at (X,Y)
        if (Dest_statustile != Status.EMPTY) 
        {
            //Debug Retired Code: System.out.println("Destination tile is blocked.");
            return false;
        }

        int DeltaX = Dest_X - X;
        int DeltaY = Dest_Y - Y;
        boolean IsJump = Math.abs(DeltaX) == 2 && Math.abs(DeltaY) == 2; //Check if the move is a jump

        // 3-Check for Normal Move 
        if (!IsValidMove(DeltaX, DeltaY, ActivePlayerColor, Start_statustile)) 
        {
            return false;
        }

        // 4-Is Capture Forced
        if (IsCaptureForced(ActivePlayerColor) && !IsJump) 
        {
            //Debug Retired Code: System.out.println("You must jump");
            return false;
        }


        // 5-Check for Capture Move
        if(IsJump) 
        {
            int CaptureX = X + (DeltaX / 2);
            int CaptureY = Y + (DeltaY / 2);
            Status CaptureTileStatus = board[CaptureX][CaptureY].GetStatus();

            // Helper Variables to make it more readable  
            boolean IsJumpingRed = (CaptureTileStatus == Status.NORMAL_PIECE_RED || CaptureTileStatus == Status.KING_PIECE_RED);
            boolean IsJumpingWhite = (CaptureTileStatus == Status.NORMAL_PIECE_WHITE || CaptureTileStatus == Status.KING_PIECE_WHITE);

            if ((ActivePlayerColor == 1 && !IsJumpingRed) || (ActivePlayerColor == 0 && !IsJumpingWhite)) 
            {
                //Debug Retired Code: System.out.println("You can only capture enemy pieces!");
                return false;
            }

            // Clear the capture piece and decrement the pience count
            board[CaptureX][CaptureY].SetStatus(Status.EMPTY);
            if (ActivePlayerColor == 1) 
            {
                RedPieceCount--;
            }
            else 
            {
                WhitePieceCount--;
            }

            SoundManager.Play("take.wav");
        }

        // Moving the piece
        board[Dest_X][Dest_Y].SetStatus(Start_statustile);
        board[X][Y].SetStatus(Status.EMPTY);

        // 6-King Promotion Check
        boolean IsPromoted = false; // We added this becuase if a player jumps and promotes, rules say that he can jump again

        if (ActivePlayerColor == 0 && Dest_X == 0 && Start_statustile == Status.NORMAL_PIECE_RED) 
        {
            board[Dest_X][Dest_Y].SetStatus(Status.KING_PIECE_RED);
            IsPromoted = true;
            SoundManager.Play("promotion.wav");
        } 
        else if (ActivePlayerColor == 1 && Dest_X == 7 && Start_statustile == Status.NORMAL_PIECE_WHITE) 
        {
            board[Dest_X][Dest_Y].SetStatus(Status.KING_PIECE_WHITE);
            IsPromoted = true;
            SoundManager.Play("promotion.wav");
        }

        // 7-Double Jump Validation
        if (IsJump && !IsPromoted && CanPieceJump(Dest_X, Dest_Y, ActivePlayerColor)) 
        {
            IsMidJump = true;
            //Debug Retired Code: System.out.println("Double jump required!");
        } 
        else 
        {
            IsMidJump = false;
        }

        return true; // Move is Valid!
    }
    // if there's no Tile in X-Y Position, returns null
    public Tile GetTile(int X, int Y)
    {
        if(IsValidCoordinate(X,Y) == false)
        {
            return null;
        }
        else
        {
            return board[X][Y];
        }
    }
    public boolean GetIsMidJump()
    {
        return IsMidJump;
    }
    public boolean GetCurrentPlayerHasJump() 
    {
        return CurrentPlayerHasJump;
    }
    public boolean GetCurrentPlayerHasAnyMove() 
    {
        return CurrentPlayerHasAnyMove;
    }
    public int GetRedPieceCount() 
    {
        return RedPieceCount;
    }
    public int GetWhitePieceCount() 
    {
        return WhitePieceCount;
    }
    
    public void EvaluatePlayerState(int PlayerColor) 
    {
        CurrentPlayerHasJump = false;
        CurrentPlayerHasAnyMove = false;

        // No pieces left 
        if ((PlayerColor == 1 && WhitePieceCount == 0) || (PlayerColor == 0 && RedPieceCount == 0)) 
        {
            return;
        }

        //TODO: optimization idea for this method would be storing each Piece and then iterating through them (OOP Piece class)
        for (int r = 0; r < 8; r++) 
        {
            for (int c = 0; c < 8; c++) 
            {
                //Skiping Empty Tiles and Enemy Tiles

                Status CurrentStatus = board[r][c].GetStatus();
                if(CurrentStatus == Status.EMPTY) 
                {
                    continue; // Skip empty tiles
                }
                boolean IsWhitePiece = (CurrentStatus == Status.NORMAL_PIECE_WHITE || CurrentStatus == Status.KING_PIECE_WHITE);
                if ((PlayerColor == 1 && !IsWhitePiece) || (PlayerColor == 0 && IsWhitePiece)) 
                {
                    continue;
                }


                if (CanPieceJump(r, c, PlayerColor)) 
                {
                    CurrentPlayerHasJump = true;
                    CurrentPlayerHasAnyMove = true;
                    return;
                }
                
                if (!CurrentPlayerHasAnyMove && CanPieceMoveNormal(r, c, PlayerColor)) 
                {
                    CurrentPlayerHasAnyMove = true;
                    // No return because we still need to check if a jump is available.
                }
            }
        }
    }

    private boolean IsValidMove(int DeltaX, int DeltaY, int PlayerColor, Status PieceStatus) 
    {
        int absX = Math.abs(DeltaX);
        int absY = Math.abs(DeltaY);

        // 1. Is it a valid diagonal distance? (Only 1 or 2 tiles allowed)
        boolean isNormalMove = (absX == 1 && absY == 1);
        boolean isJumpMove = (absX == 2 && absY == 2);

        if (!isNormalMove && !isJumpMove) 
        {
            return false;
        }

        boolean IsKing = (PieceStatus == Status.KING_PIECE_WHITE || PieceStatus == Status.KING_PIECE_RED);

        if (!IsKing) 
        {
            if (PlayerColor == 1) 
            { 
  
                if (DeltaX < 0)
                {
                    return false;
                }
            } 
            else 
            { 
                if (DeltaX > 0) 
                {
                    return false;
                }
            }
        }
        else 
        {
            // Kings can move anywhere it wants, so nothing
        }

        return true;
    }
    private boolean IsCaptureForced(int PlayerColor) 
    {
        return CurrentPlayerHasJump;
    }
    private boolean CanPieceJump(int X, int Y, int PlayerColor) 
    {
        // Optimized and final Algorithm for this method is that we assign the correct group of coordinates
        // to work around, then we just go through them and check if a jump is available or not

        Status currentStatus = board[X][Y].GetStatus();

        int[] XForJump, YForJump, XForEnemy, YForEnemy;
        Status NormalEnemyState, KingEnemyState;

        if (PlayerColor == 1) 
        { 
            // White's Turn
            if (currentStatus == Status.NORMAL_PIECE_WHITE) 
            {
                XForJump = W_JUMP_R; YForJump = W_JUMP_C; XForEnemy = W_ENEMY_R; YForEnemy = W_ENEMY_C;
            } 
            else if (currentStatus == Status.KING_PIECE_WHITE) 
            {
                XForJump = K_JUMP_R; YForJump = K_JUMP_C; XForEnemy = K_ENEMY_R; YForEnemy = K_ENEMY_C;
            }
            else
            {
                return false; // It's Red's turn, but this is a White piece
            }
            NormalEnemyState = Status.NORMAL_PIECE_RED;
            KingEnemyState = Status.KING_PIECE_RED;
            
        } 
        else 
        { 
            // Red's Turn
            if (currentStatus == Status.NORMAL_PIECE_RED) 
            {
                XForJump = R_JUMP_R; YForJump = R_JUMP_C; XForEnemy = R_ENEMY_R; YForEnemy = R_ENEMY_C;
            } 
            else if (currentStatus == Status.KING_PIECE_RED) 
            {
                XForJump = K_JUMP_R; YForJump = K_JUMP_C; XForEnemy = K_ENEMY_R; YForEnemy = K_ENEMY_C;
            } 
            else
            {
                return false; // It's Red's turn, but this is a White piece
            }
            NormalEnemyState = Status.NORMAL_PIECE_WHITE;
            KingEnemyState = Status.KING_PIECE_WHITE;
        }


        for (int i = 0; i < XForJump.length; i++) 
        {
            int Dest_X = X + XForJump[i];
            int Dest_Y = Y + YForJump[i];

            if (IsValidCoordinate(Dest_X, Dest_Y) == true) 
            {
                int EnemyX = X + XForEnemy[i];
                int EnemyY = Y + YForEnemy[i];
                
                Status DestTileStatus = board[Dest_X][Dest_Y].GetStatus();
                Status EnemyTileStatus = board[EnemyX][EnemyY].GetStatus();

                if (DestTileStatus == Status.EMPTY && (EnemyTileStatus == NormalEnemyState || EnemyTileStatus == KingEnemyState)) 
                {
                    return true; 
                }
            }
        }

        return false;
    }
    private boolean CanPieceMoveNormal(int X, int Y, int PlayerColor) 
    {
        Status CurrentStatus = board[X][Y].GetStatus();

        if (CurrentStatus == Status.EMPTY) 
        {
            return false;
        }

        int[] XForMove, YForMove;

        // Assign correct arrays based on turn and piece
        if (PlayerColor == 1) // White Player
        {
            if (CurrentStatus == Status.NORMAL_PIECE_WHITE) 
            {
                XForMove = W_MOVE_R; YForMove = W_MOVE_C;
            } 
            else if (CurrentStatus == Status.KING_PIECE_WHITE) 
            {
                XForMove = K_MOVE_R; YForMove = K_MOVE_C;
            }
            else
            {
                return false; // It's White's turn, but this is a Red piece
            }
        } 
        else // Red Player
        { 
            if (CurrentStatus == Status.NORMAL_PIECE_RED) 
            {
                XForMove = R_MOVE_R; YForMove = R_MOVE_C;
            } 
            else if (CurrentStatus == Status.KING_PIECE_RED) 
            {
                XForMove = K_MOVE_R; YForMove = K_MOVE_C;
            }
            else
            {
                return false; // It's Red's turn, but this is a White piece
            }
        }

        // Looping through all posible moves
        for (int i = 0; i < XForMove.length; i++) 
        {
            int Dest_X = X + XForMove[i];
            int Dest_Y = Y + YForMove[i];

            if (IsValidCoordinate(Dest_X, Dest_Y)) 
            {
                if (board[Dest_X][Dest_Y].GetStatus() == Status.EMPTY) 
                {
                    return true; // Found a valid move
                }
            }
        }

        return false; // Piece is blocked
    }

    // Following Members are helpers to the function CanPieceJump
    // The Optimized Algorithm is that we allocate and use memory about 96 Bytes more that the usual but
    // we don't generate the cordinates for each piece real-time, instead they are ready to use also
    // dont need to allocate on Heap on each call of the CanPieceJump

    // Jump Coordinates and their Enemy Coordinates for each piece type

    // Normal White Pieces and their Enemies Coordinates 
    private static final int[] W_JUMP_R  = {2, 2};
    private static final int[] W_JUMP_C  = {-2, 2};
    private static final int[] W_ENEMY_R = {1, 1};
    private static final int[] W_ENEMY_C = {-1, 1};
    // Normal Red Pieces and their Enemies Coordinates 
    private static final int[] R_JUMP_R  = {-2, -2};
    private static final int[] R_JUMP_C  = {-2, 2};
    private static final int[] R_ENEMY_R = {-1, -1};
    private static final int[] R_ENEMY_C = {-1, 1};
    // General Kings and their Enemies Coordinates
    private static final int[] K_JUMP_R  = {-2, -2, 2, 2};
    private static final int[] K_JUMP_C  = {-2, 2, -2, 2};
    private static final int[] K_ENEMY_R = {-1, -1, 1, 1};
    private static final int[] K_ENEMY_C = {-1, 1, -1, 1};

    // Normal Moves

    // Normal White Pieces Moves Coordinates
    private static final int[] W_MOVE_R = {1, 1};
    private static final int[] W_MOVE_C = {-1, 1};

    // Normal Red Pieces Moves Coordinates
    private static final int[] R_MOVE_R = {-1, -1};
    private static final int[] R_MOVE_C = {-1, 1};

    // Kings can move in all 4 directions
    private static final int[] K_MOVE_R = {-1, -1, 1, 1};
    private static final int[] K_MOVE_C = {-1, 1, -1, 1};

    private boolean IsValidCoordinate(int X, int Y) 
    {
        return X >= 0 && X < 8 && Y >= 0 && Y < 8;
    }

    private final Tile[][] board;
    private int RedPieceCount = 12;
    private int WhitePieceCount = 12;
    private boolean IsMidJump = false;

    // Player State Variables
    private boolean CurrentPlayerHasJump = false;
    private boolean CurrentPlayerHasAnyMove = false;
}