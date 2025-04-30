package Memo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TrashManager {
    private NoteManager noteManager;
    private UIComponents uiComponents;

    public TrashManager(NoteManager noteManager, UIComponents uiComponents) {
        this.noteManager = noteManager;
        this.uiComponents = uiComponents;
    }

    public void showTrash() {
        Stage trashStage = new Stage();
        trashStage.setTitle("Çöp Kutusu");
        VBox trashPane = new VBox(10);
        ListView<String> trashList = new ListView<>();
        trashList.setPrefSize(400, 300);

        Path deletedDir = Paths.get(noteManager.getDeletedFolder());
        try {
            Files.walk(deletedDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(deletedDir::relativize)
                    .map(Path::toString)
                    .forEach(trashList.getItems()::add);
        } catch (IOException e) {
            uiComponents.showNotification("Çöp kutusu yüklenirken hata: " + e.getMessage());
        }

        Button restoreButton = new Button("Geri Yükle");
        restoreButton.setOnAction(e -> {
            String selected = trashList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    Path src = Paths.get(noteManager.getDeletedFolder(), selected);
                    Path dst = Paths.get(noteManager.getNotesFolder(), selected);
                    Files.move(src, dst);
                    trashList.getItems().remove(selected);
                    noteManager.loadNotes(uiComponents.getNoteListView());
                    uiComponents.showNotification(selected + " geri yüklendi.");
                } catch (IOException ex) {
                    uiComponents.showNotification("Geri yükleme hatası: " + ex.getMessage());
                }
            }
        });

        Button deletePermButton = new Button("Kalıcı Sil");
        deletePermButton.setOnAction(e -> {
            String selected = trashList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    Files.delete(Paths.get(noteManager.getDeletedFolder(), selected));
                    trashList.getItems().remove(selected);
                    uiComponents.showNotification(selected + " kalıcı olarak silindi.");
                } catch (IOException ex) {
                    uiComponents.showNotification("Silme hatası: " + ex.getMessage());
                }
            }
        });

        HBox trashButtons = new HBox(10, restoreButton, deletePermButton);
        trashPane.getChildren().addAll(trashList, trashButtons);
        Scene trashScene = new Scene(trashPane, 400, 350);
        trashScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        trashStage.setScene(trashScene);
        trashStage.show();
    }
}