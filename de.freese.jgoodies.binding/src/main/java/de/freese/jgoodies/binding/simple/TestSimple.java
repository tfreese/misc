/**
 * 
 */
package de.freese.jgoodies.binding.simple;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.value.ValueModel;

/**
 * @author Thomas Freese
 */
public class TestSimple
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		MyBean bean = new MyBean();

		// Bean adapter is an adapter that can create many value model objects for a single
		// bean. It is more efficient than the property adapter. The 'true' once again means
		// we want it to observe our bean for changes.
		BeanAdapter<MyBean> adapter = new BeanAdapter<>(bean, true);

		ValueModel booleanModel = adapter.getValueModel("booleanValue");
		ValueModel stringModel = adapter.getValueModel("stringValue");

		// Creates Swing Components with the property adapter providing the underlying model.
		JTextField field = BasicComponentFactory.createTextField(stringModel);

		// JCheckBox checkbox = BasicComponentFactory.createCheckBox(booleanModel, "Boolean Value");
		JCheckBox checkbox = new JCheckBox("Boolean Value");
		Bindings.bind(checkbox, booleanModel);

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new GridLayout(2, 1));
		frame.getContentPane().add(checkbox);
		frame.getContentPane().add(field);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
}
