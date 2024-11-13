public class UIConstants {
    // Window dimensions
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 450;
    
    // Tile selector dimensions
    public static final int TILE_SELECTOR_WIDTH = 120;
    public static final int VISIBLE_TILES = 10;
    
    // Button dimensions
    public static final int BUTTON_WIDTH = 100;  // Reduced from previous value
    public static final int BUTTON_HEIGHT = 30;  // Reduced to fit in window height
    public static final int BUTTON_SPACING = 2;  // Reduced spacing
    
    // Top bar dimensions
    public static final int TOP_BAR_HEIGHT = 40;
    
    // Tile dimensions
    public static final int TILE_SIZE = 30;
    
    // Game canvas dimensions
    public static final int CANVAS_WIDTH = WINDOW_WIDTH - TILE_SELECTOR_WIDTH;
    public static final int CANVAS_HEIGHT = WINDOW_HEIGHT - TOP_BAR_HEIGHT;
}