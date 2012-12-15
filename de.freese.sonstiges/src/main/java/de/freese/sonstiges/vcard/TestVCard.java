/**
 * Created: 25.12.2011
 */

package de.freese.sonstiges.vcard;

import java.io.File;
import java.util.List;

import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.io.BinaryFoldingScheme;
import net.sourceforge.cardme.io.CompatibilityMode;
import net.sourceforge.cardme.io.FoldingScheme;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.util.StringUtil;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardImpl;
import net.sourceforge.cardme.vcard.arch.VCardVersion;
import net.sourceforge.cardme.vcard.errors.VCardError;
import net.sourceforge.cardme.vcard.types.AdrType;
import net.sourceforge.cardme.vcard.types.TelType;

/**
 * @author Thomas Freese
 */
public class TestVCard
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		VCardEngine engine = new VCardEngine();
		engine.setCompatibilityMode(CompatibilityMode.RFC2426);

		File file = new File("Freese Thomas.vcf");

		VCard vcard = engine.parse(file);

		List<AdrType> addresses = vcard.getAdrs();

		for (AdrType address : addresses)
		{
			System.out.println(address);
		}

		List<TelType> telephones = vcard.getTels();

		for (TelType telephone : telephones)
		{
			System.out.println(telephone);
		}

		System.out.println();

		VCardWriter writer = new VCardWriter();
		writer.setOutputVersion(VCardVersion.V3_0);
		writer.setCompatibilityMode(CompatibilityMode.RFC2426);
		writer.setFoldingScheme(FoldingScheme.MIME_DIR);
		writer.setBinaryfoldingScheme(BinaryFoldingScheme.MIME_DIR);
		writer.setVCard(vcard);
		String vstring = writer.buildVCardString();

		VCardImpl vCardImpl = (VCardImpl) vcard;

		if (writer.hasErrors())
		{
			List<VCardError> errors = vCardImpl.getErrors();

			for (int j = 0; j < errors.size(); j++)
			{
				System.out.println(StringUtil.formatException(errors.get(j).getError()));
			}
		}

		System.out.println(vstring);
	}

	/**
	 * Erstellt ein neues {@link TestVCard} Object.
	 */
	public TestVCard()
	{
		super();
	}
}
