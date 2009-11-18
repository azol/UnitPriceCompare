package leoliang.unitpricecompare.model;

import java.io.Serializable;

import leoliang.unitpricecompare.util.SimpleMathEvaluator;

public class Quantity implements Serializable {

    public enum UnitType {
        WEIGHT, VOLUME
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

    public void setValue(String valueExpression) throws ArithmeticException {
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
        return value;
    }

    public UnitType getUnitType() {
        if (unit.equals("g") || unit.equals("kg") || unit.equals("lb") || unit.equals("oz")) {
            return UnitType.WEIGHT;
        }
        return UnitType.VOLUME;
    }

    @Override
    public String toString() {
        return expression + " " + unit;
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