import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileSelectorButton extends Button {
    // UI Constants - modified sizes
    private static final double BUTTON_WIDTH = 70;  // Reduced from 100
    private static final double BUTTON_HEIGHT = 40;  // Increased from 30
    private static final double TILE_PREVIEW_SIZE = 25;  // Increased from 20

    // Member Variables
    private static TileSelectorButton selectedButton = null;
    private final VBox layout;
    private final Rectangle background;
    private final Tile tileToPlace;
    private final String tileConfig;

    public TileSelectorButton(Tile tileToPlace, String tileConfig) {
        this.tileToPlace = tileToPlace;
        this.tileConfig = tileConfig;

        // Set button size
        setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        setMaxSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        // Create layout
        layout = new VBox(2);
        layout.setAlignment(Pos.CENTER);

        // Add tile name text
        Text nameText = new Text(tileToPlace.getName());
        nameText.setFill(Color.WHITE);
        nameText.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;");

        // Create tile preview container
        StackPane tilePreview = new StackPane();
        tilePreview.setMinSize(TILE_PREVIEW_SIZE, TILE_PREVIEW_SIZE);
        tilePreview.setMaxSize(TILE_PREVIEW_SIZE, TILE_PREVIEW_SIZE);

        // Create background rectangle for tile preview
        background = new Rectangle(TILE_PREVIEW_SIZE, TILE_PREVIEW_SIZE);
        background.setFill(tileToPlace.getFillColor());
        background.setStroke(tileToPlace.getOutlineColor());
        background.setStrokeWidth(1);
        tilePreview.getChildren().add(background);

        // Add image if available
        if (tileToPlace.getImage() != null) {
            try {
                ImageView imageView = new ImageView(tileToPlace.getImage());
                imageView.setFitWidth(TILE_PREVIEW_SIZE - 2);
                imageView.setFitHeight(TILE_PREVIEW_SIZE - 2);
                imageView.setPreserveRatio(true);
                tilePreview.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Failed to load image for " + tileToPlace.getName() + ": " + e.getMessage());
            }
        }

        // Add components to layout
        layout.getChildren().addAll(nameText, tilePreview);
        setGraphic(layout);

        // Style the button
        setupButtonStyle();
        
        // Setup click handler
        setOnAction(e -> handleSelection());
    }

    public Tile getTileToPlace() {
        return tileToPlace;
    }

    public String getTileConfig() { 
        return tileConfig; 
    }

    public static TileSelectorButton getSelectedButton() {
        return selectedButton;
    }

    private void setupButtonStyle() {
        setStyle("""
            -fx-background-color: #2c2c2c;
            -fx-border-color: #4c4c4c;
            -fx-border-width: 1px;
            -fx-background-radius: 3;
            -fx-border-radius: 3;
            -fx-padding: 2px;
            """);

        setOnMouseEntered(e -> {
            if (this != selectedButton) {
                setStyle("""
                    -fx-background-color: #3c3c3c;
                    -fx-border-color: #5c5c5c;
                    -fx-border-width: 1px;
                    -fx-background-radius: 3;
                    -fx-border-radius: 3;
                    -fx-padding: 2px;
                    """);
            }
        });

        setOnMouseExited(e -> {
            if (this != selectedButton) {
                setStyle("""
                    -fx-background-color: #2c2c2c;
                    -fx-border-color: #4c4c4c;
                    -fx-border-width: 1px;
                    -fx-background-radius: 3;
                    -fx-border-radius: 3;
                    -fx-padding: 2px;
                    """);
            }
        });
    }

    private void handleSelection() {
        if (selectedButton != null) {
            selectedButton.setStyle("""
                -fx-background-color: #2c2c2c;
                -fx-border-color: #4c4c4c;
                -fx-border-width: 1px;
                -fx-background-radius: 3;
                -fx-border-radius: 3;
                -fx-padding: 2px;
                """);
        }

        selectedButton = this;
        setStyle("""
            -fx-background-color: #2a2a2a;
            -fx-border-color: #0078d7;
            -fx-border-width: 2px;
            -fx-background-radius: 3;
            -fx-border-radius: 3;
            -fx-padding: 1px;
            """);
    }

    public static String getSelectedTileType() {
        return selectedButton != null ? selectedButton.getTileType() : "normal";
    }

    public String getTileType() {
        return tileToPlace.getName();
    }

    public boolean isSelected() {
        return this == selectedButton;
    }

    public void select() {
        handleSelection();
    }

    public void deselect() {
        if (this == selectedButton) {
            selectedButton = null;
            setStyle("""
                -fx-background-color: #2c2c2c;
                -fx-border-color: #4c4c4c;
                -fx-border-width: 1px;
                -fx-background-radius: 3;
                -fx-border-radius: 3;
                -fx-padding: 2px;
                """);
        }
    }
}