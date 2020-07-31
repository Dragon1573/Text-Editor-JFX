/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StatisticsController {
    /** <h3>字符数量（不计空格）</h3> */
    @FXML private Label charNoSpaceLabel;
    /** <h3>字符数量（计空格）</h3> */
    @FXML private Label charWithSpaceLabel;
    /** <h3>行数</h3> */
    @FXML private Label linesLabel;

    /**
     * <h3>设置字符数量（不计空格）</h3>
     *
     * @param charsCount
     *     统计得到的字符数量
     */
    public void setCharNoSpaceLabel(final long charsCount) {
        charNoSpaceLabel.setText(String.valueOf(charsCount));
    }

    /**
     * <h3>设置字符数量（计空格）</h3>
     *
     * @param charsCount
     *     统计得到的字符数量
     */
    public void setCharWithSpaceLabel(final int charsCount) {
        charWithSpaceLabel.setText(String.valueOf(charsCount));
    }

    /**
     * <h3>设置行数</h3>
     *
     * @param linesCount
     *     统计得到的行数
     */
    public void setLinesLabel(final long linesCount) {
        linesLabel.setText(String.valueOf(linesCount));
    }
}
