package id.syafira.dapoer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DetailOrder {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final ObjectProperty<Order> order = new SimpleObjectProperty<>(this, "order");
    private final ObjectProperty<MenuMasakan> menu = new SimpleObjectProperty<>(this, "menu");
    private final IntegerProperty qty = new SimpleIntegerProperty(this, "qty");
    private final LongProperty subtotal = new SimpleLongProperty(this, "subtotal");

    public DetailOrder() {
    }

    public DetailOrder(MenuMasakan menu, int qty, long subtotal) {
        this.menu.set(menu);
        this.qty.set(qty);
        this.subtotal.set(subtotal);
    }

    public int getIdDetail() {
        return id.get();
    }

    public void setIdDetail(int value) {
        id.set(value);
    }

    public IntegerProperty idDetailProperty() {
        return id;
    }

    public Order getOrder() {
        return order.get();
    }

    public void setOrder(Order o) {
        order.set(o);
    }

    public ObjectProperty<Order> orderProperty() {
        return order;
    }

    public MenuMasakan getMenu() {
        return menu.get();
    }

    public void setMenu(MenuMasakan m) {
        menu.set(m);
    }

    public ObjectProperty<MenuMasakan> menuProperty() {
        return menu;
    }

    public int getQty() {
        return qty.get();
    }

    public void setQty(int value) {
        qty.set(value);
    }

    public IntegerProperty qtyProperty() {
        return qty;
    }

    public long getSubtotal() {
        return subtotal.get();
    }

    public void setSubtotal(long value) {
        subtotal.set(value);
    }

    public LongProperty subtotalProperty() {
        return subtotal;
    }

}
