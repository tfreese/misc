// Erzeugt: 22.09.2015
package de.freese.sonstiges.javafx;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Mit JavaFxDemoLauncher ausführen oder JavaFxDemo direkt mit foldenden Restruktionen:<br>
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
public class JavaFxDemo extends Application
{
    /**
     * @param args final String[]
     */
    public static void main(final String[] args)
    {
        launch(args);
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage stage)
    {
        Group root = new Group();
        Scene scene = new Scene(root, 500, 500, Color.BLACK);
        Rectangle r = new Rectangle(0, 0, 250, 250);
        r.setFill(Color.BLUE);
        root.getChildren().add(r);

        TranslateTransition translate = new TranslateTransition(Duration.millis(750));
        translate.setToX(390);
        translate.setToY(390);

        FillTransition fill = new FillTransition(Duration.millis(750));
        fill.setToValue(Color.RED);

        RotateTransition rotate = new RotateTransition(Duration.millis(750));
        rotate.setToAngle(360);

        ScaleTransition scale = new ScaleTransition(Duration.millis(750));
        scale.setToX(0.1);
        scale.setToY(0.1);

        ParallelTransition transition = new ParallelTransition(r, translate, fill, rotate, scale);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();

        stage.setTitle("JavaFX Scene Graph Demo");
        stage.setScene(scene);
        stage.show();
    }
}
