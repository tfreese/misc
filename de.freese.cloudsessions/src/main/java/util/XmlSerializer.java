package util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Thomas Freese
 */
public class XmlSerializer
{
	/**
	 * @param xml String
	 * @return Object
	 */
	public static Object fromXML(final String xml)
	{
		return fromXML(xml, null);
	}

	/**
	 * @param xml String
	 * @param clz Class
	 * @return Object
	 */
	public static Object fromXML(final String xml, final Class<?> clz)
	{
		if (xml == null)
		{
			return null;
		}

		XStream xstream = new XStream(new DomDriver());
		Object o = xstream.fromXML(xml);

		if ((clz != null) && (clz.isInstance(o) == false))
		{
			throw new RuntimeException("instance isn't " + clz.getName());
		}

		return o;
	}

	/**
	 * @param o Object
	 * @return String
	 */
	public static String toXML(final Object o)
	{
		if (o == null)
		{
			return null;
		}

		XStream xstream = new XStream(new DomDriver());

		return xstream.toXML(o);
	}
}
