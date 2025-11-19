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

package org.teavm.classlib.java.awt.datatransfer;

import java.io.IOException;
import java.io.StringBufferInputStream;

@SuppressWarnings("deprecation")
public class TStringSelection implements TTransferable, TClipboardOwner {

    private static final TDataFlavor[] supportedFlavors = { TDataFlavor.stringFlavor,
            TDataFlavor.plainTextFlavor };

    private final String string;

    public TStringSelection(String data) {
        string = data;
    }

    public Object getTransferData(TDataFlavor flavor) throws TUnsupportedFlavorException,
            IOException {
        if (flavor.equals(TDataFlavor.stringFlavor)) {
            return string;
        } else if (flavor.equals(TDataFlavor.plainTextFlavor)) {
            return new StringBufferInputStream(string);
        } else {
            throw new TUnsupportedFlavorException(flavor);
        }
    }

    public boolean isDataFlavorSupported(TDataFlavor flavor) {
        return (flavor.equals(TDataFlavor.stringFlavor) || flavor
                .equals(TDataFlavor.plainTextFlavor));
    }

    public TDataFlavor[] getTransferDataFlavors() {
        return supportedFlavors.clone();
    }

    public void lostOwnership(TClipboard clipboard, TTransferable contents) {
    }
}
