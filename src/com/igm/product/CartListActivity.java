package com.igm.product;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.igm.product.adapter.CartArrayAdapter;
import com.igm.product.app.DataHolder;
import com.igm.product.entity.Cart;

import java.util.ArrayList;

/**
 * User: Amir Nikjoo,  01/23/2016,  04:27 PM
 */
public class CartListActivity extends Activity {
    private ArrayList<Cart> cartList = new ArrayList<Cart>();
    ListView lv;
    CartArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.cart_list_view);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(332));
            bar.setIcon(R.drawable.igm_logo2);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        lv = (ListView) findViewById(R.id.cartList);

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null)
//            cartList = bundle.getParcelableArrayList(Constants.INTENT_KEY_CART);

//        MyApplicationData myApplicationData = (MyApplicationData) getApplicationContext();
//        cartList = myApplicationData.getCartArrayList();

        showAdapter();

        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_fade);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                boolean visible = ((Cart) adapterView.getItemAtPosition(i)).getIsDelButtonVisible();
                if (visible) {

                    // animate delete
                    anim.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
//                            view.setHasTransientState(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            cartList.remove(i);
                            adapter.notifyDataSetChanged();
//                            view.setHasTransientState(false);
                        }
                    });
                    view.startAnimation(anim);

/*
                    cartList.remove(i);
                    adapter.notifyDataSetChanged();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                    Toast.makeText(CartListActivity.this, getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();
*/

                } else {
                    /*
                        NumberPicker picker = new NumberPicker(this);
                        picker.setMinValue(0);
                        picker.setMaxValue(2);
                        picker.setDisplayedValues( new String[] { "Belgium", "France", "United Kingdom" } );
                    **/

                    final Dialog dialog = new Dialog(CartListActivity.this);

                    dialog.setTitle(getString(R.string.input_quantity));
                    dialog.setContentView(R.layout.quantity_dialogue);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
                    Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

                    NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
                    np.setMaxValue(1000);
                    np.setMinValue(1);
                    np.setWrapSelectorWheel(true);
                    np.setValue(((Cart) adapterView.getItemAtPosition(i)).getQty());
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
                            np.clearFocus();
                            ((Cart) adapterView.getItemAtPosition(i)).setQty(np.getValue());
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });

                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean visible = ((Cart) adapterView.getItemAtPosition(i)).getIsDelButtonVisible();
                ((Cart) adapterView.getItemAtPosition(i)).setIsDelButtonVisible(!visible);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void showAdapter() {
        DataHolder holder = DataHolder.getInstance();
        cartList = holder.getCartArrayList();
        if (cartList == null)
            cartList = new ArrayList<Cart>();
        adapter = new CartArrayAdapter(this, cartList);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_cart, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                if (cartList.size() > 0) {      // if have row(s)
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    cartList = new ArrayList<Cart>();
                                    DataHolder holder = DataHolder.getInstance();
                                    holder.setCartArrayList(cartList);
                                    showAdapter();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartListActivity.this);
                    builder.setMessage(getString(R.string.are_you_sure_to_delete_all))
                            .setPositiveButton(getString(R.string.positive_answer), dialogClickListener)
                            .setNegativeButton(getString(R.string.negative_answer), dialogClickListener).show();
                }
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void copy2Clipboard(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//        ListView listView = (ListView) view.findViewById(R.id.cartList);
//        ListAdapter listAdapter = listView.getAdapter();
        String factor = getCartListContent();
        if (factor.length() > 0) {
            ClipData clip = ClipData.newPlainText("cart", factor);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), getString(R.string.reside_in_clipboard), Toast.LENGTH_SHORT).show();
        }
    }

    public String getCartListContent() {
        String out = "";
        for (int i = 0; i < adapter.getCount(); i++) {
            Cart c = (Cart) adapter.getItem(i);
            out += c.getPart().getDescription() + ", " + c.getQty() + "\n";
        }
        return out;
    }

    public void sendSms(View v) {
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(String.valueOf(txtRecipients.getText()), null, String.valueOf(txtMessage.getText()), null, null);

        String out = getCartListContent();
        if (out.length() > 0) {
            Uri smsToUri = Uri.parse("smsto: " + getString(R.string.ashrafi_mobile));
            Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body", out);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.no_data_to_send), Toast.LENGTH_SHORT).show();
        }
    }

    public void send2Telegram(View view) {
        String out = getCartListContent();
        if (out.length() > 0) {
            final String appName = "org.telegram.messenger";
            final boolean isAppInstalled = isAppAvailable(getApplicationContext(), appName);
            if (isAppInstalled) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                myIntent.setPackage(appName);
                myIntent.putExtra(Intent.EXTRA_TEXT, out);//
                this.startActivity(Intent.createChooser(myIntent, "Share with"));
            } else {
                Toast.makeText(this, getString(R.string.no_data_to_send), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_data_to_send), Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void sendEmail(View view) {
        String out = getCartListContent();
        if (out.length() > 0) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@iraniangm.ir"});
            i.putExtra(Intent.EXTRA_SUBJECT, "سفارش قطعه");
            i.putExtra(Intent.EXTRA_TEXT, out);
            try {
                startActivity(Intent.createChooser(i, "Send via..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(CartListActivity.this, "There are no clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_data_to_send), Toast.LENGTH_SHORT).show();
        }
    }
}

