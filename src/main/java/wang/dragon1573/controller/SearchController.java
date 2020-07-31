/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import wang.dragon1573.model.Direction;
import wang.dragon1573.utils.SearchUtils;

public class SearchController implements Initializable {
    /** 大小写敏感属性 */
    private final SimpleBooleanProperty isCaseSensitive =
        new SimpleBooleanProperty(false);
    /** 大小写敏感复选框 */
    @FXML private CheckBox caseSensitiveBox;
    /** 顶层对话框 */
    private Dialog<Object> dialog;
    /** 查找方向枚举值 */
    private Direction direction;
    /** 单选按钮组 */
    @FXML private ToggleGroup group;
    /** 关键词计数器 */
    @FXML private Text keywordCounter;
    /** 搜索按钮 */
    @FXML private Button searchButton;
    /** 关键词文本框 */
    @FXML private TextField searchField;
    /** 文本编辑区 */
    private TextArea textArea;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        /* 双向绑定 */
        caseSensitiveBox.selectedProperty().bindBidirectional(isCaseSensitive);

        /* 默认搜索方向 */
        direction = Direction.BACKWARD;

        /* 按钮屏蔽 */
        searchField.textProperty().addListener(
            (observable, oldValue, newValue) ->
                searchButton.setDisable(newValue.isEmpty())
        );
    }

    /** <h3>退出对话框</h3> */
    public void exitDialog(final ActionEvent event) {
        dialog.setResult(event);
        dialog.close();
    }

    /** <h3>查找一项</h3> */
    public void search(final ActionEvent event) {
        SearchUtils.search(
            isCaseSensitive.get(), textArea,
            searchField.getText(), direction,
            keywordCounter
        );
    }

    /**
     * <h3>依赖注入</h3>
     *
     * @param dialog
     *     对话框对象
     */
    public void setDialog(final Dialog<Object> dialog) {
        this.dialog = dialog;
    }

    /**
     * <h3>依赖注入</h3>
     *
     * @param textArea
     *     文本编辑区对象
     */
    public void setTextArea(final TextArea textArea) {
        this.textArea = textArea;
    }
}
