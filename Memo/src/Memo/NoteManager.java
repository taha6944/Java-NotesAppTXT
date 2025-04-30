package Memo;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NoteManager {
    private String notesFolder = "notlar";
    private String deletedFolder = notesFolder + "/deleted";
    private UIComponents uiComponents;

    public NoteManager() {
    }

    public void setUIComponents(UIComponents uiComponents) {
        this.uiComponents = uiComponents;
    }

    public void createDirectories() {
        try {
            Files.createDirectories(Paths.get(notesFolder));
            Files.createDirectories(Paths.get(deletedFolder));
        } catch (IOException e) {
            System.err.println("Klasörler oluşturulamadı: " + e.getMessage());
        }
    }

    public void loadNotes(ListView<String> noteListView) {
        ObservableList<String> notes = noteListView.getItems();
        notes.clear();
        Path notesDir = Paths.get(notesFolder);
        if (!Files.exists(notesDir)) {
            try {
                Files.createDirectories(notesDir);
            } catch (IOException e) {
                uiComponents.showNotification("Notlar klasörü oluşturulamadı: " + e.getMessage());
                return;
            }
        }
        try {
            Files.walk(notesDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .filter(path -> !path.startsWith(Paths.get(deletedFolder)))
                    .map(path -> notesDir.relativize(path).toString())
                    .forEach(notes::add);
        } catch (IOException e) {
            uiComponents.showNotification("Notlar yüklenirken hata: " + e.getMessage());
        }
        if (notes.isEmpty()) {
            uiComponents.showNotification("Listelenecek not bulunamadı.");
        }
    }

    public void loadNote(String notePath) {
        if (notePath == null) return;
        try {
            String content = Files.readString(Paths.get(notesFolder, notePath));
            uiComponents.getEditArea().setText(content);
        } catch (IOException e) {
            uiComponents.showNotification("Not yüklenirken hata: " + e.getMessage());
        }
    }

    public void saveNote(String selectedNote) {
        if (selectedNote == null) return;
        try {
            Files.writeString(Paths.get(notesFolder, selectedNote), uiComponents.getEditArea().getText());
            uiComponents.showNotification("Not güncellendi.");
        } catch (IOException e) {
            uiComponents.showNotification("Not kaydedilirken hata: " + e.getMessage());
        }
    }

    public void saveNoteForNote(String note) {
        if (note == null) return;
        try {
            Files.writeString(Paths.get(notesFolder, note), uiComponents.getEditArea().getText());
        } catch (IOException e) {
            uiComponents.showNotification("Not kaydedilirken hata: " + e.getMessage());
        }
    }

    public void createNewNote(ListView<String> noteListView) {
        String newNoteName = "Yeni_Not.txt";
        int counter = 1;
        Path notesDir = Paths.get(notesFolder);
        try {
            Files.createDirectories(notesDir);
        } catch (IOException e) {
            uiComponents.showNotification("Notlar klasörü oluşturulamadı: " + e.getMessage());
            return;
        }
        Path notePath = notesDir.resolve(newNoteName);
        while (Files.exists(notePath)) {
            newNoteName = "Yeni_Not_" + counter++ + ".txt";
            notePath = notesDir.resolve(newNoteName);
        }
        try {
            Files.createFile(notePath);
            loadNotes(noteListView);
            noteListView.getSelectionModel().select(newNoteName);
            uiComponents.showNotification(newNoteName + " oluşturuldu.");
        } catch (IOException e) {
            uiComponents.showNotification("Yeni not oluşturulurken hata: " + e.getMessage());
        }
    }

    public void deleteNote(String selectedNote, ListView<String> noteListView) {
        if (selectedNote == null) return;
        try {
            Path src = Paths.get(notesFolder, selectedNote);
            Path dst = Paths.get(deletedFolder, selectedNote);
            Files.createDirectories(dst.getParent());
            Files.move(src, dst);
            loadNotes(noteListView);
            uiComponents.getEditArea().clear();
            uiComponents.showNotification(selectedNote + " çöp kutusuna taşındı.");
        } catch (IOException e) {
            uiComponents.showNotification("Not silinirken hata: " + e.getMessage());
        }
    }

    public void renameNote(String selectedNote, String newName, ListView<String> noteListView) {
        if (selectedNote == null) return;
        if (!newName.endsWith(".txt")) newName += ".txt";
        try {
            Path oldPath = Paths.get(notesFolder, selectedNote);
            Path newPath = Paths.get(notesFolder, newName);
            Files.move(oldPath, newPath);
            loadNotes(noteListView);
            uiComponents.showNotification(selectedNote + " → " + newName + " olarak yeniden adlandırıldı.");
        } catch (IOException e) {
            uiComponents.showNotification("Yeniden adlandırma hatası: " + e.getMessage());
        }
    }

    public String getNotesFolder() {
        return notesFolder;
    }

    public String getDeletedFolder() {
        return deletedFolder;
    }
}