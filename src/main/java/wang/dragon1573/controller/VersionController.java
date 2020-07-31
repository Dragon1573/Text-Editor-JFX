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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import static wang.dragon1573.Version.VERSION;

public class VersionController implements Initializable {
    public Label version;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        version.setText(VERSION);
    }
}
