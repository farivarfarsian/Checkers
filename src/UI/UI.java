package UI;

import Game.Game;
import Board.Status;
import Board.Tile;
import Effect.Effect;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UI extends JFrame 
{
    public UI(Game Game) 
    {
        this.Game = Game;

        setTitle("Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Not Supporting Resizable Game at the moment
        setLayout(new BorderLayout()); // Splits the frame to 5 regions

        LoadAssets();

        // Setting Up the Board and Pieces
        BoardPanel = new BoardPanel();

        this.Effect = new Effect(BoardPanel);

        // Setting the Default Text for Status Bar
        StatusLabel = new JLabel("Red's Turn", SwingConstants.CENTER);
        StatusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        StatusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Creating a space

        add(BoardPanel, BorderLayout.CENTER);
        add(StatusLabel, BorderLayout.SOUTH);
        pack(); // Packing the added Layouts

        setLocationRelativeTo(null);
        setVisible(true);

        String RedName = JOptionPane.showInputDialog(
            null, 
            "Enter Name for Red Player:", 
            "Player Registration", 
            JOptionPane.QUESTION_MESSAGE
        );

        String WhiteName = JOptionPane.showInputDialog(
            null, 
            "Enter Name for White Player:", 
            "Player Registration", 
            JOptionPane.QUESTION_MESSAGE
        );

        this.Game.GetPlayers()[0].SetPlayerID(RedName);
        this.Game.GetPlayers()[1].SetPlayerID(WhiteName);
    }

    public void Update() 
    {
        int CurrentPlayerColor = this.Game.GetCurrentPlayer().GetPlayerColor();
        
        this.Game.GetBoard().EvaluatePlayerState(CurrentPlayerColor);

        if (this.Game.IsGameOver() == true) 
        {
            SoundManager.Play("success.wav");
            
            this.Effect.Run(); // Confetti!

            // Wait 700ms for better visuals
            javax.swing.Timer DelayTimer = new javax.swing.Timer(700, e-> 
            {
                String Winner = "";
                if (CurrentPlayerColor == 1)
                {
                    Winner += "Red (Player " + this.Game.GetPlayers()[0].GetPlayerID() + ")";
                }
                else
                {
                    Winner += "White (Player " + this.Game.GetPlayers()[1].GetPlayerID() + ")";
                }

                StatusLabel.setText("Game Over! " + Winner + " wins!");
                JOptionPane.showMessageDialog(this, "Game Over! " + Winner + " Wins!", "Match Ended", JOptionPane.INFORMATION_MESSAGE);
            });

            DelayTimer.setRepeats(false);
            DelayTimer.start();

            return;
        }

        String TurnText = (CurrentPlayerColor == 0) ? "Red's Turn" : "White's Turn";
        if (this.Game.GetBoard().GetIsMidJump() == true) 
        {
            TurnText += " (Must Double Jump!)";
        } 
        else if (this.Game.GetBoard().GetCurrentPlayerHasJump() == true) 
        {
            TurnText += " (Must Jump!)";
        }
        
        StatusLabel.setText(TurnText + " | Red: " + this.Game.GetBoard().GetRedPieceCount() + " White: " + this.Game.GetBoard().GetWhitePieceCount());
    }  
       
    private final Game Game;
    private final BoardPanel BoardPanel;

    // For Confetti Effect
    private final Effect Effect;

    // For Bottom Status Label
    private final JLabel StatusLabel;

    // Texture Assets
    private BufferedImage BoardTexture;
    private BufferedImage RedNormalTexture;
    private BufferedImage RedKingTexture;
    private BufferedImage WhiteNormalTexture;
    private BufferedImage WhiteKingTexture;

    private void LoadAssets() 
    {
        try 
        {
            BoardTexture = ImageIO.read(new File("assets/board.png"));
            RedNormalTexture = ImageIO.read(new File("assets/RedNormal.png"));
            RedKingTexture = ImageIO.read(new File("assets/RedKing.png"));
            WhiteNormalTexture = ImageIO.read(new File("assets/WhiteNormal.png"));
            WhiteKingTexture = ImageIO.read(new File("assets/WhiteKing.png"));
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, 
                "Couldn't load the assets, try again",
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private class BoardPanel extends JPanel 
    {
        public BoardPanel() 
        {
            setPreferredSize(new Dimension(640, 640));

            PieceDragHandler DragHandler = new PieceDragHandler();

            addMouseListener(DragHandler);
            addMouseMotionListener(DragHandler);
        }

        @Override protected void paintComponent(Graphics Graphics) 
        {
            super.paintComponent(Graphics);
            Graphics2D Graphics2D = (Graphics2D)Graphics;
            Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Board
            Graphics2D.drawImage(BoardTexture, 0, 0, 640, 640, null);

            // Draw Pieces
            for (int r = 0; r < 8; r++) 
            {
                for (int c = 0; c < 8; c++) 
                {
                    // Skip painting the piece currently being dragged
                    if (r == DragStartX && c == DragStartY) 
                    {
                        continue;
                    }

                    if (Game.GetBoard().GetTile(r, c).GetStatus() != Status.EMPTY) 
                    {
                        DrawPiece(Graphics2D, Game.GetBoard().GetTile(r, c).GetStatus(), c * TILE_SIZE, r * TILE_SIZE);
                    }
                }
            }

            // Draw Dragged Piece
            if (DragPieceStatus != null && DragPoint != null) 
            {
                // Center the cursor on Dragging Piece
                DrawPiece(Graphics2D, DragPieceStatus, DragPoint.x - (TILE_SIZE / 2), DragPoint.y - (TILE_SIZE / 2));
            }

            Effect.Render(Graphics2D);
        }

        private void DrawPiece(Graphics2D g, Status Status, int X, int Y) 
        {
            BufferedImage TextureToRender = null;

            switch (Status) 
            {
                case NORMAL_PIECE_RED:
                    TextureToRender = RedNormalTexture;
                    break;
                case KING_PIECE_RED:
                    TextureToRender = RedKingTexture;
                    break;
                case NORMAL_PIECE_WHITE:
                    TextureToRender = WhiteNormalTexture;
                    break;
                case KING_PIECE_WHITE:
                    TextureToRender = WhiteKingTexture;
                    break;
                case EMPTY:
                    break;
            }

            if (TextureToRender != null) 
            {
                // We Center the Piece in the Tile Positin with 6 Pixels
                int Size = TILE_SIZE - (6 * 2);
                g.drawImage(TextureToRender, X + 6, Y + 6, Size, Size, null);
            }
        }

        // We set the board as 640*640, with each Tile being 80 pixels
        private static final int TILE_SIZE = 80;

        // Drag Variables for Implementing Drag and Drop Gameplay for Pieces
        private Point DragPoint = null;
        private int DragStartX = -1;
        private int DragStartY = -1;
        private Status DragPieceStatus = null;

        // Drag Handler for Actual GamePlay
        private class PieceDragHandler extends MouseAdapter 
        {    
            @Override public void mousePressed(MouseEvent Event) 
            {
                // Game over, do nothing
                if (Game.IsGameOver() == true) 
                {
                    return;
                }

                int CurrentPlayerColor = Game.GetCurrentPlayer().GetPlayerColor();
                int Y = Event.getX() / TILE_SIZE;
                int X = Event.getY() / TILE_SIZE;

                Tile ClickedTile = Game.GetBoard().GetTile(X, Y);
                Status ClickedTileStatus = ClickedTile.GetStatus();

                boolean IsWhitePiece = (ClickedTileStatus == Status.NORMAL_PIECE_WHITE || ClickedTileStatus == Status.KING_PIECE_WHITE);
                boolean IsRedPiece = (ClickedTileStatus == Status.NORMAL_PIECE_RED || ClickedTileStatus == Status.KING_PIECE_RED);


                // Empty Tile, do nothing
                if (ClickedTile == null || ClickedTile.GetStatus() == Status.EMPTY) 
                {
                    return;
                }

                // Enemy Piece, do nothing
                if ((CurrentPlayerColor == 1 && !IsWhitePiece) || (CurrentPlayerColor == 0 && !IsRedPiece)) 
                {
                    return;
                }

                // The correct piece selected, track mouse movement
                DragStartX = X;
                DragStartY = Y;
                DragPieceStatus = ClickedTileStatus;
                DragPoint = Event.getPoint();

                repaint();
            }

            @Override public void mouseDragged(MouseEvent Event) 
            {
                if (DragPieceStatus == null)
                {
                    return;
                }
                
                DragPoint = Event.getPoint();
                
                repaint();
            }

            @Override public void mouseReleased(MouseEvent Event) 
            {
                // Guard Clause: Only execute if we were holding a piece
                if (DragPieceStatus == null)
                {
                    return;
                }

                int DestY = Event.getX() / TILE_SIZE;
                int DestX = Event.getY() / TILE_SIZE;

                // Execute the backend engine move
                boolean IsMoveLegal = Game.Move(DragStartX, DragStartY, DestX, DestY);

                // Update UI
                if (IsMoveLegal) 
                {
                    if((Math.abs(DragStartX - DestX) == 2) == false)
                    {
                        SoundManager.Play("click.wav");
                    }

                    Update();
                }
                
                // Clear the drag states
                DragStartX = -1;
                DragStartY = -1;
                DragPieceStatus = null;
                DragPoint = null;

                repaint();
            }
        }
    }
}