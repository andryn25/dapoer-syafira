package id.syafira.dapoer.preloader;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class PreloaderController {

    private static PreloaderController instance;

    @FXML
    private Label statusLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private VBox errorBox;
    @FXML
    private Button retryButton;
    @FXML
    private Button exitButton;

    @FXML
    public void initialize() {
        // Set instance saat controller di-load oleh FXMLLoader
        instance = this;
    }

    public static PreloaderController getInstance() {
        return instance;
    }

    // Method untuk update UI dari AppPreloader
    public void showStatus(String msg, double progress) {
        statusLabel.setText(msg);
        progressBar.setProgress(progress);
        hideError();
    }

    public void showError(String msg) {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setText(msg);
        errorBox.setManaged(true);
        errorBox.setVisible(true);
        progressBar.setManaged(false);
        progressBar.setVisible(false);
    }

    private void hideError() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
        errorBox.setManaged(false);
        errorBox.setVisible(false);
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }
}
