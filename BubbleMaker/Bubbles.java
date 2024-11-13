import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.*;

public class Bubbles extends Application {
    // Constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;

    // UI Components
    private BorderPane root;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private VBox tileSelector;
    private Button switchModeButton;
    private Button saveButton;
    private StackPane gameArea;
    private Screen screen;

    // Game State & Logic
    private DataSingleton singleton = DataSingleton.getInstance();
    private final StandardTileFactory standardTileFactory = new StandardTileFactory();
    private final TileConfigurationParser tileConfigurationParser = new TileConfigurationParser(standardTileFactory);

    @Override
    public void start(Stage primaryStage) {
        try {
            singleton = DataSingleton.getInstance();
            TileFileLoader tileLoader = new TileFileLoader(tileConfigurationParser);
            tileLoader.loadTiles();

            setTileSelectors();
            initializeComponents();
            setupLayout();
            
            // Initialize screen after components
            screen = new Screen();
            
            setupEventHandlers();

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            setupSceneStyles(scene);

            primaryStage.setTitle("Bubbles Maker 3000");
            primaryStage.setScene(scene);
            primaryStage.show();

            gc = gameCanvas.getGraphicsContext2D();
            singleton.setGraphicsContext(gc);
            
            GameEditor ge = singleton.getGameEditor();
            ge.start(scene);

        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTileSelectors() throws FileNotFoundException {
        List<TileSelectorButton> tileSelectorList = new ArrayList<>();

        try (Scanner scan = new Scanner(new File("start.txt"))) {
            while (scan.hasNextLine()) {
                String configLine = scan.nextLine().trim();

                if (configLine.isEmpty()) {
                    continue;
                }

                configLine = configLine.replaceAll("\\s+", " ");

                try {
                    TileSelectorButton newTileSelector = new TileSelectorButton(
                            tileConfigurationParser.createTileFromConfig(configLine), 
                            configLine
                    );
                    tileSelectorList.add(newTileSelector);
                } catch (Exception e) {
                    System.err.println("Error processing line: " + configLine);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not find start.txt file: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error reading start.txt: " + e.getMessage());
            throw e;
        }

        singleton.setTileSelectorButtons(tileSelectorList);
    }

    private void initializeComponents() {
        root = new BorderPane();
        gameCanvas = new Canvas(WIDTH, HEIGHT);

        // Initialize top buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.TOP_CENTER);
        buttonContainer.setPadding(new javafx.geometry.Insets(5, 0, 5, 0));
        buttonContainer.setMaxHeight(30);
        buttonContainer.setMouseTransparent(false);

        switchModeButton = createStyledButton("Play");
        saveButton = createStyledButton("Save");
        
        switchModeButton.setFocusTraversable(false);
        saveButton.setFocusTraversable(false);

        buttonContainer.getChildren().addAll(switchModeButton, saveButton);

        initializeTileSelector();

        gameArea = new StackPane();
        gameArea.getChildren().addAll(gameCanvas, buttonContainer, tileSelector);
        StackPane.setAlignment(buttonContainer, Pos.TOP_CENTER);
        StackPane.setAlignment(tileSelector, Pos.CENTER_RIGHT);
        // Move selector slightly to the left and add right margin
        StackPane.setMargin(tileSelector, new javafx.geometry.Insets(50, 10, 50, 0));
    }

    private void initializeTileSelector() {
        VBox selectorContainer = new VBox();
        selectorContainer.setMinWidth(130);
        selectorContainer.setMaxWidth(130);
        selectorContainer.setMaxHeight(350);  // Limit height to just contain buttons and scroll
        selectorContainer.setAlignment(Pos.CENTER);
        selectorContainer.setSpacing(0);
        
        // Create background for buttons area only
        VBox buttonBackground = new VBox();
        buttonBackground.setStyle("""
            -fx-background-color: #2c2c2c;
            -fx-padding: 5;
            -fx-border-color: #3c3c3c;
            -fx-border-width: 1;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            """);
        
        // Create grid for tile buttons
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(4);
        buttonGrid.setVgap(4);
        buttonGrid.setAlignment(Pos.CENTER);
        
        // Create scroll buttons
        HBox scrollButtons = new HBox(4);
        scrollButtons.setAlignment(Pos.CENTER);
        scrollButtons.setPadding(new javafx.geometry.Insets(5));
        scrollButtons.setStyle("""
            -fx-background-color: #2c2c2c;
            -fx-border-color: #3c3c3c;
            -fx-border-width: 1;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            """);
        
        ScrollButton scrollUpButton = ScrollButton.createUpButton();
        ScrollButton scrollDownButton = ScrollButton.createDownButton();
        scrollButtons.getChildren().addAll(scrollUpButton, scrollDownButton);

        List<TileSelectorButton> tileSelectorButtons = singleton.getTileSelectorButtons();
        
        final int[] currentIndex = {0};
        int maxVisibleTiles = 10;

        scrollUpButton.setEnabled(false);
        scrollDownButton.setEnabled(tileSelectorButtons.size() > maxVisibleTiles);

        scrollUpButton.setOnAction(e -> {
            if (currentIndex[0] > 0) {
                currentIndex[0] -= 2;
                updateVisibleButtons(buttonGrid, tileSelectorButtons, currentIndex[0]);
                scrollUpButton.setEnabled(currentIndex[0] > 0);
                scrollDownButton.setEnabled(true);
            }
        });

        scrollDownButton.setOnAction(e -> {
            if (currentIndex[0] < tileSelectorButtons.size() - maxVisibleTiles) {
                currentIndex[0] += 2;
                updateVisibleButtons(buttonGrid, tileSelectorButtons, currentIndex[0]);
                scrollUpButton.setEnabled(true);
                scrollDownButton.setEnabled(currentIndex[0] < tileSelectorButtons.size() - maxVisibleTiles);
            }
        });

        updateVisibleButtons(buttonGrid, tileSelectorButtons, 0);
        buttonBackground.getChildren().add(buttonGrid);

        selectorContainer.getChildren().addAll(buttonBackground, scrollButtons);
        
        this.tileSelector = selectorContainer;

        if (!tileSelectorButtons.isEmpty()) {
            tileSelectorButtons.get(0).select();
        }
    }

    private void updateVisibleButtons(GridPane grid, List<TileSelectorButton> buttons, int startIndex) {
        grid.getChildren().clear();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                int index = startIndex + (i * 2) + j;
                if (index < buttons.size()) {
                    TileSelectorButton button = buttons.get(index);
                    button.setPrefWidth(70);
                    button.setPrefHeight(40);
                    grid.add(button, j, i);
                } else {
                    Button emptyButton = new Button("");
                    emptyButton.setPrefWidth(70);
                    emptyButton.setPrefHeight(40);
                    emptyButton.setDisable(true);
                    emptyButton.setStyle("""
                        -fx-background-color: rgba(60, 60, 60, 0.5);
                        -fx-border-color: #4c4c4c;
                        -fx-border-width: 1;
                        -fx-background-radius: 3;
                        -fx-border-radius: 3;
                        -fx-padding: 5 15;
                        -fx-opacity: 0.5;
                        """);
                    grid.add(emptyButton, j, i);
                }
            }
        }
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: rgba(60, 60, 60, 0.8);
            -fx-text-fill: white;
            -fx-border-color: #4c4c4c;
            -fx-border-width: 1;
            -fx-border-radius: 3;
            -fx-background-radius: 3;
            -fx-padding: 3 10;
            -fx-font-size: 10px;
            """);

        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: rgba(76, 76, 76, 0.9);
            -fx-text-fill: white;
            -fx-border-color: #5c5c5c;
            -fx-border-width: 1;
            -fx-border-radius: 3;
            -fx-background-radius: 3;
            -fx-padding: 3 10;
            -fx-font-size: 10px;
            """));

        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: rgba(60, 60, 60, 0.8);
            -fx-text-fill: white;
            -fx-border-color: #4c4c4c;
            -fx-border-width: 1;
            -fx-border-radius: 3;
            -fx-background-radius: 3;
            -fx-padding: 3 10;
            -fx-font-size: 10px;
            """));

        return button;
    }

    private void setupLayout() {
        root.setCenter(gameArea);
        root.setStyle("-fx-background-color: #1a1a1a;");
    }

    private void setupSceneStyles(Scene scene) {
        scene.getRoot().setStyle("-fx-background-color: #1a1a1a;");
    }

    private void setupEventHandlers() {
        switchModeButton.setOnAction(e -> {
            if (singleton.getGameRunning()) {
                switchModeButton.setText("Play");
                switchToEditorMode();
            } else {
                switchModeButton.setText("Stop");
                switchToRunnerMode();
            }

            singleton.getGameEditor().setGraphicsContext(gc);
            singleton.getGameRunner().setGraphicsContext(gc);
            singleton.toggleGameState(gameCanvas.getScene());
        });

        saveButton.setOnAction(e -> saveCurrentLevel());

        // Only prevent tile placement in the actual selector area
        gameCanvas.setOnMouseClicked(e -> {
            double mouseY = e.getY();
            double mouseX = e.getX();
            
            // Check if click is within the tile selector bounds
            if (mouseX > gameCanvas.getWidth() - 90 && // Moved in slightly from the edge
                mouseY > 50 && mouseY < 400) {  // Only block middle section where selector is
                return;
            }
            screen.handleClick(e);
        });
    }

    private void switchToEditorMode() {
        tileSelector.setVisible(true);
        HBox buttonContainer = (HBox) gameArea.getChildren().get(1);
        if (!buttonContainer.getChildren().contains(saveButton)) {
            buttonContainer.getChildren().add(saveButton);
        }
    }

    private void switchToRunnerMode() {
        tileSelector.setVisible(false);
        HBox buttonContainer = (HBox) gameArea.getChildren().get(1);
        buttonContainer.getChildren().remove(saveButton);
    }

    private void saveCurrentLevel() {
        try {
            HashMap<Long, Tile> tiles = singleton.getAllTiles();
            TileFileWriter fileWriter = new TileFileWriter();
            fileWriter.saveLevel(tiles);
            showSaveSuccessMessage();
        } catch (IOException e) {
            showSaveErrorMessage(e.getMessage());
        }
    }

    private void showSaveSuccessMessage() {
        System.out.println("Level saved successfully!");
    }

    private void showSaveErrorMessage(String message) {
        System.err.println("Error saving level: " + message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}