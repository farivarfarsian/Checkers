package Effect;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Effect 
{
    public Effect(JComponent RenderTarget)
    {
        this.RenderTarget = RenderTarget;
    }

    public void Run() 
    {
        ConfettiParticles.clear();

        // Create 150 confetti particles
        for (int i = 0; i < 150; i++) 
        {
            ConfettiParticles.add(new Confetti(640)); 
        }

        // Stop any existing effect timer
        if (EffectTimer != null && EffectTimer.isRunning()) 
        {
            EffectTimer.stop();
        }

        // Defining the time for 16ms (about 60 FPS more than enough for a Checkers game)
        EffectTimer = new Timer(16, e -> 
            {
                boolean ParticlesVisible = false;

                for (Confetti p : this.ConfettiParticles) 
                {
                    p.Update();
                    // Check if at least one particle is still falling inside the window
                    if (p.Y < 680) // 640 + the status bar(40px)
                    {
                        ParticlesVisible = true;
                    }
                }

                this.RenderTarget.repaint(); // Update and Draw

                // The Effect iss done, clsoe the timer
                if (!ParticlesVisible) 
                {
                    EffectTimer.stop();
                    ConfettiParticles.clear();
                }
            });

        EffectTimer.start();
    }

    public void Render(Graphics2D Graphics2D) 
    {
        if (this.ConfettiParticles.isEmpty()) 
        {
            return;
        }

        Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Confetti element : this.ConfettiParticles) 
        {
            AffineTransform OriginalTransform = Graphics2D.getTransform();

            Graphics2D.translate(element.X, element.Y);
            Graphics2D.rotate(Math.toRadians(element.Angle));
            Graphics2D.setColor(element.color);
            Graphics2D.fillRect(-element.Width / 2, -element.Height / 2, element.Width, element.Height);

            Graphics2D.setTransform(OriginalTransform); // Back to original transform for the scene
        }
    }

    private final ArrayList<Confetti> ConfettiParticles = new ArrayList<Confetti>();
    private Timer EffectTimer; // For limiting the update for each effect and controling the presentation time
    private final JComponent RenderTarget;
}
