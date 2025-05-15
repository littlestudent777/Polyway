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

public class RingActivity extends AppCompatActivity {
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private static final String TAG = "RingActivity";

    // Список корпусов с координатами и описаниями
    private static final List<Building> BUILDINGS = new ArrayList<Building>() {{
        add(new Building(new Point(59.994442, 30.358909), "Корпус ИПМЭиТ", "50 учебный корпус (потому что на Новороссийской 50 находится). Довольно большой учебный корпус, в котором есть классный коворкинг и конференц-зал."));
        add(new Building(new Point(60.000161, 30.367594), "6 корпус", "Сейчас там иногда проходят пары для лингвистов, но по большей части это административный корпус, где сидит дирекция ГИ."));
        add(new Building(new Point(60.000585, 30.366435), "9 корпус", "Раньше это был корпус ИКНТ, но сейчас он полностью закреплен за ГИ, ничем не примечателен, бывшее старое общежитие."));
        add(new Building(new Point(59.999700, 30.374603), "5-ый корпус", "— В нем расположена Высшая школа Биотехнических систем и технологий;\n— В этом же здании расположена 76 поликлиника;\n— Единственный корпус в Политехе, где можно встретить необычных животных: мышей, крыс и кроликов."));
        add(new Building(new Point(59.995075, 30.353687), "48-й корпус", "На Новороссийской 48 находится:\n— В нем расположена Высшая школа Биотехнологий и Пищевых производств;\n— Есть лабораторные для готовки;\n— На одной из лабораторных ребята готовят настойку))"));
        add(new Building(new Point(60.009252, 30.378203), "11 корпус", "Это корпус на Обручевых 1, в котором учатся студенты ИКНК, а также располагается военная кафедра. В корпусе есть два новых коворкинга, которые появились год назад, но нет столовой. Есть автоматы с едой."));
        add(new Building(new Point(60.007841, 30.390005), "ИМОП", "Он состоит из нескольких зданий: учебные корпуса №15 и №16, которые соединяются стеклянным переходом, комплекс общежитий для иностранных студентов и другие объекты социальной инфраструктуры. Кстати, история обучения иностранных студентов в Политехническом университете началась в далеком 1965 году с открытия подготовительного факультета для иностранных студентов."));
    }};

    // Класс для корпусов
    private static class Building {
        Point point;
        String name;
        String description;

        Building(Point point, String name, String description) {
            this.point = point;
            this.name = name;
            this.description = description;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        // Инициализация UI
        TextView titleTextView = findViewById(R.id.activity_title);
        titleTextView.setText("Внешнее кольцо");

        TextView buildingInfoTextView = findViewById(R.id.building_info);

       // Button backButton = findViewById(R.id.back_button);
       // backButton.setOnClickListener(v -> finish());
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(RingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
        // Инициализация карты
        // Инициализация карты
        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(new Point(60.008716, 30.370683), 17.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        mapObjects.clear();  // <-- Важно!
        // Проверка инициализации MapKit
        MapKit mapKit = MapKitFactory.getInstance();
        if (mapKit == null) {
            Log.e(TAG, "MapKit not initialized! Please ensure MapKitFactory.setApiKey is called.");
            Toast.makeText(this, "Ошибка: MapKit не инициализирован", Toast.LENGTH_LONG).show();
            return;
        }

        // Добавление маркеров для корпусов
        addBuildingMarkers();

        // Центрирование карты на всех корпусах
        fitBuildingPoints();
    }

    private void addBuildingMarkers() {
        for (Building building : BUILDINGS) {
            PlacemarkMapObject marker = mapObjects.addPlacemark(building.point);

            // Попытка использовать иконку корпуса
            try {
                ImageProvider iconProvider = ImageProvider.fromResource(this, R.drawable.ic_building);
                marker.setIcon(iconProvider);
                Log.d(TAG, "Building icon set successfully: ic_building at " + building.point);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load ic_building at " + building.point + ": " + e.getMessage());

            }

            // Установка подписи (название корпуса)
            marker.setText(building.name);

            // Обработчик клика
            final String name = building.name;
            final String description = building.description;
            marker.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(com.yandex.mapkit.map.MapObject mapObject, Point point) {
                    Toast.makeText(RingActivity.this, name, Toast.LENGTH_SHORT).show();
                    TextView infoTextView = findViewById(R.id.building_info);
                    infoTextView.setText(name + "\n" + description);
                    infoTextView.setVisibility(TextView.VISIBLE);
                    return true;
                }
            });
        }
    }


    // Центрирование карты на всех корпусах
    private void fitBuildingPoints() {
        if (BUILDINGS.isEmpty()) return;

        double minLat = BUILDINGS.get(0).point.getLatitude();
        double maxLat = minLat;
        double minLon = BUILDINGS.get(0).point.getLongitude();
        double maxLon = minLon;

        for (Building building : BUILDINGS) {
            minLat = Math.min(minLat, building.point.getLatitude());
            maxLat = Math.max(maxLat, building.point.getLatitude());
            minLon = Math.min(minLon, building.point.getLongitude());
            maxLon = Math.max(maxLon, building.point.getLongitude());
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