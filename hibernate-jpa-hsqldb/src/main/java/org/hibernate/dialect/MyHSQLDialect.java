/**
 * 22.12.2006
 */
package org.hibernate.dialect;

/**
 * @author Thomas Freese
 */
public class MyHSQLDialect extends HSQLDialect
{
	/**
	 * Creates a new {@link MyHSQLDialect} object.
	 */
	public MyHSQLDialect()
	{
		super();
	}

	/**
	 * @see org.hibernate.dialect.HSQLDialect#getSequenceNextValString(java.lang.String)
	 */
	@Override
	public String getSequenceNextValString(final String sequenceName)
	{
		return "SELECT NEXT VALUE FOR " + sequenceName + " FROM DUAL";
	}
}
