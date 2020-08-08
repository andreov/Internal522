package com.example.internai_521;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText mTextLog;
    private EditText mTextPass;
    private Button mBtnLog;
    private Button mBtnReg;
    private CheckBox mCheckExt;
    private CompoundButton.OnCheckedChangeListener checkedChangeLilstn;
    private String log;
    private  String pass;
    private boolean checkStatus;
    private SharedPreferences myCheckSave;
    private static String CHECK_ON = "Check_on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBtnLog();
        initBtnReg();
        initCheckBox();
        readCheckExt();
    }
    private void initViews(){
       mTextLog= findViewById(R.id.textLog);
       mTextPass= findViewById(R.id.textPassword);
       mBtnLog= findViewById(R.id.buttonOK);
       mBtnReg= findViewById(R.id.buttonReg);
       mCheckExt= findViewById(R.id.checkBox);
       myCheckSave = getSharedPreferences("MyCheck", MODE_PRIVATE);
       mCheckExt.setOnCheckedChangeListener(checkedChangeLilstn);
    }

    private void saveCheckExt(){
        if(mCheckExt.isChecked()) {
            checkStatus = true;
        }else {
            checkStatus = false;
        }
        SharedPreferences.Editor myEditor = myCheckSave.edit();
        myEditor.putBoolean(CHECK_ON, checkStatus);
        myEditor.apply();
    }

    private void readCheckExt(){
        boolean check = myCheckSave.getBoolean(CHECK_ON, checkStatus);
        mCheckExt.setChecked(check);
    }

    private void writeExt(String fileName,EditText logPass){
        if (isExternalStorageWritable()) {
            //Получаем ссылку на файл
            File textSample = new File(getApplicationContext().getExternalFilesDir(null),fileName);
            //Загружаем text
            FileWriter sampleWriter=null;
            String item;
            // считываем все элементы из адаптера и сохраняем их в файл
            try {
                item = logPass.getText().toString();
                sampleWriter= new FileWriter(textSample,false);
                sampleWriter.append(item);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sampleWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readExt(String fileName){
        StringBuilder text = null;
        if(isExternalStorageReadable()) {
            File textSample = new File(getApplicationContext().getExternalFilesDir(null), fileName);
            if (!textSample.exists()) return null;
            //Загружаем text
            FileReader sampleRead = null;
            text = new StringBuilder();
            BufferedReader br;
            String line;
            try {
                sampleRead = new FileReader(textSample);
                br = new BufferedReader(sampleRead);
                while ((line = br.readLine()) != null) {
                    text.append(line);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                sampleRead.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return text.toString();
    }

    private void initBtnLog(){
        mBtnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCheckExt.isChecked()){
                    log=readExt("log.txt");
                    pass=readExt("pass.txt");
                    //Toast.makeText(MainActivity.this, "Логин и пароль чтеник из внешний файл", Toast.LENGTH_LONG).show();
                }else {
                    log=readLog("log.txt");
                    pass=readLog("pass.txt");
                }
                if(log==null||pass==null){
                    Toast.makeText(MainActivity.this, "Файл не существует", Toast.LENGTH_LONG).show();
                }else {
                    if(mTextLog.getText().toString().equals(log) && mTextPass.getText().toString().equals(pass)){
                        Toast.makeText(MainActivity.this, "Логин и пароль совпадают", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Вы ввели неправильный логин или пароль", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    private void initCheckBox(){
        mCheckExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCheckExt();
            }
        });
    }
    private void initBtnReg(){
        mBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTextLog.getText().length()==0||mTextPass.getText().length()==0){
                    Toast.makeText(MainActivity.this, "Введите логин и пароль", Toast.LENGTH_LONG).show();
                }else {
                    if(mCheckExt.isChecked()){
                        writeExt("log.txt",mTextLog);
                        writeExt("pass.txt",mTextPass);
                        Toast.makeText(MainActivity.this, "Логин и пароль зарегистрированы во внешний файл", Toast.LENGTH_LONG).show();
                    }else {
                        writeReg("log.txt",mTextLog);
                        writeReg("pass.txt",mTextPass);
                        Toast.makeText(MainActivity.this, "Логин и пароль зарегистрированы во внутренний файл", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void writeReg(String fileName,EditText logPass){
        // Создадим файл и откроем поток для записи данных
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Обеспечим переход символьных потока данных к байтовым потокам.
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        // Запишем текст в поток вывода данных, буферизуя символы так, чтобы обеспечить эффективную запись отдельных символов.
        BufferedWriter bw = new BufferedWriter(outputStreamWriter);
        // Осуществим запись данных
        try {
            bw.write(logPass.getText().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // закроем поток
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String readLog(String fileName){
        // Получим входные байты из файла которых нужно прочесть.
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        // Декодируем байты в символы
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        // Читаем данные из потока ввода, буферизуя символы так, чтобы обеспечить эффективную запись отдельных символов.
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder output = new StringBuilder();
        //String line;
        try {
            output=output.append(reader.readLine());
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}