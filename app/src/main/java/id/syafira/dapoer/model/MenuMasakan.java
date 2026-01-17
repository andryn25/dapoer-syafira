package id.syafira.dapoer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class MenuMasakan {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty nama = new SimpleStringProperty(this, "nama");
    private final LongProperty harga = new SimpleLongProperty(this, "harga");
    private final StringProperty status = new SimpleStringProperty(this, "status");
    private final IntegerProperty stok = new SimpleIntegerProperty(this, "stok");

    public MenuMasakan() {
    }

    public MenuMasakan(String nama, long harga, String status, int stok) {
        this.nama.set(nama);
        this.harga.set(harga);
        this.status.set(status);
        this.stok.set(stok);
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

    public String getNama() {
        return nama.get();
    }

    public void setNama(String value) {
        nama.set(value);
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public long getHarga() {
        return harga.get();
    }

    public void setHarga(long value) {
        harga.set(value);
    }

    public LongProperty hargaProperty() {
        return harga;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public int getStok() {
        return stok.get();
    }

    public void setStok(int value) {
        stok.set(value);
    }

    public IntegerProperty stokProperty() {
        return stok;
    }
}
