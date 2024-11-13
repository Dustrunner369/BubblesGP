import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ScrollButton extends Button {
    private static final String UP_ARROW = "▲";
    private static final String DOWN_ARROW = "▼";
    
    public static ScrollButton createUpButton() {
        return new ScrollButton(UP_ARROW);
    }
    
    public static ScrollButton createDownButton() {
        return new ScrollButton(DOWN_ARROW);
    }
    
    private ScrollButton(String arrow) {
        // Set up the basic button
        setPrefWidth(100); // Width of the tile selector
        setPrefHeight(10); 
        setMinHeight(20);
        setMaxHeight(20);
        
        // Create and style the arrow text
        Text arrowText = new Text(arrow);
        arrowText.setFill(Color.WHITE);
        arrowText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        
        // Create a container for the arrow
        StackPane content = new StackPane(arrowText);
        setGraphic(content);
        
        // Apply initial style
        setDefaultStyle();
        
        // Add hover effects
        setupHoverEffects();
    }
    
    private void setDefaultStyle() {
        setStyle("""
            -fx-background-color: #3c3c3c;
            -fx-text-fill: white;
            -fx-border-color: #4c4c4c;
            -fx-border-width: 1;
            -fx-background-radius: 3;
            -fx-border-radius: 3;
            -fx-padding: 2 5;
            """);
    }
    
    private void setupHoverEffects() {
        setOnMouseEntered(e -> setStyle("""
            -fx-background-color: #4c4c4c;
            -fx-text-fill: white;
            -fx-border-color: #5c5c5c;
            -fx-border-width: 1;
            -fx-background-radius: 3;
            -fx-border-radius: 3;
            -fx-padding: 2 5;
            """));
        
        setOnMouseExited(e -> setDefaultStyle());
        
        setOnMousePressed(e -> setStyle("""
            -fx-background-color: #2c2c2c;
            -fx-text-fill: white;
            -fx-border-color: #5c5c5c;
            -fx-border-width: 1;
            -fx-background-radius: 3;
            -fx-border-radius: 3;
            -fx-padding: 2 5;
            """));
        
        setOnMouseReleased(e -> {
            if (isHover()) {
                setStyle("""
                    -fx-background-color: #4c4c4c;
                    -fx-text-fill: white;
                    -fx-border-color: #5c5c5c;
                    -fx-border-width: 1;
                    -fx-background-radius: 3;
                    -fx-border-radius: 3;
                    -fx-padding: 2 5;
                    """);
            } else {
                setDefaultStyle();
            }
        });
    }
    
    public void setEnabled(boolean enabled) {
        setDisable(!enabled);
        if (!enabled) {
            setStyle("""
                -fx-background-color: #2c2c2c;
                -fx-text-fill: #666666;
                -fx-border-color: #3c3c3c;
                -fx-border-width: 1;
                -fx-background-radius: 3;
                -fx-border-radius: 3;
                -fx-padding: 2 5;
                -fx-opacity: 0.5;
                """);
        } else {
            setDefaultStyle();
        }
    }
}