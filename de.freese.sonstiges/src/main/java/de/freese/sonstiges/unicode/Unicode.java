package de.freese.sonstiges.unicode;

import de.freese.base.core.math.ExtMath;

/**
 * @author Thomas Freese
 */
public class Unicode
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		// # \u00C4 = `Ä`, AE
		// # \u00D6 = `Ö`, OE
		// # \u00DC = `Ü`, UE
		// # \u00DF = `ß`, ss
		// # \u00E4 = `ä`, ae
		// # \u00F6 = `ö`, oe
		// # \u00FC = `ü`, ue

		long ae = 'ä';
		System.out.println((ae >>> 4) & 0xF);
		System.out.println((ae >>> 0) & 0xF);

		System.out.println(((int) ae) + " = " + ExtMath.dec2Base(ae, 16));
		System.out.println((char) ExtMath.base2Dec("00E4", 16));
	}
}
