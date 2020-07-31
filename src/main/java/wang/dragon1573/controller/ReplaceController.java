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

public class ReplaceController implements Initializable {
    private final SimpleBooleanProperty caseSensitiveProperty =
        new SimpleBooleanProperty(false);
    @FXML private CheckBox caseSensitiveBox;
    private Dialog<Object> dialog;
    private Direction direction;
    @FXML private ToggleGroup group;
    @FXML private Text keywordCounter;
    @FXML private Button replaceAllButton;
    @FXML private TextField replaceField;
    @FXML private Button replaceSingleButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    private TextArea textArea;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // 双向绑定
        caseSensitiveBox.selectedProperty().bindBidirectional(caseSensitiveProperty);

        // 默认搜索方向
        direction = Direction.BACKWARD;

        // 按钮屏蔽
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            final boolean isEmpty = newValue.isEmpty();
            replaceAllButton.setDisable(isEmpty);
            replaceSingleButton.setDisable(isEmpty);
            searchButton.setDisable(isEmpty);
        });
    }

    /**
     * <h3>关闭对话框</h3>
     */
    public void closeDialog(final ActionEvent event) {
        dialog.setResult(event);
        dialog.close();
    }

    /** <h3>全部替换</h3> */
    public void replaceAll(final ActionEvent event) {
        keywordCounter.setText("替换" + SearchUtils.getKeywordCounts(
            searchField.getText(), caseSensitiveProperty.get(), textArea.getText()
        ) + "项");
        if (caseSensitiveProperty.getValue()) {
            textArea.setText(textArea.getText().replaceAll(
                searchField.getText(),
                replaceField.getText()
            ));
        } else {
            textArea.setText(textArea.getText().replaceAll(
                "(?i)" + searchField.getText(),
                replaceField.getText()
            ));
        }
    }

    /**
     * <h3>向下替换</h3>
     */
    public void replaceBackwards(final ActionEvent event) {
        direction = Direction.BACKWARD;
    }

    /** <h3>向上替换</h3> */
    public void replaceForward(final ActionEvent event) {
        direction = Direction.FORWARD;
    }

    /** <h3>替换一项</h3> */
    public void replaceSingle(final ActionEvent event) {
        textArea.replaceSelection(replaceField.getText());
        search(event);
    }

    /** <h3>查找一项</h3> */
    public void search(final ActionEvent event) {
        SearchUtils.search(
            caseSensitiveProperty.get(), textArea,
            searchField.getText(), direction,
            keywordCounter
        );
    }

    /**
     * <h3>设置对话框</h3>
     *
     * @param dialog
     *     顶层对话框
     */
    public void setDialog(final Dialog<Object> dialog) { this.dialog = dialog; }

    /**
     * <h3>设置文本编辑区</h3>
     *
     * @param textArea
     *     文本编辑区对象
     */
    public void setTextArea(final TextArea textArea) {
        this.textArea = textArea;
    }
}
