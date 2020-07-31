/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573.controller;

import java.awt.GraphicsEnvironment;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

import wang.dragon1573.model.CssModel;

public class FontController implements Initializable {
    /** <h3>字体选项组</h3> */
    @FXML private ChoiceBox<String> familyBox;
    /** <h3>样式展示框</h3> */
    @FXML private Text sampleText;
    /** <h3>字号选项组</h3> */
    @FXML private ChoiceBox<Integer> sizeBox;
    /** <h3>字形选项组</h3> */
    @FXML private ChoiceBox<String> styleBox;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // 导入字体列表
        familyBox.getItems().setAll(
            GraphicsEnvironment
                // 获取本地图像环境
                .getLocalGraphicsEnvironment()
                // 获取可用字体名列表
                .getAvailableFontFamilyNames()
        );
    }

    /** <h3>绑定事件监听器</h3> */
    public void attachListener() {
        // 绑定事件监听器
        familyBox.valueProperty().addListener(this::changed);
        styleBox.valueProperty().addListener(this::changed);
        sizeBox.valueProperty().addListener(this::changed);
    }

    /** <h3>字体变更监听器</h3> */
    public void changed(
        final ObservableValue<?> observable,
        final Object oldValue,
        final Object newValue
    ) {
        // 设置CSS样式
        sampleText.setStyle(
            new CssModel(
                familyBox.getValue(), sizeBox.getValue(),
                getPosture(), getWeight()
            ).toString()
        );
    }

    /**
     * <h3>获取字体渲染效果</h3>
     *
     * @return 字体渲染对象
     */
    private String getPosture() {
        return styleBox.getValue().contains("斜体") ? "italic" : "normal";
    }

    /** <h3>获取用户字体</h3> */
    public String getStyle() {
        return sampleText.getStyle();
    }

    /**
     * <h3>获取字体粗细</h3>
     *
     * @return 字体粗细对象
     */
    private String getWeight() {
        return styleBox.getValue().contains("粗体") ? "bold" : "normal";
    }

    public void setFont(final String initialStyle) {
        // 解析CSS格式字符串
        final CssModel fontCss = new CssModel(initialStyle);

        // 设置初始值
        familyBox.getSelectionModel().select(fontCss.getFamily());
        styleBox.getSelectionModel().select(fontCss.getStyle());
        sizeBox.getSelectionModel().select(Integer.valueOf(fontCss.getSize()));
        sampleText.setStyle(initialStyle);
    }
}
