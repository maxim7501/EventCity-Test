package com.example.eventcity;

import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView StartDate, EndDate, CountPost;
    private ListView ListEvent;
    private FrameLayout progressbar;

    StateAdapter stateAdapter;
    Calendar dateCalendar = Calendar.getInstance();
    long startMilliDate,endMilliDate;
    String dateFormate = "dd MMMM yyyy";

    ArrayList<State> ListPost = new ArrayList<State>();
    ArrayList<State> fistData = new ArrayList<State>();
    ArrayList<State> secondDate = new ArrayList<State>();

    private int countPoisk = 1000;

    //кэш
    //Список постов
    private static Cache<Integer, ArrayList<State>> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<Integer, ArrayList<State>>() {
                @Override
                public void onRemoval(RemovalNotification <Integer,ArrayList<State>> notication){
                    System.out.println("Remove : "+notication.getKey() +"->"+notication.getValue());
                }
            })
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    //Дата
    private static Cache<String, Long> cacheDate = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, Long>() {
                @Override
                public void onRemoval(RemovalNotification <String, Long> notication){
                    System.out.println("Remove : "+notication.getKey() +"->"+notication.getValue());
                }
            })
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartDate = (TextView) findViewById(R.id.start_date);
        EndDate = (TextView) findViewById(R.id.end_date);
        progressbar = (FrameLayout) findViewById(R.id.progressBar);
        ListEvent = (ListView) findViewById(R.id.list_event);
        CountPost = (TextView) findViewById(R.id.count_post);

        FirstStartDate();
        if(cache.asMap().get(1) != null) {
            System.out.println("cache: " + cache.asMap().get(1));
            secondDate = cache.asMap().get(1);
            SetListEvent();
        }

        //Загрузка данных
        if(savedInstanceState == null){
            new GetVKEvent().start();
        }

        //создаем адаптер
        stateAdapter = new StateAdapter(this, R.layout.list_item, ListPost);
        //устанавливаем адаптер
        ListEvent.setAdapter(stateAdapter);
        //слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                State selectedState = (State)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Выбранно событие " + selectedState.getName(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("Data", selectedState.getDate());
                intent.putExtra("Time", selectedState.getTime());
                intent.putExtra("Name",selectedState.getName());
                intent.putExtra("Description",selectedState.getDescription());
                intent.putExtra("Img",selectedState.getFlagResource());
                startActivity(intent);
            }
        };
        ListEvent.setOnItemClickListener(itemListener);

    }

    //Выделение данных из описания постов
    private String[] SeparatText(String text){
        String[] lineText = text.split("\n");
        String  date = "-",
                time = "-",
                name = "Данные отсутствуют!",
                desc = "Данные отсутствуют!";

        int ind1 = text.indexOf("\n");
        int ind2 = text.indexOf("____");

        if(ind1 > 0)
            name = text.substring(0,ind1);
        else
            name = text;
        if(ind2 > 0)
            desc = text.substring(ind1+2,ind2);

        for (String line : lineText){
            //System.out.println("\n"+line);
            if(line.indexOf("Дата:") > 0)
                date = line.substring(line.indexOf("Дата:")+6,line.length());
            if(line.indexOf("Время:") > 0)
                time = line.substring(line.indexOf("Время:")+7,line.length());
        }

        //System.out.println("Name: " + name.trim() + "\n" + desc.trim() + "\n" + date + "\n" + time.trim());
        String[] post = {name,desc,date,time};
        return post;

    }
    //Получеине данных с VK
    class GetVKEvent extends Thread {
        String token = "d1b487b2a1dc3a276832370e256514f1ccab355a17f7c534c93d076435eb7d7f4c1189b5c5dc2a9347179";
        String idGroup = "-87905825";
        int count = 100;
        int offset = 0;
        String v = "5.131";
        boolean offsetOne = false;

        int poisk = 0;
        public void run(){
            if(cache.asMap().get(1) == null) {
                progressbar.post(new Thread() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.VISIBLE);
                    }
                });
            }
            while (poisk < countPoisk ){

                String URLVK = "https://api.vk.com/method/wall.get?access_token="+token+"&owner_id="+idGroup+"&count="+count+"&offset="+offset+"&v="+v;
                ConnectUrl connect = new ConnectUrl(URLVK);
                connect.start();

                try {
                    connect.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(!offsetOne) {
                    offset += count + 1;
                    offsetOne = true;
                }else
                    offset += count;
                poisk += count;

            }
            //System.out.println("Count Post: " + fistData.size());

            secondDate = fistData;
            cache.put(1,fistData);  //сохранение в кэш
            SetListEvent();

        }
    }
    //Получение JSON данных
    class ConnectUrl extends Thread{
        private String urlImg;

        ConnectUrl(String urlImg){
            this.urlImg = urlImg;
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        public void run(){
            try {
                URL url = new URL(urlImg);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buf = new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null) {
                    buf.append(line).append("\n");
                }
                GetEvent(buf.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //Выделение постов из общего массива
        private void GetEvent(String s){
            try {
                JSONObject json = new JSONObject(s);
                int index = 0;
                while (index < 100){
                    try {
                        //Long ed = json.getJSONObject("response").getJSONArray("items").getJSONObject(index).
                        //        getLong("edited");
                        String text,urlImg;
                        long date;
                        date = json.getJSONObject("response").getJSONArray("items").getJSONObject(index).
                                getLong("date");
                        text = json.getJSONObject("response").getJSONArray("items").getJSONObject(index).
                                getString("text");
                        urlImg = json.getJSONObject("response").getJSONArray("items").getJSONObject(index).
                                getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").
                                getJSONArray("sizes").getJSONObject(2).getString("url");

                        fistData.add(new com.example.eventcity.State(date,SeparatText(text)[0],SeparatText(text)[1],SeparatText(text)[2],SeparatText(text)[3],urlImg));

                        /*
                        System.out.println("Date Post= " + fistData.get(fistData.size()-1).getDatePost());
                        System.out.println("Name= " + fistData.get(fistData.size()-1).getName());
                        System.out.println("Text= " + fistData.get(fistData.size()-1).getDescription());
                        System.out.println("Date= " + fistData.get(fistData.size()-1).getDate());
                        System.out.println("Time= " + fistData.get(fistData.size()-1).getTime());
                        System.out.println("UrlImg= " + fistData.get(fistData.size()-1).getFlagResource());
                         */
                    } catch (JSONException e){
                        e.printStackTrace();
                        System.out.println("Пост не подходит! ");
                    }
                    index ++;
                    //System.out.println("index= "+index);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //получение отсортированного списка постов для загрузки в listView
    public void SetListEvent(){
        ListPost.clear();
        int post=0;
        for(int i =0; i < secondDate.size(); i++) {
            if(secondDate.get(i).getDatePost()*1000 >= startMilliDate && secondDate.get(i).getDatePost()*1000 <= endMilliDate) {
                ListPost.add(new State(secondDate.get(i).getName(), secondDate.get(i).getDescription(), secondDate.get(i).getDate(), secondDate.get(i).getTime(), secondDate.get(i).getFlagResource()));
                post++;
            }
        }
        progressbar.post(new Thread(){
            @Override
            public void run() {
                progressbar.setVisibility(View.INVISIBLE);
            }
        });
        CountPost.post(new Thread(){
            @Override
            public void run() {
                CountPost.setText(String.valueOf(ListPost.size()));
            }
        });
        ListEvent.post(new Thread(){
            @Override
            public void run() {
                stateAdapter.notifyDataSetChanged();
            }
        });
        System.out.println("fistData - " + fistData.size());
        System.out.println("listPost - " + ListPost.size());
    }

    //Первичная установка начальной даты
    private void FirstStartDate(){

        if(cacheDate.asMap().get("endDate") != null)
            endMilliDate = cacheDate.asMap().get("endDate");    //загрузка из кэша
        else
            endMilliDate = dateCalendar.getTimeInMillis();

        if(cacheDate.asMap().get("startDate") != null)
            startMilliDate = cacheDate.asMap().get("startDate");    //загрузка из кэша
        else if(cacheDate.asMap().get("startDate") == null)
            startMilliDate = endMilliDate-172800000L;

        System.out.println("firstStartMilliDate: "+startMilliDate);
        Date dateStart = new Date(startMilliDate);
        Date dateEnd = new Date(endMilliDate);
        SimpleDateFormat format0 = new SimpleDateFormat(dateFormate);
        String sdateStart = format0.format(dateStart);
        String sdateEnd = format0.format(dateEnd);
        StartDate.setText(sdateStart);
        EndDate.setText(sdateEnd);

        System.out.println("first start date: "+sdateStart);
        System.out.println("first end date: "+sdateEnd);

        //сохранение в кэш
        cacheDate.put("startDate",startMilliDate);
        cacheDate.put("endDate",endMilliDate);
    }

    //установка начальной даты
    private void SetStartDate(){

        startMilliDate = dateCalendar.getTimeInMillis();
        System.out.println("startMilliDate: "+startMilliDate);

        Date date = new Date(startMilliDate);
        SimpleDateFormat format0 = new SimpleDateFormat(dateFormate);
        String sdate = format0.format(date);
        StartDate.setText(sdate);
        System.out.println("start date: "+sdate);

        cacheDate.put("startDate",startMilliDate); //сохранение в кэш

    }
    //установка конечной даты
    private void SetEndDate(){

        endMilliDate = dateCalendar.getTimeInMillis();
        System.out.println("endMilliDate: "+endMilliDate);

        Date date = new Date(endMilliDate);
        SimpleDateFormat format0 = new SimpleDateFormat(dateFormate);
        String sdate = format0.format(date);
        EndDate.setText(sdate);
        System.out.println("end date : "+sdate);

        cacheDate.put("endDate",endMilliDate);  //сохранение в кэш
    }
    // установка обработчика выбора начальной даты
    DatePickerDialog.OnDateSetListener sd = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SetStartDate();
            SetListEvent();
            stateAdapter.notifyDataSetChanged();
        }
    };
    // отображаем диалоговое окно для выбора начальной даты
    public void set_start_date(View v) {
        new DatePickerDialog(MainActivity.this, sd,
                dateCalendar.get(Calendar.YEAR),
                dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора конечной даты
    DatePickerDialog.OnDateSetListener ed = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SetEndDate();
            SetListEvent();
            stateAdapter.notifyDataSetChanged();
        }
    };
    // отображаем диалоговое окно для выбора конечной даты
    public void set_end_date(View v) {
        new DatePickerDialog(MainActivity.this, ed,
                dateCalendar.get(Calendar.YEAR),
                dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }
}
