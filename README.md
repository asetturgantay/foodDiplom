# Проект Доставка

Это проект мобильного приложения для доставки, написанный на языке программирования Kotlin и использующий Firebase для хранения данных и аутентификации.

## Функциональные возможности

- Регистрация и аутентификация пользователей с использованием Firebase Authentication
- Добавление, редактирование и удаление товаров
- Просмотр списка товаров
- Добавление товаров в корзину
- Оформление заказа
- Отслеживание статуса заказа

## Технологии

- [Kotlin](https://kotlinlang.org/)
- [Firebase Authentication](https://firebase.google.com/products/auth)
- [Firebase Firestore](https://firebase.google.com/products/firestore)
- [Firebase Realtime Database](https://firebase.google.com/products/realtime-database)
- [Firebase Cloud Functions](https://firebase.google.com/products/functions)
- [Firebase Storage](https://firebase.google.com/products/storage)

## Установка

### Требования

- Android Studio 4.0 или выше
- Установленный JDK 8 или выше
- Аккаунт Firebase

### Шаги установки

1. Клонируйте репозиторий:
    ```bash
    git clone https://github.com/your-username/delivery-app.git
    ```
2. Откройте проект в Android Studio.
3. Настройте Firebase проект:
   - Создайте проект в [Firebase Console](https://console.firebase.google.com/).
   - Добавьте ваше приложение в проект Firebase.
   - Скачайте файл `google-services.json` и поместите его в директорию `app/` вашего Android проекта.
4. Синхронизируйте проект с Gradle.

## Использование

После установки и настройки вы можете запустить приложение на эмуляторе или реальном устройстве через Android Studio.

## Структура проекта

- `app/` - директория с исходным кодом приложения
- `app/src/main/java/` - исходный код на Kotlin
- `app/src/main/res/` - ресурсы приложения (макеты, строки, иконки и т.д.)

## Вклад в проект

Если вы хотите внести вклад в проект, пожалуйста, следуйте этим шагам:

1. Форкните репозиторий.
2. Создайте ветку для ваших изменений:
    ```bash
    git checkout -b feature-name
    ```
3. Внесите изменения и закоммитьте их:
    ```bash
    git commit -m 'Добавление новой функциональности'
    ```
4. Запушьте изменения в вашу ветку:
    ```bash
    git push origin feature-name
    ```
5. Создайте Pull Request.

## Лицензия

Этот проект лицензирован под лицензией MIT. Подробнее см. файл [LICENSE](LICENSE).

## Контакты

Если у вас есть вопросы или предложения, пожалуйста, свяжитесь с нами по адресу asetturgantay@gmail.com.
