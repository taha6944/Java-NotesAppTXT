package Memo;

import javafx.application.Application;
import javafx.geometry.Insets; // Eksik olan import
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class NotesApp extends Application {
    private BorderPane root;
    private NoteManager noteManager;
    private UIComponents uiComponents;
    private ThemeManager themeManager;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        noteManager = new NoteManager();
        themeManager = new ThemeManager();
        uiComponents = new UIComponents(noteManager, themeManager);

        themeManager.loadUserPreferences();
        noteManager.createDirectories();

        root.setPadding(new Insets(10)); // Hata burada çözülüyor
        uiComponents.setupUI(root);
        noteManager.loadNotes(uiComponents.getNoteListView());

        themeManager.applyTheme(root);

        Scene scene = new Scene(root, 1100, 700);
        try {
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("CSS dosyası bulunamadı: styles.css");
        }
        primaryStage.setTitle("Notlar Uygulaması");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}