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

public class FoodTourActivity extends AppCompatActivity {
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private static final String TAG = "FoodTourActivity";

    private static final List<FoodPlace> FOOD_PLACES = new ArrayList<FoodPlace>() {{
        add(new FoodPlace(new Point(60.0071999, 30.3771420), "Столовая", "restaurant", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0098220, 30.3586212), "Арт-клуб Куклы", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0017934, 30.3648972), "Столовая", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0030780, 30.3601419), "New York & Tbilisi", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0057555, 30.3588510), "Лимон", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0011106, 30.3653366), "Subway", "fast_food", "проспект Тореза"));
        add(new FoodPlace(new Point(60.0103457, 30.3685194), "Водопад", "restaurant", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0161062, 30.3684847), "Хванчкара", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0085364, 30.3606611), "Пекарня-кондитерская", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0109234, 30.3866434), "Китайская кухня", "cafe", "Гжатская улица"));
        add(new FoodPlace(new Point(60.0078340, 30.3685180), "Coffee Like", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0096348, 30.3693585), "Пироговый дворик", "cafe", "Политехническая улица"));
        add(new FoodPlace(new Point(60.0012212, 30.3653800), "Север-Метрополь", "cafe", "проспект Тореза"));
        add(new FoodPlace(new Point(60.0083544, 30.3700742), "Теремок", "fast_food", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0090360, 30.3698180), "One Price Coffee", "fast_food", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0121535, 30.3788844), "Шаверлэнд", "fast_food", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0160839, 30.3806468), "Цех85", "cafe", "проспект Науки"));
        add(new FoodPlace(new Point(60.0175991, 30.3668699), "Суворов", "cafe", "Тихорецкий проспект"));
        add(new FoodPlace(new Point(60.0127460, 30.3792950), "Кебаб", "fast_food", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0127273, 30.3793377), "Кебаб", "fast_food", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0015556, 30.3669137), "Кебаб", "fast_food", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0097979, 30.3698660), "Цех85", "cafe", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0079012, 30.3604093), "Южная ночь", "restaurant", "улица Курчатова"));
        add(new FoodPlace(new Point(60.0080168, 30.3591420), "Столовая №1", "fast_food", "Адрес не указан"));
        add(new FoodPlace(new Point(60.0091572, 30.3771633), "Peperoni", "restaurant", "Адрес не указан"));
        add(new FoodPlace(new Point(60.005603937500005, 30.378248462500004), "Погребок", "cafe", "Политехническая улица"));
        add(new FoodPlace(new Point(60.0161657, 30.38014286), "Rostic`s", "fast_food", "проспект Науки"));
        add(new FoodPlace(new Point(60.00665278333333, 30.354958266666667), "Шаверма", "fast_food", "Адрес не указан"));
    }};

    // Класс для заведений
    private static class FoodPlace {
        Point point;
        String name;
        String type;
        String address;

        FoodPlace(Point point, String name, String type, String address) {
            this.point = point;
            this.name = name;
            this.type = type;
            this.address = address;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_tour);

        // Инициализация UI
        TextView titleTextView = findViewById(R.id.activity_title);
        titleTextView.setText("Обжор-тур");

        TextView foodInfoTextView = findViewById(R.id.food_info);

       // Button backButton = findViewById(R.id.back_button);
       // backButton.setOnClickListener(v -> finish());
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(FoodTourActivity.this, HomeActivity.class);
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

        // Добавление маркеров для заведений
        addFoodMarkers();

        // Центрирование карты на всех точках
        fitFoodPoints();
    }

    private void addFoodMarkers() {
        for (FoodPlace place : FOOD_PLACES) {
            PlacemarkMapObject marker = mapObjects.addPlacemark(place.point);

            // Установка иконки в зависимости от типа заведения
            try {
                int iconResource;
                switch (place.type) {
                    case "restaurant":
                        iconResource = R.drawable.ic_restaurant;
                        break;
                    case "cafe":
                        iconResource = R.drawable.ic_cafe;
                        break;
                    case "fast_food":
                        iconResource = R.drawable.ic_fast_food;
                        break;
                    case "unknown":
                    default:
                        iconResource = R.drawable.ic_unknown;
                        break;
                }
                ImageProvider iconProvider = ImageProvider.fromResource(this, iconResource);
                marker.setIcon(iconProvider);
                Log.d(TAG, "Icon set successfully: " + place.type + " at " + place.point);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load icon for " + place.type + " at " + place.point + ": " + e.getMessage());


            }

            // Установка подписи (название заведения)
            marker.setText(place.name);

            // Обработчик клика
            final String name = place.name;
            marker.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(com.yandex.mapkit.map.MapObject mapObject, Point point) {
                    Toast.makeText(FoodTourActivity.this, name, Toast.LENGTH_SHORT).show();
                    TextView infoTextView = findViewById(R.id.food_info);
                    //  infoTextView.setText(name + "\nАдрес: " + address);
                    infoTextView.setVisibility(TextView.VISIBLE);
                    return true;
                }
            });
        }
    }



    // Центрирование карты на всех точках
    private void fitFoodPoints() {
        if (FOOD_PLACES.isEmpty()) return;

        double minLat = FOOD_PLACES.get(0).point.getLatitude();
        double maxLat = minLat;
        double minLon = FOOD_PLACES.get(0).point.getLongitude();
        double maxLon = minLon;

        for (FoodPlace place : FOOD_PLACES) {
            minLat = Math.min(minLat, place.point.getLatitude());
            maxLat = Math.max(maxLat, place.point.getLatitude());
            minLon = Math.min(minLon, place.point.getLongitude());
            maxLon = Math.max(maxLon, place.point.getLongitude());
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



