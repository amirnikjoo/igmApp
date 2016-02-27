package com.igm.product;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.igm.product.adapter.ProductArrayAdapter;
import com.igm.product.app.DataHolder;
import com.igm.product.dbhelper.DatabaseHelper;
import com.igm.product.entity.Cart;
import com.igm.product.entity.Part;
import com.igm.product.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Amir Nikjoo,  01/23/2016,  04:27 PM
 */
public class ProductListActivity extends Activity {
    //    private PartList parts;
    private List<Part> parts;
    private ArrayList<Cart> cartList = new ArrayList<Cart>();
    ListView lv;
    ProductArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.product_list_view);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(332));
            bar.setIcon(R.drawable.igm_logo2);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }

        lv = (ListView) findViewById(R.id.list);

        Bundle bundle = getIntent().getExtras();
        Integer i = (Integer) bundle.get(Constants.INTENT_KEY_CAR_TYPE);

        DatabaseHelper db = new DatabaseHelper(this);
        parts = db.getAllPartsByCarType(i);

        adapter = new ProductArrayAdapter(this, parts);
        lv.setAdapter(adapter);

//        registerForContextMenu(lv);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Part part = (Part) adapter.getItem(i);
                DataHolder holder = DataHolder.getInstance();
                cartList = holder.getCartArrayList();
                boolean isNotExist = true;
                if (cartList == null)
                    cartList = new ArrayList<Cart>();
                else {
                    //check if exist this part in cart or not!
                    for (Cart a : cartList) {
                        if (a.getPart().getId() == part.getId())
                            isNotExist = false;
                    }
                }
                if (isNotExist) {
                    cartList.add(new Cart(part, 1));
                    Toast.makeText(getApplicationContext(), part.getDescription() + " " + getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
                    holder.setCartArrayList(cartList);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(50);
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.already_added_to_cart), Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        EditText txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d("TEST", String.valueOf(charSequence));
                adapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_product, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_my_cart) {
            Intent i = new Intent(getBaseContext(), CartListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}

