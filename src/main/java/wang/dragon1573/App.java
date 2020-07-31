/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import wang.dragon1573.controller.MainController;
import wang.dragon1573.utils.FileUtils;

public class App extends Application {
    /**
     * <h2>定时任务线程池</h2>
     * <p>&emsp;&emsp;根据阿里Java开发手册和P3C代码规约系统要求，禁止在程序源代码中显式开辟线程，必须使用线程池来管理所有的自定义线程。</p>
     * <p>&emsp;&emsp;这是一个定时任务池，只能容纳最多1个线程，只用作定时休息提醒功能。</p>
     */
    private static final ScheduledThreadPoolExecutor EXECUTOR =
        new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "Rest Reminder"));
    /**
     * <h2>主界面控制器</h2>
     */
    private MainController controller;

    /**
     * <h2>JavaFX主函数</h2>
     * <p>&emsp;&emsp;这才是JavaFX真正的主函数。</p>
     */
    @Override
    public void start(final Stage primaryStage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/Main.fxml")
        );

        // 加载FXML页面
        final BorderPane root = loader.load();
        // 获取控制器对象
        controller = loader.getController();

        /* 设置主场景 */
        final Scene scene = new Scene(root);

        /* 设置主舞台（窗口） */
        primaryStage.setScene(scene);
        primaryStage.setTitle("文本编辑器");
        primaryStage.getIcons().add(new Image(
            getClass().getResourceAsStream("/img/icon.png")
        ));
        // 绑定退出事件
        primaryStage.setOnCloseRequest(this::exitListener);

        /* 开幕 */
        primaryStage.show();
    }

    /**
     * <h2>提醒休息小助手</h2>
     * <p>&emsp;&emsp;这是一个定时任务线程池，用来定时提醒用户休息。</p>
     */
    private static void breakReminder() {
        EXECUTOR.scheduleWithFixedDelay(() -> {
            final Alert alert = new Alert(AlertType.INFORMATION, null);
            alert.setTitle("提醒休息小助手");
            alert.setHeaderText("潘金莲提醒您：休息一下吧，该吃药了(^v^)");
            alert.showAndWait();
        }, 1, 1, TimeUnit.HOURS);
    }

    /**
     * <h2>JavaFX程序退出监听器</h2>
     * <p>&emsp;&emsp;此监听器在应用程序退出时被触发。</p>
     *
     * @param event
     *     窗口事件
     */
    private void exitListener(final WindowEvent event) {
        // 阻止事件进一步传递，防止取消后仍然退出
        event.consume();
        assert event.isConsumed();

        if (Status.isUnsaved) {
            /* 创建按钮系列 */
            final ButtonType yesButton = new ButtonType("保存", ButtonBar.ButtonData.YES);
            final ButtonType noButton = new ButtonType("丢弃", ButtonBar.ButtonData.NO);

            // 创建询问对话框
            final Alert alert = new Alert(
                AlertType.CONFIRMATION, null,
                yesButton, noButton, ButtonType.CANCEL
            );

            /* 设置对话框 */
            alert.setTitle("关闭软件...");
            alert.setHeaderText("您有尚未保存的更改，是否保存并关闭？");

            /* 弹出对话框 */
            alert.showAndWait().ifPresent(buttonType -> {
                if (
                    // 保存按钮
                    yesButton == buttonType &&
                    // 文件被正确保存
                    FileUtils.saveFile(controller.content.getText())
                ) {
                    Platform.exit();
                } else if (noButton == buttonType) {    // 丢弃按钮
                    Platform.exit();
                }
            });
        } else {
            final ButtonType exitButton = new ButtonType("退出", ButtonBar.ButtonData.YES);

            final Alert alert = new Alert(
                AlertType.CONFIRMATION, null, exitButton, ButtonType.CANCEL
            );
            alert.setTitle("关闭软件...");
            alert.setHeaderText("确认退出？");

            // 退出程序
            alert.showAndWait().filter(buttonType -> exitButton == buttonType)
                 .ifPresent(buttonType -> Platform.exit());
        }
    }
}
