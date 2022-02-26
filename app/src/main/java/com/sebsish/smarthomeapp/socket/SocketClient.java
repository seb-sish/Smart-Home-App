package com.sebsish.smarthomeapp.socket;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.sebsish.smarthomeapp.HomeControlActivity;
import com.sebsish.smarthomeapp.databinding.ActivityHomeControlBinding;

import java.io.*;
import java.net.Socket;

public class SocketClient extends Thread {

    public String LOG_TAG = "SocketClient";
    public String host = "192.168.4.1";
    public int port = 45328;

    public Activity activity;
    public Socket clientSocket; //сокет для общения

    public BufferedReader inStream; // поток чтения из сокета
    public BufferedWriter outStream; // поток записи в сокет

    public SocketClient(Activity activity) {
        this.activity = activity; }
    public SocketClient(Activity activity, final String host) {
        this.activity = activity;
        this.host = host; }
    public SocketClient(Activity activity, final String host, final int port) {
        this.host = host;
        this.port = port;
        this.activity = activity;}

    public void connect() throws Exception {
        // Если сокет уже открыт, то он закрывается
        close();
        try {
            // Создание сокета
            this.clientSocket = new Socket(this.host, this.port);
            // читать соообщения с сервера
            this.inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // писать туда же
            this.outStream = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            ToastException("Успешное подключение!");
            Log.d(LOG_TAG, "Подключение к сокету");
            enableAllTexts();

        } catch (IOException e) {
            ToastException("Ошибка при подключении! Проверьте соединение.");
            throw new Exception("Невозможно создать сокет: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        if (this.clientSocket != null && !this.clientSocket.isClosed()) {
            try {
                this.clientSocket.close();
                this.inStream.close();
                this.outStream.close();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Ошибка при закрытии сокета :" + e.getMessage()); }
        }
        this.clientSocket = null;
        this.inStream = null;
        this.outStream = null;
    }

    public void send(String data) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                // Проверка открытия сокета
                if (clientSocket == null || clientSocket.isClosed()) {
                    Log.w(LOG_TAG, "Ошибка отправки данных. Сокет не создан или закрыт");
                }
                // Отправка данных
                try {
                    outStream.write(data);
                    outStream.flush();
                } catch (IOException e) {
                    Log.w(LOG_TAG, "Ошибка отправки данных : " + e.getMessage()); }
                    } };
        thread.start();
    }

    public void listenData() throws Exception {
        String serverWord = null; // ждём, что скажет сервер
        try {
            serverWord = inStream.readLine();
            Log.i(LOG_TAG, serverWord);

        } catch (IOException e) {
            Log.w(LOG_TAG, e);
        }
    }
    public void enableAllTexts() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((HomeControlActivity) activity).enableAll();
            }
        });
    }

    public void ToastException(String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void run () {
        try {
            connect();
            send("Mobile app connection");

        while (this.clientSocket != null) {
            listenData();
            }
        }
        catch (Exception e) {
                Log.w(LOG_TAG, e);
        }
    }

}