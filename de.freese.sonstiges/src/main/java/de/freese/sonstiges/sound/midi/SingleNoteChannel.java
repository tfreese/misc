/*
 * Created on 07.08.2003
 */
package de.freese.sonstiges.sound.midi;

/**
 */
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 * @author Thomas Freese
 */
public class SingleNoteChannel
{
	/**
     *
     */
	private MidiChannel channel;

	/**
	 * Creates a new {@link SingleNoteChannel} object.
	 */
	public SingleNoteChannel()
	{
		try
		{
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			this.channel = synth.getChannels()[0];
		}
		catch (MidiUnavailableException ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		new SingleNoteChannel().playNote(60);
	}

	/**
	 * @param note int
	 */
	public void playNote(final int note)
	{
		this.channel.noteOn(note, 70);

		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		this.channel.noteOff(note, 70);

		System.exit(0);
	}
}
