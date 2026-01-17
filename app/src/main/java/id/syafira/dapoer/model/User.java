package id.syafira.dapoer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty username = new SimpleStringProperty(this, "username");
    private final StringProperty password = new SimpleStringProperty(this, "password");
    private final StringProperty nama = new SimpleStringProperty(this, "nama");
    private final ObjectProperty<Level> level = new SimpleObjectProperty<>(this, "level");

    public User() {
    }

    public User(String username, String password, String nama, Level level) {
        this.username.set(username);
        this.password.set(password);
        this.nama.set(nama);
        this.level.set(level);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String value) {
        username.set(value);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String value) {
        password.set(value);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getNama() {
        return nama.get();
    }

    public void setNama(String value) {
        nama.set(value);
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public Level getLevel() {
        return level.get();
    }

    public void setLevel(Level value) {
        level.set(value);
    }

    public ObjectProperty<Level> levelProperty() {
        return level;
    }
}
