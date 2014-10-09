package my.portablehotspot.app;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import java.lang.reflect.Method;


public class MainActivity extends Activity implements OnClickListener {

    private Button mStart, mCancel;
    private final String TAG = "HotSpot";
    private EditText mSSID, mPassword;
    private String mUserInput, mSecurityPref;
    private RadioGroup mRadioGrp;
    private RadioButton mRadioBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mStart = (Button) findViewById(R.id.start);
        mCancel = (Button) findViewById(R.id.cancel);
        mSSID = (EditText) findViewById(R.id.ssid);
        mPassword = (EditText) findViewById(R.id.password);
        mRadioGrp = (RadioGroup) findViewById(R.id.rdgrp);
        mStart.setOnClickListener(this);
        mCancel.setOnClickListener(this);

    }


    public void onClick(View v) {

        if (v == mStart) {
            mRadioBtn = (RadioButton) findViewById(mRadioGrp.getCheckedRadioButtonId());
            mSecurityPref = mRadioBtn.getText().toString();
            initConnection(this);

        } else if (v == mCancel) {
            mSSID.setText("");
            mPassword.setText("");

        }

    }


    private void initConnection(final Context context) {

        WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }

        WifiConfiguration mWifiConfig = new WifiConfiguration();
        mWifiConfig.SSID = mSSID.getText().toString();
        mWifiConfig.preSharedKey = mPassword.getText().toString();
        mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        mWifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        mWifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        if (mSecurityPref.equals("Open")) {
            mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            Toast.makeText(this, "Your Preferred Security is : " + mSecurityPref,
                    Toast.LENGTH_LONG).show();
        } else if (mSecurityPref.equals("Secured")) {
            mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            Toast.makeText(this, "Your Preferred Security is : " + mSecurityPref,
                    Toast.LENGTH_LONG).show();

        }

        try {
            /* Here we set Wi-Fi hotspot with the configuration defined above */

            Method setAPEnable = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apStatus = (Boolean) setAPEnable.invoke(mWifiManager, mWifiConfig, true);
 /* Check if the Wi-Fi hotspot was successfully enabled */
            Method isApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled");
            while (!(Boolean) isApEnabled.invoke(mWifiManager)) {
            }
            ;
            Method getWifiApStateMethod = mWifiManager.getClass().getMethod("getWifiApState");
            int apState = (Integer) getWifiApStateMethod.invoke(mWifiManager);
            Method getWifiApConfigurationMethod = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            mWifiConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(mWifiManager);

        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
