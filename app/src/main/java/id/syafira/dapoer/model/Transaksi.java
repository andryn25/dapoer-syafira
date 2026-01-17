package id.syafira.dapoer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import java.time.LocalDateTime;

public class Transaksi {
    private final IntegerProperty idTransaksi = new SimpleIntegerProperty(this, "idTransaksi");
    private final ObjectProperty<Order> order = new SimpleObjectProperty<>(this, "order");
    private final ObjectProperty<User> user = new SimpleObjectProperty<>(this, "user");
    private final ObjectProperty<LocalDateTime> tanggal = new SimpleObjectProperty<>(this, "tanggal");
    private final LongProperty totalBayar = new SimpleLongProperty(this, "totalBayar");

    public Transaksi() {
    }

    public Transaksi(Order order, User user, long totalBayar) {
        this.order.set(order);
        this.user.set(user);
        this.tanggal.set(LocalDateTime.now());
        this.totalBayar.set(totalBayar);
    }

    // idTransaksi
    public int getIdTransaksi() {
        return idTransaksi.get();
    }

    public void setIdTransaksi(int value) {
        idTransaksi.set(value);
    }

    public IntegerProperty idTransaksiProperty() {
        return idTransaksi;
    }

    // order
    public Order getOrder() {
        return order.get();
    }

    public void setOrder(Order value) {
        order.set(value);
    }

    public ObjectProperty<Order> orderProperty() {
        return order;
    }

    // user
    public User getUser() {
        return user.get();
    }

    public void setUser(User value) {
        user.set(value);
    }

    public ObjectProperty<User> userProperty() {
        return user;
    }

    // tanggal
    public LocalDateTime getTanggal() {
        return tanggal.get();
    }

    public void setTanggal(LocalDateTime value) {
        tanggal.set(value);
    }

    public ObjectProperty<LocalDateTime> tanggalProperty() {
        return tanggal;
    }

    // totalBayar
    public long getTotalBayar() {
        return totalBayar.get();
    }

    public void setTotalBayar(long value) {
        totalBayar.set(value);
    }

    public LongProperty totalBayarProperty() {
        return totalBayar;
    }
}
