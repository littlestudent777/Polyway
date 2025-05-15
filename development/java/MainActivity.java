package com.example.polyway;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.*;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private Point pointA = null;
    private Point pointB = null;
    private PolylineMapObject line;
    private TextView distanceText;
    private Button clearButton;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;

    // Новые элементы UI
    private EditText pointALatEditText;
    private EditText pointALonEditText;
    private EditText pointBLatEditText;
    private EditText pointBLonEditText;
    private Button addPointAButton;
    private Button addPointBButton;
    private Button backButton; // Добавляем кнопку "Назад"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        setupMap();
        initRouter();
    }

    private void initUI() {
        // Инициализация существующих элементов
        distanceText = findViewById(R.id.distance_text);
        clearButton = findViewById(R.id.clear_button);

        // Инициализация новых элементов
        pointALatEditText = findViewById(R.id.point_a_lat);
        pointALonEditText = findViewById(R.id.point_a_lon);
        pointBLatEditText = findViewById(R.id.point_b_lat);
        pointBLonEditText = findViewById(R.id.point_b_lon);
        addPointAButton = findViewById(R.id.add_point_a_button);
        addPointBButton = findViewById(R.id.add_point_b_button);
        backButton = findViewById(R.id.back_button); // Инициализация кнопки "Назад"

        // Обработчики для кнопок
        addPointAButton.setOnClickListener(v -> addPointAFromInput());
        addPointBButton.setOnClickListener(v -> addPointBFromInput());
        clearButton.setOnClickListener(v -> clearAll());

        // Обработчик для кнопки "Назад"
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Завершаем текущую активность
        });

        clearButton.setBackgroundColor(Color.RED);
        clearButton.setTextColor(Color.WHITE);
    }

    private void setupMap() {
        mapView = findViewById(R.id.mapview);
        // Устанавливаем начальную позицию камеры на Санкт-Петербург
        mapView.getMap().move(
                new CameraPosition(new Point(60.008716,  30.370683), 17.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        mapObjects = mapView.getMap().getMapObjects().addCollection();

        mapView.getMap().addInputListener(new InputListener() {
            @Override
            public void onMapTap(Map map, Point point) {
                handleMapTap(point);
            }

            @Override
            public void onMapLongTap(Map map, Point point) {
            }
        });
    }

    private void initRouter() {
        MapKit mapKit = MapKitFactory.getInstance();
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
    }

    private void handleMapTap(Point point) {
        // Проверяем, что точка находится в пределах Санкт-Петербурга
        double lat = point.getLatitude();
        double lon = point.getLongitude();
        if (lat < 59.7 || lat > 60.1 || lon < 29.5 || lon > 31.0) {
            System.out.println("Точка находится за пределами Санкт-Петербурга");
            return;
        }

        // Если точка A ещё не выбрана, добавляем её
        if (pointA == null) {
            pointA = point;
            addMarker(pointA, "Точка A", Color.BLUE);
            // Заполняем поля ввода координатами точки A
            pointALatEditText.setText(String.valueOf(pointA.getLatitude()));
            pointALonEditText.setText(String.valueOf(pointA.getLongitude()));
        }
        // Если точка A уже выбрана, но точка B ещё нет, добавляем точку B
        else if (pointB == null) {
            pointB = point;
            addMarker(pointB, "Точка B", Color.GREEN);
            // Заполняем поля ввода координатами точки B
            pointBLatEditText.setText(String.valueOf(pointB.getLatitude()));
            pointBLonEditText.setText(String.valueOf(pointB.getLongitude()));
            buildRoute();
            clearButton.setEnabled(true);
        }
    }

    // Метод для добавления точки A из полей ввода
    private void addPointAFromInput() {
        try {
            double lat = Double.parseDouble(pointALatEditText.getText().toString());
            double lon = Double.parseDouble(pointALonEditText.getText().toString());

            // Проверяем, что точка находится в пределах Санкт-Петербурга
            if (lat < 59.7 || lat > 60.1 || lon < 29.5 || lon > 31.0) {
                System.out.println("Точка A находится за пределами Санкт-Петербурга");
                return;
            }

            // Если точка A уже была выбрана, удаляем старый маркер
            if (pointA != null) {
                mapObjects.clear(); // Очищаем старые маркеры
                if (pointB != null) {
                    addMarker(pointB, "Точка B", Color.GREEN); // Восстанавливаем маркер точки B
                }
            }

            pointA = new Point(lat, lon);
            addMarker(pointA, "Точка A", Color.BLUE);

            // Если точка B уже выбрана, строим маршрут
            if (pointB != null) {
                buildRoute();
                clearButton.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода координат для точки A: " + e.getMessage());
        }
    }

    // Метод для добавления точки B из полей ввода
    private void addPointBFromInput() {
        try {
            double lat = Double.parseDouble(pointBLatEditText.getText().toString());
            double lon = Double.parseDouble(pointBLonEditText.getText().toString());

            // Проверяем, что точка находится в пределах Санкт-Петербурга
            if (lat < 59.7 || lat > 60.1 || lon < 29.5 || lon > 31.0) {
                System.out.println("Точка B находится за пределами Санкт-Петербурга");
                return;
            }

            // Если точка B уже была выбрана, удаляем старый маркер
            if (pointB != null) {
                mapObjects.clear(); // Очищаем старые маркеры
                if (pointA != null) {
                    addMarker(pointA, "Точка A", Color.BLUE); // Восстанавливаем маркер точки A
                }
            }

            pointB = new Point(lat, lon);
            addMarker(pointB, "Точка B", Color.GREEN);

            // Если точка A уже выбрана, строим маршрут
            if (pointA != null) {
                buildRoute();
                clearButton.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода координат для точки B: " + e.getMessage());
        }
    }

    private void addMarker(Point point, String text, int color) {
        PlacemarkMapObject marker = mapObjects.addPlacemark(point);
        marker.setIcon(ImageProvider.fromResource(this, R.drawable.ic_map_marker));
        marker.setText(text);
    }

    private void buildRoute() {
        if (pointA == null || pointB == null) {
            System.out.println("Cannot build route: pointA or pointB is null");
            return;
        }

        // Создаём точки маршрута
        List<RequestPoint> points = new ArrayList<>();
        points.add(new RequestPoint(pointA, RequestPointType.WAYPOINT, null));
        points.add(new RequestPoint(pointB, RequestPointType.WAYPOINT, null));

        // Настраиваем параметры маршрута
        DrivingOptions options = new DrivingOptions();
        options.setRoutesCount(1); // Запрашиваем один маршрут

        // Настраиваем VehicleOptions (минимальные настройки)
        VehicleOptions vehicleOptions = new VehicleOptions();

        // Запрашиваем маршрут
        drivingSession = drivingRouter.requestRoutes(points, options, vehicleOptions, this);
    }

    @Override
    public void onDrivingRoutes(List<DrivingRoute> routes) {
        if (routes.isEmpty()) {
            System.out.println("No routes found");
            return;
        }

        // Берём первый маршрут
        DrivingRoute route = routes.get(0);
        Polyline polyline = route.getGeometry();

        // Отображаем маршрут на карте
        if (line != null) {
            mapObjects.remove(line);
        }
        line = mapObjects.addPolyline(polyline);
        line.setStrokeColor(Color.RED);
        line.setStrokeWidth(5f);

        // Показываем расстояние
        double distance = route.getMetadata().getWeight().getDistance().getValue();
        distanceText.setText(String.format(Locale.getDefault(), "Расстояние: %.2f м", distance));
        distanceText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDrivingRoutesError(Error error) {
        System.err.println("Error building route: " + error.toString());
    }

    private void clearAll() {
        // Удаляем маршрут, если он существует
        if (line != null) {
            mapObjects.remove(line);
            line = null;
        }

        // Очищаем все маркеры на карте
        mapObjects.clear();

        // Сбрасываем точки A и B
        pointA = null;
        pointB = null;

        // Очищаем поля ввода
        pointALatEditText.setText("");
        pointALonEditText.setText("");
        pointBLatEditText.setText("");
        pointBLonEditText.setText("");

        // Скрываем текст с расстоянием
        distanceText.setVisibility(View.GONE);

        // Отключаем кнопку "Очистить", пока не будет построен новый маршрут
        clearButton.setEnabled(false);

        // Показываем уведомление пользователю
        Toast.makeText(this, "Маршрут сброшен. Выберите новые точки A и B.", Toast.LENGTH_SHORT).show();
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