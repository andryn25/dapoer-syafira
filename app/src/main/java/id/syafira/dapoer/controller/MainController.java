package id.syafira.dapoer.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import id.syafira.dapoer.util.AlertHelper;
import id.syafira.dapoer.util.AnimationHelper;
import id.syafira.dapoer.util.SceneManager;

import java.io.IOException;
import java.net.URL;

import id.syafira.dapoer.Session;
import id.syafira.dapoer.model.User;

/**
 * Kontroler utama aplikasi (Dashboard).
 * Berfungsi sebagai wadah induk untuk navigasi antar menu, pengaturan akses
 * pengguna (RBAC),
 * dan pengelolaan dialog kustom di atas lapisan antarmuka utama.
 */
public class MainController {

    @FXML
    private BorderPane root; // Wadah utama aplikasi

    @FXML
    private VBox sidebarVBox; // Tempat menu navigasi ditampilkan

    @FXML
    private VBox menuGreatings; // Menu branding/salam pembuka

    @FXML
    private Label lblCurrentUser; // Label nama user yang login

    @FXML
    private Label lblUserType; // Label role/level user

    @FXML
    private ImageView iconTheme;

    @FXML
    private Label lblTheme;

    @FXML
    private Label lblTitleItem;

    @FXML
    private StackPane mainStack; // Layer bertumpuk untuk menampung dialong overlay

    @FXML
    private VBox overlayLayer; // Kontainer untuk dialog konfirmasi/alert kustom

    @FXML
    private Label lblOverlayTitle;

    @FXML
    private Label lblOverlayMessage;

    @FXML
    private Button btnOverlayCancel;

    @FXML
    private Button btnOverlayConfirm;

    @FXML
    private ImageView imgOverlayIcon;

    private static MainController instance;
    private Runnable onConfirmAction; // Aksi yang dijalankan saat tombol 'Ya' di klik pada overlay

    /**
     * Mengambil instance MainController agar bisa diakses dari kontroler anak.
     */
    public static MainController getInstance() {
        return instance;
    }

    /**
     * Inisialisasi awal dashboard.
     * Mengatur profil pengguna, hak akses menu, dan tampilan pembuka.
     */
    @FXML
    public void initialize() {
        instance = this;
        AnimationHelper.sceneEntrance(root);

        menuGreatings.setOnMouseClicked(e -> handleOpenGreatings());

        // Menyiapkan profil pengguna jika sesi tersedia
        if (Session.isLoggedIn()) {
            lblCurrentUser.setText(Session.getCurrentUser().getNama());
            lblUserType.setText(Session.getCurrentUser().getLevel().getNamaLevel());

            // Merender sidebar yang berbeda untuk setiap role (RBAC)
            renderSidebar(Session.getCurrentUser());
        }
        handleOpenGreatings(); // Tampilan default saat pertama masuk
    }

    /**
     * Merender item menu di sidebar berdasarkan hak akses (Role) pengguna.
     */
    private void renderSidebar(User user) {
        sidebarVBox.getChildren().clear();
        String role = user.getLevel().getNamaLevel();
        if (role == null)
            return;

        // Pembatasan Akses berdasar Role
        if (role.equalsIgnoreCase("Admin")) {
            createMenuSection("TRANSAKSI",
                    new MenuItem("Pesanan Baru", "/images/pesanan_icon.png", this::handleOpenPesanan),
                    new MenuItem("Pembayaran (Kasir)", "/images/transaksi_icon.png", this::handleOpenTransaksi));
            createMenuSection("MASTER DATA",
                    new MenuItem("Data Menu Masakan", "/images/masakan_icon.png", this::handleOpenMenu),
                    new MenuItem("Data Pelanggan", "/images/pelanggan_icon.png", this::handleOpenPelanggan),
                    new MenuItem("Data Pengguna", "/images/pengguna_icon.png", this::handleOpenPengguna),
                    new MenuItem("Level Akses", "/images/level_icon.png", this::handleOpenLevel));
            createMenuSection("LAPORAN",
                    new MenuItem("Laporan & Cetak", "/images/laporan_icon.png", this::handleOpenLaporan));
        } else if (role.equalsIgnoreCase("Kasir")) {
            createMenuSection("TRANSAKSI",
                    new MenuItem("Pesanan Baru", "/images/pesanan_icon.png", this::handleOpenPesanan),
                    new MenuItem("Pembayaran (Kasir)", "/images/transaksi_icon.png", this::handleOpenTransaksi));
            createMenuSection("MASTER DATA",
                    new MenuItem("Data Pelanggan", "/images/pelanggan_icon.png", this::handleOpenPelanggan));
        } else if (role.equalsIgnoreCase("Owner")) {
            createMenuSection("LAPORAN",
                    new MenuItem("Laporan & Cetak", "/images/laporan_icon.png", this::handleOpenLaporan));
        }
    }

    /**
     * Membuat kategori menu beserta item-itemnya di sidebar dengan animasi.
     */
    private void createMenuSection(String title, MenuItem... items) {
        Label labelTitle = new Label(title);
        labelTitle.getStyleClass().add("text-muted");
        labelTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        VBox.setMargin(labelTitle, new Insets(10, 0, 5, 16));
        sidebarVBox.getChildren().add(labelTitle);

        int delay = 0;
        for (MenuItem item : items) {
            HBox menuHBox = new HBox(12);
            menuHBox.getStyleClass().add("sidebar-item");
            menuHBox.setAlignment(Pos.CENTER_LEFT);

            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(item.iconPath)));
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            icon.setPreserveRatio(true);

            Label labelMenu = new Label(item.name);
            menuHBox.getChildren().addAll(icon, labelMenu);

            // Kejadian saat menu di klik
            menuHBox.setOnMouseClicked(e -> {
                item.action.run();
                setActiveMenu(menuHBox); // Menandai menu yang aktif
            });

            sidebarVBox.getChildren().add(menuHBox);
            AnimationHelper.slideInLeft(menuHBox, delay);
            delay += 50;
        }
    }

    /**
     * Kelas internal untuk struktur data item menu.
     */
    private static class MenuItem {
        String name;
        String iconPath;
        Runnable action;

        MenuItem(String name, String iconPath, Runnable action) {
            this.name = name;
            this.iconPath = iconPath;
            this.action = action;
        }
    }

    /**
     * Mengatur gaya visual untuk menandai menu mana yang sedang dibuka.
     */
    private void setActiveMenu(HBox activeMenu) {
        sidebarVBox.getChildren().forEach(node -> {
            if (node instanceof HBox) {
                node.getStyleClass().remove("sidebar-item-active");
            }
        });
        activeMenu.getStyleClass().add("sidebar-item-active");
    }

    private void clearView() {
        root.setCenter(null);
    }

    // --- Navigasi Halaman ---

    @FXML
    private void handleOpenPelanggan() {
        switchModule("/view/PelangganView.fxml", "Gagal Buka Pelanggan");
    }

    @FXML
    private void handleOpenPengguna() {
        switchModule("/view/UserView.fxml", "Gagal Buka Pengguna");
    }

    @FXML
    private void handleOpenLevel() {
        switchModule("/view/LevelView.fxml", "Gagal Buka Level");
    }

    @FXML
    private void handleOpenPesanan() {
        switchModule("/view/OrderView.fxml", "Gagal Buka Pesanan");
    }

    @FXML
    private void handleOpenMenu() {
        switchModule("/view/MenuView.fxml", "Gagal Buka Menu Masakan");
    }

    @FXML
    private void handleOpenTransaksi() {
        switchModule("/view/TransaksiView.fxml", "Gagal Buka Transaksi");
    }

    @FXML
    private void handleOpenLaporan() {
        switchModule("/view/LaporanView.fxml", "Gagal Buka Laporan");
    }

    /**
     * Helper untuk memuat moful FXML ke area tengah (Center) BorderPane.
     */
    private void switchModule(String fxmlPath, String errorPrefix) {
        try {
            clearView();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            root.setCenter(view);
            AnimationHelper.fadeIn(view); // Efek transisi halus saat modul terbuka
        } catch (IOException e) {
            AlertHelper.showError(errorPrefix, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    /**
     * Membuka layar salam pembuka (branding) dengan gambar background.
     */
    @FXML
    private void handleOpenGreatings() {
        clearView();
        URL imagePath = getClass().getResource("/images/greatings.jpeg");
        if (imagePath == null)
            return;

        String imagePathString = imagePath.toExternalForm();

        StackPane imagePane = new StackPane();
        imagePane.setStyle(
                "-fx-background-image: url('" + imagePathString + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;");

        ScrollPane greatingsView = new ScrollPane();
        greatingsView.setContent(imagePane);
        greatingsView.setFitToWidth(true);
        greatingsView.setFitToHeight(true);

        // Sinkronisasi ukuran gambar dengan viewport scroller
        imagePane.prefWidthProperty().bind(greatingsView.widthProperty());
        imagePane.prefHeightProperty().bind(greatingsView.heightProperty());

        root.setCenter(greatingsView);
        AnimationHelper.fadeIn(greatingsView);
    }

    // --- Custom Overlay Dialog ---
    // Digunakan untuk menggantikan Alert standard Java agar sinkron dengan desain
    // aplikasi.

    /**
     * Menampilkan dialog konfirmasi kustom.
     */
    public void showCustomConfirm(String message, Runnable onConfirm) {
        lblOverlayTitle.setText("Konfirmasi");
        lblOverlayMessage.setText(message);
        this.onConfirmAction = onConfirm;

        btnOverlayCancel.setVisible(true);
        btnOverlayCancel.setManaged(true);
        btnOverlayConfirm.setText("Ya");
        btnOverlayConfirm.getStyleClass().setAll("button");

        animateOverlay();
    }

    /**
     * Menampilkan dialog informasi atau kesalahan kustom.
     */
    public void showCustomAlert(String title, String message, boolean isError) {
        lblOverlayTitle.setText(title);
        lblOverlayMessage.setText(message);
        this.onConfirmAction = null;

        btnOverlayCancel.setVisible(false);
        btnOverlayCancel.setManaged(false);
        btnOverlayConfirm.setText("OK");

        if (isError) {
            btnOverlayConfirm.getStyleClass().setAll("button", "button-danger");
        } else {
            btnOverlayConfirm.getStyleClass().setAll("button");
        }

        animateOverlay();
    }

    /**
     * Menjalankan animasi munculnya dialog overlay.
     */
    private void animateOverlay() {
        overlayLayer.setVisible(true);
        AnimationHelper.fadeIn(overlayLayer, 200);

        Node dialogCard = overlayLayer.getChildren().get(0);
        dialogCard.setScaleX(0.7);
        dialogCard.setScaleY(0.7);
        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(300),
                dialogCard);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        st.play();
    }

    @FXML
    private void handleOverlayConfirm() {
        overlayLayer.setVisible(false);
        if (onConfirmAction != null) {
            onConfirmAction.run();
        }
    }

    @FXML
    private void handleOverlayCancel() {
        overlayLayer.setVisible(false);
        onConfirmAction = null;
    }

    /**
     * Menangani proses logout pengguna.
     */
    @FXML
    private void handleLogout() {
        showCustomConfirm("Apakah Anda yakin ingin logout?", () -> {
            AnimationHelper.sceneExit(root, () -> {
                try {
                    Session.logout();
                    SceneManager.switchScene(root, "/view/LoginView.fxml", SceneManager.loginConfig());
                } catch (Exception e) {
                    AlertHelper.showError("Gagal Logout", "Terjadi kesalahan: " + e.getMessage());
                }
            });
        });
    }
}