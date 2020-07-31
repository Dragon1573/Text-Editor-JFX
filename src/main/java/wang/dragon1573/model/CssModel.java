/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CssModel {
    /** <h3>CSS反序列化正则表达式</h3> */
    private static final Pattern PATTERN = Pattern.compile(
        "-fx-font-family: '(.*)';"
        + "(\\s*)-fx-font-style: '(.*)';"
        + "(\\s*)-fx-font-weight: '(.*)';(\\s*)"
        + "-fx-font-size: (\\d+)px"
    );
    /** <h3>字体</h3> */
    private String family;
    /** <h3>大小</h3> */
    private int size;
    /** <h3>效果</h3> */
    private String style;
    /** <h3>粗细</h3> */
    private String weight;

    /**
     * <h3>序列化方法</h3>
     * <p>&emsp;&emsp;它能够将字体对象序列化为CSS样式字符串</p>
     */
    @Override
    public String toString() {
        return "-fx-font-family: '" + family + "';"
               + "-fx-font-style: '" + style + "';"
               + "-fx-font-weight: '" + weight + "';"
               + "-fx-font-size: " + size + "px";
    }

    /**
     * <h3>构造方法</h3>
     *
     * @param family
     *     字体
     * @param size
     *     字号
     * @param style
     *     样式
     * @param weight
     *     粗细
     */
    public CssModel(
        final String family, final int size, final String style, final String weight
    ) {
        this.family = family;
        this.size = size;
        this.style = style;
        this.weight = weight;
    }

    /**
     * <h3>构造方法</h3>
     *
     * @param cssString
     *     CSS样式字符串
     */
    public CssModel(final String cssString) {
        final Matcher matcher = PATTERN.matcher(cssString);
        if (matcher.find()) {
            this.family = matcher.group(1);
            this.style = matcher.group(3);
            this.weight = matcher.group(5);
            this.size = Integer.parseInt(matcher.group(7));
        }
    }

    public String getFamily() {
        return family;
    }

    public int getSize() {
        return size;
    }

    public String getStyle() {
        final int isBold = weight.contentEquals("bold") ? 0x10 : 0x00;
        final int isItalic = style.contentEquals("italic") ? 0x01 : 0x00;
        return switch (isBold | isItalic) {
            case 0x11 -> "粗体＋斜体";
            case 0x10 -> "粗体";
            case 0x01 -> "斜体";
            default -> "常规";
        };
    }
}
