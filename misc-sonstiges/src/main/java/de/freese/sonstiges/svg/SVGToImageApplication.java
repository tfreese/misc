/**
 * Created: 29.11.2018
 */

package de.freese.sonstiges.svg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderInput;

/**
 * @author Thomas Freese
 */
public class SVGToImageApplication extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = 8384522285700890883L;

    /**
     * @param svgFile {@link File}
     * @param width width
     * @param height height
     * @return {@link BufferedImage}
     * @throws Exception Falls was schief geht.
     */
    private static BufferedImage loadImage(final File svgFile, final float width, final float height) throws Exception
    {
        BufferedImage image = null;

        try (InputStream inputStream = new FileInputStream(svgFile))
        {
            image = loadImage(inputStream, width, height);
        }

        return image;
    }

    /**
     * @param inputStream {@link InputStream}
     * @param width float
     * @param height float
     * @return {@link BufferedImage}
     * @throws Exception Falls was schief geht.
     */
    private static BufferedImage loadImage(final InputStream inputStream, final float width, final float height) throws Exception
    {
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);

        TranscoderInput input = new TranscoderInput(inputStream);
        transcoder.transcode(input, null);

        return transcoder.getBufferedImage();
    }

    /**
     * @param url {@link URL}
     * @param width float
     * @param height float
     * @return {@link BufferedImage}
     * @throws Exception Falls was schief geht.
     */
    private static BufferedImage loadImage(final URL url, final float width, final float height) throws Exception
    {
        try (InputStream inputStream = url.openStream())
        {
            return loadImage(inputStream, width, height);
        }
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        SVGToImageApplication application = new SVGToImageApplication();
        application.initAndShowGUI();

        application.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e)
            {
                application.dispose();
                System.exit(0);
            }
        });
    }

    /**
     * Erstellt ein neues {@link SVGToImageApplication} Object.
     */
    public SVGToImageApplication()
    {
        super();

        setTitle("Batik");
    }

    /**
    *
    */
    public void initAndShowGUI()
    {
        final JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton button = new JButton("Load...");
        p.add(button);

        ImageIcon imageIcon = new ImageIcon();
        JLabel picLabel = new JLabel(imageIcon);

        panel.add("North", p);
        panel.add("Center", picLabel);

        button.addActionListener(event -> {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Vector / SVG Images", "svg");

            JFileChooser fc = new JFileChooser(".");
            fc.setFileFilter(filter);

            int choice = fc.showOpenDialog(panel);

            if (choice == JFileChooser.APPROVE_OPTION)
            {
                File svgFile = fc.getSelectedFile();

                try
                {
                    imageIcon.setImage(loadImage(svgFile, 600, 600));
                    pack();
                    setLocationRelativeTo(null);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        SwingUtilities.invokeLater(() -> {
            try
            {
                URL url = ClassLoader.getSystemResource("image.svg");
                imageIcon.setImage(loadImage(url, 600, 600));
                pack();
                setLocationRelativeTo(null);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });

        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
