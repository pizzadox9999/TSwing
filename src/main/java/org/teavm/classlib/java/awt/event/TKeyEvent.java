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
 * @author Michael Danilov
 */
package org.teavm.classlib.java.awt.event;

import java.util.HashMap;
import org.apache.harmony.awt.internal.nls.Messages;
import org.teavm.classlib.java.awt.TComponent;
import org.teavm.classlib.java.awt.TToolkit;

public class TKeyEvent extends TInputEvent {
    
    private static final HashMap<String, Integer>  FIELD_VALUE_MAP = new HashMap<>();
    private static int registerField(String field, int value) {
        FIELD_VALUE_MAP.put(field, value);
        return value;
    }
    public static int getFieldValue(String field) {
        return FIELD_VALUE_MAP.get(VK_Y);
    }

    private static final long serialVersionUID = -2352130953028126954L;

    public static final int KEY_FIRST = registerField("KEY_FIRST", 400);

    public static final int KEY_LAST = registerField("KEY_LAST", 402);

    public static final int KEY_TYPED = registerField("KEY_TYPED", 400);

    public static final int KEY_PRESSED = registerField("KEY_PRESSED", 401);

    public static final int KEY_RELEASED = registerField("KEY_RELEASED", 402);

    public static final int VK_ENTER = registerField("VK_ENTER", 10);

    public static final int VK_BACK_SPACE = registerField("VK_BACK_SPACE", 8);

    public static final int VK_TAB = registerField("VK_TAB", 9);

    public static final int VK_CANCEL = registerField("VK_CANCEL", 3);

    public static final int VK_CLEAR = registerField("VK_CLEAR", 12);

    public static final int VK_SHIFT = registerField("VK_SHIFT", 16);

    public static final int VK_CONTROL = registerField("VK_CONTROL", 17);

    public static final int VK_ALT = registerField("VK_ALT", 18);

    public static final int VK_PAUSE = registerField("VK_PAUSE", 19);

    public static final int VK_CAPS_LOCK = registerField("VK_CAPS_LOCK", 20);

    public static final int VK_ESCAPE = registerField("VK_ESCAPE ", 27);

    public static final int VK_SPACE = registerField("VK_SPACE", 32);

    public static final int VK_PAGE_UP = registerField("VK_PAGE_UP", 33);

    public static final int VK_PAGE_DOWN = registerField("VK_PAGE_DOWN", 34);

    public static final int VK_END = registerField("VK_END", 35);

    public static final int VK_HOME = registerField("VK_HOME", 36);

    public static final int VK_LEFT = registerField("VK_LEFT", 37);

    public static final int VK_UP = registerField("VK_UP", 38);

    public static final int VK_RIGHT = registerField("VK_RIGHT", 39);

    public static final int VK_DOWN = registerField("VK_DOWN", 40);

    public static final int VK_COMMA = registerField("VK_COMMA", 44);

    public static final int VK_MINUS = registerField("VK_MINUS", 45);

    public static final int VK_PERIOD = registerField("VK_PERIOD", 46);

    public static final int VK_SLASH = registerField("VK_SLASH", 47);

    public static final int VK_0 = registerField("VK_0", 48);

    public static final int VK_1 = registerField("VK_1", 49);

    public static final int VK_2 = registerField("VK_2", 50);

    public static final int VK_3 = registerField("VK_3", 51);

    public static final int VK_4 = registerField("VK_4", 52);

    public static final int VK_5 = registerField("VK_5", 53);

    public static final int VK_6 = registerField("VK_6", 54);

    public static final int VK_7 = registerField("VK_7", 55);

    public static final int VK_8 = registerField("VK_8", 56);

    public static final int VK_9 = registerField("VK_9", 57);

    public static final int VK_SEMICOLON = registerField("VK_SEMICOLON", 59);

    public static final int VK_EQUALS = registerField("VK_EQUALS", 61);

    public static final int VK_A = registerField("VK_A", 65);

    public static final int VK_B = registerField("VK_B", 66);

    public static final int VK_C = registerField("VK_C", 67);

    public static final int VK_D = registerField("VK_D", 68);

    public static final int VK_E = registerField("VK_E", 69);

    public static final int VK_F = registerField("VK_F", 70);

    public static final int VK_G = registerField("VK_G", 71);

    public static final int VK_H = registerField("VK_H", 72);

    public static final int VK_I = registerField("VK_I", 73);

    public static final int VK_J = registerField("VK_J", 74);

    public static final int VK_K = registerField("VK_K", 75);

    public static final int VK_L = registerField("VK_L", 76);

    public static final int VK_M = registerField("VK_M", 77);

    public static final int VK_N = registerField("VK_N", 78);

    public static final int VK_O = registerField("VK_O", 79);

    public static final int VK_P = registerField("VK_P", 80);

    public static final int VK_Q = registerField("VK_Q", 81);

    public static final int VK_R = registerField("VK_R", 82);

    public static final int VK_S = registerField("VK_S", 83);

    public static final int VK_T = registerField("VK_T", 84);

    public static final int VK_U = registerField("U", 85);

    public static final int VK_V = registerField("VK_V", 86);

    public static final int VK_W = registerField("VK_W", 87);

    public static final int VK_X = registerField("VK_X", 88);

    public static final int VK_Y = registerField("VK_Y", 89);

    public static final int VK_Z = registerField("VK_Z", 90);

    public static final int VK_OPEN_BRACKET = registerField("VK_OPEN_BRACKET", 91);

    public static final int VK_BACK_SLASH = registerField("VK_BACK_SLASH", 92);

    public static final int VK_CLOSE_BRACKET = registerField("VK_CLOSE_BRACKET", 93);

    public static final int VK_NUMPAD0 = registerField("VK_NUMPAD0", 96);

    public static final int VK_NUMPAD1 = registerField("VK_NUMPAD1", 97);

    public static final int VK_NUMPAD2 = registerField("VK_NUMPAD2", 98);

    public static final int VK_NUMPAD3 = registerField("VK_NUMPAD3", 99);

    public static final int VK_NUMPAD4 = registerField("VK_NUMPAD4", 100);

    public static final int VK_NUMPAD5 = registerField("VK_NUMPAD5", 101);

    public static final int VK_NUMPAD6 = registerField("VK_NUMPAD6", 102);

    public static final int VK_NUMPAD7 = registerField("VK_NUMPAD7", 103);

    public static final int VK_NUMPAD8 = registerField("VK_NUMPAD8", 104);

    public static final int VK_NUMPAD9 = registerField("VK_NUMPAD9", 105);

    public static final int VK_MULTIPLY = registerField("VK_MULTIPLY", 106);

    public static final int VK_ADD = registerField("VK_ADD", 107);

    public static final int VK_SEPARATER = registerField("VK_SEPARATER", 108);

    public static final int VK_SEPARATOR = registerField("VK_SEPARATOR", 108);

    public static final int VK_SUBTRACT = registerField("VK_SUBTRACT", 109);

    public static final int VK_DECIMAL = registerField("VK_DECIMAL", 110);

    public static final int VK_DIVIDE = registerField("VK_DIVIDE", 111);

    public static final int VK_DELETE = registerField("VK_DELETE", 127);

    public static final int VK_NUM_LOCK = registerField("VK_NUM_LOCK", 144);

    public static final int VK_SCROLL_LOCK = registerField("VK_SCROLL_LOCK", 145);

    public static final int VK_F1 = registerField("VK_F1", 112);

    public static final int VK_F2 = registerField("VK_F2", 113);

    public static final int VK_F3 = registerField("VK_F3", 114);

    public static final int VK_F4 = registerField("VK_F4", 115);

    public static final int VK_F5 = registerField("VK_F5", 116);

    public static final int VK_F6 = registerField("VK_F6", 117);

    public static final int VK_F7 = registerField("VK_F7", 118);

    public static final int VK_F8 = registerField("VK_F8", 119);

    public static final int VK_F9 = registerField("VK_F9", 120);

    public static final int VK_F10 = registerField("VK_F10", 121);

    public static final int VK_F11 = registerField("VK_F11", 122);

    public static final int VK_F12 = registerField("VK_F12", 123);

    public static final int VK_F13 = registerField("VK_F13", 61440);

    public static final int VK_F14 = registerField("VK_F14", 61441);

    public static final int VK_F15 = registerField("VK_F15", 61442);

    public static final int VK_F16 = registerField("VK_F16", 61443);

    public static final int VK_F17 = registerField("VK_F17", 61444);

    public static final int VK_F18 = registerField("VK_F18", 61445);

    public static final int VK_F19 = registerField("VK_F19", 61446);

    public static final int VK_F20 = registerField("VK_F20", 61447);

    public static final int VK_F21 = registerField("VK_F21", 61448);

    public static final int VK_F22 = registerField("VK_F22", 61449);

    public static final int VK_F23 = registerField("VK_F23", 61450);

    public static final int VK_F24 = registerField("VK_F24", 61451);

    public static final int VK_PRINTSCREEN = registerField("VK_PRINTSCREEN", 154);

    public static final int VK_INSERT = registerField("VK_INSERT", 155);

    public static final int VK_HELP = registerField("VK_HELP", 156);

    public static final int VK_META = registerField("VK_META", 157);

    public static final int VK_BACK_QUOTE = registerField("VK_BACK_QUOTE", 192);

    public static final int VK_QUOTE = registerField("VK_QUOTE", 222);

    public static final int VK_KP_UP = registerField("VK_KP_UP", 224);

    public static final int VK_KP_DOWN = registerField("VK_KP_DOWN", 225);

    public static final int VK_KP_LEFT = registerField("VK_KP_LEFT", 226);

    public static final int VK_KP_RIGHT = registerField("VK_KP_RIGHT", 227);

    public static final int VK_DEAD_GRAVE = registerField("VK_DEAD_GRAVE", 128);

    public static final int VK_DEAD_ACUTE = registerField("VK_DEAD_ACUTE", 129);

    public static final int VK_DEAD_CIRCUMFLEX = registerField("VK_DEAD_CIRCUMFLEX", 130);

    public static final int VK_DEAD_TILDE = registerField("VK_DEAD_TILDE", 131);

    public static final int VK_DEAD_MACRON = registerField("VK_DEAD_MACRON", 132);

    public static final int VK_DEAD_BREVE = registerField("VK_DEAD_BREVE", 133);

    public static final int VK_DEAD_ABOVEDOT = registerField("VK_DEAD_ABOVEDOT", 134);

    public static final int VK_DEAD_DIAERESIS = registerField("VK_DEAD_DIAERESIS", 135);

    public static final int VK_DEAD_ABOVERING = registerField("VK_DEAD_ABOVERING", 136);

    public static final int VK_DEAD_DOUBLEACUTE = registerField("VK_DEAD_DOUBLEACUTE", 137);

    public static final int VK_DEAD_CARON = registerField("VK_DEAD_CARON", 138);

    public static final int VK_DEAD_CEDILLA = registerField("VK_DEAD_CEDILLA", 139);

    public static final int VK_DEAD_OGONEK = registerField("VK_DEAD_OGONEK", 140);

    public static final int VK_DEAD_IOTA = registerField("VK_DEAD_IOTA", 141);

    public static final int VK_DEAD_VOICED_SOUND = registerField("VK_DEAD_VOICED_SOUND", 142);

    public static final int VK_DEAD_SEMIVOICED_SOUND = registerField("VK_DEAD_SEMIVOICED_SOUND", 143);

    public static final int VK_AMPERSAND = registerField("VK_AMPERSAND", 150);

    public static final int VK_ASTERISK = registerField("VK_ASTERISK", 151);

    public static final int VK_QUOTEDBL = registerField("VK_QUOTEDBL", 152);

    public static final int VK_LESS = registerField("VK_LESS", 153);

    public static final int VK_GREATER = registerField("", 160);

    public static final int VK_BRACELEFT = registerField("VK_BRACELEFT", 161);

    public static final int VK_BRACERIGHT = registerField("VK_BRACERIGHT", 162);

    public static final int VK_AT = registerField("VK_AT", 512);

    public static final int VK_COLON = registerField("VK_COLON", 513);

    public static final int VK_CIRCUMFLEX = registerField("VK_CIRCUMFLEX", 514);

    public static final int VK_DOLLAR = registerField("VK_DOLLAR", 515);

    public static final int VK_EURO_SIGN = registerField("VK_EURO_SIGN", 516);

    public static final int VK_EXCLAMATION_MARK = registerField("VK_EXCLAMATION_MARK", 517);

    public static final int VK_INVERTED_EXCLAMATION_MARK = registerField("VK_INVERTED_EXCLAMATION_MARK", 518);

    public static final int VK_LEFT_PARENTHESIS = registerField("VK_LEFT_PARENTHESIS", 519);

    public static final int VK_NUMBER_SIGN = registerField("VK_NUMBER_SIGN", 520);

    public static final int VK_PLUS = registerField("VK_PLUS", 521);

    public static final int VK_RIGHT_PARENTHESIS = registerField("VK_RIGHT_PARENTHESIS", 522);

    public static final int VK_UNDERSCORE = registerField("VK_UNDERSCORE", 523);

    public static final int VK_FINAL = registerField("VK_FINAL", 24);

    public static final int VK_WINDOWS = registerField("VK_WINDOWS", 524); 

    public static final int VK_CONTEXT_MENU = registerField("VK_CONTEXT_MENU", 525);

    public static final int VK_CONVERT = registerField("VK_CONVERT", 28);

    public static final int VK_NONCONVERT = registerField("VK_NONCONVERT", 29);

    public static final int VK_ACCEPT = registerField("VK_ACCEPT", 30);

    public static final int VK_MODECHANGE = registerField("VK_MODECHANGE", 31);

    public static final int VK_KANA = registerField("VK_KANA", 21);

    public static final int VK_KANJI = registerField("VK_KANJI", 25);

    public static final int VK_ALPHANUMERIC = registerField("VK_ALPHANUMERIC", 240);

    public static final int VK_KATAKANA = registerField("VK_KATAKANA", 241);

    public static final int VK_HIRAGANA = registerField("VK_HIRAGANA", 242);

    public static final int VK_FULL_WIDTH = registerField("VK_FULL_WIDTH", 243);

    public static final int VK_HALF_WIDTH = registerField("VK_HALF_WIDTH", 244);

    public static final int VK_ROMAN_CHARACTERS = registerField("VK_ROMAN_CHARACTERS", 245);

    public static final int VK_ALL_CANDIDATES = registerField("VK_ALL_CANDIDATES", 256);

    public static final int VK_PREVIOUS_CANDIDATE = registerField("VK_PREVIOUS_CANDIDATE", 257);

    public static final int VK_CODE_INPUT = registerField("VK_CODE_INPUT", 258);

    public static final int VK_JAPANESE_KATAKANA = registerField("VK_JAPANESE_KATAKANA", 259);

    public static final int VK_JAPANESE_HIRAGANA = registerField("VK_JAPANESE_HIRAGANA", 260);

    public static final int VK_JAPANESE_ROMAN = registerField("VK_JAPANESE_ROMAN", 261);

    public static final int VK_KANA_LOCK = registerField("VK_KANA_LOCK", 262);

    public static final int VK_INPUT_METHOD_ON_OFF = registerField("VK_INPUT_METHOD_ON_OFF", 263);

    public static final int VK_CUT = registerField("VK_CUT", 65489);

    public static final int VK_COPY = registerField("VK_COPY", 65485);

    public static final int VK_PASTE = registerField("VK_PASTE", 65487);

    public static final int VK_UNDO = registerField("VK_UNDO", 65483);

    public static final int VK_AGAIN = registerField("VK_AGAIN", 65481);

    public static final int VK_FIND = registerField("VK_FIND", 65488);

    public static final int VK_PROPS = registerField("VK_PROPS", 65482);

    public static final int VK_STOP = registerField("VK_STOP", 65480);

    public static final int VK_COMPOSE = registerField("VK_COMPOSE", 65312);

    public static final int VK_ALT_GRAPH = registerField("VK_ALT_GRAPH", 65406);

    public static final int VK_BEGIN = registerField("VK_BEGIN", 65368);

    public static final int VK_UNDEFINED = registerField("VK_UNDEFINED", 0);

    public static final char CHAR_UNDEFINED = (char) registerField("CHAR_UNDEFINED", -1);

    public static final int KEY_LOCATION_UNKNOWN = registerField("KEY_LOCATION_UNKNOWN", 0);

    public static final int KEY_LOCATION_STANDARD = registerField("KEY_LOCATION_STANDARD", 1);

    public static final int KEY_LOCATION_LEFT = registerField("KEY_LOCATION_LEFT", 2);

    public static final int KEY_LOCATION_RIGHT = registerField("KEY_LOCATION_RIGHT", 3); 

    public static final int KEY_LOCATION_NUMPAD = registerField("KEY_LOCATION_NUMPAD", 4);

    private int keyCode;
    private char keyChar;
    private int keyLocation;

    public static String getKeyModifiersText(int modifiers) {
        return getKeyModifiersExText(extractModifiers(modifiers));
    }

    static String getKeyModifiersExText(int modifiersEx) {
        String text = ""; //$NON-NLS-1$

        if ((modifiersEx & TInputEvent.META_DOWN_MASK) != 0) {
            text += TToolkit.getProperty("AWT.meta", "Meta"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if ((modifiersEx & TInputEvent.CTRL_DOWN_MASK) != 0) {
            text += ((text.length() > 0) ? "+" : "") + //$NON-NLS-1$ //$NON-NLS-2$
                    TToolkit.getProperty("AWT.control", "Ctrl"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if ((modifiersEx & TInputEvent.ALT_DOWN_MASK) != 0) {
            text += ((text.length() > 0) ? "+" : "") + //$NON-NLS-1$ //$NON-NLS-2$
                    TToolkit.getProperty("AWT.alt", "Alt"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if ((modifiersEx & TInputEvent.SHIFT_DOWN_MASK) != 0) {
            text += ((text.length() > 0) ? "+" : "") + //$NON-NLS-1$ //$NON-NLS-2$
                    TToolkit.getProperty("AWT.shift", "Shift"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if ((modifiersEx & TInputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
            text += ((text.length() > 0) ? "+" : "") + //$NON-NLS-1$ //$NON-NLS-2$
                    TToolkit.getProperty("AWT.altGraph", "Alt Graph"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return text;
    }

    public static String getKeyText(int keyCode) {
        String[] rawName = getPublicStaticFinalIntFieldName(keyCode); //$NON-NLS-1$

        if ((rawName == null) || (rawName.length == 0)) {
            return ("Unknown keyCode: " + (keyCode >= 0 ? "0x" : "-0x") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    Integer.toHexString(Math.abs(keyCode)));
        }

        String propertyName = getPropertyName(rawName);
        String defaultName = getDefaultName(rawName);

        return TToolkit.getProperty(propertyName, defaultName);
    }

    private static String getDefaultName(String[] rawName) {
        String name = ""; //$NON-NLS-1$

        for (int i = 0; true; i++) {
            String part = rawName[i];

            name += new String(new char[] {part.charAt(0)}).toUpperCase() +
                    part.substring(1).toLowerCase();

            if (i == (rawName.length - 1)) {
                break;
            }
            name += " "; //$NON-NLS-1$
        }

        return name;
    }

    private static String getPropertyName(String[] rawName) {
        String name = rawName[0].toLowerCase();

        for (int i = 1; i < rawName.length; i++) {
            String part = rawName[i];

            name += new String(new char[] {part.charAt(0)}).toUpperCase() +
                    part.substring(1).toLowerCase();
        }

        return ("AWT." + name); //$NON-NLS-1$
    }

    private static String[] getPublicStaticFinalIntFieldName(int value) {
//        Field[] allFields = TKeyEvent.class.getDeclaredFields();
//
//        try {
//            for (Field field : allFields) {
//                Class<?> ssalc = field.getType();
//                int modifiers = field.getModifiers();
//
//                if (ssalc.isPrimitive() && ssalc.getName().equals("int") && //$NON-NLS-1$
//                        Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) &&
//                        Modifier.isStatic(modifiers))
//                {
//                    if (field.getInt(null) == value){
//                        final String name = field.getName();
//                        final int prefixLength = name.indexOf("_") + 1;
//                        return name.substring(prefixLength).split("_"); //$NON-NLS-1$
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        return null;
    }

    @Deprecated
    public TKeyEvent(TComponent src, int id,
                    long when, int modifiers,
                    int keyCode) {
        this(src, id, when, modifiers, keyCode,
                (keyCode > (2 << 7) - 1) ? CHAR_UNDEFINED : (char) keyCode);
    }

    public TKeyEvent(TComponent src, int id,
                    long when, int modifiers,
                    int keyCode, char keyChar) {
        this(src, id, when, modifiers, keyCode, keyChar, KEY_LOCATION_UNKNOWN);
    }

    public TKeyEvent(TComponent src, int id,
                    long when, int modifiers,
                    int keyCode, char keyChar,
                    int keyLocation) {
        super(src, id, when, extractModifiers(modifiers));

        if (id == KEY_TYPED) {
            if (keyCode != VK_UNDEFINED) {
                // awt.191=Invalid keyCode for KEY_TYPED event, must be VK_UNDEFINED
                throw new IllegalArgumentException(Messages.getString("awt.191")); //$NON-NLS-1$
            }
            if (keyChar == CHAR_UNDEFINED) {
                // awt.192=Invalid keyChar for KEY_TYPED event, can't be CHAR_UNDEFINED
                throw new IllegalArgumentException(Messages.getString("awt.192")); //$NON-NLS-1$
            }
        }
        
        if ((keyLocation < KEY_LOCATION_UNKNOWN)
                || (keyLocation > KEY_LOCATION_NUMPAD)) {
            // awt.297=Invalid keyLocation
            throw new IllegalArgumentException(Messages.getString("awt.297")); //$NON-NLS-1$
        }

        this.keyChar = keyChar;
        this.keyLocation = keyLocation;
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public void setKeyChar(char keyChar) {
        this.keyChar = keyChar;
    }

    public int getKeyLocation() {
        return keyLocation;
    }

    @Deprecated
    public void setModifiers(int modifiers) {
        this.modifiers = extractModifiers(modifiers);
    }

    public boolean isActionKey() {
        return ((keyChar == CHAR_UNDEFINED) && (keyCode != VK_UNDEFINED) &&
                !((keyCode == VK_ALT) || (keyCode == VK_ALT_GRAPH) ||
                    (keyCode == VK_CONTROL) || (keyCode == VK_META) || (keyCode == VK_SHIFT)));
    }

    @Override
    public String paramString() {
        /*
         * The format is based on 1.5 release behavior
         * which can be revealed by the following code:
         *
         * KeyEvent e = new KeyEvent(new Component() {}, 
         *       KeyEvent.KEY_PRESSED, 0, 
         *       KeyEvent.CTRL_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK, 
         *       KeyEvent.VK_A, 'A', KeyEvent.KEY_LOCATION_STANDARD);
         * System.out.println(e);
         */

        String idString = null;
        String locString = null;
        String paramString = null;
        String keyCharString = (keyChar == '\n') ?
                keyCharString = getKeyText(VK_ENTER) : "'" + keyChar + "'"; //$NON-NLS-1$ //$NON-NLS-2$
        
        if(id == KEY_PRESSED) {
            idString = "KEY_PRESSED"; //$NON-NLS-1$
        } else if(id == KEY_RELEASED) {
            idString = "KEY_RELEASED"; //$NON-NLS-1$
        } else if(id == KEY_TYPED) {
            idString = "KEY_TYPED"; //$NON-NLS-1$
        } else {
            idString = "unknown type"; //$NON-NLS-1$
        }
        
        if(keyLocation == KEY_LOCATION_STANDARD) {
            locString = "KEY_LOCATION_STANDARD"; //$NON-NLS-1$
        } else if(keyLocation == KEY_LOCATION_LEFT) {
            locString = "KEY_LOCATION_LEFT"; //$NON-NLS-1$
        } else if(keyLocation == KEY_LOCATION_RIGHT) {
            locString = "KEY_LOCATION_RIGHT"; //$NON-NLS-1$
        } else if(keyLocation == KEY_LOCATION_NUMPAD) {
            locString = "KEY_LOCATION_NUMPAD"; //$NON-NLS-1$
        } else if(keyLocation == KEY_LOCATION_UNKNOWN) {
            locString = "KEY_LOCATION_UNKNOWN"; //$NON-NLS-1$
        } else {
            locString = "unknown type"; //$NON-NLS-1$
        }

        paramString = idString + ",keyCode=" + keyCode; //$NON-NLS-1$
        if (isActionKey()) {
            paramString += "," + getKeyText(keyCode); //$NON-NLS-1$
        } else {
            paramString += ",keyChar=" + keyCharString; //$NON-NLS-1$
        }
        if (getModifiersEx() > 0) {
            paramString += ",modifiers=" + getModifiersExText(getModifiersEx()) + //$NON-NLS-1$
                    ",extModifiers=" + getModifiersExText(getModifiersEx()); //$NON-NLS-1$
        }
        paramString += ",keyLocation=" + locString; //$NON-NLS-1$

        return paramString;
    }

    private static int extractModifiers(int modifiers) {
        int mod = 0;

        if (((modifiers & SHIFT_MASK) != 0)
                || ((modifiers & SHIFT_DOWN_MASK) != 0)) {
            mod |= SHIFT_MASK | SHIFT_DOWN_MASK;
        }
        if (((modifiers & CTRL_MASK) != 0)
                || ((modifiers & CTRL_DOWN_MASK) != 0)) {
            mod |= CTRL_MASK | CTRL_DOWN_MASK;
        }
        if (((modifiers & META_MASK) != 0)
                || ((modifiers & META_DOWN_MASK) != 0)) {
            mod |= META_MASK | META_DOWN_MASK;
        }
        if (((modifiers & ALT_MASK) != 0) || ((modifiers & ALT_DOWN_MASK) != 0)) {
            mod |= ALT_MASK | ALT_DOWN_MASK;
        }
        if (((modifiers & ALT_GRAPH_MASK) != 0)
                || ((modifiers & ALT_GRAPH_DOWN_MASK) != 0)) {
            mod |= ALT_GRAPH_MASK | ALT_GRAPH_DOWN_MASK;
        }

        return mod;
    }
}
