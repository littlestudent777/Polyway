package com.example.polyway;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

public class MapOnlyActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_only);

        // Инициализация карты
        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(new Point(60.008716, 30.370683), 17.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        // Обработчики для новых кнопок
        findViewById(R.id.run_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, RunActivity.class);
         //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);

        });

        findViewById(R.id.watch_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, WatchActivity.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);

        });


        findViewById(R.id.soul_tour_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, SoulTourActivity.class);
           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);

        });

        findViewById(R.id.food_tour_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, FoodTourActivity.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);

        });

        findViewById(R.id.paracetamol_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, ParacetamolActivity.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);

        });

        findViewById(R.id.home_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, HomeSearchActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);

        });

        findViewById(R.id.ring_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, RingActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);
        });
        findViewById(R.id.fact_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, FactActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Очищает стек активностей
            startActivity(intent);

        });

        // Обработчик для кнопки "Назад"
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(MapOnlyActivity.this, HomeActivity.class);
            startActivity(intent);

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if (mapView != null) {
            mapView.onStop();
            MapKitFactory.getInstance().onStop();
            mapView = null;
        }
        super.onDestroy();
    }
}