// Created: 15.11.2020

package de.freese.jconky;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Geht momentan nicht aus der IDE, sondern nur per Console: mvn compile exec:java<br>
 * <br>
 * In Eclipse:<br>
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
     * @param args final String[]
     */
    public static void main(final String[] args)
    {
        launch(args);
    }

    /**
    *
    */
    private GraphicsContext gc;

    /**
    *
    */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * @return {@link Logger}
     */
    public Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception
    {
        getLogger().info("init");
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        getLogger().info("start");

        Canvas canvas = new Canvas();
        this.gc = canvas.getGraphicsContext2D();

        Group pane = new Group();
        pane.getChildren().add(canvas);

        // GridPane pane = new GridPane();
        // pane.add(canvas, 0, 0);

        // Scene
        Scene scene = new Scene(pane, 335, 1060, true, SceneAntialiasing.BALANCED);

        // Bind canvas size to scene size.
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        getLogger().info("Antialising: {}", scene.getAntiAliasing());

        // Transparenz
        boolean isTransparentSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        // isTransparentSupported = false;

        if (isTransparentSupported)
        {
            // Fenster wird hierbei undecorated, aber der Graph wird normal gezeichnet.

            // For Stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            // For Scene
            // scene.setFill(Color.TRANSPARENT);
            scene.setFill(new Color(0D, 0D, 0D, 0.5D));

            // For Containers
            // pane.setBackground(Background.EMPTY);
            // pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            // pane.setStyle("-fx-background-color: transparent;");

            // Das gesamte Fenster wird transparent, inklusive Titelleiste und Graph.
            // primaryStage.setOpacity(0.3D);
        }
        else
        {
            scene.setFill(Color.BLACK);
        }

        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
        this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            // TODO
        }, 500, 40, TimeUnit.MILLISECONDS);

        primaryStage.setTitle("Graph Monitor");
        primaryStage.setScene(scene);

        // Auf dem 2. Monitor
        // List<Screen> screens = Screen.getScreens();
        // Screen screen = screens.get(screens.size() - 1);
        // primaryStage.setX(screen.getVisualBounds().getMinX() + 1200);
        // primaryStage.setY(10D);

        primaryStage.show();
    }

    /**
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception
    {
        getLogger().info("stop");

        this.scheduledExecutorService.shutdown();

        System.exit(0);
    }
}
