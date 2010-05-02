package leoliang.unitpricecompare.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import leoliang.unitpricecompare.model.Quantity.UnitType;
import leoliang.util.ArrayHelper;

public class PriceRanker {

    public class UncomparableUnitException extends Exception {
    }

    private Quantity.UnitType unitType;
    private List<Double> priceRank = new LinkedList<Double>();

    public void rank(Collection<ShoppingItem> items) {
        priceRank.clear();
        unitType = getMajorityUnitType(items);
        for (ShoppingItem item : items) {
            if (item.isEnabled() && item.getQuantity().getUnitType().equals(unitType)) {
                double unitPrice = item.getUnitPrice();
                if (!priceRank.contains(unitPrice)) {
                    priceRank.add(unitPrice);
                }
            }
        }
        Collections.sort(priceRank);
    }

    private UnitType getMajorityUnitType(Collection<ShoppingItem> items) {
        int[] counters = new int[UnitType.values().length];

        for (ShoppingItem item : items) {
            if (item.isEnabled()) {
                UnitType unit = item.getQuantity().getUnitType();
                counters[unit.ordinal()]++;
            }
        }

        return UnitType.values()[ArrayHelper.getIndexOfMax(counters)];
    }

    /**
     * @param item
     * @return the rank of the item, begins from 1; 0 means no rank
     * @throws UncomparableUnitException
     */
    public int getRank(ShoppingItem item) throws UncomparableUnitException {
        if (!item.isEnabled()) {
            return 0;
        }
        if (!item.getQuantity().getUnitType().equals(unitType)) {
            throw new UncomparableUnitException();
        }
        return priceRank.indexOf(item.getUnitPrice()) + 1;
    }

    public double getRatioToBestPrice(ShoppingItem item) {
        return item.getUnitPrice() / priceRank.get(0);
    }
}
