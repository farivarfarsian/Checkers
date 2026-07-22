# Java Checkers Game

A highly optimized, lightweight 2D Checkers game built in **Java** utilizing **Swing** for a modern, fluid drag-and-drop graphical user interface.

<p align="center">
  <img src="assets/Gameplay.gif" alt="Checkers Gameplay" width="320"/>
</p>

## 📐 Coordinate System & Structure Design Note

The board grid mapping translates screen-space pixels to internal array coordinates. The backend engine evaluates moves according to the following directional orientation:

```text
|-----------> Y (Columns)        N (North / White Direction)
|                                ↑
|                            W ←   → E
|                                ↓
↓                                S (South / Red Direction)
X (Rows)
```

*   **X-Axis (Rows):** Runs vertically. Moving South (down the board towards index 7) increases `X`. Normal **White** pieces move in this direction.
*   **Y-Axis (Columns):** Runs horizontally. Moving East (right across the board towards index 7) increases `Y`.
*   **Diagonal Calculations:** Delta-offsets are computed cleanly through basic coordinate math (`|Dest_X - X| == |Dest_Y - Y|`).

## 🚀 Key Features

*   **Zero-Heap Gameplay Loop:** Uses static final pre-allocated coordinate lookup arrays for piece moves and jumps. We don't allocate much memory on heap during the actual Game Loop therefore it prevents Java garbage collection overhead, ensuring flat memory consumption and microsecond validation times.
*   **Single-Pass Turn Evaluation:** Combines Game Over state checking and Forced Jump detection into a single board sweep.
*   **Fluid GUI:** Features anti-aliased rendering for smooth piece edges.

## 📁 Project Directory Structure

```text
Checkers/
|
├── assets/                  # Graphic resources (.png)
|   ├── board.png            # Static checkered texture
|   ├── RedNormal.png        # Red normal piece
|   ├── RedKing.png          # Red king piece
|   ├── WhiteNormal.png      # White normal piece
|   └── WhiteKing.png        # White king piece
|   ├── sfx/                 # Sound effect files
|       ├── click.wav
|       ├── promotion.wav
|       ├── success.wav
|       ├── take.wav
|
├── bin/                     # Java Byte Code files    
|
├── src/                     # Java source files
|   ├── Board/
|   |   ├── Board.java       # Core board logic & state evaluation
|   |   ├── Tile.java        # Data model representing individual squares
|   |   └── Status.java      # Enum definitions (EMPTY, NORMAL_RED, etc.)
|   |
|   ├── Game/
|   |   ├── Game.java        # Player turn-swapping & move management
|   |   └── Player.java      # Player state & profile data
|   |
|   ├── UI/
|   |   └── UI.java          # Main window, board panel, drag-and-drop
|   |
|   └── Main.java            # App entry-point
|
└── README.md                # Project documentation
```

## 🛠️ Building & Running the Game

### Prerequisites
*   **Java Development Kit (JDK) 8 or higher** installed on your system.
*   **Java Swing** (usually installed with JDK)

### Compiling and Launching

You can compile and launch the Game using the `build.bat` file or do it manually with following the steps below:

Navigate to your root project directory (`Checkers/`) and run the following commands:

1.  **Compile all source files** into the binary directory:
    ```bash
    javac -d bin src/Board/*.java src/Game/*.java src/UI/*.java src/Main.java
    ```
    The compiled output files will be generated in the `bin` folder.

2.  **Run the game:**
    ```bash
    java -cp bin Main
    ```


## 🎮 How to Play

1.  **Move Pieces:** Left-click and hold on any of your active pieces, drag it to a valid diagonal square, and release to play the move.
2.  **Mandatory Captures:** If a jump is available on the board, the game status bar will display `(Mandatory Jump Active)`. The game will block normal moves until you make a capturing jump.
3.  **Double-Jumping:** If your piece lands after a jump and still has another viable jump available, the board will display `Double jump required!`. The player turn does not switch, and you must drag that same piece to complete the capture chain.
4.  **Promotion:** Reaching the opposite end of the board automatically promotes your normal piece into a King, enabling it to move and capture backward and forward.
5.  **Game Over**: Game is over whenever there's no piece left for your opponent or your opponent doesn't have any legal moves.
