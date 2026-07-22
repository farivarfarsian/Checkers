package Effect;

import java.awt.Color;

// Inner class to handle physics & rendering of each individual confetti piece
public class Confetti 
{
    double X, Y;
    double vX, vY; // For random left and right moving and falling
    double Angle; // For rotating the particle
    double AngularSpeed; // Speed of rotating
    int Width, Height;
    Color color;

    public Confetti(int BoardWidth) 
    {
        this.X = Math.random() * BoardWidth;
        this.Y = -20 - (Math.random() * 100); // Start slightly above the visible screen
        this.vX = (Math.random() - 0.5) * 4;   // Random horizontal drift (left/right)
        this.vY = 2 + Math.random() * 5;       // Random falling speed
        this.Angle = Math.random() * 360;      // Random rotation
        this.AngularSpeed = (Math.random() - 0.5) * 10; // Clockwise or anit-Clockwise
        this.Width = 8 + (int)(Math.random() * 6);
        this.Height = 4 + (int)(Math.random() * 4);

        // Color
        this.color = new Color(Color.HSBtoRGB((float)Math.random(), 0.85f, 0.95f));
    }

    public void Update() 
    {
        X += vX;
        Y += vY;
        Angle += AngularSpeed;
    }
}
