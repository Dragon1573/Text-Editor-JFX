/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

import wang.dragon1573.Status;
import wang.dragon1573.utils.FileUtils;

public class MainController implements Initializable {
    /**
     * <b>行列坐标正则表达式</b>
     * <p>&emsp;&emsp;用于后文提取用户输入的行号与列号。</p>
     */
    private static final Pattern POSITION = Pattern.compile("(\\d+)(?::(\\d+))?");
    /**
     * <b>换行符</b>
     * <p>&emsp;&emsp;用于后续正则匹配统计行数</p>
     */
    private static final Pattern SEPARATOR = Pattern.compile("\\n");
    /**
     * <b>文件名</b>
     */
    @FXML private Label fileName;
    /** <b>行尾模式</b> */
    @FXML private ChoiceBox<String> lineSeparator;
    /**
     * <b>多选组</b>
     */
    @FXML private ToggleGroup themeGroup;
    /** <b>行列定位器</b> */
    public Button caretButton;
    /**
     * <b>文本框</b>
     */
    @FXML public TextArea content;

    /**
     * <h2>事件监听器初始化方法</h2>
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        /* 双向绑定，以实现数据同步 */
        fileName.textProperty().bindBidirectional(Status.fileName);
        lineSeparator.valueProperty().bindBidirectional(Status.lineSeparator);

        /* 绑定事件监听器，它无法通过FXML绑定 */
        content.textProperty().addListener(
            (observable, oldValue, newValue) -> Status.isUnsaved = true
        );
        content.caretPositionProperty().addListener(this::changed);
    }

    /**
     * <h2>修改编辑区字体样式</h2>
     */
    public void changeFont(final ActionEvent event) throws IOException {
        /* 创建主内容面板 */
        // FXML加载器
        final FXMLLoader contentLoader = new FXMLLoader(
            getClass().getResource("/fxml/Font.fxml")
        );
        // FXML面板
        final GridPane pane = contentLoader.load();
        // FXML控制器
        final FontController controller = contentLoader.getController();
        // 设置初始字体
        controller.setFont(content.getStyle());
        // 绑定事件监听器
        controller.attachListener();

        /* 创建会话框 */
        final Dialog<ButtonType> fontDialog = new Dialog<>();
        fontDialog.getDialogPane().setHeaderText(null);
        fontDialog.getDialogPane().setContent(pane);
        fontDialog.getDialogPane().getButtonTypes().setAll(
            ButtonType.APPLY, ButtonType.CANCEL
        );
        fontDialog.showAndWait().filter(buttonType -> buttonType == ButtonType.APPLY)
                  .ifPresent(buttonType -> content.setStyle(controller.getStyle()));
    }

    /**
     * <h2>光标位置变更监听器</h2>
     * <p>&emsp;&emsp;JDK 8之后，只有一个方法的类可以被压缩为Lambda表达式。同时，JDK
     * 8还引入了类似C/C++函数指针的概念，允许使用方法名、将整个方法作为参数进行传递。</p>
     */
    private void changed(
        final ObservableValue<? extends Number> observable,
        final Number oldPosition, final Number newPosition
    ) {
        // 截取文本内容
        final String part = content.getText(0, (Integer)newPosition);

        /* 计算行号 */
        int rowNumber = 1;
        final Matcher matcher = SEPARATOR.matcher(part);
        while (matcher.find()) {
            rowNumber += 1;
        }

        // 获得列号
        final int columnNumber = (Integer)newPosition - part.lastIndexOf("\n");
        // 更新行列坐标
        caretButton.setText(rowNumber + ":" + columnNumber);
    }

    /**
     * <h2>展示版本信息</h2>
     */
    public void getVersion(final ActionEvent event) throws IOException {
        /* 加载自定义内容 */
        final Parent contentPane = FXMLLoader.load(
            getClass().getResource("/fxml/Version.fxml")
        );

        /* 创建版本信息面板 */
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("版本信息");
        alert.getDialogPane().setHeaderText(null);
        alert.getDialogPane().setContent(contentPane);
        alert.showAndWait();
    }

    /**
     * <h2>光标位置跳转</h2>
     * <p>&emsp;&emsp;实现用户光标位置跳转功能。</p>
     */
    public void jumpCaret(final ActionEvent event) {
        /* 创建对话框 */
        final TextInputDialog dialog = new TextInputDialog(caretButton.getText());
        dialog.setHeaderText(null);
        dialog.setContentText("行[:列]：");

        /* 弹出对话框 */
        dialog.showAndWait()
              .map(POSITION::matcher)
              .ifPresent(matcher -> {
                  /* 非法用户输入，让光标回到文本区 */
                  if (!matcher.matches()) {
                      content.requestFocus();
                      return;
                  }

                  /* 获取用户输入的坐标 */
                  int inputRow = Integer.parseInt(matcher.group(1));
                  int inputColumn;
                  try {
                      inputColumn = Integer.parseInt(matcher.group(2));
                  } catch (final IllegalStateException e) {
                      inputColumn = 1;
                  }

                  // 将正文按行切割为数组
                  final String[] text = (content.getText() + "?").split("\n");

                  /* 定位至与输入行号最接近的行 */
                  final int actualRows = text.length;
                  if (inputRow < 1) {
                      inputRow = 1;
                  } else if (inputRow > actualRows) {
                      inputRow = actualRows;
                  }

                  /* 定位至与输入列号最接近的列 */
                  int actualColumns = text[inputRow - 1].length();
                  if (inputRow != actualRows) {
                      actualColumns += 1;
                  }
                  if (inputColumn < 1) {
                      inputColumn = 1;
                  } else if (inputColumn > actualColumns) {
                      inputColumn = actualColumns;
                  }

                  /* 计算偏移量 */
                  final StringBuilder builder = new StringBuilder();
                  for (int i = 1; i < inputRow; i++) {
                      builder.append(text[i - 1]).append("\n");
                  }
                  builder.append(text[inputRow - 1], 0, inputColumn - 1);

                  /* 移动光标位置 */
                  content.positionCaret(builder.length());
                  caretButton.setText(inputRow + ":" + inputColumn);
                  content.requestFocus();
              });
    }

    /**
     * <h2>新建文件</h2>
     */
    public void newFile(final ActionEvent event) {
        if (Status.isUnsaved) {
            /* 文件未保存 */

            /* 创建警告 */
            final Alert alert = new Alert(
                AlertType.WARNING, null,
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL
            );
            alert.setTitle("未保存的更改！");
            alert.setHeaderText("您有尚未保存的更改，是否保存？");
            // 显示对话框
            alert.showAndWait()
                 .filter(response -> response != ButtonType.CANCEL)
                 .ifPresent(response -> {
                     if (response == ButtonType.YES) {
                         // 先保存文件
                         saveFile(event);
                     }
                     // 清空文本内容
                     content.setText(null);
                     // 重置文件名
                     fileName.setText("无标题");
                     Status.isUnsaved = false;
                     Status.currentFile = null;
                 });
        } else {
            /* 没有未保存的更改，直接新建文件 */
            content.setText(null);
            fileName.setText("无标题");
            Status.isUnsaved = false;
            Status.currentFile = null;
        }
    }

    /**
     * <h2>打开文件</h2>
     */
    public void openFile(final ActionEvent event) {
        if (Status.isUnsaved) {
            /* 文件未保存 */

            /* 创建警告 */
            final Alert alert = new Alert(
                AlertType.WARNING, null,
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL
            );
            alert.setTitle("未保存的更改！");
            alert.setHeaderText("您有尚未保存的更改，是否保存？");
            // 显示对话框，根据用户选项进行处理
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    /* 保存文件 */
                    saveFile(event);
                    Status.isUnsaved = false;

                    /* 显示文件内容 */
                    content.setText(FileUtils.openFile());
                } else if (response == ButtonType.NO) {
                    /* 直接丢弃，打开文件 */
                    Status.isUnsaved = false;
                    content.setText(FileUtils.openFile());
                }
            });
        } else {
            /* 没有未保存的更改，直接打开文件 */

            // noinspection ConstantConditions （暂时屏蔽憨批的Inspections）
            Status.isUnsaved = false;
            content.setText(FileUtils.openFile());
        }
    }

    /**
     * <h2>文本替换</h2>
     * <p>&emsp;&emsp;实现关键词的替换操作</p>
     */
    public void replaceKeyWord(final ActionEvent event) throws IOException {
        /* 加载主面板 */
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/Replace.fxml")
        );
        // FXML对象
        final DialogPane pane = loader.load();
        // 控制器
        final ReplaceController controller = loader.getController();

        /* 创建对话框 */
        final Dialog<Object> dialog = new Dialog<>();
        dialog.setDialogPane(pane);
        dialog.setTitle("替换");
        dialog.setOnCloseRequest(request -> {
            dialog.setResult(request);
            dialog.close();
        });

        // 依赖注入
        controller.setDialog(dialog);
        controller.setTextArea(content);

        // 非阻塞式对话框
        dialog.show();
    }

    /**
     * <h2>保存文件</h2>
     */
    public void saveFile(final ActionEvent event) {
        FileUtils.saveFile(content.getText());
    }

    /** <h2>文本搜索</h2> */
    public void searchKeyWord(final ActionEvent event) throws IOException {
        /* 加载FXML */
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/Search.fxml")
        );

        /* 创建对话框 */
        final Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle("查找");
        dialog.setDialogPane(loader.load());

        /* 获取FXML控制器 */
        final SearchController controller = loader.getController();
        controller.setDialog(dialog);
        controller.setTextArea(content);

        /* 展示对话框 */
        dialog.show();
    }

    /**
     * <h2>大小写切换</h2>
     * <p>&emsp;&emsp;实现编辑区文本内容的全局大小写转换。</p>
     */
    public void switchCapital(final ActionEvent event) {
        final char[] chars = content.getText().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'a' && chars[i] <= 'z') {
                chars[i] += 'A' - 'a';
            } else if (chars[i] >= 'A' && chars[i] <= 'Z') {
                chars[i] += 'a' - 'A';
            }
        }
        content.setText(new String(chars));
    }

    /**
     * <h2>切换文档换行模式</h2>
     */
    public void toggleWrapState(final ActionEvent event) {
        content.setWrapText(!content.isWrapText());
    }

    /**
     * <h2>统计文本中的字符数量</h2>
     * <p>
     * 统计面板的统计数据应该包括以下内容：
     * <ul>
     *     <li>字符数量（不计空格）</li>
     *     <li>字符数量（计空格）</li>
     *     <li>行数</li>
     * </ul>
     * </p>
     */
    public void wordCount(final ActionEvent event) throws IOException {
        /* 警告框 */
        final Alert alert = new Alert(AlertType.INFORMATION, null, ButtonType.OK);
        alert.setHeaderText(null);

        /* 获取FXML及其相关对象 */
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/Statistics.fxml")
        );
        // 加载页面描述文件
        alert.getDialogPane().setContent(loader.load());
        // 获取控制器
        final StatisticsController controller = loader.getController();

        /* 计算相关数据 */
        controller.setLinesLabel((content.getText() + "?").lines().count());
        controller.setCharWithSpaceLabel(content.getText().length());
        controller.setCharNoSpaceLabel(
            // 获取正文内容的字符集合流
            content.getText().chars()
                   // 过滤空白字符
                   .filter(value -> !Character.isWhitespace(value))
                   // 计数
                   .count()
        );

        /* 显示对话框 */
        alert.showAndWait();
    }
}
