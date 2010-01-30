package leoliang.unitpricecompare;

import leoliang.android.lib.crashreport.CrashMonitor;
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

        // init UI components

        setContentView(R.layout.item);

        final TextView priceField = (TextView) findViewById(R.id.ItemDialog_PriceField);
        final TextView quantityField = (TextView) findViewById(R.id.ItemDialog_QuantityField);
        final RadioGroup volumeGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_volume);
        final RadioGroup weightGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_weight);

        OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) {
                    return;
                }
                if (group == volumeGroup) {
                    weightGroup.clearCheck();
                } else {
                    volumeGroup.clearCheck();
                }
                priceField.clearFocus();
                quantityField.clearFocus();
            }
        };
        volumeGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        weightGroup.setOnCheckedChangeListener(onCheckedChangeListener);

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
            switch (quantity.getUnitType()) {
            case VOLUME:
                volumeGroup.check(unitButtonId);
                break;
            case WEIGHT:
                weightGroup.check(unitButtonId);
                break;
            }
        } else {
            shoppingItem = new ShoppingItem();
        }
    }

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

        RadioGroup volumeGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_volume);
        int unitButtonId = volumeGroup.getCheckedRadioButtonId();
        if (unitButtonId == -1) {
            RadioGroup weightGroup = (RadioGroup) findViewById(R.id.ItemDialog_Unit_weight);
            unitButtonId = weightGroup.getCheckedRadioButtonId();
        }
        String unit = getUnitFromButtonId(unitButtonId);
        if (unit == null) {
            return false;
        }
        shoppingItem.getQuantity().setUnit(unit);

        return true;
    }

    private int getUnitButtonId(String unitName) {
        if (unitName.equals("g")) {
            return R.id.ItemDialog_Unit_g;
        }
        if (unitName.equals("kg")) {
            return R.id.ItemDialog_Unit_kg;
        }
        if (unitName.equals("oz")) {
            return R.id.ItemDialog_Unit_oz;
        }
        if (unitName.equals("lb")) {
            return R.id.ItemDialog_Unit_lb;
        }
        if (unitName.equals("ml")) {
            return R.id.ItemDialog_Unit_ml;
        }
        if (unitName.equals("cl")) {
            return R.id.ItemDialog_Unit_cl;
        }
        if (unitName.equals("L")) {
            return R.id.ItemDialog_Unit_l;
        }
        if (unitName.equals("fl.oz")) {
            return R.id.ItemDialog_Unit_floz;
        }
        if (unitName.equals("gal")) {
            return R.id.ItemDialog_Unit_gal;
        }
        return -1;
    }

    private String getUnitFromButtonId(int id) {
        switch (id) {
        case R.id.ItemDialog_Unit_g:
            return "g";
        case R.id.ItemDialog_Unit_kg:
            return "kg";
        case R.id.ItemDialog_Unit_oz:
            return "oz";
        case R.id.ItemDialog_Unit_lb:
            return "lb";
        case R.id.ItemDialog_Unit_ml:
            return "ml";
        case R.id.ItemDialog_Unit_cl:
            return "cl";
        case R.id.ItemDialog_Unit_l:
            return "L";
        case R.id.ItemDialog_Unit_floz:
            return "fl.oz";
        case R.id.ItemDialog_Unit_gal:
            return "gal";
        default:
            Log.w(LOG_TAG, "getUnitFromButtonId() unknown button ID: " + id);
            return null;
        }
    }

}
