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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoulTourActivity extends AppCompatActivity {
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private static final String TAG = "SoulTourActivity";

    // Список достопримечательностей из Академическое_attraction.csv
    private static final List<Attraction> ATTRACTIONS = new ArrayList<Attraction>() {{
        add(new Attraction(new Point(60.004555, 30.375246), "Парк Политехнического университета", ""));
        add(new Attraction(new Point(60.005740, 30.374240), "Гидробашня СПбПУ", ""));
        add(new Attraction(new Point(60.006952, 30.370214), "Космонавт (памятник)", ""));
        add(new Attraction(new Point(60.006607, 30.372474), "Мемориал Погибшим Политехникам", ""));
        add(new Attraction(new Point(60.005147, 30.378299), "Музей Политеха", ""));
        add(new Attraction(new Point(60.006909, 30.381488), "Пётр и Феврония Муромские (памятник)", ""));
        add(new Attraction(new Point(60.008590, 30.371841), "Храм Покрова Пресвятой Богородицы", ""));
        add(new Attraction(new Point(60.008867, 30.372460), "Фонтан Мы", ""));
        add(new Attraction(new Point(60.008894, 30.372340), "Студент-политехник (скульптура)", ""));
        add(new Attraction(new Point(60.007252, 30.372663), "Белый зал", ""));
        add(new Attraction(new Point(60.011172, 30.372659), "Памятник Чапаеву", ""));
        add(new Attraction(new Point(60.006840, 30.368804), "Физико-технический институт им. А. Ф. Иоффе", ""));
        add(new Attraction(new Point(60.018415, 30.370837), "Сад Бенуа", ""));
        add(new Attraction(new Point(60.017700, 30.369618), "Дача Бенуа", ""));
        add(new Attraction(new Point(60.020942, 30.370568), "Институт робототехники и технической кибернетики", ""));
        add(new Attraction(new Point(60.002738, 30.374368), "Алфёровский университет", ""));
    }};

    // Список маршрута из attractions_route_data.csv с приоритетом описаний
    private static final Map<String, Attraction> ROUTE_ATTRACTIONS = new HashMap<String, Attraction>() {{
        put("Метро Политехническая", new Attraction(new Point(60.008936, 30.370899), "Метро Политехническая", ""));
        put("Фонтан Мы", new Attraction(new Point(60.008867, 30.372460), "Фонтан Мы", ""));
        put("Студент-политехник (скульптура)", new Attraction(new Point(60.008894, 30.372340), "Студент-политехник (скульптура)", ""));
        put("Храм Покрова Пресвятой Богородицы", new Attraction(new Point(60.008590, 30.371841), "Храм Покрова Пресвятой Богородицы", ""));
        put("Пётр и Феврония Муромские (памятник)", new Attraction(new Point(60.006909, 30.381488), "Пётр и Феврония Муромские (памятник)", ""));
        put("Музей Политеха", new Attraction(new Point(60.005147, 30.378299), "Музей Политеха", "Научно-технический музей, располагающий вековыми материалами о важнейших событиях в жизни Политеха, от периода его закладки до современных достижений."));
        put("Гидробашня СПбПУ", new Attraction(new Point(60.005740, 30.374240), "Гидробашня СПбПУ", ""));
        put("Мемориал Погибшим Политехникам", new Attraction(new Point(60.006607, 30.372474), "Мемориал Погибшим Политехникам", ""));
        put("Космонавт (памятник)", new Attraction(new Point(60.006952, 30.370214), "Космонавт (памятник)", ""));
        put("Физико-технический институт им. А. Ф. Иоффе", new Attraction(new Point(60.006840, 30.368804), "Физико-технический институт им. А. Ф. Иоффе", "Один из ведущих научных центров России, где проводятся исследования в области фундаментальной и прикладной физики."));
    }};

    // Класс для достопримечательностей
    private static class Attraction {
        Point point;
        String name;
        String description;

        Attraction(Point point, String name, String description) {
            this.point = point;
            this.name = name;
            this.description = description;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soul_tour);

        // Инициализация UI
        TextView titleTextView = findViewById(R.id.activity_title);
        titleTextView.setText("Экскурсия для души");

        TextView attractionInfoTextView = findViewById(R.id.attraction_info);


        //Button backButton = findViewById(R.id.back_button);
        //backButton.setOnClickListener(v -> finish());
// Обработчик для кнопки "Назад"
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(SoulTourActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
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

        // Добавление маркеров для всех достопримечательностей
        addAttractionMarkers();

        // Центрирование карты на маршруте
        fitRoutePoints();
    }

    private void addAttractionMarkers() {
        // Объединяем все достопримечательности с приоритетом описаний из ROUTE_ATTRACTIONS

        Map<Point, Attraction> uniqueAttractions = new HashMap<>();
        for (Attraction attr : ATTRACTIONS) {
            uniqueAttractions.put(attr.point, attr);
        }
        for (Attraction attr : ROUTE_ATTRACTIONS.values()) {
            uniqueAttractions.put(attr.point, attr); // Перезаписывает, если точка совпадает, с описанием из маршрута
        }

        for (Attraction attraction : uniqueAttractions.values()) {
            PlacemarkMapObject marker = mapObjects.addPlacemark(attraction.point);

            // Попытка использовать иконку достопримечательности
            try {
                ImageProvider iconProvider = ImageProvider.fromResource(this, R.drawable.ic_attraction);
                marker.setIcon(iconProvider);
                Log.d(TAG, "Attraction icon set successfully: ic_attraction at " + attraction.point);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load ic_attraction at " + attraction.point + ": " + e.getMessage());

            }

            // Установка подписи (название)
            marker.setText(attraction.name);

            // Обработчик клика
            final String name = attraction.name;
            final String description = attraction.description.isEmpty() ? "Описание отсутствует" : attraction.description;
            marker.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(com.yandex.mapkit.map.MapObject mapObject, Point point) {
                    Toast.makeText(SoulTourActivity.this, name, Toast.LENGTH_SHORT).show();
                    TextView infoTextView = findViewById(R.id.attraction_info);
                    infoTextView.setText(name + "\n" + description);
                    infoTextView.setVisibility(TextView.VISIBLE);
                    return true;
                }
            });
        }
    }



    // Центрирование карты на маршруте
    private void fitRoutePoints() {
        List<Point> routePoints = new ArrayList<>(ROUTE_ATTRACTIONS.values().stream().map(a -> a.point).toList());

        if (routePoints.isEmpty()) return;

        double minLat = routePoints.get(0).getLatitude();
        double maxLat = minLat;
        double minLon = routePoints.get(0).getLongitude();
        double maxLon = minLon;

        for (Point point : routePoints) {
            minLat = Math.min(minLat, point.getLatitude());
            maxLat = Math.max(maxLat, point.getLatitude());
            minLon = Math.min(minLon, point.getLongitude());
            maxLon = Math.max(maxLon, point.getLongitude());
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