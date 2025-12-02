import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {
    public static final Color BG_COLOR = new Color(30, 30, 30);
    public static final Color TEXT_COLOR = new Color(240, 240, 240);
    public static final Color DARK_FIELD_COLOR = new Color(45, 45, 45);
    public static final Color HIGHLIGHT_COLOR = new Color(255, 190, 80);
    public static final Color CORRECT_COLOR = new Color(70, 200, 70);
    public static final Color INCORRECT_COLOR = new Color(220, 50, 50);
    public static final Color CURRENT_BG_COLOR = new Color(60, 60, 60);
    public static final Color PENDING_FG_COLOR = Color.GRAY;

    public static final Font SANS_SERIF_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font STATS_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font LARGE_FONT = new Font("Tahoma", Font.BOLD, 48);
    public static final Font COUNTDOWN_FONT = new Font("Arial", Font.BOLD, 100);
    public static final Font INPUT_FONT = new Font("Monospaced", Font.PLAIN, 22);

    public static final SimpleAttributeSet STYLE_PENDING;
    public static final SimpleAttributeSet STYLE_CORRECT;
    public static final SimpleAttributeSet STYLE_INCORRECT;
    public static final SimpleAttributeSet STYLE_CURRENT_PENDING;
    public static final SimpleAttributeSet STYLE_CURRENT_CORRECT;
    public static final SimpleAttributeSet STYLE_CURRENT_ERROR;

    static {
        SimpleAttributeSet styleBase = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleBase, 24);
        StyleConstants.setFontFamily(styleBase, "Serif");
        StyleConstants.setAlignment(styleBase, StyleConstants.ALIGN_CENTER);

        STYLE_PENDING = new SimpleAttributeSet(styleBase);
        StyleConstants.setForeground(STYLE_PENDING, PENDING_FG_COLOR);

        STYLE_CORRECT = new SimpleAttributeSet(styleBase);
        StyleConstants.setForeground(STYLE_CORRECT, CORRECT_COLOR);

        STYLE_INCORRECT = new SimpleAttributeSet(styleBase);
        StyleConstants.setForeground(STYLE_INCORRECT, INCORRECT_COLOR);
        StyleConstants.setUnderline(STYLE_INCORRECT, true);

        SimpleAttributeSet styleCurrentBase = new SimpleAttributeSet(styleBase);
        StyleConstants.setBackground(styleCurrentBase, CURRENT_BG_COLOR);

        STYLE_CURRENT_PENDING = new SimpleAttributeSet(styleCurrentBase);
        StyleConstants.setForeground(STYLE_CURRENT_PENDING, PENDING_FG_COLOR);

        STYLE_CURRENT_CORRECT = new SimpleAttributeSet(styleCurrentBase);
        StyleConstants.setForeground(STYLE_CURRENT_CORRECT, TEXT_COLOR);

        STYLE_CURRENT_ERROR = new SimpleAttributeSet(styleCurrentBase);
        StyleConstants.setForeground(STYLE_CURRENT_ERROR, INCORRECT_COLOR);
    }
    public static JButton createButton(String text, boolean highlight) {
        JButton btn = new JButton(text);
        btn.setFont(UIHelper.SANS_SERIF_FONT.deriveFont(Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        if (highlight) {
            btn.setBackground(UIHelper.HIGHLIGHT_COLOR);
            btn.setForeground(Color.BLACK);
        }
        else {
            btn.setBackground(UIHelper.DARK_FIELD_COLOR);
            btn.setForeground(UIHelper.TEXT_COLOR);
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (highlight){
                    btn.setBackground(new Color(255, 210, 110));
                } 
                else{
                    btn.setBackground(new Color(60, 60, 60));
                } 
            }

            public void mouseExited(MouseEvent evt) {
                if (highlight) {
                    btn.setBackground(UIHelper.HIGHLIGHT_COLOR);
                }
                else {
                    btn.setBackground(UIHelper.DARK_FIELD_COLOR);
                }
            }
        });
        return btn;
    }
}

