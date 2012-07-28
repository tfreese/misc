/**
 * Created: 26.04.2012
 */

package de.freese.sonstiges.sound;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

/**
 * To play sound using Clip, the process need to be alive.<br>
 * Hence, we use a Swing application.
 */
public class SoundClipTest extends JFrame
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4981933991308257211L;

	/**
	 * @param args final String[]
	 */
	public static void main(final String[] args)
	{
		new SoundClipTest();
	}

	/**
	 * Erstellt ein neues {@link SoundClipTest} Object.
	 */
	public SoundClipTest()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Test Sound Clip");
		this.setSize(300, 200);
		setVisible(true);

		try
		{
			File soundFileWAV = new File("C:/Windows/media/tada.wav");
			// File soundFileMP3 = new File("D:/sonstiges/AreYouFeelinMe.mp3");

			// Open an audio input stream.
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFileWAV);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);

			// Stop the player if it is still running
			if (clip.isRunning())
			{
				clip.stop();
			}

			// rewind to the beginning
			clip.setFramePosition(0);

			// play once
			clip.start();

			// repeat none (play once), can be used in place of start().
			// clip.loop(0);

			// repeat 5 times (play 6 times)
			// clip.loop(5);

			// repeat forever
			// clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		// dispose();
		// System.exit(0);
	}
}