package com.igm.product;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.Toast;
import com.igm.product.util.Constants;

public class MainActivity extends Activity {
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(332));
            bar.setIcon(R.drawable.igm_logo2);
        }
    }

    public void showDaewoo(View view) {
        Intent i = new Intent(getBaseContext(), ProductListActivity.class);
        i.putExtra(Constants.INTENT_KEY_CAR_TYPE, 1);
        startActivity(i);
    }

    public void showPeugeot(View view) {
        Intent i = new Intent(getBaseContext(), ProductListActivity.class);
        i.putExtra(Constants.INTENT_KEY_CAR_TYPE, 2);
        startActivity(i);
    }

    public void showPride(View view) {
        Intent i = new Intent(getBaseContext(), ProductListActivity.class);
        i.putExtra(Constants.INTENT_KEY_CAR_TYPE, 3);
        startActivity(i);
    }

    public void helpMe(View view) {
        Intent i = new Intent(getBaseContext(), HelpActivity.class);
        startActivity(i);
    }

    public void aboutUs(View view) {
        Intent i = new Intent(getBaseContext(), AboutUsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_cart){
            Intent i = new Intent(getBaseContext(), CartListActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.two_tap_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
