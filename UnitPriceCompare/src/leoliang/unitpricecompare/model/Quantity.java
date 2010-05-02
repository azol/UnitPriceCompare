package leoliang.unitpricecompare.model;

import java.io.Serializable;

import leoliang.unitpricecompare.util.SimpleMathEvaluator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Quantity implements Serializable {

    private static final String LOG_TAG = "UnitPriceCompare";

    public enum UnitType {
        WEIGHT, VOLUME, NONE, LENGTH
    }

    private String expression;
    private String unit;
    private double value;

    public Quantity() {
        // empty
    }

    public Quantity(String valueExpression, String unit) throws ArithmeticException {
        setValue(valueExpression);
        setUnit(unit);
    }

    public Quantity(JSONObject jsonObject) throws ArithmeticException, JSONException {
        setValue(jsonObject.getString("expression"));
        setUnit(jsonObject.getString("unit"));
    }

    public void setValue(String valueExpression) throws ArithmeticException {
        Log.d(LOG_TAG, "Set quantity value to:" + valueExpression);
        expression = valueExpression;
        value = SimpleMathEvaluator.eval(valueExpression);
    }

    public double getQuantityInBasicUnit() {
        if (unit.equals("kg")) {
            return value * 1000;
        }
        if (unit.equals("lb")) {
            return value * 453.6;
        }
        if (unit.equals("oz")) {
            return value * 28.35;
        }
        if (unit.equals("cl")) {
            return value * 10;
        }
        if (unit.equals("L")) {
            return value * 1000;
        }
        if (unit.equals("fl.oz")) {
            return value * 29.57;
        }
        if (unit.equals("gal")) {
            return value * 3785;
        }
        if (unit.equals("cm")) {
            return value * 10;
        }
        if (unit.equals("m")) {
            return value * 1000;
        }
        if (unit.equals("inch")) {
            return value * 25.4;
        }
        if (unit.equals("ft")) {
            return value * 304.8;
        }
        return value;
    }

    public UnitType getUnitType() {
        if (unit.equals("")) {
            return UnitType.NONE;
        }
        if (unit.equals("g") || unit.equals("kg") || unit.equals("lb") || unit.equals("oz")) {
            return UnitType.WEIGHT;
        }
        if (unit.equals("mm") || unit.equals("cm") || unit.equals("m") || unit.equals("inch") || unit.equals("ft")) {
            return UnitType.LENGTH;
        }
        return UnitType.VOLUME;
    }

    @Override
    public String toString() {
        return expression + " " + unit;
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject().put("expression", expression).put("unit", unit);
    }

    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getValueExpression() {
        return expression;
    }

    public String getUnitName() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
