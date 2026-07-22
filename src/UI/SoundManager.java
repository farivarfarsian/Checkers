package UI;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager 
{
    public static void Play(String FileName) 
    {
        // Using mutil-threading to not hold up the main thread(game) if the loading takes longer or something comes up
        new Thread(() -> 
        {
            try 
            {
                File File = new File("assets/sfx/" + FileName);
                if (!File.exists()) return;

                AudioInputStream AudioStream = AudioSystem.getAudioInputStream(File);
                Clip Clip = AudioSystem.getClip();
                Clip.open(AudioStream);
                Clip.start();

                // When the sound finishes (tracking with the lambda below), we close the clip and free the memory
                Clip.addLineListener(
                    event -> 
                    {
                        if (event.getType() == LineEvent.Type.STOP) 
                        {
                            Clip.close();
                        }
                    }
                );

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
            {
                //Debug Retired Code: System.err.println("Error playing sound: " + FileName);
                e.printStackTrace();
            }
        })
        .start();
    }
}