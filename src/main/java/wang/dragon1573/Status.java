/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573;

import java.io.File;
import javafx.beans.property.SimpleStringProperty;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class Status {
    /**
     * <b>行尾模式</b>
     */
    public static final BidiMap<String, String> LS_MAPPER = new DualHashBidiMap<>() {
        private static final long serialVersionUID = 1345107796949231496L;

        {
            this.put("\r\n", "CRLF - Windows (\\r\\n)");
            this.put("\n", "LF - Unix & Linux (\\n)");
            this.put("\r", "CR - Classic Unix (\\r)");
        }
    };
    /** <b>当前文件对象</b> */
    public static File currentFile = null;
    /**
     * <b>当前文件名</b>
     */
    public static SimpleStringProperty fileName = new SimpleStringProperty("无标题");
    /**
     * <b>文本框内容保存标记</b>
     */
    public static boolean isUnsaved = false;
    /**
     * <b>文件行尾模式</b>
     */
    public static SimpleStringProperty lineSeparator = new SimpleStringProperty(
        LS_MAPPER.get(System.getProperty("line.separator"))
    );
}
