package yan;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PongApp extends GameApplication {
    // 主界面传值的游戏难度
    static int dif = 1;
    // 玩家胜利的提示
    final String[] playerWinStrings = new String[]{
            "打赢这种程度的敌人有什么好骄傲的",
            "恭喜玩家获得胜利!",
            "不错不错,挺有实力的嘛",
            "------您------"// 这里可以改一下
    };
    // 玩家失败的提示
    final String[] enemyWinStrings = new String[]{
            "怎么连入机版的入机都打不过? 你才是真入机",
            "菜就多练",
            "杂鱼~ 杂鱼~",
            "再接再厉喵!"
    };

    @Override
    protected void initSettings(GameSettings settings) {
        // 名字
        settings.setTitle("Pong");
        settings.setVersion("1.0");
        // 窗口大小
        settings.setWidth(800);
        settings.setHeight(600);
        // 启用开始菜单
        settings.setMainMenuEnabled(true);
//        settings.setGameMenuEnabled(false);
        settings.setSceneFactory(new PongSceneFactory());
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("player1score", 0);
        vars.put("player2score", 0);

        // 用于控制玩家移动
        vars.put("key_w", false);
        vars.put("key_s", false);
        vars.put("mousePressed", false);

        vars.put("difficulty", dif);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("up") {
            @Override
            protected void onActionBegin() {
                set("key_w", true);
            }

            @Override
            protected void onActionEnd() {
                set("key_w", false);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("down") {
            @Override
            protected void onActionBegin() {
                set("key_s", true);
            }

            @Override
            protected void onActionEnd() {
                set("key_s", false);
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("mouseControl") {
            @Override
            protected void onActionBegin() {
                set("mousePressed", true);
            }

            @Override
            protected void onActionEnd() {
                set("mousePressed", false);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initUI() {
        // 得分显示
        Label player1ScoreLabel = new Label();
        Label player2ScoreLabel = new Label();
        // 设置样式
        player1ScoreLabel.textProperty().bind(getip("player1score").asString());
        player1ScoreLabel.setFont(Font.font(80));
        player1ScoreLabel.setLayoutX(120);
        player1ScoreLabel.setLayoutY(100);
        player1ScoreLabel.setTextFill(Color.WHITE);

        player2ScoreLabel.textProperty().bind(getip("player2score").asString());
        player2ScoreLabel.setFont(Font.font(80));
        player2ScoreLabel.setLayoutX(getAppWidth() - 50 - 120);
        player2ScoreLabel.setLayoutY(100);
        player2ScoreLabel.setTextFill(Color.WHITE);

        // 添加Node
        addUINode(player1ScoreLabel);
        addUINode(player2ScoreLabel);

        // 添加动画
        player1ScoreLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            animationBuilder()
                    .autoReverse(true)
                    .repeat(2)
                    .duration(Duration.seconds(0.05))
                    .translate(player1ScoreLabel)
                    .from(new Point2D(0, 0))
                    .to(new Point2D(0, 40))
                    .buildAndPlay();
            animationBuilder()
                    .autoReverse(true).repeat(2)
                    .duration(Duration.seconds(0.1))
                    .fade(player1ScoreLabel)
                    .from(1).to(0.2)
                    .buildAndPlay();
        });

        player2ScoreLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            animationBuilder()
                    .autoReverse(true)
                    .repeat(2)
                    .duration(Duration.seconds(0.05))
                    .translate(player2ScoreLabel)
                    .from(new Point2D(0, 0))
                    .to(new Point2D(0, 40))
                    .buildAndPlay();
            animationBuilder()
                    .autoReverse(true).repeat(2)
                    .duration(Duration.seconds(0.05))
                    .fade(player2ScoreLabel)
                    .from(1).to(0.2)
                    .buildAndPlay();
        });
    }

    @Override
    protected void initPhysics() {
        // 球与墙壁的碰撞
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PongType.ball, PongType.wall) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("LEFT")) {
                    inc("player2score", +1);
                } else if (boxB.getName().equals("RIGHT")) {
                    inc("player1score", +1);
                }
                play("hit_wall.wav");
                getGameScene().getViewport().shakeTranslational(5);
            }
        });
        // 球与拍子的碰撞
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PongType.ball, PongType.bat) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                // 播放声音
                play("hit_bat.wav");
            }
        });
    }

    @Override
    protected void initGame() {
        //添加实体工厂
        getGameWorld().addEntityFactory(new PongFactory());

        // 设置世界重力
        getPhysicsWorld().setGravity(0, 0);
        // 设置背景颜色
        getGameScene().setBackgroundColor(Color.BLACK);

        // 生成球
        spawn("ball", new SpawnData(getAppWidth() / 2d, getAppHeight() / 2d));
        // 生成玩家拍子
        spawn("bat", new SpawnData().put("isPlayer", true));
        // 生成电脑拍子
        spawn("bat", new SpawnData().put("isPlayer", false));

        // 生成边界
        entityBuilder().type(PongType.wall).collidable().with(new PhysicsComponent()).buildScreenBoundsAndAttach(100);

        // 当游戏结束时
        getip("player1score").addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 11) {
                getDialogService().showChoiceBox(playerWinStrings[dif] + "\n是否重新开始?", new ArrayList<>(Arrays.asList("是", "否")), o -> {
                    if (o.equals("是")) {
                        getGameController().startNewGame();
                    } else {
                        getGameController().exit();
                    }
                });
            }
        });
        getip("player2score").addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 11) {
                getDialogService().showChoiceBox(enemyWinStrings[dif] + "\n是否重新开始?", new ArrayList<>(Arrays.asList("是", "否")), o -> {
                    if (o.equals("是")) {
                        getGameController().startNewGame();
                    } else {
                        getGameController().exit();
                    }
                });
            }
        });
    }


    public static void main(String[] args) {
        // 禁用系统缩放
        System.setProperty("prism.allowhidpi", "false"); // 其中，XXX 在实际的环境下应改为 true 或 false

        launch(args);
    }
}
