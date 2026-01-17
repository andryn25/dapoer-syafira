package id.syafira.dapoer.model;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class Order {

    private final IntegerProperty idOrder = new SimpleIntegerProperty(this, "idOrder");
    private final ObjectProperty<User> user = new SimpleObjectProperty<>(this, "user");
    private final ObjectProperty<Pelanggan> pelanggan = new SimpleObjectProperty<>(this, "pelanggan");
    private final ObjectProperty<LocalDateTime> tanggal = new SimpleObjectProperty<>(this, "tanggal");
    private final ObjectProperty<LocalDateTime> tanggalKirim = new SimpleObjectProperty<>(this, "tanggalKirim");
    private final StringProperty jenisPesanan = new SimpleStringProperty(this, "jenisPesanan");
    private final LongProperty total = new SimpleLongProperty(this, "total");
    private final StringProperty catatan = new SimpleStringProperty(this, "catatan");

    public Order() {
    }

    public Order(int id, Pelanggan pelanggan) {
        this.idOrder.set(id);
        this.pelanggan.set(pelanggan);
        this.tanggal.set(LocalDateTime.now());
        this.jenisPesanan.set("Dine In");
        this.total.set(0);
    }

    public int getIdOrder() {
        return idOrder.get();
    }

    public void setIdOrder(int value) {
        idOrder.set(value);
    }

    public IntegerProperty idOrderProperty() {
        return idOrder;
    }

    public Pelanggan getPelanggan() {
        return pelanggan.get();
    }

    public void setPelanggan(Pelanggan p) {
        pelanggan.set(p);
    }

    public ObjectProperty<Pelanggan> pelangganProperty() {
        return pelanggan;
    }

    public User getUser() {
        return user.get();
    }

    public void setUser(User u) {
        user.set(u);
    }

    public ObjectProperty<User> userProperty() {
        return user;
    }

    public LocalDateTime getTanggal() {
        return tanggal.get();
    }

    public void setTanggal(LocalDateTime value) {
        tanggal.set(value);
    }

    public ObjectProperty<LocalDateTime> tanggalProperty() {
        return tanggal;
    }

    public LocalDateTime getTanggalKirim() {
        return tanggalKirim.get();
    }

    public void setTanggalKirim(LocalDateTime value) {
        tanggalKirim.set(value);
    }

    public ObjectProperty<LocalDateTime> tanggalKirimProperty() {
        return tanggalKirim;
    }

    public String getJenisPesanan() {
        return jenisPesanan.get();
    }

    public void setJenisPesanan(String value) {
        jenisPesanan.set(value);
    }

    public StringProperty jenisPesananProperty() {
        return jenisPesanan;
    }

    public long getTotal() {
        return total.get();
    }

    public void setTotal(long value) {
        total.set(value);
    }

    public LongProperty totalProperty() {
        return total;
    }

    public String getCatatan() {
        return catatan.get();
    }

    public void setCatatan(String value) {
        catatan.set(value);
    }

    public StringProperty catatanProperty() {
        return catatan;
    }

}
