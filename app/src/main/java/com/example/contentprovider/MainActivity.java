package com.example.contentprovider;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_SMS = 100;
    ArrayList<String> smsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra quyền
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
        } else {
            // Nếu đã cấp quyền, gọi hàm đọc tin nhắn
            readSmsMessages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readSmsMessages();
            } else {
                // Người dùng từ chối cấp quyền
                Toast.makeText(this, "Quyền đọc SMS đã bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Đọc tin nhắn SMS từ inbox và hiển thị ra ListView
    private void readSmsMessages() {
        Uri smsUri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(smsUri, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Lấy cột "address" và "body"
                int addressIndex = cursor.getColumnIndex("address");
                int bodyIndex = cursor.getColumnIndex("body");

                if (addressIndex != -1 && bodyIndex != -1) {
                    String address = cursor.getString(addressIndex);
                    String body = cursor.getString(bodyIndex);

                    smsList.add("From: " + address + "\nMessage: " + body);
                }
            }
            cursor.close();
        }

        // Hiển thị danh sách tin nhắn trong ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
        ListView listView = findViewById(R.id.smsListView);
        listView.setAdapter(adapter);
    }
}
