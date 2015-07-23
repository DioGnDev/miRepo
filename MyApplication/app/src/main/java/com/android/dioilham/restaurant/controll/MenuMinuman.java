package com.android.dioilham.restaurant.controll;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.dioilham.restaurant.R;
import com.android.dioilham.restaurant.helper.DatabaseHandler;
import com.android.dioilham.restaurant.helper.TampilToast;
import com.android.dioilham.restaurant.ui.Drink_Detail;
import com.android.dioilham.restaurant.adapter.MinumanAdapter;
import com.android.dioilham.restaurant.helper.Koneksi;
import com.android.dioilham.restaurant.model.Minuman;
import com.android.dioilham.restaurant.parser.JSONParser;
import com.android.dioilham.restaurant.ui.Keranjang;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dioilham on 5/22/15.
 */
public class MenuMinuman extends ActionBarActivity {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    //list
    List<Minuman> drink;
    private ListView listdrink;
    MinumanAdapter adapter;
    TampilToast tos;
    DatabaseHandler db;

    // url to get all products list
    private static String url_menu_minuman;
//    private static String url_menu_minuman = "http://192.168.56.1/restaurant_server/getdrink.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FD = "item_restoran";
    private static final String TAG_KODE = "kode_item";
    private static final String TAG_NAMA = "nama_item";
    private static final String TAG_HARGA = "harga_item";
    private static final String TAG_JENIS = "jenis_item";
    private static final String TAG_KETERANGAN = "keterangan_item";
    private static String TAG_FOOD = "food";
    private static String TAG_DRINK = "drink";
    Koneksi koneksi;

    // products JSONArray
    JSONArray minuman = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_daftar_minuman);
        listdrink = (ListView) findViewById(R.id.list_minuman);
        callNewObjects();

        // Loading products in Background Thread
        new LoadAllProducts().execute();
        Log.d("DRINKS", String.valueOf(drink.toArray()));
        listdrink.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Minuman mimik = drink.get(position);
                String kode = mimik.getKode();
                String nama = mimik.getNama();
                String jenis = mimik.getJenis();
                String ket = mimik.getKeterangan();
                String harga = mimik.getHarga();
                String pesan = "Tambahkan " + "'" + nama + "'" + " ke Daftar Item Transaksi?";

                tampilDialog(pesan,kode,nama,harga,jenis,ket);

//                Intent intent = new Intent(MenuMinuman.this, Drink_Detail.class);
//                intent.putExtra("name", nama);
//                intent.putExtra("detail", ket);
/*                intent.putExtra("price", harga);*/
//                startActivity(intent);
            }
        });
    }

    private void callNewObjects() {
        drink = new ArrayList<Minuman>();
        db = new DatabaseHandler(MenuMinuman.this);
        tos = new TampilToast(MenuMinuman.this);
        adapter = new MinumanAdapter(MenuMinuman.this,R.layout.row_drink,drink);
        koneksi = new Koneksi(MenuMinuman.this);
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MenuMinuman.this);
            pDialog.setMessage("Tunggu Sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            url_menu_minuman = koneksi.urlItem() + "getitem.php";
            Log.d("URL", url_menu_minuman);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag",TAG_DRINK));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_menu_minuman, "POST", params);
            // Check your log cat for JSON reponse
            Log.d("Semua Minuman: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found Getting Array of Products
                    minuman = json.getJSONArray(TAG_FD);
                    // looping through All Products
                    for (int i = 0; i < minuman.length(); i++) {
                        JSONObject object = minuman.getJSONObject(i);
                        /* Storing each json item in variable*/
                        //int kode = object.getInt(TAG_KODE);
                        String nama = object.getString(TAG_NAMA);
                        String harga = object.getString(TAG_HARGA);
                        String keterangan = object.getString(TAG_KETERANGAN);

                        Minuman minuman = new Minuman();
                        minuman.setKode(object.getString(TAG_KODE));
                        minuman.setJenis(object.getString(TAG_JENIS));
                        minuman.setNama(nama);
                        minuman.setHarga(harga);
                        minuman.setKeterangan(keterangan);

                        //masukkan dalam list
                        drink.add(minuman);
                    }
                } else {
                    tos.tosShort("Data Tidak Ditemukan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            listdrink.setAdapter(adapter);
        }

    }

    private void tampilDialog(final String pesan, final String kode, final String nama, final String harga, final String jenis, final String keterangan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MenuMinuman.this);
        builder.setTitle("Daftar Item");
        builder.setMessage(pesan);
        builder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        db = new DatabaseHandler(MenuMinuman.this);
                        int countItem = db.cekSameItemCart(kode);
                        if (countItem > 0) {
                            String jumlah = db.getQtyItemCart(kode);
                            int jum = Integer.parseInt(jumlah) + 1;
                            String qty = String.valueOf(jum);
                            int rSub = calcSubtotal(qty, harga);
                            String subtotal = String.valueOf(rSub);
                            /*Log.d("HASIL PARSE ", subtotal);*/
//                            db.addToCart(kode, nama, harga, jenis, keterangan, qty, harga);
                            db.updateItemCart(kode, qty, subtotal);
                        } else {
                            db.addToCart(kode, nama, harga, jenis, keterangan, "1", harga);
                        }
                        tos.tosShort("Item " + nama + " telah ditambahkan.");
                    }
                });
        builder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog ad = builder.create();
        ad.show();
    }

    private int calcSubtotal(String qty, String harga) {
        int price = Integer.parseInt(harga);
        int quant = Integer.parseInt(qty);
        int result = price * quant;
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            Intent in = new Intent(MenuMinuman.this, Keranjang.class);
            startActivity(in);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
