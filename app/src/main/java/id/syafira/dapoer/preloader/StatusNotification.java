package id.syafira.dapoer.preloader;

import javafx.application.Preloader;

public record StatusNotification(String message, double progress)
        implements Preloader.PreloaderNotification { }
