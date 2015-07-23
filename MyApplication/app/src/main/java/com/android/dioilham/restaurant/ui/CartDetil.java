package com.android.dioilham.restaurant.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.dioilham.restaurant.R;
import com.android.dioilham.restaurant.helper.DatabaseHandler;
import com.android.dioilham.restaurant.helper.TampilToast;
import com.android.dioilham.restaurant.model.ItemCart;

/**
 * Created by danielnimafa on 20/06/2015.
 */
public class CartDetil extends ActionBarActivity {

    TampilToast tos;
    DatabaseHandler db;
    TextView namaMenu, hargaMenu, qtyMenu, subtotalMenu, ketMenu, kodeMenu;
    EditText eQty;
    Button bQty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keranjang_detil);
        getRefRes();
        callNewObjects();
        loadStringIntent();
        onSetJumlah();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(CartDetil.this, Keranjang.class);
        startActivity(in);
    }

    private void onSetJumlah() {
        bQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eJumlah = eQty.getText().toString();
                if (eJumlah.length() != 0) {
                    updateItemCart(view);
                    setViewValue();
                } else {
                    tos.tosShort("Silakan mengisi jumlah item");
                }
            }
        });
    }

    private void setViewValue() {
        ItemCart data = db.selectCart(kodeMenu.getText().toString());
        qtyMenu.setText(data.getQtyCart() + " item");
        subtotalMenu.setText("Rp " + data.getSubtotalCart());
    }

    private void updateItemCart(View v) {
        ItemCart data = db.selectCart(kodeMenu.getText().toString());
        String subtotal = "";
        int qty = Integer.parseInt(eQty.getText().toString());
        int harga = Integer.parseInt(data.getHargaCart());
        subtotal = String.valueOf(qty * harga);
        db.updateItemCart(kodeMenu.getText().toString(), eQty.getText().toString(), subtotal);
    }

    private void loadStringIntent() {
        Intent in = getIntent();
        kodeMenu.setText(in.getStringExtra("kode"));
        namaMenu.setText(in.getStringExtra("nama"));
        hargaMenu.setText(in.getStringExtra("harga"));
        qtyMenu.setText(in.getStringExtra("qty"));
        subtotalMenu.setText(in.getStringExtra("subtotal"));
        ketMenu.setText(in.getStringExtra("ket"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart_detil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delitem) {
            openDialogHapus();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialogHapus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CartDetil.this);
        builder.setMessage("Hapus item "+namaMenu.getText().toString()+" ini?");
        builder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        hapusItem();
                        Intent in = new Intent(CartDetil.this, Keranjang.class);
                        startActivity(in);
                        finish();
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

    private void callNewObjects() {
        tos = new TampilToast(CartDetil.this);
        db = new DatabaseHandler(CartDetil.this);
    }

    private void getRefRes() {
        eQty = (EditText) findViewById(R.id.setqty_cart_detail);
        bQty = (Button) findViewById(R.id.b_qty);
        kodeMenu = (TextView) findViewById(R.id.kode);
        namaMenu = (TextView) findViewById(R.id.nama_menu_cart_detail);
        hargaMenu = (TextView) findViewById(R.id.harga_cart_detail);
        qtyMenu = (TextView) findViewById(R.id.qty_cart_detail);
        subtotalMenu = (TextView) findViewById(R.id.subtotal_cart_detail);
        ketMenu = (TextView) findViewById(R.id.deskripsi_menu_cart_detail);
    }

    private void hapusItem() {
        db.hapusItemCart(kodeMenu.getText().toString());
    }
}
