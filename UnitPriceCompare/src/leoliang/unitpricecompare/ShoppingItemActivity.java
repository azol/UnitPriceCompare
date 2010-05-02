package leoliang.unitpricecompare;

import java.util.HashMap;
import java.util.Map;

import leoliang.android.lib.crashreport.CrashMonitor;
import leoliang.android.widget.util.CompoundRadioGroup;
import leoliang.unitpricecompare.model.Quantity;
import leoliang.unitpricecompare.model.ShoppingItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.flurry.android.FlurryAgent;

/**
 * UI for input shopping item information.
 */
public class ShoppingItemActivity extends Activity {

    public static final String EXTRA_SHOPPING_ITEM = "shoppingItem";
    public static final String EXTRA_SHOPPING_ITEM_INDEX = "shoppingItemIndex";

    private static final String LOG_TAG = "UnitPriceCompare";

    private ShoppingItem shoppingItem;
    private CompoundRadioGroup radioGroups = new CompoundRadioGroup();
    private Map<Integer, String> buttons;

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "onStart");
        super.onStart();
        CrashMonitor.monitor(this);
        FlurryAgent.setCaptureUncaughtExceptions(false);
        FlurryAgent.onStartSession(this, "5Q82B7WVG6DAIHNFF649");
    }

    @Override
    public void onStop() {
        Log.v(LOG_TAG, "onStop");
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUnitButtonMap();

        // init UI components

        setContentView(R.layout.item);

        final TextView priceField = (TextView) findViewById(R.id.ItemDialog_PriceField);
        final TextView quantityField = (TextView) findViewById(R.id.ItemDialog_QuantityField);
        final RadioGroup noUnitGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_notAvailable);
        final RadioGroup volumeGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_volume);
        final RadioGroup weightGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_weight);
        final RadioGroup lengthGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_length);

        radioGroups.add(weightGroup);
        radioGroups.add(volumeGroup);
        radioGroups.add(noUnitGroup);
        radioGroups.add(lengthGroup);

        OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                priceField.clearFocus();
                quantityField.clearFocus();
            }
        };
        radioGroups.setOnCheckedChangeListener(onCheckedChangeListener);

        Button okButton = (Button) findViewById(R.id.ItemDialog_ok);
        okButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (updateShoppingItem()) {
                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_SHOPPING_ITEM, shoppingItem);
                    ShoppingItemActivity.this.setResult(RESULT_OK, intent);
                    ShoppingItemActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.invalid_input, Toast.LENGTH_LONG).show();
                }
            }
        });
        Button cancelButton = (Button) findViewById(R.id.ItemDialog_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ShoppingItemActivity.this.setResult(RESULT_CANCELED);
                ShoppingItemActivity.this.finish();
            }
        });

        // init values

        shoppingItem = (ShoppingItem) getIntent().getSerializableExtra(EXTRA_SHOPPING_ITEM);
        if (shoppingItem != null) {
            priceField.setText(Double.toString(shoppingItem.getPrice()));
            Quantity quantity = shoppingItem.getQuantity();
            quantityField.setText(quantity.getValueExpression());

            int unitButtonId = getUnitButtonId(quantity.getUnitName());
            radioGroups.check(unitButtonId);
        } else {
            shoppingItem = new ShoppingItem();
        }
    }

    /**
     * @return false if input is invalid
     */
    protected boolean updateShoppingItem() {

        TextView priceField = (TextView) findViewById(R.id.ItemDialog_PriceField);
        String price = priceField.getText().toString();
        try {
            shoppingItem.setPrice(Double.parseDouble(price));
        } catch (NumberFormatException e) {
            priceField.requestFocus();
            return false;
        }

        TextView quantityField = (TextView) findViewById(R.id.ItemDialog_QuantityField);
        try {
            shoppingItem.getQuantity().setValue(quantityField.getText().toString());
        } catch (ArithmeticException e) {
            quantityField.requestFocus();
            return false;
        }

        int checkedRadioButtonId = radioGroups.getCheckedRadioButtonId();
        String unit = getUnitFromButtonId(checkedRadioButtonId);
        if (unit == null) {
            Log.w(LOG_TAG, "updateShoppingItem(): Unknown button ID: " + checkedRadioButtonId);
            return false;
        }
        shoppingItem.getQuantity().setUnit(unit);

        return true;
    }

    private int getUnitButtonId(String unitName) {
        for (int id : buttons.keySet()) {
            if (buttons.get(id).equals(unitName)) {
                return id;
            }
        }
        Log.w(LOG_TAG, "getUnitButtonId(): Unknow unit: " + unitName);
        return -1;
    }

    private String getUnitFromButtonId(int id) {
        return buttons.get(id);
    }

    private void initializeUnitButtonMap() {
        buttons = new HashMap<Integer, String>();

        buttons.put(R.id.ItemDialog_Unit_mm, "mm");
        buttons.put(R.id.ItemDialog_Unit_cm, "cm");
        buttons.put(R.id.ItemDialog_Unit_m, "m");
        buttons.put(R.id.ItemDialog_Unit_inch, "inch");
        buttons.put(R.id.ItemDialog_Unit_ft, "ft");

        buttons.put(R.id.ItemDialog_Unit_g, "g");
        buttons.put(R.id.ItemDialog_Unit_kg, "kg");
        buttons.put(R.id.ItemDialog_Unit_oz, "oz");
        buttons.put(R.id.ItemDialog_Unit_lb, "lb");

        buttons.put(R.id.ItemDialog_Unit_ml, "ml");
        buttons.put(R.id.ItemDialog_Unit_cl, "cl");
        buttons.put(R.id.ItemDialog_Unit_l, "L");
        buttons.put(R.id.ItemDialog_Unit_floz, "fl.oz");
        buttons.put(R.id.ItemDialog_Unit_gal, "gal");

        buttons.put(R.id.ItemDialog_Unit_none, "");
    }
}
