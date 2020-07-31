/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573.utils;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import wang.dragon1573.model.Direction;

public class SearchUtils {
    /**
     * <h3>计算子串在母串中的起始位置</h3>
     *
     * @param baseString
     *     正文字符串
     * @param subString
     *     关键词字符串
     * @param direction
     *     搜索方向
     * @param caretPosition
     *     当前光标位置
     */
    private static int getBeginPosition(
        final String baseString, final String subString,
        final Direction direction, final int caretPosition
    ) {
        if (direction == Direction.FORWARD) {
            return baseString.substring(0, caretPosition).lastIndexOf(subString);
        } else {
            final int relatedOffset = baseString.substring(caretPosition)
                                                .indexOf(subString);
            return relatedOffset != -1 ? caretPosition + relatedOffset : -1;
        }
    }

    /**
     * <h3>关键词计数</h3>
     *
     * @param keyword
     *     关键词字符串
     * @param content
     *     文本内容
     * @param isCaseSensitive
     *     大小写敏感模式
     */
    public static int getKeywordCounts(
        String keyword, final boolean isCaseSensitive, final String content
    ) {
        if (!isCaseSensitive) {
            keyword = "(?i)" + keyword;
        }
        final String mess = content.replaceAll(keyword, "");
        return (content.length() - mess.length()) / keyword.length();
    }

    /**
     * <h3>查找关键词</h3>
     *
     * @param direction
     *     查找方向
     * @param isCaseSensitive
     *     大小写敏感模式
     * @param keywordCounter
     *     关键词计数器
     * @param searchKeyword
     *     关键词字符串
     * @param textArea
     *     文本编辑区
     */
    public static void search(
        final boolean isCaseSensitive, final TextArea textArea,
        final String searchKeyword, final Direction direction,
        final Text keywordCounter
    ) {
        final int begin;
        if (isCaseSensitive) {
            begin = SearchUtils.getBeginPosition(
                textArea.getText(), searchKeyword,
                direction, textArea.getCaretPosition()
            );
        } else {
            begin = SearchUtils.getBeginPosition(
                textArea.getText().toLowerCase(), searchKeyword.toLowerCase(),
                direction, textArea.getCaretPosition()
            );
        }
        if (begin != -1) {
            SearchUtils.selectKeyword(
                direction, textArea, begin, searchKeyword.length()
            );
            keywordCounter.setText("找到" + getKeywordCounts(
                searchKeyword, isCaseSensitive, textArea.getText()
            ) + "项");
        } else {
            keywordCounter.setText("没有更多匹配项！");
        }
    }

    /**
     * <h3>选中关键词 </h3>
     *
     * @param direction
     *     搜索方向
     * @param textArea
     *     文本编辑区
     * @param begin
     *     关键词起始位置
     * @param length
     *     关键词长度
     */
    private static void selectKeyword(
        final Direction direction, final TextArea textArea,
        final int begin, final int length
    ) {
        if (direction == Direction.BACKWARD) {
            textArea.selectRange(begin, begin + length);
        } else {
            textArea.selectRange(begin + length, begin);
        }
    }
}
