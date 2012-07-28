/**
 * Created: 20.01.2011
 */

package de.freese.sonstiges.pentaho;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

/**
 * TestKlasse f�r Pentaho Data Integration.
 * 
 * @author Thomas Freese
 */
public class TestPentaho
{
	/**
	 *
	 */
	@BeforeClass
	public static void beforeClass()
	{
		try
		{
			// LocalNamingService.init();
			KettleEnvironment.init();
			// EnvUtil.environmentInit();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Erstellt ein neues {@link TestPentaho} Object.
	 */
	public TestPentaho()
	{
		super();
	}

	/**
	 * @throws Exception Falls was schief geht.
	 */
	@Test
	public void parameterTest() throws Exception
	{
		File file = new File("pentahoTest.ktr");

		TransMeta transMeta = new TransMeta(file.getAbsolutePath());
		Trans trans = new Trans(transMeta);

		trans.setVariable("arbeitsStand", "225379");
		// trans.setVariable("arbeitsStand", "31560");
		trans.execute(null);
		trans.waitUntilFinished();

		if (trans.getErrors() > 0)
		{
			throw new RuntimeException("There were errors during transformation execution.");
		}

		Result result = trans.getResult();
		List<RowMetaAndData> rows = result.getRows();

		for (RowMetaAndData row : rows)
		{
			long id = row.getInteger("id", -1);
			// long arbeitsStand = row.getInteger("arbeitsStand", -1);
			String name = row.getString("name", "");

			System.out.println(String.format("%3d, %s", id, name));
		}
	}
}
