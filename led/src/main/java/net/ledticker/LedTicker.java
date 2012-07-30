package net.ledticker;

import java.awt.Color;

import javax.swing.JComponent;

import net.led.elements.Element;

public interface LedTicker
{

	public abstract void addElement(Element tickerelement);

	public abstract void removeElement(Element tickerelement);

	public abstract void removeAll();

	public abstract void update(Element tickerelement);

	public abstract void updateAll();

	public abstract void setDotSize(int i, int j);

	public abstract void setDotGaps(int i, int j);

	public abstract void setElementGap(int i);

	public abstract void setTokenGap(int i);

	public abstract void setSpeed(int i);

	public abstract void setBackgroundColor(Color color);

	public abstract void setDotOffColor(Color color);

	public abstract void startAnimation();

	public abstract void stopAnimation();

	public abstract void pauseAnimation();

	public abstract JComponent getTickerComponent();
}
