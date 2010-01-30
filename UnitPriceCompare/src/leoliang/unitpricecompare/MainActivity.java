package leoliang.unitpricecompare;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import leoliang.android.lib.crashreport.CrashMonitor;
import leoliang.unitpricecompare.model.PriceRanker;
import leoliang.unitpricecompare.model.ShoppingItem;
import leoliang.unitpricecompare.model.PriceRanker.UncomparableUnitException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.flurry.android.FlurryAgent;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "UnitPriceCompare";

    private static final int ACTION_CREATE = 45341;
    private static final int ACTION_EDIT = 45342;

    private ItemList itemList;

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
        setContentView(R.layout.main);

        itemList = new ItemList(this);

        ListView listView = (ListView) findViewById(R.id.ShoppingItemList);
        listView.setAdapter(itemList);
        listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu contextmenu, View view, ContextMenuInfo contextmenuinfo) {
                android.view.MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.item_context_menu, contextmenu);
            }
        });

        Button addButton = (Button) findViewById(R.id.AddItemButton);
        addButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShoppingItemActivity.class);
                startActivityForResult(intent, ACTION_CREATE);
            }

        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemIndex = (int) ((AdapterContextMenuInfo) item.getMenuInfo()).id;
        switch (item.getItemId()) {
        case R.id.EditItem:
            ShoppingItem shoppingItem = (ShoppingItem) itemList.getItem(itemIndex);
            Intent intent = new Intent(getApplicationContext(), ShoppingItemActivity.class);
            intent.putExtra(ShoppingItemActivity.EXTRA_SHOPPING_ITEM, shoppingItem);
            intent.putExtra(ShoppingItemActivity.EXTRA_SHOPPING_ITEM_INDEX, itemIndex);
            startActivityForResult(intent, ACTION_EDIT);
            return true;
        case R.id.DeleteItem:
            itemList.deleteItem(itemIndex);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ShoppingItem item = (ShoppingItem) data.getSerializableExtra(ShoppingItemActivity.EXTRA_SHOPPING_ITEM);

            switch (requestCode) {
            case ACTION_CREATE:
                itemList.addItem(item);
                break;
            case ACTION_EDIT:
                itemList.setItem(data.getIntExtra(ShoppingItemActivity.EXTRA_SHOPPING_ITEM_INDEX, -1), item);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.ClearAllItems:
            itemList.clear();
            return true;
        case R.id.help:
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public class ItemList extends BaseAdapter {

        private final String[] rankName = { "Best Buy", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th" };
        private final LayoutInflater inflater;
        private List<ShoppingItem> items = new ArrayList<ShoppingItem>();
        private PriceRanker ranker = new PriceRanker();
        private NumberFormat numberFormat;

        public ItemList(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            numberFormat = NumberFormat.getPercentInstance();
            numberFormat.setMaximumFractionDigits(0);
        }

        public void setItem(int index, ShoppingItem item) {
            if (index < 0) {
                items.add(item);
            } else {
                items.set(index, item);
            }
            notifyItemUpdated();
        }

        public void addItem(ShoppingItem item) {
            setItem(-1, item);
        }

        public int getCount() {
            return items.size();
        }

        public Object getItem(int i) {
            return items.get(i);
        }

        public long getItemId(int i) {
            return i;
        }

        public View getView(int i, View convertView, ViewGroup viewgroup) {
            ShoppingItem item = items.get(i);
            View view = inflater.inflate(R.layout.item_view, null);

            TextView priceView = (TextView) view.findViewById(R.id.price);
            TextView rankView = (TextView) view.findViewById(R.id.rank);
            TextView quantityView = (TextView) view.findViewById(R.id.quantity);
            TextView ratioView = (TextView) view.findViewById(R.id.ratio);

            NumberFormat format = NumberFormat.getCurrencyInstance();
            priceView.setText(format.format(item.getPrice()));
            quantityView.setText(item.getQuantity().toString());
            try {
                int rank = ranker.getRank(item);
                if ((rank > 0) && (rank <= rankName.length)) {
                    rankView.setText(rankName[rank - 1]);
                    if (rank <= 3) {
                        rankView.setTextColor(Color.rgb(0, 153, 0)); // dark green
                    }
                }
                if (rank > 1) {
                    ratioView.setText("+" + numberFormat.format(ranker.getRatioToBestPrice(item) - 1));
                }
            } catch (UncomparableUnitException e) {
                rankView.setText(R.string.uncomparable);
                rankView.setTextColor(Color.RED);
            }

            return view;
        }

        public void deleteItem(int index) {
            items.remove(index);
            notifyItemUpdated();
        }

        public void clear() {
            items.clear();
            notifyItemUpdated();
        }

        public void notifyItemUpdated() {
            ranker.rank(items);
            notifyDataSetChanged();
        }
    }
}