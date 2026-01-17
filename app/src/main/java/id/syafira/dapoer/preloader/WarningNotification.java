package id.syafira.dapoer.preloader;

import javafx.application.Preloader;

public record WarningNotification(String message)
        implements Preloader.PreloaderNotification { }
