package com.manuelpeinado.refreshactionitem.demo;

import java.util.Random;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.demo.R;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;
import com.manuelpeinado.refreshactionitem.RefreshActionItem.RefreshActionListener;

public class StyledActivity extends SherlockListActivity implements RefreshActionListener {
    private RefreshActionItem mSaveButton;
    private Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styled);
    }

    private void saveData() {
        mSaveButton.setDisplayMode(RefreshActionItem.MODE_DETERMINATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; ++i) {
                    try {
                        Thread.sleep(20);
                        mSaveButton.setProgress(i);
                    } catch (InterruptedException e) {
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSaveButton.setDisplayMode(RefreshActionItem.MODE_BUTTON);
                        Toast.makeText(getApplicationContext(), "Your data has been saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }).start();
    }

    private String[] generateRandomItemList() {
        String[] result = new String[100];
        for (int i = 0; i < result.length; ++i) {
            result[i] = Integer.toString(r.nextInt(1000));
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.save, menu);
        MenuItem item = menu.findItem(R.id.save_button);
        mSaveButton = (RefreshActionItem) item.getActionView();
        mSaveButton.setMenuItem(item);
        mSaveButton.setMax(100);
        mSaveButton.setRefreshActionListener(this);
        String[] items = generateRandomItemList();
        setListAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item, android.R.id.text1, items));
        return true;
    }

    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        saveData();
    }

    public void setDoughnutStyle(View view) {
        mSaveButton.setDeterminateIndicatorStyle(RefreshActionItem.STYLE_DOUGHNUT);
    }

    public void setPieStyle(View view) {
        mSaveButton.setDeterminateIndicatorStyle(RefreshActionItem.STYLE_PIE);
    }
}