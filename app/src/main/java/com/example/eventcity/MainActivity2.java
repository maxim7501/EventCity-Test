package com.example.eventcity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


public class MainActivity2 extends AppCompatActivity {

    TextView Name, Date, Time, Description;
    ImageView Img;
    private String NameEvent, DescriptionEvent,DateEvent,TimeEvent;
    private String ImgEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Name = (TextView)findViewById(R.id.active_name);
        Date = (TextView)findViewById(R.id.active_date);
        Time = (TextView)findViewById(R.id.active_time);
        Description = (TextView)findViewById(R.id.active_description);
        Img = (ImageView) findViewById(R.id.active_img);

        Intent intent = getIntent();
        NameEvent = intent.getStringExtra("Name");
        DescriptionEvent = intent.getStringExtra("Description");
        DateEvent = intent.getStringExtra("Data");
        TimeEvent = intent.getStringExtra("Time");
        ImgEvent = intent.getStringExtra("Img");

        //ImgEvent = getIntent().getExtras().getInt("Img");
        System.out.println("img =" +ImgEvent);

        SetTextEvent();

    }

    private void SetTextEvent(){
        Name.setText(NameEvent.toString());
        Date.setText(DateEvent.toString());
        Time.setText(TimeEvent.toString());
        Description.setText(DescriptionEvent.toString());

        Picasso.with(this)
                .load(ImgEvent.toString())
                .into(Img);
    }
}

