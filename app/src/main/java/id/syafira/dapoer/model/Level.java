package id.syafira.dapoer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Level {

    private final IntegerProperty idLevel = new SimpleIntegerProperty(this, "idLevel");
    private final StringProperty namaLevel = new SimpleStringProperty(this, "namaLevel");

    public Level() {
    }

    public Level(String namaLevel) {
        this.namaLevel.set(namaLevel);
    }

    public int getIdLevel() {
        return idLevel.get();
    }

    public void setIdLevel(int value) {
        idLevel.set(value);
    }

    public IntegerProperty idLevelProperty() {
        return idLevel;
    }

    public String getNamaLevel() {
        return namaLevel.get();
    }

    public void setNamaLevel(String value) {
        namaLevel.set(value);
    }

    public StringProperty namaLevelProperty() {
        return namaLevel;
    }
}
