package Memo;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UIComponents {
    private ListView<String> noteListView;
    private TextArea editArea;
    private Label notificationLabel;
    private ObservableList<String> notes = FXCollections.observableArrayList();
    private BorderPane root;
    private NoteManager noteManager;
    private ThemeManager themeManager;
    private TrashManager trashManager;

    public UIComponents(NoteManager noteManager, ThemeManager themeManager) {
        this.noteManager = noteManager;
        this.themeManager = themeManager;
        this.trashManager = new TrashManager(noteManager, this);
        noteManager.setUIComponents(this);
        themeManager.setUIComponents(this);
    }

    public void setupUI(BorderPane root) {
        this.root = root;

        Button themeButton = new Button("Tema");
        themeButton.setOnAction(e -> themeManager.toggleTheme());
        Button trashButton = new Button("Çöp Kutusu");
        trashButton.setOnAction(e -> trashManager.showTrash());
        HBox topBar = new HBox(10, themeButton, trashButton);
        topBar.setAlignment(Pos.TOP_LEFT);

        editArea = new TextArea();
        editArea.setWrapText(true);
        editArea.setPrefWidth(600);
        editArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && noteListView.getSelectionModel().getSelectedItem() != null) {
                noteManager.saveNote(noteListView.getSelectionModel().getSelectedItem());
            }
        });
        root.setCenter(editArea);

        noteListView = new ListView<>(notes);
        noteListView.setPrefWidth(200);
        noteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null && !editArea.getText().isEmpty()) {
                noteManager.saveNoteForNote(oldVal);
            }
            noteManager.loadNote(newVal);
        });
        VBox leftPane = new VBox(noteListView);
        root.setLeft(leftPane);

        Button newNoteButton = new Button("Yeni Not");
        newNoteButton.setOnAction(e -> noteManager.createNewNote(noteListView));
        Button deleteButton = new Button("Sil");
        deleteButton.setOnAction(e -> noteManager.deleteNote(noteListView.getSelectionModel().getSelectedItem(), noteListView));
        deleteButton.disableProperty().bind(Bindings.isEmpty(noteListView.getSelectionModel().getSelectedItems()));
        Button renameButton = new Button("Yeniden Adlandır");
        renameButton.setOnAction(e -> {
            String selectedNote = noteListView.getSelectionModel().getSelectedItem();
            if (selectedNote != null) {
                TextInputDialog dialog = new TextInputDialog(selectedNote);
                dialog.setTitle("Yeniden Adlandır");
                dialog.setHeaderText(selectedNote + " için yeni isim girin:");
                dialog.setContentText("Yeni isim:");
                dialog.showAndWait().ifPresent(newName -> noteManager.renameNote(selectedNote, newName, noteListView));
            }
        });
        Button clearButton = new Button("Seçimi Kaldır");
        clearButton.setOnAction(e -> {
            noteListView.getSelectionModel().clearSelection();
            editArea.clear();
        });

        VBox buttonBox = new VBox(10, newNoteButton, renameButton, deleteButton, clearButton);
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);
        root.setBottom(buttonBox);

        notificationLabel = new Label();
        notificationLabel.getStyleClass().add("notification");
        root.setTop(new VBox(topBar, notificationLabel));
    }

    public void showNotification(String message) {
        notificationLabel.setText(message);
        new Thread(() -> {
            try {
                Thread.sleep(500);
                javafx.application.Platform.runLater(() -> notificationLabel.setText(""));
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    public ListView<String> getNoteListView() {
        return noteListView;
    }

    public TextArea getEditArea() {
        return editArea;
    }

    public BorderPane getRoot() {
        return root;
    }
}