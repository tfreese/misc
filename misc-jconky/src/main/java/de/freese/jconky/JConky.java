// Created: 15.11.2020

package de.freese.jconky;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.javafx.application.PlatformImpl;
import de.freese.jconky.painter.CpuMonitorPainter;
import de.freese.jconky.painter.HostMonitorPainter;
import de.freese.jconky.painter.MusicMonitorPainter;
import de.freese.jconky.painter.NetworkMonitorPainter;
import de.freese.jconky.painter.ProcessMonitorPainter;
import de.freese.jconky.painter.SystemMonitorPainter;
import de.freese.jconky.painter.TemperatureMonitorPainter;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * In Eclipse: mit JConkyLauncher ausführen oder JConky direkt mit foldenden Restruktionen:<br>
 * <ol>
 * <li>Konstruktor muss public empty-arg sein oder nicht vorhanden sein.
 * <li>VM-Parameter: --add-modules javafx.controls
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen
 * </ol>
 *
 * @author Thomas Freese
 */
public final class JConky extends Application
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JConky.class);

    /**
     * @return {@link Logger}
     */
    public static Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @param args final String[]
     */
    public static void main(final String[] args)
    {
        // Kein Taskbar Icon, funktioniert unter Linux aber nicht.
        PlatformImpl.setTaskbarApplication(false);

        // Runtime wird nicht beendet, wenn letztes Fenster geschlossen wird.
        // Platform.setImplicitExit(false);

        // System.setProperty("apple.awt.UIElement", "true");
        // System.setProperty("apple.awt.headless", "true");
        // System.setProperty("java.awt.headless", "true");
        // System.setProperty("javafx.macosx.embedded", "true");
        // java.awt.Toolkit.getDefaultToolkit();

        launch(args);
    }

    /**
     *
     */
    private ContextPainter conkyContextPainter;

    /**
     *
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * @return {@link Scene}
     */
    public Scene createScene()
    {
        // Font-Antializing
        System.setProperty("prism.lcdtext", "true");

        Canvas canvas = new Canvas();
        this.conkyContextPainter.setCanvas(canvas);

        Group pane = new Group();
        pane.getChildren().add(canvas);

        // GridPane pane = new GridPane();
        // pane.add(canvas, 0, 0);

        // Scene
        Scene scene = new Scene(pane, 335, 1070, true, SceneAntialiasing.BALANCED);

        // Bind canvas size to scene size.
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        getLogger().info("Antialising: {}", scene.getAntiAliasing());

        return scene;
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    private ScheduledExecutorService getScheduledExecutorService()
    {
        return this.scheduledExecutorService;
    }

    /**
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception
    {
        // "JavaFX-Launcher" umbenennen.
        Thread.currentThread().setName("JavaFX-Init");

        getLogger().info("init");

        this.scheduledExecutorService = Executors.newScheduledThreadPool(4);
        this.conkyContextPainter = new ContextPainter();

        this.conkyContextPainter.addMonitorPainter(new HostMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new CpuMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new SystemMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new NetworkMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new ProcessMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new TemperatureMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new MusicMonitorPainter());

        getScheduledExecutorService().execute(() -> Context.getInstance().updateOneShot());

        // Short-Scheduled
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long delay = 3000;
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateUptimeInSeconds(), 0, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateCpuInfos(), 0, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateNetworkInfos(), 0, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateUsages(), 0, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateProcessInfos(), 0, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateTemperatures(), 0, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateMusicInfo(), 0, delay, timeUnit);

        // Long-Scheduled
        timeUnit = TimeUnit.MINUTES;
        delay = 15;
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateHostInfo(), 0, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateUpdates(), 0, delay, timeUnit);
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        // "JavaFX Application Thread" umbenennen.
        Thread.currentThread().setName("JavaFX-Thread");

        getLogger().info("start");

        Scene scene = createScene();

        // Transparenz
        boolean isTransparentSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        // isTransparentSupported = false;

        if (isTransparentSupported)
        {
            // Fenster wird hierbei undecorated, aber der Content wird normal gezeichnet.

            // For Stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            // Das gesamte Fenster wird transparent, inklusive Titelleiste und Inhalt.
            // primaryStage.setOpacity(Settings.getInstance().getAlpha());

            // For Scene
            // scene.setFill(Color.TRANSPARENT);
            scene.setFill(new Color(0D, 0D, 0D, Settings.getInstance().getAlpha()));

            // canvas.setOpacity(Settings.getInstance().getAlpha());

            // Für Container.
            // pane.setBackground(Background.EMPTY);
            // pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            // pane.setStyle("-fx-background-color: transparent;");
        }
        else
        {
            scene.setFill(Color.BLACK);
        }

        primaryStage.setTitle("jConky");
        primaryStage.getIcons().add(new Image("conky.png"));
        primaryStage.setScene(scene);

        // Auf dem 2. Monitor
        List<Screen> screens = Screen.getScreens();
        Screen screen = screens.get(screens.size() - 1);
        primaryStage.setX(screen.getVisualBounds().getMinX() + 1240);
        primaryStage.setY(5D);

        startRepaintSchedule();

        // primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     *
     */
    public void startRepaintSchedule()
    {
        getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            try
            {
                Platform.runLater(this.conkyContextPainter::paint);
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }, 400, 3000, TimeUnit.MILLISECONDS);
    }

    /**
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception
    {
        getLogger().info("stop");

        getScheduledExecutorService().shutdown();

        System.exit(0);
    }
}
