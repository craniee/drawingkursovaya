package org.example;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Главный класс приложения — точка входа в JavaFX-приложение.
 * Обеспечивает ввод параметров генерации через GUI и открытие окна отрисовки.
 */
public class Main extends Application {

    /**
     * Точка входа в JavaFX-приложение. Вызывается системой при запуске.
     * Инициализирует главное окно с формой ввода параметров.
     *
     * @param primaryStage основное окно приложения (создаётся платформой)
     */
    @Override
    public void start(Stage primaryStage) {
        LoggingConfig.init(); //Инициализация логгера

        primaryStage.setTitle("Параметры генерации");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(15));

        // Поля ввода
        TextField countField = new TextField("10");
        TextField xMinField = new TextField("-100");
        TextField xMaxField = new TextField("100");
        TextField yMinField = new TextField("-100");
        TextField yMaxField = new TextField("100");
        TextField densityField = new TextField("0.3");

        CheckBox gridCheckbox = new CheckBox("Показать координатную сетку");
        gridCheckbox.setSelected(true);

        // Чекбоксы для типов фигур
        Map<FigureType, CheckBox> figureCheckBoxes = new HashMap<>();
        for (FigureType type : FigureType.values()) {
            CheckBox cb = new CheckBox(type.getLabel());
            cb.setSelected(true);
            figureCheckBoxes.put(type, cb);
        }

        // Расположение
        int row = 0;
        grid.add(new Label("Количество фигур:"), 0, row);
        grid.add(countField, 1, row++);
        grid.add(new Label("X min:"), 0, row);
        grid.add(xMinField, 1, row++);
        grid.add(new Label("X max:"), 0, row);
        grid.add(xMaxField, 1, row++);
        grid.add(new Label("Y min:"), 0, row);
        grid.add(yMinField, 1, row++);
        grid.add(new Label("Y max:"), 0, row);
        grid.add(yMaxField, 1, row++);
        grid.add(new Label("Кучность (0.0–1.0):"), 0, row);
        grid.add(densityField, 1, row++);

        grid.add(new Label("Типы фигур:"), 0, row++);
        FlowPane typesBox = new FlowPane(10, 10);
        typesBox.getChildren().addAll(figureCheckBoxes.values());
        typesBox.setPrefWrapLength(350);
        grid.add(typesBox, 0, row, 2, 1);
        row++;

        grid.add(gridCheckbox, 0, row, 2, 1);
        row++;

        Button generateBtn = new Button("Сгенерировать");
        generateBtn.setOnAction(e -> {
            try {
                int count = Integer.parseInt(countField.getText().trim());
                double xMin = Double.parseDouble(xMinField.getText().trim());
                double xMax = Double.parseDouble(xMaxField.getText().trim());
                double yMin = Double.parseDouble(yMinField.getText().trim());
                double yMax = Double.parseDouble(yMaxField.getText().trim());
                double density = Double.parseDouble(densityField.getText().trim());

                if (density < 0 || density > 1) {
                    showAlert("Кучность должна быть от 0.0 до 1.0");
                    return;
                }

                List<FigureType> selected = figureCheckBoxes.entrySet().stream()
                        .filter(entry -> entry.getValue().isSelected())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                if (selected.isEmpty()) {
                    showAlert("Выберите хотя бы один тип фигуры!");
                    return;
                }

                boolean showGrid = gridCheckbox.isSelected();

                openDrawingWindow(count, xMin, xMax, yMin, yMax, density, showGrid, selected);
                primaryStage.close();

            } catch (NumberFormatException ex) {
                showAlert("Проверьте формат чисел!");
            }
        });

        grid.add(generateBtn, 0, row, 2, 1);

        Scene scene = new Scene(grid, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Отображает модальное предупреждающее диалоговое окно с заданным сообщением.
     * Блокирует дальнейшие действия до закрытия окна.
     *
     * @param msg текст сообщения для пользователя
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Открывает новое окно для отрисовки случайного рисунка.
     * Рисует фигуры на холсте, добавляет кнопку сохранения.
     *
     * @param count количество фигур для генерации
     * @param xMin минимальное значение по оси X
     * @param xMax максимальное значение по оси X
     * @param yMin минимальное значение по оси Y
     * @param yMax максимальное значение по оси Y
     * @param density степень кластеризации (0.0–1.0)
     * @param showGrid флаг отображения координатной сетки
     * @param allowedTypes список выбранных пользователем типов фигур
     */
    private void openDrawingWindow(int count, double xMin, double xMax, double yMin, double yMax,
                                   double density, boolean showGrid, List<FigureType> allowedTypes) {
        Stage stage = new Stage();
        stage.setTitle("Случайный рисунок");

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc;
        gc = canvas.getGraphicsContext2D();

        Button saveBtn = new Button("Сохранить как PNG");
        saveBtn.setOnAction(e -> saveAsPng(stage, canvas, xMin, xMax, yMin, yMax, density, showGrid, allowedTypes));

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setBottom(saveBtn);
        BorderPane.setAlignment(saveBtn, Pos.CENTER);
        BorderPane.setMargin(saveBtn, new Insets(10));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Рисуем
        DrawingRenderer.drawFigures(gc, canvas.getWidth(), canvas.getHeight(),
                count, xMin, xMax, yMin, yMax, density, showGrid, allowedTypes);
    }

    /**
     * Сохраняет текущее содержимое холста в PNG-файл по выбору пользователя.
     * Перерисовывает изображение "с нуля" для избежания артефактов.
     *
     * @param stage родительское окно для диалога сохранения
     * @param canvas холст с текущим изображением
     * @param xMin минимальное значение по оси X
     * @param xMax максимальное значение по оси X
     * @param yMin минимальное значение по оси Y
     * @param yMax максимальное значение по оси Y
     * @param density степень кластеризации
     * @param showGrid флаг отображения сетки
     * @param allowedTypes список разрешённых типов фигур
     */
    private void saveAsPng(Stage stage, Canvas canvas, double xMin, double xMax, double yMin, double yMax,
                           double density, boolean showGrid, List<FigureType> allowedTypes) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить как PNG");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files", "*.png"));
        chooser.setInitialFileName("random_drawing.png");

        File file = chooser.showSaveDialog(stage);
        if (file == null) return;

        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }

        try {
            // Перерисовываем "чисто" в новое изображение (чтобы избежать артефактов)
            double w = canvas.getWidth();
            double h = canvas.getHeight();
            WritableImage wi = new WritableImage((int) w, (int) h);
            Canvas tmp = new Canvas(w, h);
            GraphicsContext gc = tmp.getGraphicsContext2D();

            DrawingRenderer.drawFigures(gc, w, h, 20, xMin, xMax, yMin, yMax, density, showGrid, allowedTypes);

            tmp.snapshot(null, wi);

            BufferedImage bi = SwingFXUtils.fromFXImage(wi, null);
            ImageIO.write(bi, "png", file);

            new Alert(Alert.AlertType.INFORMATION, "Изображение сохранено:\n" + file.getAbsolutePath()).showAndWait();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Ошибка сохранения:\n" + ex.getMessage()).showAndWait();
        }
    }

    /**
     * Статическая точка входа в приложение. Делегирует запуск JavaFX-платформе.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        launch(args);
    }
}