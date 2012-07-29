/**
 * Created: 14.04.2012
 */

package de.freese.spring.integration.cafe;

import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Thomas Freese
 */
public class TestCafe
{
	/**
	 * Erstellt ein neues {@link TestCafe} Object.
	 */
	public TestCafe()
	{
		super();
	}

	/**
	 * @param context {@link AbstractApplicationContext}
	 */
	private void testCafe(final AbstractApplicationContext context)
	{
		context.registerShutdownHook();

		// context.getBean("orders");
		// context.getBean("coldDrinks");
		Cafe cafe = (Cafe) context.getBean("cafe");

		for (int i = 1; i <= 20; i++)
		{
			Order order = new Order(i);
			order.addItem(DrinkType.LATTE, false);
			order.addItem(DrinkType.MOCHA, true);
			cafe.placeOrder(order);
		}

		context.close();
	}

	/**
	 * 
	 */
	@Test
	public void testCafeDemoWithAnnotationSupport()
	{
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext("cafeDemo-annotation.xml");

		testCafe(context);
	}

	/**
	 * 
	 */
	@Test
	public void testCafeDemoWithXmlSupport()
	{
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("cafeDemo-xml.xml");

		testCafe(context);
	}
}
