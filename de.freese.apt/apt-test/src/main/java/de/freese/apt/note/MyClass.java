/**
 * Created: 05.12.2011
 */

package de.freese.apt.note;

/**
 * @author Thomas Freese
 */
@Note(value = "This class isn't finished")
public class MyClass
{
	/**
	 * 
	 */
	@Note("This field isn't finished")
	private String fieldA = null;

	/**
	 * Erstellt ein neues {@link MyClass} Object.
	 */
	@Note("Constructor isn't finished")
	public MyClass()
	{
		super();
	}

	/**
	  * 
	  */
	@Note("methodA isn't finished")
	public void methodA()
	{
		System.out.println(this.fieldA);
	}
}
