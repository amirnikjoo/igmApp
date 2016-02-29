package com.igm.product;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import com.igm.product.dbhelper.DatabaseHelper;
import com.igm.product.entity.Part;
import com.igm.product.util.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends Activity {
    // URL to get contacts JSON
    private static String url = "http://iraniangm.ir/app/products.json";
//    private static String url = "http://api.androidhive.info/contacts/";

    // JSON Node names
    private static final String TAG_PRODUCTS = "products";

    private ProgressDialog pDialog;
    boolean doubleBackToExitPressedOnce = false;
    DatabaseHelper db = new DatabaseHelper(this);
    TextView tvLastUpdate = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main);

        List<Part> ps = db.getAllParts();
        if (ps.size() == 0) {
            insertAllRecords();
        } else
            Toast.makeText(this, ps.size() + " records already defined...", Toast.LENGTH_SHORT).show();


//        db.insertLastEdit();
        Long a = db.getLastEditDate();
        tvLastUpdate = (TextView) findViewById(R.id.tvLastUpdate);
        tvLastUpdate.setText("updated at: " + a);

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
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    public void showPeugeot(View view) {
        Intent i = new Intent(getBaseContext(), ProductListActivity.class);
        i.putExtra(Constants.INTENT_KEY_CAR_TYPE, 2);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    public void showPride(View view) {
        Intent i = new Intent(getBaseContext(), ProductListActivity.class);
        i.putExtra(Constants.INTENT_KEY_CAR_TYPE, 3);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    public void helpMe(View view) {
        Intent i = new Intent(getBaseContext(), HelpActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    public void aboutUs(View view) {
        Intent i = new Intent(getBaseContext(), AboutUsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    public void readJsonFromAsset(View view) {
        if (isNetworkAvailable())
            new RefreshProducts().execute();
        else
            Toast.makeText(this, getString(R.string.internet_connection_not_present), Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_cart) {
            Intent i = new Intent(getBaseContext(), CartListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    private class RefreshProducts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(getString(R.string.refresh_products));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONArray parts = null;
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String jsonStr = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonStr = sb.toString();
            } catch (Exception e) {
                // Oops
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                }
            }

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    parts = jsonObj.getJSONArray(TAG_PRODUCTS);
//                    parts = jsonObj.getJSONArray("contacts");
                    for (int i = 0; i < parts.length(); i++) {
                        JSONObject c = parts.getJSONObject(i);

                        String id = c.getString("id");
                        String desc = c.getString("description");
                        String carType = c.getString("type");
                        String status = c.getString("status");

                        if (db.getPartById(Integer.valueOf(id)) != null)
                            db.updatePart(new Part(Integer.valueOf(id), desc, Integer.valueOf(carType), Integer.valueOf(status)));
                        else
                            db.insertPart(new Part(Integer.valueOf(id), desc, Integer.valueOf(carType), Integer.valueOf(status)));

//                        Toast.makeText(MainActivity.this, id + " " + desc + " به روز رسانی شد.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            long currentTime = db.updateLastModified();
            tvLastUpdate = (TextView) findViewById(R.id.tvLastUpdate);
            tvLastUpdate.setText("updated at: " + currentTime);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void insertAllRecords() {
        DatabaseHelper db = new DatabaseHelper(this);

        db.insertPart(new Part(1, "آرم جلو و عقب سي يلو", 1, 1));
        db.insertPart(new Part(2, "آرم جلو و عقب ماتيز", 1, 1));
        db.insertPart(new Part(3, "آهني کوچک توپي سر کمک ماتيز", 1, 1));
        db.insertPart(new Part(4, "ابرويي زير چراغ جلو ماتيز چپ", 1, 1));
        db.insertPart(new Part(5, "ابرويي زير چراغ جلو ماتيز راست", 1, 1));
        db.insertPart(new Part(6, "اورينگ کشويي چرخ جلو دوو", 1, 1));
        db.insertPart(new Part(7, "اورينگ ليور دسته دنده دوو", 1, 1));
        db.insertPart(new Part(8, "اورينگ ترموستات دوو", 1, 1));
        db.insertPart(new Part(9, "اورينگ دلکو دوو", 1, 1));
        db.insertPart(new Part(10, "اورينگ دلکو ماتيز", 1, 1));
        db.insertPart(new Part(11, "اورينگ واتر پمپ دوو", 1, 1));
        db.insertPart(new Part(12, "بست کمربندي اگزوز عقب دوو", 1, 1));
        db.insertPart(new Part(13, "بوش پلاستيکي پين کلاج دوو", 1, 1));
        db.insertPart(new Part(14, "بوش خرچنگي ليور دنده (تک اورينگ) دوو", 1, 1));
        db.insertPart(new Part(15, "بوش رابط خرچنگي ليور دنده (دو اورينگ) دوو", 1, 1));
        db.insertPart(new Part(16, "بوش کله قندي طبق ماتيز", 1, 1));
        db.insertPart(new Part(17, "بوش لقي گلويي فرمان دوو", 1, 1));
        db.insertPart(new Part(18, "بوش مچي طبق دوو", 1, 1));
        db.insertPart(new Part(19, "بوش ميل فرمان دوو", 1, 1));
        db.insertPart(new Part(20, "بوش ميل موجگير دوو", 1, 1));
        db.insertPart(new Part(21, "بوش سيم دوقلو تعويض دنده ماتيز و MVM", 1, 1));
        db.insertPart(new Part(22, "بوش و پين پدال کلاج دوو", 1, 1));
        db.insertPart(new Part(23, "پولک آب بزرگ ماتيز ( بغل سيلندر )", 1, 1));
        db.insertPart(new Part(24, "پولک آب کوچک ماتيز ( سر سيلندر ) ", 1, 1));
        db.insertPart(new Part(25, "پولک آب بزرگ دوو", 1, 1));
        db.insertPart(new Part(26, "پولک آب متوسط دوو", 1, 1));
        db.insertPart(new Part(27, "گل پاش عقب دوو سي يلو", 1, 1));
        db.insertPart(new Part(28, "پين خرچنگي ليور دنده دوو", 1, 1));
        db.insertPart(new Part(29, "تشتکي سيلندر چرخ عقب دوو اسپرو", 1, 1));
        db.insertPart(new Part(30, "تشتکي سيلندر چرخ عقب دوو سي يلو", 1, 1));
        db.insertPart(new Part(31, "دياق ابرويي سپر جلو سي يلو چپ ", 1, 1));
        db.insertPart(new Part(32, "دياق ابرويي سپر جلو سي يلو راست", 1, 1));
        db.insertPart(new Part(33, "دياق خاک اندازي سپر عقب سي يلو چپ ", 1, 1));
        db.insertPart(new Part(34, "دياق خاک اندازي سپر عقب سي يلو راست", 1, 1));
        db.insertPart(new Part(35, "دياق خاک اندازي سپر جلو سي يلو چپ ", 1, 1));
        db.insertPart(new Part(36, "دياق خاک اندازي سپر جلو سي يلو راست", 1, 1));
        db.insertPart(new Part(37, "دستگيره سقف سي يلو", 1, 1));
        db.insertPart(new Part(38, "دستگيره مچي در راست سي يلو", 1, 1));
        db.insertPart(new Part(39, "دسته موتور راست دوو سي يلو", 1, 1));
        db.insertPart(new Part(40, "دسته موتور عقب گيربکس دوو سي يلو", 1, 1));
        db.insertPart(new Part(41, "دوشاخه استارت دوو", 1, 1));
        db.insertPart(new Part(42, "خار تيغي دياق سپر دوو", 1, 1));
        db.insertPart(new Part(43, "خارملخي ميل کاپوت دوو", 1, 1));
        db.insertPart(new Part(44, "خار مربعي ته ميل کاپوت دوو", 1, 1));
        db.insertPart(new Part(45, "شيشه پرژکتور چپ سی يلو", 1, 1));
        db.insertPart(new Part(46, "شيشه پرژکتور راست سي يلو ", 1, 1));
        db.insertPart(new Part(47, "شيلنگ واسطه سه راهی آب ماتيز", 1, 1));
        db.insertPart(new Part(48, "شيلنگ بخاری ماتيز ", 1, 1));
        db.insertPart(new Part(49, "شيلنگ بخار روغن سی يلو", 1, 1));
        db.insertPart(new Part(50, "شيلنگ بالاي رادياتور منجيددار سي يلو", 1, 1));
        db.insertPart(new Part(51, "شيلنگ زانويي کمپرس روغن سي يلو", 1, 1));
        db.insertPart(new Part(52, "طلق راهنماي جلو چپ ماتيز", 1, 1));
        db.insertPart(new Part(53, "طلق راهنماي جلو راست ماتيز", 1, 1));
        db.insertPart(new Part(54, "فنر اگزوز دوو", 1, 1));
        db.insertPart(new Part(55, "قاب دور پرژکتور سي يلو چپ", 1, 1));
        db.insertPart(new Part(56, "قاب دور پرژکتور سي يلو راست", 1, 1));
        db.insertPart(new Part(57, "قاب پلاک جلو سي يلو سفيد", 1, 1));
        db.insertPart(new Part(58, "قاب پلاک عقب سي يلو سفيد", 1, 1));
        db.insertPart(new Part(59, "قاب پلاک جلو سي يلو مشکی", 1, 1));
        db.insertPart(new Part(60, "قاب پلاک عقب سي يلو مشکی", 1, 1));
        db.insertPart(new Part(61, "قاب تسمه تايم کامل سي يلو", 1, 1));
        db.insertPart(new Part(62, "قاب دور دستگيره داخل سي يلو ", 1, 1));
        db.insertPart(new Part(63, "کاپ کشويي چرخ جلو دوو", 1, 1));
        db.insertPart(new Part(64, "کورکن سيلندر ماتيز", 1, 1));
        db.insertPart(new Part(65, "کورکن منيفولد ماتيز با واشر", 1, 1));
        db.insertPart(new Part(66, "گردگير پلوس سمت چرخ دوو", 1, 1));
        db.insertPart(new Part(67, "گردگير پلوس سمت گيربکس دوو", 1, 1));
        db.insertPart(new Part(68, "گردگير پلوس سمت چرخ ماتيز", 1, 1));
        db.insertPart(new Part(69, "گردگير پلوس سمت گيربکس دوو ماتيز", 1, 1));
        db.insertPart(new Part(70, "گردگير سيلندر چرخ عقب دوو", 1, 1));
        db.insertPart(new Part(71, "صفحه مشکي پلاستيکي رو بوقي سي يلو", 1, 1));
        db.insertPart(new Part(72, "گردگير کشويي لبه دار چرخ جلو دوو", 1, 1));
        db.insertPart(new Part(73, "لاستيک داخل در مخزن کلاج بالا دوو ", 1, 1));
        db.insertPart(new Part(74, "لاستيک شلنگ روي بوستر دوو", 1, 1));
        db.insertPart(new Part(75, "لاستيک گرد ته بوستر دوو", 1, 1));
        db.insertPart(new Part(76, "لاستيک پدال کلاج و ترمز دوو", 1, 1));
        db.insertPart(new Part(77, "لاستيک پدال گاز دوو", 1, 1));
        db.insertPart(new Part(78, "لاستيک توپي سر کمک ماتيز", 1, 1));
        db.insertPart(new Part(79, "لاستيک چاکدار موجگير ماتيز", 1, 1));
        db.insertPart(new Part(80, "واشر سه گوش گلويِي اگزوز ماتيز اورجينال", 1, 1));
        db.insertPart(new Part(81, "لاستيک چاکدار موجگير اسپرو اصلي", 1, 1));
        db.insertPart(new Part(82, "لاستيک دور ليواني دوو", 1, 1));
        db.insertPart(new Part(83, "لاستيک ضربگير بالا راديات سي يلو", 1, 1));
        db.insertPart(new Part(84, "لاستيک ضربگير بالا راديات اسپرو", 1, 1));
        db.insertPart(new Part(85, "لوازم پمپ ترمز سي يلو ( نيمه کامل با فنر )", 1, 1));
        db.insertPart(new Part(86, "لوازم سيلندر چرخ عقب اسپرو کامل", 1, 1));
        db.insertPart(new Part(87, "لوازم سيلندر چرخ عقب سي يلو و ماتيز کامل ", 1, 1));
        db.insertPart(new Part(88, "لوازم کشوئي چرخ جلو دوو ( يکطرف )", 1, 1));
        db.insertPart(new Part(89, "ليواني کمک کامل دوو", 1, 1));
        db.insertPart(new Part(90, "ميل موجگير کامل دوو", 1, 1));
        db.insertPart(new Part(91, "منجيد عقب اگزوز دوو ", 1, 1));
        db.insertPart(new Part(92, "منجيد وسط اگزوز دوو ", 1, 1));
        db.insertPart(new Part(93, "واشر اويل پمپ سي يلو", 1, 1));
        db.insertPart(new Part(94, "واشر اويل پمپ ماتيز", 1, 1));
        db.insertPart(new Part(95, "واشر در سوپاپ سي يلو", 1, 1));
        db.insertPart(new Part(96, "واشر در سوپاپ اسپرو", 1, 1));
        db.insertPart(new Part(97, "واشر کارتل موتور سي يلو (چوب پنبه مشکي)", 1, 1));
        db.insertPart(new Part(98, "واشر کارتل موتور سي يلو ( ويکتور )", 1, 1));
        db.insertPart(new Part(99, "واشر کارتل گيربکس دوو ( ويکتور )", 1, 1));
        db.insertPart(new Part(100, "واشر منيفولد دود سي يلو و ريسر ", 1, 1));
        db.insertPart(new Part(101, "واشر منيفولد هواي سي يلو و ريسر 94", 1, 1));
        db.insertPart(new Part(102, "واشر منيفولد هواي ريسر 92", 1, 1));
        db.insertPart(new Part(103, "واشر منيفولد هواي اسپرو", 1, 1));
        db.insertPart(new Part(104, "واشر منيفولد دود اسپرو", 1, 1));
        db.insertPart(new Part(105, "واشر پايه دلکو ماتيز", 1, 1));
        db.insertPart(new Part(106, "واشر منيفولد هوا اورينگي ماتيز", 1, 1));
        db.insertPart(new Part(107, "واشر منيفولد دود ماتيز ( فلزي )", 1, 1));
        db.insertPart(new Part(108, "واشر در سوپاپ ماتيز", 1, 1));
        db.insertPart(new Part(109, "واشر گلويي اگزوز دوو", 1, 1));
        db.insertPart(new Part(110, "واشر لاستيکي در روغن موتور دوو", 1, 1));
        db.insertPart(new Part(111, "واشر لاستيکي در پمپ بنزين دوو", 1, 1));
        db.insertPart(new Part(112, "هوزينگ ترموستات سي يلو", 1, 1));
        db.insertPart(new Part(113, "خرطومي هواکش چهار راهي سي يلو", 1, 1));
        db.insertPart(new Part(114, "مهره کارتل دوو و هيونداي اصلي کره", 1, 1));
        db.insertPart(new Part(115, "ضربگير دو سر پيچ راديات کولر سي يلو", 1, 1));
        db.insertPart(new Part(116, "بست بالاي راديات آب سيلو", 1, 1));
        db.insertPart(new Part(117, "گردگير دور توپي سوئيچ موتور", 1, 1));
        db.insertPart(new Part(118, "خار آمپولي سپر دوو سي يلو", 1, 1));
        db.insertPart(new Part(119, "قاب تسمه تايم پاييني ماتيز ", 1, 1));
        db.insertPart(new Part(120, "قاب دور ضبط دوو سی يلو", 1, 1));

        db.insertPart(new Part(200, "بوش جناقي پژو 405 ", 2, 1));
        db.insertPart(new Part(201, "بوش لبه دار طبق پژو 405 ", 2, 1));
        db.insertPart(new Part(202, "ميل موجگير پژو 405", 2, 1));
        db.insertPart(new Part(203, "چاکدار موجگير پژو 405 (دست 4عدد)", 2, 1));
        db.insertPart(new Part(204, "قرقری فرمان پژو 405", 2, 1));
        db.insertPart(new Part(205, "سيبک فرمان پژو 405 چپ و راست", 2, 1));
        db.insertPart(new Part(206, "سيبک زير کمک پژو 405", 2, 1));
        db.insertPart(new Part(207, "توپي سر کمک پژو 405 ساده ", 2, 1));
        db.insertPart(new Part(208, "توپي سر کمک پژو 405 ( لبه دار)", 2, 1));
        db.insertPart(new Part(209, "توپي سر کمک پژو 405 (جديد دو لبه دار)", 2, 1));
        db.insertPart(new Part(210, "گردگير پلوس سمت چرخ پژو 1800", 2, 1));
        db.insertPart(new Part(211, "گردگير پلوس 'گيربکس پژو 1800 ", 2, 1));
        db.insertPart(new Part(212, "گردگير جعبه فرمان (دو سرگشاد) راست", 2, 1));
        db.insertPart(new Part(213, "گردگير جعبه فرمان ( يکسر گشاد ) چپ", 2, 1));
        db.insertPart(new Part(215, "دسته موتور زير باتري پژو 405 ", 2, 1));
        db.insertPart(new Part(216, "دسته موتورگرد پلاستيکی پژو 405", 2, 1));
        db.insertPart(new Part(217, "دسته موتورگرد فلزی پژو 405", 2, 1));
        db.insertPart(new Part(218, "دسته موتور دو سر پيچ پژو 405 روغن دار", 2, 1));
        db.insertPart(new Part(219, "واشر در سوپاپ پژو 405 و پرشيا", 2, 1));
        db.insertPart(new Part(220, "واشر بغل اگزوز پژو 405 (يک دست)", 2, 1));
        db.insertPart(new Part(221, "پولي هرزگرد پژو 405", 2, 1));
        db.insertPart(new Part(222, "اورينگ ترموستات پژو 405 ", 2, 1));
        db.insertPart(new Part(223, "اورينگ انژکتور پژو 405 و زانتيا (يکدست)", 2, 1));
        db.insertPart(new Part(224, "لوازم سيلندر چرخ عقب پژو 405 ", 2, 1));
        db.insertPart(new Part(225, "لوازم سيلندر چرخ عقب سمند ", 2, 1));
        db.insertPart(new Part(226, "منجيد اگزوز انتها يي پژو 1800 ", 2, 1));
        db.insertPart(new Part(227, "منجيد اگزوز مياني پژو 1800 ", 2, 1));
        db.insertPart(new Part(228, "بوش طبق پژو 206 ساده", 2, 1));
        db.insertPart(new Part(229, "ميل موجگير پژو 206 ", 2, 1));
        db.insertPart(new Part(230, "سيبک فرمان  پژو 206 چپ و راست", 2, 1));
        db.insertPart(new Part(231, "قرقری فرمان پژو 206", 2, 1));
        db.insertPart(new Part(232, "دسته موتور دو سر پيچ  پژو 206 تيپ 5  و زانتيا  ", 2, 1));
        db.insertPart(new Part(233, "دسته موتورگرد فلزی پژو 206", 2, 1));
        db.insertPart(new Part(234, "دسته موتورگرد پلاستيکی پژو 206", 2, 1));
        db.insertPart(new Part(235, "دسته موتور زير باتري پژو 206 ", 2, 1));
        db.insertPart(new Part(236, "اورينگ پايه فيلتر روغن پژو 206  همه تيپ ها", 2, 1));
        db.insertPart(new Part(237, "اورينگ انژکتور 206 پرايد و پيکان و آردي", 2, 1));
        db.insertPart(new Part(238, "واشر گلويي اگزوز پژو 206", 2, 1));
        db.insertPart(new Part(239, "منجيد اگزوز پژو 206 ", 2, 1));
        db.insertPart(new Part(240, "لوازم سيلندر چرخ عقب پژو 206", 2, 1));

        db.insertPart(new Part(300, "دسته موتور شماره  1  پرايد", 3, 1));
        db.insertPart(new Part(301, "دسته موتور شماره  2  پرايد", 3, 1));
        db.insertPart(new Part(302, "واشر در سوپاپ پرايد", 3, 1));
        db.insertPart(new Part(303, "گردگير پلوس سمت چرخ پرايد ", 3, 1));
        db.insertPart(new Part(304, "گردگير پلوس سه گوش سمت گيربکس پرايد ", 3, 1));
        db.insertPart(new Part(305, "گردگير جعبه فرمان پرايد  ", 3, 1));
        db.insertPart(new Part(306, "گردگير بلند سيلندر چرخ جلو پرايد ", 3, 1));
        db.insertPart(new Part(307, "گردگير کوتاه سيلندر چرخ جلو پرايد ", 3, 1));
        db.insertPart(new Part(308, "منجيد اگزوز پرايد ", 3, 1));
        db.insertPart(new Part(309, "لوازم سيلندر چرخ عقب پرايد ", 3, 1));
        db.insertPart(new Part(310, "لاستيک زير منبع پمپ ترمز پرايد ", 3, 1));
        db.insertPart(new Part(311, "لاستيک تعادل پرايد", 3, 1));
        db.insertPart(new Part(312, "لاستيک چاکدار پرايد", 3, 1));
        db.insertPart(new Part(313, "گردگير بالاي کمک فنر جلو پرايد  ", 3, 1));
        db.insertPart(new Part(314, "گردگير بالاي کمک فنر عقب پرايد ", 3, 1));
        db.insertPart(new Part(315, "لاستيک گرد بالاي فنرلول جلو پرايد(بدون لبه)", 3, 1));
        db.insertPart(new Part(316, "لاستيک گرد بالاي فنرلول عقب پرايد(لبه دار)", 3, 1));
        db.insertPart(new Part(317, "لاستيکهاي نر و ماده بالا کمک عقب", 3, 1));
        db.insertPart(new Part(318, "توپی سر کمک پرايد", 3, 1));

        Toast.makeText(this, " all records inserted successfully...", Toast.LENGTH_SHORT).show();
    }


}
