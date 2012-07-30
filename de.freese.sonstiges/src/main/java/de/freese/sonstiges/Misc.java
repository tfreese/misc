package de.freese.sonstiges;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author Thomas Freese
 */
public class Misc
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		// System.out.println("Anzahl Prozezzoren: " + Runtime.getRuntime().availableProcessors());
		// URL url = Misc.class.getResource("/data/shape");
		//
		// File file = new File(url.getFile());
		//
		// File[] files = file.listFiles();
		//
		// for (int i = 0; i < files.length; i++)
		// {
		// File f = files[i];
		//
		// System.out.println(f.getAbsolutePath());
		// }
		// for (int i = 0; i < 10; i++)
		// {
		// System.out.println((Math.random() - 0.5D) + " - "
		// + (((Double) (-0.05 + (Math.random() * 0.1))).floatValue()));
		// }

		// static int fibonacci(int n) {
		// if ((n==1) || (n==2)) return 1;
		// else return fibonacci(n-1)+fibonacci(n-2);
		// }
		//
		// for ( i = 1 ; i <= schritte ; i++ ){
		// neuer_wert = wert1 + wert2;
		// wert1 = wert2;
		// wert2 = neuer_wert;
		// System.out.println("Schritt " + i + ": " + neuer_wert );
		// }

		// System.out.println((200 * 199) % 199);
		// for (Object value : System.getProperties().keySet())
		// {
		// System.out.println(value.toString() + "\t" + System.getProperty(value.toString()));
		// }
		//
		// System.out.println();
		//
		// for (String string : ImageIO.getReaderFormatNames())
		// {
		// System.out.println(string);
		// }

		// String regex = "20\\d\\d\\d\\d\\d\\d.\\d\\d\\d\\d\\d\\d";
		// String fileName =
		// "webdavHTTPS://https://sd2dav.1und1.de/maven/repository-snapshots/de/auel/auel-swing/0.0.1-SNAPSHOT/auel-swing-0.0.1-20120322.171541-1-sources.jar";
		// Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		// Matcher matcher = pattern.matcher(fileName);
		//
		// while (matcher.find())
		// {
		// System.out.print("Start index: " + matcher.start());
		// System.out.println("; End index: " + matcher.end());
		//
		// String file = matcher.group();
		// System.out.println(file);
		// System.out.println(fileName.substring(matcher.start(), matcher.end()));
		// }

		// Tasks
		// int length = 999;
		// int prozessors = 4;
		// int tasks = 1 + (((length + 7) >>> 3) / prozessors);
		// System.out.println(tasks);

		// Mocking
		// Club club = Mockito.mock(Club.class);
		// Club club = new Club();
		// club = Mockito.spy(club);
		//
		// Mockito.when(club.getEmployees()).thenReturn(new Integer(5));
		// int employees = club.getEmployees();
		//
		// System.out.println(employees);

		// SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		SimpleNamingContextBuilder builder =
				SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		builder.bind("java:comp/env/bla", "BlaBla");
		// builder.activate();

		Context context = new InitialContext();
		Object object = context.lookup("java:comp/env/bla");
		System.out.println(object);

		builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		builder.bind("java:comp/env/blo", "BloBlo");
		object = context.lookup("java:comp/env/blo");
		System.out.println(object);
	}
}
