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
 * @author Dmitry A. Durnev
 */
package org.teavm.classlib.java.awt.im;

public final class TInputSubset extends Character.Subset {

    public static final TInputSubset LATIN = new TInputSubset("LATIN"); //$NON-NLS-1$

    public static final TInputSubset 
        LATIN_DIGITS = new TInputSubset("LATIN_DIGITS"); //$NON-NLS-1$

    public static final TInputSubset 
        TRADITIONAL_HANZI = new TInputSubset("TRADITIONAL_HANZI"); //$NON-NLS-1$

    public static final TInputSubset 
        SIMPLIFIED_HANZI = new TInputSubset("SIMPLIFIED_HANZI"); //$NON-NLS-1$

    public static final TInputSubset KANJI = new TInputSubset("KANJI"); //$NON-NLS-1$

    public static final TInputSubset HANJA = new TInputSubset("HANJA"); //$NON-NLS-1$

    public static final TInputSubset 
        HALFWIDTH_KATAKANA = new TInputSubset("HALFWIDTH_KATAKANA"); //$NON-NLS-1$

    public static final TInputSubset 
        FULLWIDTH_LATIN = new TInputSubset("FULLWIDTH_LATIN"); //$NON-NLS-1$

    public static final TInputSubset 
        FULLWIDTH_DIGITS = new TInputSubset("FULLWIDTH_DIGITS"); //$NON-NLS-1$

    private TInputSubset(String name) {
        super(name);
    }
}

