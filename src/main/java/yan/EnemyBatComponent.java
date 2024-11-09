package yan;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

import static java.lang.Math.*;

// 由电脑控制的拍子
public class EnemyBatComponent extends Component {
    // 物理组件
    PhysicsComponent physics;

    // 加速度
    double A = 3200;
    // 减速
    double A_ = -5000;
    // 最大速度
    double MaxSpeed = 400;

    // 当前加速度
    double a = 0;

    // AI难度
    int difficulty = FXGL.geti("difficulty");

    // 球
    Entity ball = FXGL.getGameWorld().getEntitiesByType(PongType.ball).getFirst();

    @Override
    public void onUpdate(double tpf) {
        // 调用ai 设置a
        switch (difficulty) {
            case 0 -> AI_0();
            case 1 -> AI_1();
            case 2 -> AI_2();
            case 3 -> AI_3();
        }

        // 按照加速度修改拍子速度,同玩家
        if (a != 0) {
            physics.setVelocityY(min(max(physics.getVelocityY() + a * tpf, -MaxSpeed), MaxSpeed));
        } else {
            double vy = physics.getVelocityY();
            physics.setVelocityY(max(0, abs(vy) + A_ * tpf) * signum(vy));
        }

        // 出界判定
        if (entity.getY() < 0) {
            physics.overwritePosition(new Point2D(entity.getX(), 0));
        }
        if (entity.getBottomY() > FXGL.getAppHeight()) {
            physics.overwritePosition(new Point2D(entity.getX(), FXGL.getAppHeight() - entity.getHeight()));
        }

        // 旋转
        entity.setRotation(toDegrees(atan2(physics.getVelocityY(), -500)));
    }

    void AI_0() {
        // 判断球是否在左边...
        boolean isBallLeft = !(ball.getX() < entity.getRightX());
        // ...但是脑抽

        if (entity.getY() > ball.getBottomY()) {// 如果球在上面
            // 向上移动
            a = isBallLeft ? -A : A;
        } else if (entity.getBottomY() < ball.getY()) {//如果球在下面
            // 向下移动
            a = isBallLeft ? A : -A;
        } else { // 就在中间
            a = 0;
        }
    }

    void AI_1() {
        // 跟着球跑
        // 判断球是否在左边
        boolean isBallLeft = ball.getX() < entity.getRightX();

        if (entity.getY() > ball.getBottomY() - 20) {// 如果球在上面
            // 向上移动
            a = isBallLeft ? -A : A;
        } else if (entity.getBottomY() < ball.getY() + 20) {//如果球在下面
            // 向下移动
            a = isBallLeft ? A : -A;
        } else { // 就在中间
            a = 0;
        }
    }

    double target;

    void AI_2() {
        if (ball.getX() > entity.getX()) {// 球在拍子右边
            // 避让
            if (ball.getY() > 300) {
                target = 20;
            } else {
                target = 580;
            }
        } else if (ball.getX() >= 800 - ball.getComponent(PhysicsComponent.class).getVelocityX() * 1.1 && ball.getComponent(PhysicsComponent.class).getVelocityX() > 0) {// 当球正在往这边飞来时
            // 跟随球
            target = ball.getY();
        } else {
            // 回中
            target = FXGL.getAppHeight() / 2d;
        }

        // 向目标移动
        if (entity.getY() + 20 > target) {// 如果球在上面
            // 向上移动
            a = -A;
        } else if (entity.getBottomY() - 20 < target) {//如果球在下面
            // 向下移动
            a = A;
        } else { // 就在中间
            a = 0;
        }
    }

    boolean isTarget = false;

    void AI_3() {// 尝试预测球的轨迹
        // 获取
        double bx = ball.getX(), by = ball.getY(),
                vx = ball.getComponent(PhysicsComponent.class).getVelocityX(), vy = ball.getComponent(PhysicsComponent.class).getVelocityY();
        // 避让
        if (bx > entity.getX()) {// 球在拍子右边
            if (by > entity.getY()) {
                target = 20;
            } else {
                target = 580;
            }
        } else if (entity.getX() - bx < 40){
            // 距离很近了,机动一下
            target = by;
        }else if (!isTarget && vx > 0 && bx > 200) {
            // 如果还没有预测球的轨迹,并且球正在往这边飞来
            // 有1/5的概率瞎猜
            if (random() < 0.2) {
                target = random() * 600;
            } else {
                // 不瞎猜!
//                System.out.println("-------------开始预测-------------");
                // 计算球将要飞的距离
                double disY = by + (entity.getX() - bx) / vx * vy;
//                System.out.println("disY = " + disY);
                // 计算球的落点
                if (disY > 0 && disY < 600) {
                    // 没有经过反弹,可以直接赋值
                    target = disY;
//                    System.out.println("未反弹");
                } else {
                    // 在墙上弹过至少一次
                    if (disY < 0) {
                        // 总位移向上,将其等价为向下的位移
                        disY = 1200 - disY;
//                        System.out.println("总位移向上,将其等价为向下的位移:" + disY);
                    }
                    // 将总位移%600 再判断反弹次数 得到最终位置
                    target = disY % 600;
//                    System.out.println("target :" + target);
                    if (((int) disY / 600) % 2 != 0) {
                        target = 600 - target;
//                        System.out.println("反向!");
                    }
                }
            }
//            System.out.println("预测落点为:" + target);
            // 调试:显示落点
//            FXGL.entityBuilder().at(entity.getX(), target).view(new Circle(5, Color.RED)).with(new ExpireCleanComponent(Duration.seconds(2.0))).buildAndAttach();
            // 猜完了
            isTarget = true;
        } else if (vx < 0 && bx < entity.getX() && isTarget) {
            // 这是在回去的路上了
            isTarget = false;
            target = 300;
        }

        // 向目标移动
        if (entity.getY() + 20 > target) {// 如果球在上面
            // 向上移动
            a = -A;
        } else if (entity.getBottomY() - 20 < target) {//如果球在下面
            // 向下移动
            a = A;
        } else { // 就在中间
            a = 0;
        }
    }

}
