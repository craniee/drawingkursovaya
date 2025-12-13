package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

/**
 * Класс-рендерер, отвечающий за отрисовку случайных геометрических фигур на холсте.
 * Реализует логику генерации, преобразование координат и отрисовку сетки.
 */
public class DrawingRenderer {

    /**
     * Отрисовывает заданное количество случайных фигур на указанном холсте.
     * Выполняет очистку фона, рисует сетку (при необходимости) и генерирует фигуры.
     *
     * @param gc контекст рисования JavaFX
     * @param width ширина холста в пикселях
     * @param height высота холста в пикселях
     * @param count общее количество генерируемых фигур
     * @param xMin минимальное значение по оси X в логических координатах
     * @param xMax максимальное значение по оси X в логических координатах
     * @param yMin минимальное значение по оси Y в логических координатах
     * @param yMax максимальное значение по оси Y в логических координатах
     * @param density степень кластеризации фигур в центре области (0.0 — равномерно, 1.0 — строго в центре)
     * @param showGrid флаг: {@code true} — отобразить координатную сетку, {@code false} — скрыть
     * @param allowedTypes список разрешённых типов фигур (не может быть пустым)
     */
    public static void drawFigures(GraphicsContext gc, double width, double height,
                                   int count, double xMin, double xMax, double yMin, double yMax,
                                   double density, boolean showGrid, List<FigureType> allowedTypes) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        Random random = new Random();

        DoubleUnaryOperator toPixelX = x -> (x - xMin) / (xMax - xMin) * width;
        DoubleUnaryOperator toPixelY = y -> (yMax - y) / (yMax - yMin) * height;

        if (showGrid) {
            drawGrid(gc, width, height, xMin, xMax, yMin, yMax, toPixelX, toPixelY);
        }

        for (int i = 0; i < count; i++) {
            drawRandomFigure(gc, width, height, xMin, xMax, yMin, yMax, density, allowedTypes, random, toPixelX, toPixelY);
        }
    }

    /**
     * Рисует координатную сетку и оси X/Y на холсте.
     * Сетка состоит из 10 линий по каждой оси, оси выделены чёрным цветом.
     *
     * @param gc контекст рисования
     * @param w ширина холста
     * @param h высота холста
     * @param xMin минимальное логическое значение X
     * @param xMax максимальное логическое значение X
     * @param yMin минимальное логическое значение Y
     * @param yMax максимальное логическое значение Y
     * @param toPixelX функция преобразования логической координаты X → пиксель
     * @param toPixelY функция преобразования логической координаты Y → пиксель
     */
    private static void drawGrid(GraphicsContext gc, double w, double h,
                                 double xMin, double xMax, double yMin, double yMax,
                                 DoubleUnaryOperator toPixelX, DoubleUnaryOperator toPixelY) {
        gc.setStroke(Color.rgb(230, 230, 230));
        double stepX = (xMax - xMin) / 10;
        for (double x = xMin; x <= xMax; x += stepX) {
            double px = toPixelX.applyAsDouble(x);
            gc.strokeLine(px, 0, px, h);
        }
        double stepY = (yMax - yMin) / 10;
        for (double y = yMin; y <= yMax; y += stepY) {
            double py = toPixelY.applyAsDouble(y);
            gc.strokeLine(0, py, w, py);
        }

        gc.setStroke(Color.BLACK);
        double ox = toPixelX.applyAsDouble(0);
        double oy = toPixelY.applyAsDouble(0);
        if (ox >= 0 && ox <= w) gc.strokeLine(ox, 0, ox, h);
        if (oy >= 0 && oy <= h) gc.strokeLine(0, oy, w, oy);
    }

    /**
     * Отрисовывает одну случайную фигуру заданного типа в указанной области.
     * Цвет и размер фигуры генерируются случайно.
     *
     * @param gc контекст рисования
     * @param w ширина холста
     * @param h высота холста
     * @param xMin минимальное логическое значение X
     * @param xMax максимальное логическое значение X
     * @param yMin минимальное логическое значение Y
     * @param yMax максимальное логическое значение Y
     * @param density степень кластеризации (0.0–1.0)
     * @param allowedTypes список разрешённых типов фигур
     * @param random экземпляр генератора случайных чисел
     * @param toPixelX преобразователь X-координат
     * @param toPixelY преобразователь Y-координат
     */
    private static void drawRandomFigure(GraphicsContext gc, double w, double h,
                                         double xMin, double xMax, double yMin, double yMax,
                                         double density, List<FigureType> allowedTypes,
                                         Random random,
                                         DoubleUnaryOperator toPixelX, DoubleUnaryOperator toPixelY) {
        gc.setStroke(Color.rgb(random.nextInt(200), random.nextInt(200), random.nextInt(200)));

        FigureType type = allowedTypes.get(random.nextInt(allowedTypes.size()));
        double centerX, centerY;

        if (density > 0) {
            double clusterX = (xMin + xMax) / 2;
            double clusterY = (yMin + yMax) / 2;
            double rangeX = (xMax - xMin) * (1 - density);
            double rangeY = (yMax - yMin) * (1 - density);
            centerX = clusterX + (random.nextDouble() - 0.5) * rangeX;
            centerY = clusterY + (random.nextDouble() - 0.5) * rangeY;
        } else {
            centerX = xMin + random.nextDouble() * (xMax - xMin);
            centerY = yMin + random.nextDouble() * (yMax - yMin);
        }

        double size = 5 + random.nextDouble() * 40;

        switch (type) {
            case LINE -> {
                double x1 = centerX - size;
                double y1 = centerY;
                double x2 = centerX + size;
                double y2 = centerY + size;
                gc.strokeLine(toPixelX.applyAsDouble(x1), toPixelY.applyAsDouble(y1),
                        toPixelX.applyAsDouble(x2), toPixelY.applyAsDouble(y2));
            }
            case CIRCLE -> {
                double px = toPixelX.applyAsDouble(centerX - size);
                double py = toPixelY.applyAsDouble(centerY + size);
                double pw = toPixelX.applyAsDouble(centerX + size) - px;
                double ph = toPixelY.applyAsDouble(centerY - size) - py;
                gc.strokeOval(px, py, pw, ph);
            }
            case RECTANGLE -> {
                double px = toPixelX.applyAsDouble(centerX - size);
                double py = toPixelY.applyAsDouble(centerY + size);
                double pw = toPixelX.applyAsDouble(centerX + size) - px;
                double ph = toPixelY.applyAsDouble(centerY - size) - py;
                gc.strokeRect(px, py, pw, ph);
            }
            case TRIANGLE -> {
                double x0 = toPixelX.applyAsDouble(centerX);
                double y0 = toPixelY.applyAsDouble(centerY - size);
                double x1 = toPixelX.applyAsDouble(centerX - size);
                double y1 = toPixelY.applyAsDouble(centerY + size);
                double x2 = toPixelX.applyAsDouble(centerX + size);
                double y2 = toPixelY.applyAsDouble(centerY + size);
                gc.strokePolygon(new double[]{x0, x1, x2}, new double[]{y0, y1, y2}, 3);
            }
            case PARABOLA -> {
                int steps = 30;
                double startX = centerX - size;
                double endX = centerX + size;

                // Первая точка параболы
                double firstX = startX;
                double firstY = centerY + (firstX - centerX) * (firstX - centerX) / size;
                double px0 = toPixelX.applyAsDouble(firstX);
                double py0 = toPixelY.applyAsDouble(firstY);

                gc.beginPath();
                gc.moveTo(px0, py0);

                for (int i = 1; i <= steps; i++) {
                    double x = startX + (endX - startX) * i / steps;
                    double y = centerY + (x - centerX) * (x - centerX) / size;
                    double px = toPixelX.applyAsDouble(x);
                    double py = toPixelY.applyAsDouble(y);
                    gc.lineTo(px, py);
                }
                gc.stroke();
            }
            case TRAPEZOID -> {
                double top = size * 0.6;
                double x1 = toPixelX.applyAsDouble(centerX - size);
                double y1 = toPixelY.applyAsDouble(centerY + size);
                double x2 = toPixelX.applyAsDouble(centerX - top);
                double y2 = toPixelY.applyAsDouble(centerY - size);
                double x3 = toPixelX.applyAsDouble(centerX + top);
                double y3 = toPixelY.applyAsDouble(centerY - size);
                double x4 = toPixelX.applyAsDouble(centerX + size);
                double y4 = toPixelY.applyAsDouble(centerY + size);
                gc.strokePolygon(new double[]{x1, x2, x3, x4}, new double[]{y1, y2, y3, y4}, 4);
            }
        }
    }
}
