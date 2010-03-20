package leoliang.unitpricecompare.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class ShoppingItem implements Serializable {

    private double price;
    private Quantity quantity;

    public ShoppingItem() {
        quantity = new Quantity();
    }

    public ShoppingItem(JSONObject jsonObject) throws JSONException {
        setPrice(jsonObject.getDouble("price"));
        quantity = new Quantity(jsonObject.getJSONObject("quantity"));
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

    public JSONObject toJson() throws JSONException {
        return new JSONObject().put("price", price).put("quantity", quantity.toJson());
    }
}
