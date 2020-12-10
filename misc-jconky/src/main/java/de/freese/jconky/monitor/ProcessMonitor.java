// Created: 10.12.2020
package de.freese.jconky.monitor;

import de.freese.jconky.model.ProcessInfos;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public class ProcessMonitor extends AbstractMonitor
{
    /**
     *
     */
    private ProcessInfos processInfos = new ProcessInfos();

    /**
     * @see de.freese.jconky.monitor.Monitor#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        ProcessInfos infos = this.processInfos;

        return 0;
    }

    /**
     * @see de.freese.jconky.monitor.Monitor#updateValue()
     */
    @Override
    public void updateValue()
    {
        this.processInfos = getSystemMonitor().getProcessInfos();
    }
}
