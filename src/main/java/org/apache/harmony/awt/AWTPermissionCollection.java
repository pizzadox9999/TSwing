/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Evgueni V. Brevnov
 */

package org.apache.harmony.awt;

import org.teavm.classlib.java.awt.TAWTPermission;

public interface AWTPermissionCollection {

    TAWTPermission ACCESS_CLIPBOARD_PERMISSION = new TAWTPermission(
        "accessClipboard"); //$NON-NLS-1$

    TAWTPermission ACCESS_EVENT_QUEUE_PERMISSION = new TAWTPermission(
        "accessEventQueue"); //$NON-NLS-1$

    TAWTPermission CREATE_ROBOT_PERMISSION = new TAWTPermission("createRobot"); //$NON-NLS-1$

    TAWTPermission FULL_SCREEN_EXCLUSIVE_PERMISSION = new TAWTPermission(
        "fullScreenExclusive"); //$NON-NLS-1$

    TAWTPermission LISTEN_TO_ALL_AWTEVENTS_PERMISSION = new TAWTPermission(
        "listenToAllAWTEvents"); //$NON-NLS-1$

    TAWTPermission READ_DISPLAY_PIXELS_PERMISSION = new TAWTPermission(
        "readDisplayPixels"); //$NON-NLS-1$

    TAWTPermission REPLACE_KEYBOARD_FOCUS_MANAGER_PERMISSION = new TAWTPermission(
        "replaceKeyboardFocusManager"); //$NON-NLS-1$

    TAWTPermission SET_APPLET_STUB_PERMISSION = new TAWTPermission(
        "setAppletStub"); //$NON-NLS-1$

    TAWTPermission SET_WINDOW_ALWAYS_ON_TOP_PERMISSION = new TAWTPermission(
        "setWindowAlwaysOnTop"); //$NON-NLS-1$

    TAWTPermission SHOW_WINDOW_WITHOUT_WARNING_BANNER_PERMISSION = new TAWTPermission(
        "showWindowWithoutWarningBanner"); //$NON-NLS-1$

    TAWTPermission WATCH_MAOUSE_POINTER_PERMISSION = new TAWTPermission(
        "watchMousePointer"); //$NON-NLS-1$
}

