package de.freese.sonstiges.dnd.demo.image;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.SystemColor;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * A BufferedImage of one of four types of arrow (up, down, left or right) drawn to the size specified on the constructor.
 */
public class CArrowImage extends BufferedImage
{
    /**
     *
     */
    public static final int ARROW_DOWN = 1;

    /**
     *
     */
    public static final int ARROW_LEFT = 2;

    /**
     *
     */
    public static final int ARROW_RIGHT = 3;

    /**
     *
     */
    public static final int ARROW_UP = 0;

    /**
     *
     */
    private final GeneralPath pathArrow = new GeneralPath();

    /**
     * Erstellt ein neues {@link CArrowImage} Object.
     *
     * @param nArrowDirection int
     */
    public CArrowImage(final int nArrowDirection)
    {
        this(15, 9, nArrowDirection);
    }

    /**
     * Creates a new CArrowImage object.
     *
     * @param width int
     * @param height int
     * @param arrowDirection int
     */
    public CArrowImage(final int width, final int height, final int arrowDirection)
    {
        super(width, height, TYPE_INT_ARGB_PRE); // Set the width, height and image type

        Map<Key, Object> map = new HashMap<>();
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        RenderingHints hints = new RenderingHints(map);

        Graphics2D g2 = createGraphics(); // Create a graphics context for this buffered image
        g2.setRenderingHints(hints);

        float h = getHeight();
        float w = getWidth();
        float w13 = w / 3;
        float w12 = w / 2;
        float w23 = (w * 2) / 3;
        float h13 = h / 3;
        float h12 = h / 2;
        float h23 = (h * 2) / 3;

        switch (arrowDirection)
        {
            case ARROW_UP:
                this.pathArrow.moveTo(w12, h12);
                this.pathArrow.lineTo(w12, 0);
                this.pathArrow.lineTo(w, h - 1);
                this.pathArrow.lineTo(0, h - 1);
                this.pathArrow.closePath();
                g2.setPaint(new GradientPaint(w13, h13, SystemColor.controlLtHighlight, w, h - 1, SystemColor.controlShadow));

                g2.fill(this.pathArrow);

                g2.setColor(SystemColor.controlDkShadow);
                g2.draw(new Line2D.Float(0, h - 1, w, h - 1));
                g2.setColor(SystemColor.controlShadow);
                g2.draw(new Line2D.Float(w12, 0, w, h - 1));
                g2.setColor(SystemColor.controlLtHighlight);
                g2.draw(new Line2D.Float(0, h - 1, w12, 0));

                break;

            case ARROW_DOWN:
                this.pathArrow.moveTo(w12, h12);
                this.pathArrow.lineTo(w, 0);
                this.pathArrow.lineTo(w12, h - 1);
                this.pathArrow.closePath();
                g2.setPaint(new GradientPaint(0, 0, SystemColor.controlLtHighlight, w23, h23, SystemColor.controlShadow));
                g2.fill(this.pathArrow);

                g2.setColor(SystemColor.controlDkShadow);
                g2.draw(new Line2D.Float(w, 0, w12, h - 1));
                g2.setColor(SystemColor.controlShadow);
                g2.draw(new Line2D.Float(w12, h - 1, 0, 0));
                g2.setColor(SystemColor.controlLtHighlight);
                g2.draw(new Line2D.Float(0, 0, w, 0));

                break;

            case ARROW_LEFT:
                this.pathArrow.moveTo(w - 1, h13);
                this.pathArrow.lineTo(w13, h13);
                this.pathArrow.lineTo(w13, 0);
                this.pathArrow.lineTo(0, h12);
                this.pathArrow.lineTo(w13, h - 1);
                this.pathArrow.lineTo(w13, h23);
                this.pathArrow.lineTo(w - 1, h23);
                this.pathArrow.closePath();
                g2.setPaint(new GradientPaint(0, 0, Color.white, // SystemColor.controlLtHighlight,
                        0, h, SystemColor.controlShadow));
                g2.fill(this.pathArrow);

                this.pathArrow.reset();
                this.pathArrow.moveTo(w13, 0);
                this.pathArrow.lineTo(w13, h13);
                this.pathArrow.moveTo(w - 1, h13);
                this.pathArrow.lineTo(w - 1, h23);
                this.pathArrow.lineTo(w13, h23);
                this.pathArrow.lineTo(w13, h - 1);
                g2.setColor(SystemColor.controlDkShadow);
                g2.draw(this.pathArrow);

                g2.setColor(SystemColor.controlShadow);
                g2.draw(new Line2D.Float(0, h12, w13, h - 1));

                this.pathArrow.reset();
                this.pathArrow.moveTo(0, h12);
                this.pathArrow.lineTo(w13, 0);
                this.pathArrow.moveTo(w13, h13);
                this.pathArrow.lineTo(w - 1, h13);
                g2.setColor(SystemColor.controlLtHighlight);
                g2.draw(this.pathArrow);

                break;

            case ARROW_RIGHT:
            default:
            {
                this.pathArrow.moveTo(0, h13);
                this.pathArrow.lineTo(w23, h13);
                this.pathArrow.lineTo(w23, 0);
                this.pathArrow.lineTo(w - 1, h12);
                this.pathArrow.lineTo(w23, h - 1);
                this.pathArrow.lineTo(w23, h23);
                this.pathArrow.lineTo(0, h23);
                this.pathArrow.closePath();
                g2.setPaint(new GradientPaint(0, 0, Color.white, // SystemColor.controlLtHighlight,
                        0, h, SystemColor.controlShadow));
                g2.fill(this.pathArrow);

                this.pathArrow.reset();
                this.pathArrow.moveTo(0, h23);
                this.pathArrow.lineTo(w23, h23);
                this.pathArrow.moveTo(w23, h - 1);
                this.pathArrow.lineTo(w - 1, h12);
                g2.setColor(SystemColor.controlDkShadow);
                g2.draw(this.pathArrow);

                g2.setColor(SystemColor.controlShadow);
                g2.draw(new Line2D.Float(w - 1, h12, w23, 0));

                this.pathArrow.reset();
                this.pathArrow.moveTo(w23, 0);
                this.pathArrow.lineTo(w23, h13);
                this.pathArrow.lineTo(0, h13);
                this.pathArrow.lineTo(0, h23);
                this.pathArrow.moveTo(w23, h23);
                this.pathArrow.lineTo(w23, h - 1);
                g2.setColor(SystemColor.controlLtHighlight);
                g2.draw(this.pathArrow);

                break;
            }
        }
    }
}
