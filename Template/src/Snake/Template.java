
package Snake;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Třída Template
 */
public class Template extends Application {

    private final int grid_x = 10;
    private final int grid_y = 15;
    private Timeline action;
    private Rectangle[][] grid;
    private final Point direction;
    private Point point, meal;
    private LinkedList<Point> snake;

    /**
     * Konstruktor
     */
    public Template() {
        point = new Point(grid_x / 2, grid_y / 2);
        direction = new Point();
        snake = new LinkedList<>();
        snake.addFirst(point);
        meal = new Point();
        meal.setLocation(200, 100);
    }

    @Override
    public void start(Stage primaryStage) {
        AnchorPane basePane = new AnchorPane();
        Button btnStart = new Button();
        btnStart.setText("Start game");
        btnStart.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (action.getStatus() == Animation.Status.RUNNING) {
                    action.stop();
                    btnStart.setText("Start game");
                } else {
                    action.play();
                    btnStart.setText("Stop game");
                    generateFood();
                }
            }
        });
        basePane.getChildren().add(btnStart);
        AnchorPane.setTopAnchor(btnStart, 1.0);
        AnchorPane.setLeftAnchor(btnStart, 1.0);
        AnchorPane.setRightAnchor(btnStart, 1.0);

        Pane root = new Pane();
        basePane.getChildren().add(root);
        AnchorPane.setBottomAnchor(root, 1.0);
        AnchorPane.setLeftAnchor(root, 1.0);
        AnchorPane.setRightAnchor(root, 1.0);
        AnchorPane.setTopAnchor(root, 30.0);

        grid = new Rectangle[grid_x][grid_y];

        // this binding will find out which parameter is smaller: height or width
        NumberBinding rectSize = Bindings.min(root.heightProperty().divide(grid_y), root.widthProperty().divide(grid_x));

        for (int x = 0; x < grid_x; x++) {
            for (int y = 0; y < grid_y; y++) {
                grid[x][y] = new Rectangle();
                grid[x][y].setStroke(Color.BLACK);
                grid[x][y].setFill(Color.LIGHTGRAY);
                // here we position rects (this depends on pane size as well)
                grid[x][y].xProperty().bind(rectSize.multiply(x));
                grid[x][y].yProperty().bind(rectSize.multiply(y));

                // here we bind rectangle size to pane size
                grid[x][y].heightProperty().bind(rectSize);
                grid[x][y].widthProperty().bind(grid[x][y].heightProperty());

                root.getChildren().add(grid[x][y]);
            }
        }
        Scene scene = new Scene(basePane, 400, 600);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT) {
                    if (direction.x != -1) {
                        direction.x = 1;
                        direction.y = 0;
                    }
                }
                if (event.getCode() == KeyCode.LEFT) {
                    if (direction.x != 1) {
                        direction.x = -1;
                        direction.y = 0;
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (direction.y != 1) {
                        direction.x = 0;
                        direction.y = -1;
                    }
                }
                if (event.getCode() == KeyCode.DOWN) {
                    if (direction.y != -1) {
                        direction.x = 0;
                        direction.y = 1;
                    }
                }
            }

        });

        action = new Timeline(new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                snakeMove();
            }
        }));
        action.setCycleCount(Timeline.INDEFINITE);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Funkce pro pohyb hada
     */
    private void snakeMove() {
        Point head = new Point();
        Point last = new Point();

        last = snake.getLast();
        point = snake.get(0);

        grid[point.x][point.y].setFill(Color.LIGHTGRAY);

        if (eatFood()) {
            snake.addFirst(point);
        }

        if (snake.size() > 1) {
            snake.remove(0);
        }

        for (Point point : snake) {
            grid[point.x][point.y].setFill(Color.ROYALBLUE);
        }

        head.x = (last.x + direction.x + grid_x) % grid_x;
        head.y = (last.y + direction.y + grid_y) % grid_y;

        solveCollisions(head);
        snake.add(head);
    }

    /**
     * Funkce pro nastaveni pozice jidla
     */
    private void generateFood() {
        generateFoodPosition();

        for (int x = 0; x < grid_x; x++) {
            for (int y = 0; y < grid_y; y++) {
                if(grid[x][y].getFill() == Color.BROWN)
                        grid[x][y].setFill(Color.LIGHTGRAY);
            }
        }

        grid[meal.x][meal.y].setFill(Color.BROWN);
    }

    /**
     * Funkce pro generovani jidla
     */
    private void generateFoodPosition() {
        boolean isInBody = false;
        Random rand = new Random();
        meal.x = rand.nextInt(grid_x);
        meal.y = rand.nextInt(grid_y);

        if (snake.contains(meal)) {
            isInBody = true;
        }
        if (isInBody) {
            generateFoodPosition();
        }
    }

    /**
     * Funkce pro řešení kolizí
     *
     * @param head hlava
     */
    private void solveCollisions(Point head) {
        boolean isCollision = false;
        for (int i = 0; i < snake.size() - 1; i++) {
            if (snake.get(i).equals(head))
                isCollision = true;
        }
        if (isCollision) {
            this.action.stop();
        }
    }

    /**
     * Funkce pro zjištění, zda bylo snězeno jídlo
     *
     * @return bool
     */
    private boolean eatFood() {
        Point tmp = new Point();
        tmp = snake.getLast();
        if (tmp.equals(meal)) {
            generateFood();
            return true;
        }
        return false;
    }

}
