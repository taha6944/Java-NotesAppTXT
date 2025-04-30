package Memo;

import javafx.scene.layout.BorderPane;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ThemeManager {
    private String theme = "light";
    private UIComponents uiComponents;

    public ThemeManager() {
    }

    public void setUIComponents(UIComponents uiComponents) {
        this.uiComponents = uiComponents;
    }

    public void toggleTheme() {
        theme = theme.equals("light") ? "dark" : "light";
        applyTheme(uiComponents.getRoot());
        saveUserPreferences();
    }

    public void applyTheme(BorderPane root) {
        root.getStyleClass().removeAll("light-theme", "dark-theme");
        root.getStyleClass().add(theme + "-theme");
    }

    public void loadUserPreferences() {
        try (BufferedReader reader = new BufferedReader(new FileReader("user.dat"))) {
            theme = reader.readLine().trim();
            if (!theme.equals("light") && !theme.equals("dark")) theme = "light";
        } catch (IOException e) {
            theme = "light"; // Varsayılan tema
        }
    }

    private void saveUserPreferences() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user.dat"))) {
            writer.write(theme);
        } catch (IOException e) {
            uiComponents.showNotification("Tema kaydedilemedi: " + e.getMessage());
        }
    }
}