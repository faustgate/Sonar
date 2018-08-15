package com.faustgate.sonar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

public class ActivityStart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabPay = findViewById(R.id.fabPay);

        String ticketActions[] = new String[]{"Delete"};

        AdapterPlaceList placeListAdapter = new AdapterPlaceList(getApplicationContext(), ApplicationSonar.getInstance().ticketStorage);
        ListView lv = findViewById(R.id.order_place_list);

        lv.setAdapter(placeListAdapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityStart.this);
                builder.setTitle("Please, select an action");
                builder.setCancelable(true);
                builder.setItems(ticketActions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApplicationSonar.getInstance().ticketStorage.remove(which);
                        placeListAdapter.notifyDataSetChanged();
                        if (ApplicationSonar.getInstance().ticketStorage.size() == 0)
                            fabPay.setVisibility(View.GONE);
                        else
                            fabPay.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();
                return false;
            }
        });


        if (ApplicationSonar.getInstance().ticketStorage.size() == 0)
            fabPay.setVisibility(View.GONE);
        else
            fabPay.setVisibility(View.VISIBLE);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
                startActivity(intent);
            }
        });
        fabPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resp = UZRequests.getInstance().buyTickets(ApplicationSonar.getInstance().ticketStorage);
                try {Thread.sleep(15000);} catch (InterruptedException e){}
                ApplicationSonar.getInstance().ticketStorage.clear();
                Intent intent = new Intent(ActivityStart.this, ActivityBuyTickets.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
