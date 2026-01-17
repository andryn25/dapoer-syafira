package id.syafira.dapoer.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Kelas bantuan untuk mengelola animasi antarmuka pengguna (UI) yang umum
 * digunakan.
 * Menyediakan berbagai efek seperti fade, slide, bounce, dan pulse untuk
 * meningkatkan pengalaman pengguna.
 */
public class AnimationHelper {

    /**
     * Efek Fade-In standar (durasi 500ms).
     * 
     * @param node Komponen JavaFX yang akan diberi animasi.
     */
    public static void fadeIn(Node node) {
        fadeIn(node, 500);
    }

    /**
     * Efek Fade-In dengan durasi kustom.
     * 
     * @param node       Komponen JavaFX.
     * @param durationMs Durasi animasi dalam milidetik.
     */
    public static void fadeIn(Node node, int durationMs) {
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(durationMs), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setInterpolator(Interpolator.EASE_OUT);
        fade.play();
    }

    /**
     * Animasi muncul mendatar dari bawah ke atas disertai efek fade-in.
     * 
     * @param node Komponen JavaFX.
     */
    public static void slideInUp(Node node) {
        node.setOpacity(0);
        node.setTranslateY(20);

        ParallelTransition parallel = new ParallelTransition();

        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(400), node);
        slide.setFromY(20);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        parallel.getChildren().addAll(fade, slide);
        parallel.play();
    }

    /**
     * Animasi muncul dari kiri ke kanan (biasanya untuk item sidebar).
     * 
     * @param node  Komponen JavaFX.
     * @param delay Penundaan sebelum animasi dimulai (milidetik).
     */
    public static void slideInLeft(Node node, int delay) {
        node.setOpacity(0);
        node.setTranslateX(-50);

        PauseTransition pause = new PauseTransition(Duration.millis(delay));
        pause.setOnFinished(e -> {
            ParallelTransition parallel = new ParallelTransition();

            FadeTransition fade = new FadeTransition(Duration.millis(300), node);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);

            TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
            slide.setFromX(-50);
            slide.setToX(0);
            slide.setInterpolator(Interpolator.EASE_OUT);

            parallel.getChildren().addAll(fade, slide);
            parallel.play();
        });
        pause.play();
    }

    /**
     * Efek pantulan lembut (bounce) saat tombol ditekan atau disorot.
     * 
     * @param node Komponen JavaFX.
     */
    public static void bounce(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.play();
    }

    /**
     * Animasi denyut sukses (biasanya dengan pendaran hijau/skala).
     * 
     * @param node Komponen JavaFX.
     */
    public static void successPulse(Node node) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.scaleXProperty(), 1.0),
                        new KeyValue(node.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(node.scaleXProperty(), 1.08, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleYProperty(), 1.08, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(node.scaleXProperty(), 1.0, Interpolator.EASE_IN),
                        new KeyValue(node.scaleYProperty(), 1.0, Interpolator.EASE_IN)));
        timeline.play();
    }

    /**
     * Animasi getar (shake) untuk menunjukkan adanya kesalahan input.
     * 
     * @param node Komponen JavaFX.
     */
    public static void shake(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.play();
    }

    /**
     * Animasi fade-in berurutan untuk beberapa komponen sekaligus (misalnya baris
     * tabel).
     * 
     * @param nodes Kumpulan komponen JavaFX.
     */
    public static void staggeredFadeIn(Node... nodes) {
        for (int i = 0; i < nodes.length; i++) {
            final Node node = nodes[i];
            final int delay = i * 50; // Penundaan 50ms antar komponen

            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(e -> fadeIn(node, 200));
            pause.play();
        }
    }

    /**
     * Animasi transisi masuk saat Scene pertama kali ditampilkan.
     * 
     * @param node Node utama dalam Scene.
     */
    public static void sceneEntrance(Node node) {
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setInterpolator(Interpolator.EASE_OUT);
        fade.play();
    }

    /**
     * Animasi transisi keluar saat Scene akan ditutup atau diganti.
     * 
     * @param node       Node utama dalam Scene.
     * @param onFinished Kode yang dijalankan setelah animasi selesai.
     */
    public static void sceneExit(Node node, Runnable onFinished) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setInterpolator(Interpolator.EASE_IN);
        fade.setOnFinished(e -> onFinished.run());
        fade.play();
    }
}
