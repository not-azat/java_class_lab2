package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.Note;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class NoteItemController {

    @FXML private Pane root;
    @FXML private Text text;
    @Nullable private Note note;

    public NoteItemController() {
        System.out.println("NoteItemController()");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/noteItem.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        text.wrappingWidthProperty().bind(root.maxWidthProperty().subtract(10));
        root.maxHeightProperty().setValue(Double.MAX_VALUE);
    }

    public Pane getRoot() {
        return root;
    }

    public void setNote(@NotNull Note note) {
        this.note = note;
        text.setText(note.getText());
    }

}
