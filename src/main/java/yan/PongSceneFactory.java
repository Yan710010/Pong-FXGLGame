package yan;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class PongSceneFactory extends SceneFactory {
    @Override
    public FXGLMenu newMainMenu() {
        return new FXGLMenu(MenuType.MAIN_MENU) {
            final String[] difficultyTooltip = new String[]{
                    "入机模式就是入机模式嘛",
                    "只会追着球傻跑的AI,\n但是并不简单",
                    "更加沉稳的AI",
                    "会尝试预测球的轨迹的AI",
                    "会更加准确的预测轨迹\n会尝试使球转向"
            };
            final String[] difficultyName = new String[]{"入机", "经典","普通","困难","困难+"};

            int difficulty = 1;
            final int num_dif = 5;

            @Override
            public void onCreate() {
                // 背景
                getContentRoot().setBackground(Background.fill(Color.BLACK));
                getContentRoot().setPrefWidth(800);
                getContentRoot().setPrefHeight(700);
                // 标题
                Label titleLabelLabel = new Label("P o n g");
                titleLabelLabel.setFont(Font.font(80));
                titleLabelLabel.setTextFill(Color.WHITE);
                titleLabelLabel.setLayoutX(120);
                titleLabelLabel.setLayoutY(120);

                TranslateTransition titleLabelTranslateTransition = new TranslateTransition(Duration.seconds(1), titleLabelLabel);
                titleLabelTranslateTransition.setFromX(-200);
                titleLabelTranslateTransition.setFromY(0);
                titleLabelTranslateTransition.setToX(0);
                titleLabelTranslateTransition.setToY(0);
                titleLabelTranslateTransition.play();
                FadeTransition titleLabelFadeTransition = new FadeTransition(Duration.seconds(1), titleLabelLabel);
                titleLabelFadeTransition.setFromValue(0);
                titleLabelFadeTransition.setToValue(1);
                titleLabelFadeTransition.play();

                // 开始游戏按钮
                Button startGameButton = new Button(String.format("开始游戏(%s)", difficultyName[difficulty]));
                startGameButton.setFont(Font.font(20));
                startGameButton.setTextFill(Color.WHITE);
                startGameButton.setBackground(Background.EMPTY);
                startGameButton.setLayoutX(100);
                startGameButton.setLayoutY(400);
                startGameButton.setOpacity(0);
                startGameButton.setOnAction(event -> getController().startNewGame());
                startGameButton.setOnMouseEntered(event -> startGameButton.setTextFill(Color.GOLDENROD));
                startGameButton.setOnMouseExited(event -> startGameButton.setTextFill(Color.WHITE));
                Tooltip difficultyTip = new Tooltip(difficultyTooltip[difficulty]);
                difficultyTip.setShowDelay(Duration.ZERO);
                startGameButton.setTooltip(difficultyTip);

                TranslateTransition startGameTranslateTransition = new TranslateTransition(Duration.seconds(1), startGameButton);
                startGameTranslateTransition.setDelay(Duration.seconds(0.3));
                startGameTranslateTransition.setFromX(-130);
                startGameTranslateTransition.setFromY(0);
                startGameTranslateTransition.setToX(0);
                startGameTranslateTransition.setToY(0);
                startGameTranslateTransition.play();
                FadeTransition startGameFadeTransition = new FadeTransition(Duration.seconds(1), startGameButton);
                startGameFadeTransition.setDelay(Duration.seconds(0.3));
                startGameFadeTransition.setFromValue(0);
                startGameFadeTransition.setToValue(1);
                startGameFadeTransition.play();

                // 调整难度按钮
                Button changeDifficultyButton = new Button("切换难度");
                changeDifficultyButton.setFont(Font.font(20));
                changeDifficultyButton.setTextFill(Color.WHITE);
                changeDifficultyButton.setBackground(Background.EMPTY);
                changeDifficultyButton.setLayoutX(80);
                changeDifficultyButton.setLayoutY(450);
                changeDifficultyButton.setOpacity(0);
                changeDifficultyButton.setOnAction(event -> {
                    // 难度
                    difficulty = ++difficulty % num_dif;
                    // 设置开始按钮的名字
                    startGameButton.setText(String.format("开始游戏(%s)", difficultyName[difficulty]));
                    // 设置难度提示
                    difficultyTip.setText(difficultyTooltip[difficulty]);
                    // 设置难度
                    PongApp.dif = difficulty;
                });
                changeDifficultyButton.setOnMouseEntered(event -> changeDifficultyButton.setTextFill(Color.GOLDENROD));
                changeDifficultyButton.setOnMouseExited(event -> changeDifficultyButton.setTextFill(Color.WHITE));

                TranslateTransition changeDifficultyTranslateTransition = new TranslateTransition(Duration.seconds(1),changeDifficultyButton);
                changeDifficultyTranslateTransition.setDelay(Duration.seconds(0.5));
                changeDifficultyTranslateTransition.setFromX(-130);
                changeDifficultyTranslateTransition.setToX(0);
                changeDifficultyTranslateTransition.play();
                FadeTransition changeDifficultyFadeTransition = new FadeTransition(Duration.seconds(1),changeDifficultyButton);
                changeDifficultyFadeTransition.setDelay(Duration.seconds(0.5));
                changeDifficultyFadeTransition.setToValue(1);
                changeDifficultyFadeTransition.play();

                getContentRoot().getChildren().addAll(titleLabelLabel, startGameButton, changeDifficultyButton);
            }
        };
    }
}
