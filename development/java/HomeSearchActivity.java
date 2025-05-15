package com.example.polyway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

public class HomeSearchActivity extends AppCompatActivity {
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private static final String TAG = "HomeSearchActivity";

    // Список станций метро из Академическое_metro.csv
    private static final List<MetroStation> METRO_STATIONS = new ArrayList<MetroStation>() {{
        add(new MetroStation(new Point(60.008936, 30.370899), "Политехническая"));
        add(new MetroStation(new Point(60.012750, 30.395935), "Академическая"));
        add(new MetroStation(new Point(59.999584, 30.366533), "Площадь Мужества"));
    }};

    // Класс для станций метро
    private static class MetroStation {
        Point point;
        String name;

        MetroStation(Point point, String name) {
            this.point = point;
            this.name = name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        // Инициализация UI
        TextView titleTextView = findViewById(R.id.activity_title);
        titleTextView.setText("В поисках дома");

        TextView metroInfoTextView = findViewById(R.id.metro_info);

      //  Button backButton = findViewById(R.id.back_button);
      //  backButton.setOnClickListener(v -> finish());
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(HomeSearchActivity.this, MapOnlyActivity.class);
            startActivity(intent);
            finish();
        });
        // Инициализация карты

        mapView = findViewById(R.id.mapview);
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        mapObjects.clear();  // <-- Важно!
        // Проверка инициализации MapKit
        MapKit mapKit = MapKitFactory.getInstance();
        if (mapKit == null) {
            Log.e(TAG, "MapKit not initialized! Please ensure MapKitFactory.setApiKey is called.");
            Toast.makeText(this, "Ошибка: MapKit не инициализирован", Toast.LENGTH_LONG).show();
            return;
        }

        // Добавление маркеров для станций метро
        addMetroMarkers();

        // Центрирование карты на всех станциях
        fitMetroPoints();
    }

    private void addMetroMarkers() {
        for (MetroStation station : METRO_STATIONS) {
            PlacemarkMapObject marker = mapObjects.addPlacemark(station.point);

            // Попытка использовать иконку метро
            try {
                ImageProvider iconProvider = ImageProvider.fromResource(this, R.drawable.ic_metro);
                marker.setIcon(iconProvider);
                Log.d(TAG, "Metro icon set successfully: ic_metro at " + station.point);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load ic_metro at " + station.point + ": " + e.getMessage());
            }

            // Установка подписи (название станции)
            marker.setText(station.name);

            // Обработчик клика
            final String name = station.name;
            marker.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(com.yandex.mapkit.map.MapObject mapObject, Point point) {
                    Toast.makeText(HomeSearchActivity.this, name, Toast.LENGTH_SHORT).show();
                    TextView infoTextView = findViewById(R.id.metro_info);
                    infoTextView.setText(name);
                    infoTextView.setVisibility(TextView.VISIBLE);
                    return true;
                }
            });

        }
    }



    // Центрирование карты на всех станциях
    private void fitMetroPoints() {
        if (METRO_STATIONS.isEmpty()) return;

        double minLat = METRO_STATIONS.get(0).point.getLatitude();
        double maxLat = minLat;
        double minLon = METRO_STATIONS.get(0).point.getLongitude();
        double maxLon = minLon;

        for (MetroStation station : METRO_STATIONS) {
            minLat = Math.min(minLat, station.point.getLatitude());
            maxLat = Math.max(maxLat, station.point.getLatitude());
            minLon = Math.min(minLon, station.point.getLongitude());
            maxLon = Math.max(maxLon, station.point.getLongitude());
        }

        Point center = new Point((minLat + maxLat) / 2, (minLon + maxLon) / 2);
        BoundingBox boundingBox = new BoundingBox(new Point(minLat, minLon), new Point(maxLat, maxLon));
        CameraPosition cameraPosition = mapView.getMap().cameraPosition(boundingBox);
        mapView.getMap().move(
                new CameraPosition(center, cameraPosition.getZoom() - 0.5f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
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
            // 1. Остановка MapView
            mapView.onStop();
            // 2. Очистка объектов карты
            if (mapObjects != null) {
                mapObjects.clear();
            }
            // 3. Освобождение ресурсов
            mapView = null;
        }
        // 4. Вызов super в конце
        super.onDestroy();
    }
}