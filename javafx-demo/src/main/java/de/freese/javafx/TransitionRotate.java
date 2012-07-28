/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package de.freese.javafx;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Thomas Freese
 */
public class TransitionRotate extends Application
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(final String[] args)
	{
		launch(args);
	}

	/**
	 * 
	 */
	private RotateTransition rotateTransition = null;

	/**
	 * @param stage {@link Stage}
	 */
	public void init(final Stage stage)
	{
		Group root = new Group();
		Scene scene = new Scene(root, 300, 300, Color.WHITE);
		stage.setScene(scene);

		stage.setResizable(false);

		Stop[] stops = new Stop[]
		{
				new Stop(0, Color.CYAN), new Stop(1, Color.DODGERBLUE)
		};
		LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

		Rectangle rect = new Rectangle(100, 100, 100, 100);
		rect.setArcHeight(20);
		rect.setArcWidth(20);
		// rect.setFill(Color.ORANGE);
		rect.setFill(gradient);
		root.getChildren().add(rect);

		this.rotateTransition = new RotateTransition(Duration.seconds(4), rect);
		this.rotateTransition.setFromAngle(0);
		this.rotateTransition.setToAngle(720);
		this.rotateTransition.setCycleCount(Animation.INDEFINITE);
		this.rotateTransition.setAutoReverse(true);

		// Effekte
		DropShadow ds = new DropShadow();
		ds.setOffsetY(5.0);
		ds.setOffsetX(5.0);
		ds.setColor(Color.GREY);

		// Reflection reflection = new Reflection();
		// ds.setInput(reflection);

		rect.setEffect(ds);
	}

	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage stage)
	{
		System.err.println("start");

		init(stage);
		stage.show();
		stage.toFront();
		this.rotateTransition.play();
	}

	/**
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception
	{
		this.rotateTransition.stop();

		super.stop();
		System.err.println("stop");
	}
}
