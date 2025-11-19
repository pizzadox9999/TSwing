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
 * @author Michael Danilov, Pavel Dolgov
 */
package org.teavm.classlib.java.awt.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.harmony.awt.datatransfer.DTK;


public final class TSystemFlavorMap implements TFlavorMap, TFlavorTable {

    private static final String SERIALIZED_PREFIX = 
        "org.apache.harmony.awt.datatransfer:"; //$NON-NLS-1$

    private final HashMap<TDataFlavor, List<String>> flavor2Native = new HashMap<TDataFlavor, List<String>>();
    private final HashMap<String, List<TDataFlavor>> native2Flavor = new HashMap<String, List<TDataFlavor>>();

    public static boolean isJavaMIMEType(String str) {
        return ((str != null) && str.startsWith(SERIALIZED_PREFIX));
    }

    public static String encodeJavaMIMEType(String mimeType) {
        if (mimeType == null) {
            return null;
        }
        return (SERIALIZED_PREFIX + mimeType);
    }

    public static String decodeJavaMIMEType(String nat) {
        if (isJavaMIMEType(nat)) {
            return nat.substring(SERIALIZED_PREFIX.length());
        }
        return null;
    }

    public static String encodeDataFlavor(TDataFlavor flav) {
        if (flav == null) {
            return null;
        }
        return (SERIALIZED_PREFIX + flav.getMimeType());
    }

    public static TDataFlavor decodeDataFlavor(String nat)
            throws ClassNotFoundException {
        if (isJavaMIMEType(nat)) {
            return new TDataFlavor(nat.substring(SERIALIZED_PREFIX.length()));
        }
        return null;
    }

    public static TFlavorMap getDefaultFlavorMap() {
        DTK dtk = DTK.getDTK();

        synchronized (dtk) {
            TSystemFlavorMap flavorMap = dtk.getSystemFlavorMap();

            if (flavorMap == null) {
                flavorMap = new TSystemFlavorMap(dtk);
                dtk.setSystemFlavorMap(flavorMap);
            }

            return flavorMap;
        }
    }

    private TSystemFlavorMap(DTK dtk) {
        dtk.initSystemFlavorMap(this);
    }

    public synchronized List<TDataFlavor> getFlavorsForNative(String nat) {
        if (nat == null) {
            ArrayList<TDataFlavor> result = new ArrayList<TDataFlavor>();
            for (String key : native2Flavor.keySet()) {
                result.addAll(native2Flavor.get(key));
            }
            return result;
        }

        List<TDataFlavor> list = native2Flavor.get(nat);
        if ((list == null || list.isEmpty()) && isJavaMIMEType(nat)) {
            String decodedNat = decodeJavaMIMEType(nat);
            try {
                TDataFlavor flavor = new TDataFlavor(decodedNat);
                addMapping(nat, flavor);
                list = native2Flavor.get(nat);
            } catch (ClassNotFoundException e) {}
        }
        return (list != null) ? new ArrayList<TDataFlavor>(list) : new ArrayList<TDataFlavor>();
    }

    public synchronized List<String> getNativesForFlavor(TDataFlavor flav) {
        if (flav == null) {
            ArrayList<String> result = new ArrayList<String>();
            for (TDataFlavor key : flavor2Native.keySet()) {
                result.addAll(flavor2Native.get(key));
            }
            return result;
        }
        
        List<String> list = flavor2Native.get(flav);
        if ((list == null || list.isEmpty()) 
                && flav.isFlavorSerializedObjectType()) {
            String nat = encodeDataFlavor(flav);
            addMapping(nat, flav);
            list = flavor2Native.get(flav);
        }
        return (list != null) ? new ArrayList<String>(list) : new ArrayList<String>();
    }

    public synchronized Map<String, TDataFlavor> getFlavorsForNatives(String[] natives) {
        HashMap<String, TDataFlavor> map = new HashMap<String, TDataFlavor>();
        Iterator<String> it = (natives != null) ? 
                Arrays.asList(natives).iterator() : 
                    native2Flavor.keySet().iterator();
        while (it.hasNext()) {
            String nat = it.next();
            List<TDataFlavor> list = getFlavorsForNative(nat);
            if (list.size() > 0) {
                map.put(nat, list.get(0));
            }
        }
        return map;
    }

    public synchronized Map<TDataFlavor, String> getNativesForFlavors(TDataFlavor[] flavors) {
        HashMap<TDataFlavor, String> map = new HashMap<TDataFlavor, String>();
        Iterator<TDataFlavor> it = (flavors != null) ? 
                Arrays.asList(flavors).iterator() : 
                    flavor2Native.keySet().iterator();
        while (it.hasNext()) {
            TDataFlavor flavor = it.next();
            List<String> list = getNativesForFlavor(flavor);
            if (list.size() > 0) {
                map.put(flavor, list.get(0));
            }
        }
        return map;
    }

    public synchronized void setNativesForFlavor(
            TDataFlavor flav, String[] natives) {
        LinkedList<String> list = new LinkedList<String>();

        for (String nat : natives) {
            if (!list.contains(nat)) {
                list.add(nat);
            }
        }

        if (!list.isEmpty()) {
            flavor2Native.put(flav, list);
        } else {
            flavor2Native.remove(flav);
        }
    }

    public synchronized void setFlavorsForNative(
            String nat, TDataFlavor[] flavors) {
        LinkedList<TDataFlavor> list = new LinkedList<TDataFlavor>();

        for (TDataFlavor flav : flavors) {
            if (!list.contains(flav)) {
                list.add(flav);
            }
        }

        if (!list.isEmpty()) {
            native2Flavor.put(nat, list);
        } else {
            native2Flavor.remove(nat);
        }
    }

    public synchronized void addUnencodedNativeForFlavor(
           TDataFlavor flav, String nat) {
        List<String> natives = flavor2Native.get(flav);

        if (natives == null) {
            natives = new LinkedList<String>();
            flavor2Native.put(flav, natives);
        }
        if (!natives.contains(nat)) {
            natives.add(nat);
        }
    }

    public synchronized void addFlavorForUnencodedNative(
            String nat, TDataFlavor flav) {
        List<TDataFlavor> flavors = native2Flavor.get(nat);

        if (flavors == null) {
            flavors = new LinkedList<TDataFlavor>();
            native2Flavor.put(nat, flavors);
        }
        if (!flavors.contains(flav)) {
            flavors.add(flav);
        }
    }

    private void addMapping(String nat, TDataFlavor flav) {
        addUnencodedNativeForFlavor(flav, nat);
        addFlavorForUnencodedNative(nat, flav);
    }
}
