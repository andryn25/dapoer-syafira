package id.syafira.dapoer.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Pelanggan {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty nama = new SimpleStringProperty(this, "nama");
    private final StringProperty noHp = new SimpleStringProperty(this, "noHp");
    private final StringProperty alamat = new SimpleStringProperty(this, "alamat");

    public Pelanggan() {
    }

    public Pelanggan(String nama, String noHp, String alamat) {
        this.nama.set(nama);
        this.noHp.set(noHp);
        this.alamat.set(alamat);
    }

    // id
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // nama
    public String getNama() {
        return nama.get();
    }

    public void setNama(String value) {
        nama.set(value);
    }

    public StringProperty namaProperty() {
        return nama;
    }

    // noHp
    public String getNoHp() {
        return noHp.get();
    }

    public void setNoHp(String value) {
        noHp.set(value);
    }

    public StringProperty noHpProperty() {
        return noHp;
    }

    // alamat
    public String getAlamat() {
        return alamat.get();
    }

    public void setAlamat(String value) {
        alamat.set(value);
    }

    public StringProperty alamatProperty() {
        return alamat;
    }
}
