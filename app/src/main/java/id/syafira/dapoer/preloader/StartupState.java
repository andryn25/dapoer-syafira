package id.syafira.dapoer.preloader;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class StartupState {
    private static final BooleanProperty dbReady = new SimpleBooleanProperty(false);
    public static BooleanProperty dbReadyProperty() { return dbReady; }
    private StartupState() {}
}
