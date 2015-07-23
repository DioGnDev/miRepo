package com.android.dioilham.restaurant.ui;

import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.android.dioilham.restaurant.R;
import com.android.dioilham.restaurant.adapter.CartAdapter;
import com.android.dioilham.restaurant.helper.DatabaseHandler;
import com.android.dioilham.restaurant.helper.TampilToast;
import com.android.dioilham.restaurant.model.ItemCart;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielnimafa on 19/06/2015.
 */
public class Cart extends ActionBarActivity {

    TextView subtotal, total;
    ListView listItem;
    CartAdapter adapter;
    ArrayList<ItemCart> items;
    DatabaseHandler db;
    ProgressDialog pDialog;
    TampilToast tos;
    int jumTagihan;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        getRefRes(); // get resource ID
        callNewObjects();
        loadTagihanItems();
    }

    private void onClickListItem() {
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemCart ic = items.get(position);
                String nama = ic.getNamaCart();
                String harga = ic.getHargaCart();
                String qty = ic.getQtyCart();

                Intent in = new Intent(Cart.this, CartDetil.class);
                in.putExtra("nama", nama);
                in.putExtra("harga", harga);
                in.putExtra("qty", qty);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        items.clear();
        db = new DatabaseHandler(Cart.this);
        loadTagihanItems();
    }

    private void loadTagihanItems() {
        jumTagihan = db.countCart();
        if (jumTagihan > 0) {
            new LoadAllItemsCart().execute();
            listItem.setAdapter(adapter);
            onClickListItem();
        } else {
            tos.tosShort("Daftar Tagihan kosong.");
        }
    }

    private void callNewObjects() {
        items = new ArrayList<ItemCart>();
        db = new DatabaseHandler(Cart.this);
        tos = new TampilToast(Cart.this);
        adapter = new CartAdapter(Cart.this, R.layout.keranjang, items);
    }

    private void getRefRes() {
        subtotal = (TextView) findViewById(R.id.subtot);
        total = (TextView) findViewById(R.id.total);
        listItem = (ListView) findViewById(R.id.listCart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_checkout) {
            if (jumTagihan > 0) {
                Log.d("JUMLAH TAGIHAN", String.valueOf(jumTagihan));
                openDialogCheckout();
            } else {
                tos.tosShort("Daftar Tagihan kosong, silahkan memilih menu yang tersedia.");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialogCheckout() {

    }

    class CheckOutCart extends AsyncTask<String, String, String> {
        Boolean flag = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<ItemCart> haha = db.getListCartData();
            for (int i = 0; i < haha.size(); i++) {
                ItemCart record = haha.get(i);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "checkout"));
                params.add(new BasicNameValuePair("tgl", "checkout"));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent in = new Intent(Cart.this, MainActivity.class);
            tos.tosShort("Transaksi selesai.");
            startActivity(in);
        }
    }

    class LoadAllItemsCart extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Cart.this);
            pDialog.setMessage("Tunggu Sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<ItemCart> haha = new ArrayList<ItemCart>();
            haha = db.getListCartData();
            for (int i = 0; i < haha.size(); i++) {
                ItemCart data = haha.get(i);
                ItemCart ic = new ItemCart();

                ic.setNamaCart(data.getNamaCart());
                ic.setHargaCart(data.getHargaCart());
                ic.setQtyCart(data.getQtyCart());
                ic.setSubtotalCart(data.getSubtotalCart());
                Log.d("COBA1", data.getNamaCart());
                Log.d("COBA2", data.getSubtotalCart());

                items.add(ic);
                Log.d("IIIISIIII", ic.toString());
            }
//            Log.d("Semua ItemCart: ", items.toArray().toString());
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
    }

}
