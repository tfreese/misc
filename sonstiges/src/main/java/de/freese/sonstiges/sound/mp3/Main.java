/**
 * Created: 26.04.2012
 */

package de.freese.sonstiges.sound.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javazoom.jl.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class Main extends Thread
{
	/**
	 * 
	 */
	private String filename;

	/**
	 * 
	 */
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		File soundFileMP3 = new File("D:/sonstiges/AreYouFeelinMe.mp3");

		// logger.debug("start playing " + soundFileMP3.getAbsolutePath());
		// Main mp3Sound = new Main(soundFileMP3.getAbsolutePath());
		// mp3Sound.start();

		Player player = new Player(new FileInputStream(soundFileMP3));
		player.play();
	}

	/**
	 * Erstellt ein neues {@link Main} Object.
	 * 
	 * @param filename String
	 */
	public Main(final String filename)
	{
		super();

		this.filename = filename;
	}

	/**
	 * @param audioFormat {@link AudioFormat}
	 * @return {@link SourceDataLine}
	 * @throws LineUnavailableException Falls was schief geht.
	 */
	private synchronized SourceDataLine getLine(final AudioFormat audioFormat)
		throws LineUnavailableException
	{
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);

		return res;
	}

	/**
	 * @param targetFormat {@link AudioFormat}
	 * @param din {@link AudioInputStream}
	 * @throws IOException Falls was schief geht.
	 * @throws LineUnavailableException Falls was schief geht.
	 */
	private synchronized void rawplay(final AudioFormat targetFormat, final AudioInputStream din)
		throws IOException, LineUnavailableException
	{
		byte[] data = new byte[4096];
		SourceDataLine line = getLine(targetFormat);

		if (line != null)
		{
			// Start
			line.start();
			int nBytesRead = 0;
			int nBytesWritten = 0;

			while (nBytesRead != -1)
			{
				nBytesRead = din.read(data, 0, data.length);

				if (nBytesRead != -1)
				{
					nBytesWritten = line.write(data, 0, nBytesRead);
				}
			}

			// Stop
			line.drain();
			line.stop();
			line.close();
			din.close();

			logger.info("Bytes Written: {}", Integer.valueOf(nBytesWritten));
		}
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		try
		{
			File file = new File(this.filename);

			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioInputStream din = null;
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat =
					new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
							16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
							baseFormat.getSampleRate(), false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);

			// play it...
			rawplay(decodedFormat, din);
			in.close();

		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}
		finally
		{
			logger.debug("finish playing " + this.filename);
		}
	}
}
