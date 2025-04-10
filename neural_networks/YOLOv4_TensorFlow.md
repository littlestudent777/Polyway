# Изучение взаимодействия и обучения нейросетей: YOLOv4 и TensorFlow на Java в Android
## 1. Общее понимание задачи

YOLOv4 (You Only Look Once) — это алгоритм для обнаружения объектов в реальном времени, часто используемый в задачах компьютерного зрения. TensorFlow — фреймворк для машинного обучения, который поддерживает YOLOv4, но интеграция с Android (на Java) требует дополнительных шагов, так как YOLOv4 обычно работает на Python, а TensorFlow на Android чаще используется через TensorFlow Lite.

### Структура проекта
- **Активности**: — экраны приложения.
- **Пакет**: com.example.polyway — основной пакет.
- Файлы: AndroidManifest.xml, res/layout, Gradle-скрипты — стандартные файлы Android-проекта.

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
