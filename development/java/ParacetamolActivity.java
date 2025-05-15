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

public class ParacetamolActivity extends AppCompatActivity {

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private static final String TAG = "ParacetamolActivity";

    // Список аптек (добавлены новые из Академическое_pharmacy.csv)
    private static final List<Pharmacy> PHARMACIES = new ArrayList<Pharmacy>() {{
        add(new Pharmacy(new Point(60.009532, 30.369948), "ул. Политехническая, д. 29"));
        add(new Pharmacy(new Point(60.000238, 30.364060), "пр. Культуры, д. 10"));
        add(new Pharmacy(new Point(60.001114, 30.365358), "ул. Академика Павлова, д. 5"));
        add(new Pharmacy(new Point(60.004410, 30.389979), "ул. Торфяная дорога, д. 18"));
        add(new Pharmacy(new Point(60.002000, 30.350000), "ул. Примерная, д. 1")); // Вымышленная, замените на реальные
        add(new Pharmacy(new Point(60.005000, 30.360000), "ул. Демонстрационная, д. 2"));
        add(new Pharmacy(new Point(60.008000, 30.370000), "пр. Тестовый, д. 3"));
        // Новые аптеки из Академическое_pharmacy.csv
        add(new Pharmacy(new Point(60.0241729, 30.3948676), "Для бережливых"));
        add(new Pharmacy(new Point(60.0237548, 30.3777782), "Петербургские аптеки"));
        add(new Pharmacy(new Point(60.015593, 30.3686134), "Невис"));
        add(new Pharmacy(new Point(60.0135995, 30.3690616), "Первая помощь"));
        add(new Pharmacy(new Point(60.0140567, 30.3951296), "Озерки"));
        add(new Pharmacy(new Point(60.0137033, 30.3970698), "Озерки"));
        add(new Pharmacy(new Point(60.0120325, 30.3954914), "Столички"));
        add(new Pharmacy(new Point(60.0129162, 30.3950295), "Фиалка"));
        add(new Pharmacy(new Point(60.0132436, 30.3992721), "Озерки"));
        add(new Pharmacy(new Point(60.0171789, 30.4028269), "Аптека доктора Живило"));
        add(new Pharmacy(new Point(60.0094905, 30.3702668), "Озерки"));
        add(new Pharmacy(new Point(60.0163053, 30.3793752), "Вита Центральная"));
        add(new Pharmacy(new Point(60.0147086, 30.3927541), "Вита Экспресс"));
        add(new Pharmacy(new Point(60.012934, 30.394477), "Столички"));
        add(new Pharmacy(new Point(60.009533, 30.3699435), "Столички"));
        add(new Pharmacy(new Point(60.0150652, 30.3859949), "Столички"));
        add(new Pharmacy(new Point(60.022613, 30.3736587), "Столички"));
        add(new Pharmacy(new Point(60.0198461, 30.4034878), "Здравсити"));
        add(new Pharmacy(new Point(60.0136646, 30.3967367), "78 плюс"));
        add(new Pharmacy(new Point(60.0137355, 30.3938581), "Планета здоровья"));
        add(new Pharmacy(new Point(60.0146999, 30.3886216), "Доктор Столетов"));
        add(new Pharmacy(new Point(59.997881, 30.3708297), "Алоэ"));
        add(new Pharmacy(new Point(59.9988668, 30.3660526), "АптекаПлюс"));
        add(new Pharmacy(new Point(60.0219971, 30.3924198), "Магнит Аптека"));
        add(new Pharmacy(new Point(60.0120151, 30.4036406), "Столички"));
        add(new Pharmacy(new Point(59.9984282, 30.3661172), "Горздрав"));
        add(new Pharmacy(new Point(60.0121804, 30.4029626), "Горздрав"));
        add(new Pharmacy(new Point(60.0243509, 30.3951146), "Горздрав"));
    }};

    // Список больниц (без изменений)
    private static final List<Hospital> HOSPITALS = new ArrayList<Hospital>() {{
        add(new Hospital(new Point(60.011350, 30.386124), "Поликлиника № 112, отделение врачей общей практики", "ул. Лесная, д. 12"));
        add(new Hospital(new Point(59.997809, 30.378551), "Городская поликлиника № 112 Поликлиническое отделение № 55, травматологическое отделение", "ул. Хлопина, д. 8"));
        add(new Hospital(new Point(60.014077, 30.340455), "Санкт-Петербургская клиническая больница Российской академии наук", "пр. Тореза, д. 72"));
        add(new Hospital(new Point(60.004354, 30.337373), "Санкт-Петербургский научно-исследовательский институт фтизиопульмонологии", "ул. Политехническая, д. 32"));
        add(new Hospital(new Point(59.999117, 30.338008), "ФГБУ НМИЦ имени В.А. Алмазова", "ул. Аккуратова, д. 2"));
        add(new Hospital(new Point(59.999710, 30.374481), "Городская поликлиника № 76", "ул. Хлопина, д. 11"));
        add(new Hospital(new Point(59.997794, 30.378621), "Городская поликлиника № 112 Поликлиническое отделение № 55, травматологическое отделение", "ул. Хлопина, д. 8, к. 2"));
    }};

    // Класс для аптек
    private static class Pharmacy {
        Point point;
        String name; // Используем name вместо address для соответствия CSV

        Pharmacy(Point point, String name) {
            this.point = point;
            this.name = name;
        }
    }

    // Класс для больниц
    private static class Hospital {
        Point point;
        String name;
        String address;

        Hospital(Point point, String name, String address) {
            this.point = point;
            this.name = name;
            this.address = address;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paracetamol);

        // Инициализация UI
        TextView titleTextView = findViewById(R.id.activity_title);
        titleTextView.setText("В поисках парацетамола");

        TextView pharmacyInfoTextView = findViewById(R.id.pharmacy_info);

        //Button backButton = findViewById(R.id.back_button);
        //backButton.setOnClickListener(v -> finish());
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(ParacetamolActivity.this, HomeActivity.class);
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
// Очистка старых объектов перед добавлением новых
        mapObjects.clear();  // <-- Важно!
        // Проверка инициализации MapKit
        MapKit mapKit = MapKitFactory.getInstance();
        if (mapKit == null) {
            Log.e(TAG, "MapKit not initialized! Please ensure MapKitFactory.setApiKey is called.");
            Toast.makeText(this, "Ошибка: MapKit не инициализирован", Toast.LENGTH_LONG).show();
            return;
        }

        // Добавление маркеров
        addPharmacyMarkers();
        addHospitalMarkers();

        // Настройка камеры для охвата всех точек
        fitAllPoints();
    }

    private void addPharmacyMarkers() {
        for (Pharmacy pharmacy : PHARMACIES) {
            PlacemarkMapObject marker = mapObjects.addPlacemark(pharmacy.point);

            // Попытка использовать иконку аптеки
            try {
                ImageProvider iconProvider = ImageProvider.fromResource(this, R.drawable.ic_pharmacy);
                marker.setIcon(iconProvider);
                Log.d(TAG, "Pharmacy icon set successfully: ic_pharmacy at " + pharmacy.point);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load ic_pharmacy at " + pharmacy.point + ": " + e.getMessage());

            }

            // Установка подписи
            marker.setText("Аптека");

            // Обработчик клика
            final String name = pharmacy.name;
            final String coords = String.format("%.7f, %.7f", pharmacy.point.getLatitude(), pharmacy.point.getLongitude());
            marker.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(com.yandex.mapkit.map.MapObject mapObject, Point point) {
                    Toast.makeText(ParacetamolActivity.this, name + ": " + coords, Toast.LENGTH_SHORT).show();
                    TextView infoTextView = findViewById(R.id.pharmacy_info);
                    infoTextView.setText(name + "\nКоординаты: " + coords);
                    infoTextView.setVisibility(TextView.VISIBLE);
                    return true;
                }
            });
        }
    }

    private void addHospitalMarkers() {
        for (Hospital hospital : HOSPITALS) {
            PlacemarkMapObject marker = mapObjects.addPlacemark(hospital.point);

            // Попытка использовать иконку больницы
            try {
                ImageProvider iconProvider = ImageProvider.fromResource(this, R.drawable.ic_hospital);
                marker.setIcon(iconProvider);
                Log.d(TAG, "Hospital icon set successfully: ic_hospital at " + hospital.point);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load ic_hospital at " + hospital.point + ": " + e.getMessage());

            }

            // Установка подписи (название в скобках)
            marker.setText("(" + hospital.name + ")");

            // Обработчик клика
            final String name = hospital.name;
            final String address = hospital.address;
            marker.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(com.yandex.mapkit.map.MapObject mapObject, Point point) {
                    Toast.makeText(ParacetamolActivity.this, name + ": " + address, Toast.LENGTH_SHORT).show();
                    TextView infoTextView = findViewById(R.id.pharmacy_info);
                    infoTextView.setText(name + "\nАдрес: " + address);
                    infoTextView.setVisibility(TextView.VISIBLE);
                    return true;
                }
            });
        }
    }



    // Настройка камеры для охвата всех точек
    private void fitAllPoints() {
        List<Point> allPoints = new ArrayList<>();
        for (Pharmacy pharmacy : PHARMACIES) {
            allPoints.add(pharmacy.point);
        }
        for (Hospital hospital : HOSPITALS) {
            allPoints.add(hospital.point);
        }

        if (allPoints.isEmpty()) return;

        double minLat = allPoints.get(0).getLatitude();
        double maxLat = minLat;
        double minLon = allPoints.get(0).getLongitude();
        double maxLon = minLon;

        for (Point point : allPoints) {
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