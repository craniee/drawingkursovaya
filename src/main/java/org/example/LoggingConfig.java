package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Вспомогательный класс для инициализации и централизованного доступа к логгеру.
 * Обеспечивает единообразное логирование по всему приложению.
 */
public class LoggingConfig {
    private static boolean initialized = false;

    /**
     * Инициализирует систему логирования.
     * Вызывается один раз при запуске приложения.
     */
    public static void init() {
        if (initialized) {
            return;
        }

        try {
            // Сначала проверяем, что Log4j2 загружен
            System.out.println("Инициализация логирования...");

            // Получаем логгер ТОЛЬКО после проверки
            Logger logger = LogManager.getLogger(LoggingConfig.class);

            // Теперь можно безопасно использовать логгер
            logger.info("Логирование успешно инициализировано");
            logger.debug("Отладочное сообщение");

            initialized = true;

        } catch (Exception e) {
            // Если Log4j2 не работает, используем System.out
            System.err.println("⚠Log4j2 недоступен: " + e.getMessage());
            System.out.println("Используется System.out для логирования");
        }
    }

    /**
     * Возвращает общий логгер приложения.
     * @return экземпляр {@link Logger} для записи логов
     */
    public static Logger getLogger() {
        // Инициализируем, если еще не сделано
        if (!initialized) {
            init();
        }
        return LogManager.getLogger(LoggingConfig.class);
    }

    /**
     * Возвращает логгер для указанного класса.
     * @param clazz класс, для которого нужен логгер
     * @return экземпляр {@link Logger} для записи логов
     */
    public static Logger getLogger(Class<?> clazz) {
        if (!initialized) {
            init();
        }
        return LogManager.getLogger(clazz);
    }
}