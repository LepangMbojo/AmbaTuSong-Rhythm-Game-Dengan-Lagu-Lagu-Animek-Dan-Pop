import java.awt.*;
import javax.swing.*;

public class Theme {
    // --- WARNA (Palette) ---
    public static final Color BACKGROUND = new Color(18, 18, 25); // Gelap kebiruan dikit
    public static final Color TEXT = Color.WHITE;
    public static final Color ACCENT = new Color(0, 190, 255); // Cyan Neon
    public static final Color WARNING = new Color(255, 100, 100); // Merah soft
    public static final Color BUTTON_BG = new Color(40, 40, 60);
    
    // --- FONT ---
    public static final Font FONT_TITLE = new Font("Poppins", Font.BOLD, 40);
    public static final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 20);
    public static final Font FONT_REGULAR = new Font("Arial", Font.PLAIN, 16);

    // --- UTILITY LOAD GAMBAR (Supaya tidak try-catch terus di setiap panel) ---
    public static Image loadImage(String path) {
        try {
            return new ImageIcon(path).getImage();
        } catch (Exception e) {
            System.err.println("Gagal load gambar: " + path);
            return null;
        }
    }
}