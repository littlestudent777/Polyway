# Polyway
Проект в рамках курса "Технологии системного программирования", 6 семестр

## Состав участников

Хаяйнен Никита — руководитель проекта, дизайнер<br>
Балакшина Александра — менеджер проекта<br>
Матвеева Марина — разработчик<br>
Смаева Елизавета — ведущий разработчик<br>
Устинов Николай — разработчик, дизайнер<br>

_Все участники из группы 5030102/20101_<br>


## Цели проекта
Цель проекта - создание Андроид-приложения с возможностью построения маршрутов в районе м.Политехническая.

## Планируемые технологии разработки
IDE: android studio<br>
Язык программирования: Java<br>
Минимальная версия SDK: API 26 (Android 8.0 "Oreo")<br>
Язык конфигурации: Groovy DSL (используется файл build.gradle)<br>
БД: SQLite

## Правила ведения веток
### Основная ветка - main<br> 
Эта ветка должна содержать всегда стабильную версию приложения, готовую к развертыванию. Все изменения должны сначала проходить процесс ревью и тестирования перед тем, как попадут в основную ветку.

### Единая ветка разработки - develop <br> 
В названии остальных веток используются префиксы:<br> 
feature_ — ветки для разработки новых функций, которые должны создаваться от ветки develop, а затем сливаться обратно в develop.<br> 
fix_ — ветки для быстрого исправления ошибок, которые должны основываться на main и после исправления сливаются обратно как в main, так и в develop.<br> 

### Принцип деления веток <br> 
Одна задача - одна ветка. Большие функции делятся на мелкие задачи для удобства.  

### Стиль комментариев в коммитах <br>
"Номер задачи + краткое пояснение на английском (past simple)"<br> 
Например: git commit -m "1. Added README file"<br> 

## Правила взаимодействия с доской
Для каждой задачи обязательно указывается итерация и estimate. Estimate оценивается от 1 до 5, где 1 - простая задача, 5 - сложная задача. <br> 
На одного участника - одна задача in progress. <br> 
