package com.android.dioilham.restaurant.ui;

/**
 * Created by Danielnimafa on 27/06/2015.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dioilham.restaurant.R;
import com.android.dioilham.restaurant.adapter.CartAdapter;
import com.android.dioilham.restaurant.helper.DatabaseHandler;
import com.android.dioilham.restaurant.helper.Koneksi;
import com.android.dioilham.restaurant.helper.TampilToast;
import com.android.dioilham.restaurant.model.ItemCart;
import com.android.dioilham.restaurant.parser.JSONParser;
import com.android.dioilham.restaurant.ui.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by danielnimafa on 19/06/2015.
 */
public class Keranjang extends ActionBarActivity {

    TextView subtotal, total;
    ListView listItem;
    CartAdapter adapter;
    ArrayList<ItemCart> items; // untuk menampung items cart
    DatabaseHandler db;
    ProgressDialog pDialog;
    TampilToast tos;
    Koneksi conn;
    Boolean flag, flag1, flag2;
    private static final String TAG_SUKSES = "success";
    private static int grandTotal = 0;
    JSONParser jParser = new JSONParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        getRefRes(); // get resource ID
        callNewObjects();
        int jum = db.countCart();
        if (jum > 0) {
            adapter = new CartAdapter(Keranjang.this, R.layout.row_shopcart, items);
            new LoadAllItemsCart().execute();
            listItem.setAdapter(adapter);
            hitungTotal();
            onItemKlik();
        } else {
            tos.tosShort("Tidak ada Tagihan");
            total.setText("Rp. 0");
        }
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        items.clear();
        Log.d("ITEMS_AWAL", String.valueOf(items.size()));
        LoadAllItemsCart la = new LoadAllItemsCart();
        la.execute();
        adapter.notifyDataSetChanged();
        *//*if (la.getStatus() == AsyncTask.Status.FINISHED) {
        }*//*
        Log.d("ITEMS_Akhir", String.valueOf(items.size()));
    }*/

    private void onItemKlik() {
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kodeItem = ((TextView) view.findViewById(R.id.kode)).getText().toString();
                String namaItem = ((TextView) view.findViewById(R.id.nama)).getText().toString();
                String qtyItem = ((TextView) view.findViewById(R.id.qty)).getText().toString();
                String subtotalItem = ((TextView) view.findViewById(R.id.subtotal)).getText().toString();
                String hargaItem = ((TextView) view.findViewById(R.id.harga)).getText().toString();
                String ketItem = ((TextView) view.findViewById(R.id.ket)).getText().toString();

                Intent in = new Intent(Keranjang.this, CartDetil.class);
                in.putExtra("kode", kodeItem);
                in.putExtra("nama", namaItem);
                in.putExtra("qty", qtyItem);
                in.putExtra("subtotal", subtotalItem);
                in.putExtra("harga", hargaItem);
                in.putExtra("ket", ketItem);
                startActivity(in);
                finish();
            }
        });
    }

    private void hitungTotal() {
        int totalCart = 0;
        db = new DatabaseHandler(Keranjang.this);
        ArrayList<String> subtotal = db.getSubtotalCart();
        for (int i = 0; i < subtotal.size(); i++) {
            int subtots = Integer.parseInt(subtotal.get(i));
            totalCart += subtots;
        }
        grandTotal = totalCart;
        total.setText("Rp. " + String.valueOf(totalCart));
    }

    private void callNewObjects() {
        db = new DatabaseHandler(Keranjang.this);
        tos = new TampilToast(Keranjang.this);
        items = new ArrayList<ItemCart>();
        conn = new Koneksi(Keranjang.this);
    }

    class LoadAllItemsCart extends AsyncTask<String, String, String> {

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Keranjang.this);
            pDialog.setMessage("Sedang Memuat...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }*/

        protected String doInBackground(String... args) {
            List<ItemCart> haha = new ArrayList<ItemCart>();
            haha = db.getListCartData();
            for (int i = 0; i < haha.size(); i++) {
                ItemCart data = haha.get(i);
                ItemCart ic = new ItemCart();

                ic.setKodeCart(data.getKodeCart());
                ic.setNamaCart(data.getNamaCart());
                ic.setHargaCart(data.getHargaCart());
                ic.setQtyCart(data.getQtyCart());
                ic.setSubtotalCart(data.getSubtotalCart());
                ic.setKetCart(data.getKetCart());

                items.add(ic);
            }
            return null;
        }

        /*protected void onPostExecute(String file_url) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.dismiss();
                }
            }, 500);
        }*/
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
            /*cek itemCart*/
            if (items.size() != 0) {
                checkout();
            } else {
                tos.tosShort("Tidak ada tagihan.");
            }
            /*Intent in = new Intent(Keranjang.this, MainActivity.class);
            startActivity(in);*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkout() {
        final Dialog dialog = new Dialog(Keranjang.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkout_form);
        dialog.setTitle("Form Checkout");

        final EditText enama = (EditText) dialog.findViewById(R.id.enama);
        EditText etgl = (EditText) dialog.findViewById(R.id.etgl);
        Button cekot = (Button) dialog.findViewById(R.id.btcekot);
        Button batal = (Button) dialog.findViewById(R.id.btbatal);

        final String tanggal = getTanggal();
        etgl.setEnabled(false);
        etgl.setText(tanggal);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        cekot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enama.length() != 0) {
                    new CheckOutItems().execute(enama.getText().toString(), tanggal);
                    dialog.dismiss();
                } else {
                    tos.tosShort("Kolom nama harap diisi.");
                }
            }
        });

        dialog.show();
    }

    private String getTanggal() {
        String hasil = "";
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        hasil = df.format(c.getTime());
        return hasil;
    }

    class CheckOutItems extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Keranjang.this);
            pDialog.setMessage("Silakan Tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String nama = strings[0];
            String tgl = strings[1];
            String grTotal = String.valueOf(grandTotal);
            String url = conn.urlItem() + "getitem.php";
            List<ItemCart> ic = db.getListCartData();
            int qtyTotal = 0;
            // sum qty total
            for (int i = 0; i < ic.size(); i++) {
                ItemCart itemCart = ic.get(i);
                qtyTotal += Integer.parseInt(itemCart.getQtyCart());
            }
            //insert into transaksi
            List<NameValuePair> paramData = new ArrayList<NameValuePair>();
            paramData.add(new BasicNameValuePair("tag", "checkout"));
            paramData.add(new BasicNameValuePair("tgl", tgl));
            paramData.add(new BasicNameValuePair("nama", nama));
            paramData.add(new BasicNameValuePair("qty_total", String.valueOf(qtyTotal)));
            paramData.add(new BasicNameValuePair("grandtotal", grTotal));
            JSONObject jsonObject = jParser.makeHttpRequest(url, "POST", paramData);
            try {
                int success = jsonObject.getInt(TAG_SUKSES);
                if (success == 1) {
                    flag = true;
                    String nota = jsonObject.getString("data");
                    // insert into transaksi detil
                    for (int i = 0; i < ic.size(); i++) {
                        paramData = new ArrayList<NameValuePair>();
                        ItemCart itemCart = ic.get(i);
                        paramData.add(new BasicNameValuePair("tag", "ins"));
                        paramData.add(new BasicNameValuePair("nota", nota));
                        paramData.add(new BasicNameValuePair("idItem", itemCart.getKodeCart()));
                        paramData.add(new BasicNameValuePair("qty", itemCart.getQtyCart()));
                        jsonObject = jParser.makeHttpRequest(url, "POST", paramData);
                    }
                } else {
                    flag = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (flag == true) {
                tos.tosShort("Transaksi tersimpan.");
                db.resetCart();
                Intent in = new Intent(Keranjang.this, MainActivity.class);
                startActivity(in);
                finish();
            } else {
                tos.tosShort("Transaksi gagal.");
            }
        }
    }
}
