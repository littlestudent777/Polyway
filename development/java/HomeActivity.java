package com.example.polyway;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private TextView shortDescriptionTextView;
    private Map<PlacemarkMapObject, PostgresDataReader.Landmark> landmarkMap = new HashMap<>();
    private PlacemarkMapObject lastClickedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("4859239a-2aac-4ab3-87e1-7b815ec2869a");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_home);

        // Инициализация карты
        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(new Point(60.008716, 30.370683), 17.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        mapObjects = mapView.getMap().getMapObjects().addCollection();
        shortDescriptionTextView = findViewById(R.id.short_description_text);


        // Кнопка для перехода к построению маршрута
        Button routeButton = findViewById(R.id.route_button);
        routeButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MainActivity.class)));

        // Кнопка для перехода к просмотру карты
        Button mapOnlyButton = findViewById(R.id.map_only_button);
        mapOnlyButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MapOnlyActivity.class)));
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
}