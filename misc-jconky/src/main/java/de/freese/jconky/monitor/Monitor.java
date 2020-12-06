// Created: 01.12.2020
package de.freese.jconky.monitor;

import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public interface Monitor
{
    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @return double Höhe
     */
    public double paintValue(GraphicsContext gc, final double width);

    /**
     *
     */
    public void updateValue();
}
