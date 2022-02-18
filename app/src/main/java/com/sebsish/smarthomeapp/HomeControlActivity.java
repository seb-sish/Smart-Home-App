package com.sebsish.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class HomeControlActivity extends AppCompatActivity {

    final int REQUEST_CODE_PERMISSION_ON_SCAN = 1;
    final int REQUEST_CODE_PERMISSION_ON_CONNECT = 2;

    private ListView wifiStatesList;
    private Element[] nets;
    private WifiManager wifiManager;
    private RelativeLayout layout;
    private WifiBroadcastReceiver wifiReceiver;
    private String SSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_control);
        this.layout = this.findViewById(R.id.layout);
        wifiStatesList = findViewById(R.id.WiFiStates);

        wifiStatesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                TextView tvSSID = itemClicked.findViewById(R.id.tvSSID);
                SSID = tvSSID.getText().toString();
                check_connect_permissions(SSID);
            }
        });
        check_scan_permissions();
    }

    public void connect(View view) {
    }

    public void scan() {
        this.wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!this.wifiManager.isWifiEnabled()){
            this.wifiManager.setWifiEnabled(true);
        }

        this.wifiReceiver = new WifiBroadcastReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        this.wifiManager.startScan();
        Toast.makeText(this, "Сканирование...", Toast.LENGTH_SHORT).show();
    }

    public void check_scan_permissions() {
        int permissionStatus = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        Log.d("permissions", String.valueOf(permissionStatus));
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            scan();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION_ON_SCAN);
        }
    }

    public void build_password_input_alert(String SSID) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.wifi_password_input_alert, null);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);

        mDialogBuilder.setCancelable(false).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.Q)
                            public void onClick(DialogInterface dialog, int id) {
                                checkWiFiState(SSID, userInput.getText().toString());
                            }
                        }).setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel(); }});

        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void check_connect_permissions(String SSID) {
        int permissionStatus = checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
        Log.d("permissions", String.valueOf(permissionStatus));
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            build_password_input_alert(SSID);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},
                    REQUEST_CODE_PERMISSION_ON_SCAN);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_ON_SCAN:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scan();
                } else {
                }
                return;
            case REQUEST_CODE_PERMISSION_ON_CONNECT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check_connect_permissions(SSID);
                } else {
                }
                return;
        }
    }

    public void wifi_list_append(List<ScanResult> wifiList) {
        Log.d("WI-FI", wifiList.toString());

        nets = new Element[wifiList.size()];

        for (int i = 0; i < wifiList.size(); i++) {
            String item = wifiList.get(i).toString();
            String[] vector_item = item.split(",");
            String item_essid = vector_item[0];
            String item_capabilities = vector_item[2];
            String item_level = vector_item[3];
            String ssid = item_essid.split(": ")[1];
            String security = item_capabilities.split(": ")[1].split("\\]\\[")[0].substring(1);
            String level = item_level.split(": ")[1];
            nets[i] = new Element(ssid, security, level);
        }

        AdapterElements adapterElements = new AdapterElements(this);
        ListView netList = findViewById(R.id.WiFiStates);
        netList.setAdapter(adapterElements);
    }

    class AdapterElements extends ArrayAdapter<Object> {
        Activity context;

        public AdapterElements(Activity context) {
            super(context, R.layout.wifi_states, nets);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.wifi_states, null);

            TextView tvSsid = item.findViewById(R.id.tvSSID);
            tvSsid.setText(nets[position].getTitle());

            TextView tvSecurity = item.findViewById(R.id.tvSecurity);
            tvSecurity.setText(nets[position].getSecurity());

            TextView tvLevel = item.findViewById(R.id.tvLevel);
            String level = nets[position].getLevel();

            try {
                int i = Integer.parseInt(level);
                if (i > -50) {
                    tvLevel.setText("Высокий");
                } else if (i <= -50 && i > -80) {
                    tvLevel.setText("Средний");
                } else if (i <= -80) {
                    tvLevel.setText("Низкий");
                }
            } catch (NumberFormatException e) {
                Log.d("TAG", "Неверный формат строки");
            }
            return item;
        }
    }

    class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Wi-Fi", "onReceive()");
            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (ok) {
                Toast.makeText(context, "Сканирование завершено!", Toast.LENGTH_SHORT).show();
                Log.d("Wi-Fi", "Scan OK");

                List<ScanResult> list = wifiManager.getScanResults();
                wifi_list_append(list);
            } else {
                Toast.makeText(context, "При сканировании произошла ошибка!", Toast.LENGTH_SHORT).show();
                Log.d("Wi-Fi", "Scan not OK");
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void checkWiFiState(String SSID, String password) {
        if(!this.wifiManager.isWifiEnabled()){
            this.wifiManager.setWifiEnabled(true);
        }
        if (ConnectToNetworkWPA(SSID, password)) {
            Toast.makeText(this, "Подключение", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean ConnectToNetworkWPA(String networkSSID, String password) {
//        try {
            final NetworkSpecifier specifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsid(networkSSID)
                            .setWpa2Passphrase(password).build();

            final NetworkRequest request = new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .setNetworkSpecifier(specifier).build();

            final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback()
            {

                @Override
                public void onAvailable(Network network) {
                    Toast.makeText(getApplicationContext(), "Подключение...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLost(Network network) {
                    Toast.makeText(getApplicationContext(), "Отключение от сети...", Toast.LENGTH_SHORT).show();
                }
            };
            connectivityManager.requestNetwork(request, networkCallback);









//            WifiConfiguration wifiConfig = new WifiConfiguration();
//            conf.SSID = "\"" + networkSSID + "\"";
//            conf.preSharedKey =password;
//
//            wifiConfig.status = WifiConfiguration.Status.ENABLED;
//            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//
//            wifiConfig.SSID = String.format("\"%s\"", networkSSID);
//            wifiConfig.BSSID = "3a:2b:78:04:eb:ef";
//            wifiConfig.preSharedKey = String.format("\"%s\"", password);
//
//            WifiManager connectManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            int netId = connectManager.addNetwork(wifiConfig);

//            connectManager.disconnect();
//            Log.d("connecting", wifiConfig.SSID + " " + wifiConfig.preSharedKey);
//            connectManager.enableNetwork(netId, true);
//            connectManager.reconnect();
//
//            List<WifiConfiguration> list = connectManager.getConfiguredNetworks();
//            for( WifiConfiguration i : list ) {
//                Log.d("WifiConfiguration", i.SSID);
//                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
//                    connectManager.disconnect();
//                    connectManager.enableNetwork(i.networkId, true);
//                    connectManager.reconnect();
//                    Log.d("re connecting", i.SSID + " " + wifiConfig.preSharedKey);
//                    break;
//                }
//            }
            //WiFi Connection success, return true
            return true;
//        } catch (Exception ex) {
//            System.out.println(Arrays.toString(ex.getStackTrace()));
//            return false;
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.reg_to_login_2, R.anim.reg_to_login_1);
    }
    public void Exit (View view) {
        finish();
        overridePendingTransition(R.anim.reg_to_login_2, R.anim.reg_to_login_1);
    }
    @Override
    protected void onStop()  {
        this.unregisterReceiver(this.wifiReceiver);
        super.onStop();
    }
//    @Override
//    protected void onPause() {
//        unregisterReceiver(this.wifiReceiver);
//        super.onPause();
//    }
//    @Override
//    protected void onResume() {
//        registerReceiver(this.wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        super.onResume();
//    }
}
