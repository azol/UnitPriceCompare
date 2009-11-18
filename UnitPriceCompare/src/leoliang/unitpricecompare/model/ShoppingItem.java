package leoliang.unitpricecompare.model;

import java.io.Serializable;

public class ShoppingItem implements Serializable {

    private double price;
    private Quantity quantity;

    public ShoppingItem() {
        quantity = new Quantity();
    }

    public double getUnitPrice() {
        return price / quantity.getQuantityInBasicUnit();
    }

    public double getPrice() {
        return price;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isValid() {
        return ((price > 0) && quantity.isValid());
    }
}
