# Изучение взаимодействия и обучения нейросетей: YOLOv4 и TensorFlow на Java в Android
## 1. Общее понимание задачи

YOLOv4 (You Only Look Once) — это алгоритм для обнаружения объектов в реальном времени, часто используемый в задачах компьютерного зрения. TensorFlow — фреймворк для машинного обучения, который поддерживает YOLOv4, но интеграция с Android (на Java) требует дополнительных шагов, так как YOLOv4 обычно работает на Python, а TensorFlow на Android чаще используется через TensorFlow Lite.

### Структура проекта
- **Активности**: — экраны приложения.
- **Пакет**: com.example.polyway — основной пакет.
- **Файлы**: AndroidManifest.xml, res/layout, Gradle-скрипты — стандартные файлы Android-проекта.

Цель: добавить функционал YOLOv4 для обработки изображений (например, обнаружение объектов на камере или в загруженных изображениях).

## 2. Шаги для интеграции YOLOv4 и TensorFlow в Android на Java

### Шаг 1: Подготовка модели YOLOv4

YOLOv4 изначально предоставляется в формате Darknet. Чтобы использовать его в Android, нужно конвертировать модель в TensorFlow Lite.

#### 1.1. Конвертация модели YOLOv4 в TensorFlow
- Загрузите веса YOLOv4 (`yolov4.weights`) и конфигурационный файл (`yolov4.cfg`).
- Используйте утилиты, такие как `darknet2tf`, для конвертации модели в формат TensorFlow (`.pb`).
- Примерный процесс:
  - Установите Darknet и TensorFlow на вашем компьютере.
  - Используйте скрипты для конвертации (например, из репозитория `hunglc007/tensorflow-yolov4-tflite` на GitHub).
  - После конвертации вы получите файл `.pb` (граф TensorFlow).

#### 1.2. Конвертация модели в TensorFlow Lite
- Android-приложения используют TensorFlow Lite, так как это облегченная версия TensorFlow для мобильных устройств.
- Используйте `tflite_convert` или Python-скрипт для конвертации `.pb` в `.tflite`:
  ```bash
  tflite_convert --graph_def_file=yolov4.pb --output_file=yolov4.tflite --input_arrays=input --output_arrays=output
  ```
Полученный файл .tflite можно будет использовать в Android.

### Шаг 2: Добавление TensorFlow Lite в ваш проект

#### 2.1. Добавьте зависимости в Gradle

Для работы с TensorFlow Lite в Android-проекте необходимо добавить соответствующие зависимости. Откройте файл build.gradle на уровне модуля app и добавьте следующие строки в секцию dependencies:

```
dependencies {
    implementation 'org.tensorflow:tensorflow-lite:2.9.0'
    implementation 'org.tensorflow:tensorflow-lite-support:0.4.0'
    implementation 'org.tensorflow:tensorflow-lite-metadata:0.4.0'
}
```
Убедитесь, что в файле build.gradle на уровне проекта настроены репозитории Maven. Проверьте или добавьте следующее в секцию allprojects:

```
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```
После добавления зависимостей синхронизируйте проект с Gradle, нажав на кнопку "Sync Project with Gradle Files" в Android Studio.

#### 2.2. Добавьте модель в проект
Чтобы использовать модель YOLOv4 в формате TensorFlow Lite, необходимо добавить файл модели в ваш проект:

Создайте папку assets, если её ещё нет, по пути app/src/main/assets/.
Поместите файл модели yolov4.tflite (полученный на предыдущем шаге) в эту папку.
Теперь модель готова к использованию в вашем приложении. На следующем шаге мы напишем код для загрузки и работы с этой моделью.

### 3. Изучение взаимодействия и обучения
#### 3.1. Загрузка модели
В одной из активностей (например, MainActivity) загрузите модель TensorFlow Lite:
```java
import org.tensorflow.lite.Interpreter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("yolov4.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
```
#### 3.2. Предобработка изображения
YOLOv4 ожидает входное изображение размером 416x416 или 608x608. Нужно изменить размер изображения и нормализовать его (привести значения пикселей к диапазону [0, 1]):
```java
import android.graphics.Bitmap;

public float[][][][] preprocessImage(Bitmap bitmap) {
    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 416, 416, true);
    float[][][][] input = new float[1][416][416][3];
    for (int x = 0; x < 416; x++) {
        for (int y = 0; y < 416; y++) {
            int pixel = resizedBitmap.getPixel(x, y);
            input[0][x][y][0] = ((pixel >> 16) & 0xff) / 255.0f; // R
            input[0][x][y][1] = ((pixel >> 8) & 0xff) / 255.0f;  // G
            input[0][x][y][2] = (pixel & 0xff) / 255.0f;         // B
        }
    }
    return input;
}
```
#### 3.3. Запуск модели
Выполните инференс (предсказание) с помощью модели:
```java
float[][] output = new float[1][25200][85];
tflite.run(preprocessImage(bitmap), output);
```
#### 3.4. Постобработка результатов
YOLOv4 возвращает сырые данные, которые нужно обработать:
1. Примените Non-Max Suppression (NMS), чтобы убрать дублирующие боксы.
2. Извлеките координаты, уверенность и классы объектов. Для упрощения можно использовать библиотеку tensorflow-lite-support, которая предоставляет утилиты для постобработки.
