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
package org.teavm.classlib.java.awt;

import java.io.Serializable;
import java.util.Hashtable;

import org.apache.harmony.awt.internal.nls.Messages;

public class TCardLayout implements TLayoutManager2, Serializable {
    private static final long serialVersionUID = -4328196481005934313L;

    private static final int DEFAULT_GAP = 0;

    private int vGap;
    private int hGap;

    private Hashtable<String, TComponent> nameTable;        //Name to component
    private Hashtable<TComponent, String> compTable;        //TComponent to name
    private int curTComponent;

    private final TToolkit toolkit = TToolkit.getDefaultToolkit();

    public TCardLayout(int hgap, int vgap) {
        toolkit.lockAWT();
        try {
            vGap = vgap;
            hGap = hgap;

            nameTable = new Hashtable<String, TComponent>();
            compTable = new Hashtable<TComponent, String>();
            curTComponent = 0;
        } finally {
            toolkit.unlockAWT();
        }
    }

    public TCardLayout() {
        this(DEFAULT_GAP, DEFAULT_GAP);
        toolkit.lockAWT();
        try {
        } finally {
            toolkit.unlockAWT();
        }
    }

    @Override
    public String toString() {
        /* The format is based on 1.5 release behavior 
         * which can be revealed by the following code:
         * System.out.println(new CardLayout());
         */

        toolkit.lockAWT();
        try {
            return getClass().getName() + "[hgap=" + hGap + ",vgap=" +vGap + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } finally {
            toolkit.unlockAWT();
        }
    }

    public int getHgap() {
        toolkit.lockAWT();
        try {
            return hGap;
        } finally {
            toolkit.unlockAWT();
        }
    }

    public int getVgap() {
        toolkit.lockAWT();
        try {
            return vGap;
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void setHgap(int hgap) {
        toolkit.lockAWT();
        try {
            hGap = hgap;
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void setVgap(int vgap) {
        toolkit.lockAWT();
        try {
            vGap = vgap;
        } finally {
            toolkit.unlockAWT();
        }
    }

    public float getLayoutAlignmentX(TContainer parent) {
        toolkit.lockAWT();
        try {
            return TComponent.CENTER_ALIGNMENT;
        } finally {
            toolkit.unlockAWT();
        }
    }

    public float getLayoutAlignmentY(TContainer parent) {
        toolkit.lockAWT();
        try {
            return TComponent.CENTER_ALIGNMENT;
        } finally {
            toolkit.unlockAWT();
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void addLayoutTComponent(String name, TComponent comp) {
        toolkit.lockAWT();
        try {
            if (name == null) {
                if (compTable.get(comp) != null) {
                    return;
                }
                name = comp.toString();
            }            

            if (!nameTable.isEmpty()){
                comp.setVisible(false);
            }
            nameTable.put(name, comp);
            compTable.put(comp, name);
            
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void addLayoutTComponent(TComponent comp, Object constraints) {
        toolkit.lockAWT();
        try {
            if (!String.class.isInstance(constraints)) {
                // awt.131=AddLayoutTComponent: constraint object must be String
                throw new IllegalArgumentException(Messages.getString("awt.131")); //$NON-NLS-1$
            }
            addLayoutTComponent((String) constraints, comp);
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void removeLayoutTComponent(TComponent comp) {
        toolkit.lockAWT();
        try {
            if (!compTable.containsKey(comp)) {
                return;
            }
            TContainer parent = comp.getParent();
            if (parent != null) {
                int idx = parent.getTComponentZOrder(comp);
                if (idx == curTComponent) {
                    next(parent);
                }
            }

            String name = compTable.get(comp);
            if (name != null) {
                nameTable.remove(name);
            }
            compTable.remove(comp);
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void invalidateLayout(TContainer target) {
        toolkit.lockAWT();
        try {
            //Nothing to invalidate
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void layoutTContainer(TContainer parent) {
        toolkit.lockAWT();
        try {
            if (parent.getTComponentCount() == 0) {
                return;
            }

            showCurrent(parent);
        } finally {
            toolkit.unlockAWT();
        }
    }

    private void showCurrent(TContainer parent) {
        toolkit.lockAWT();
        try {
            if (curTComponent >= parent.getTComponentCount()) {
                curTComponent = 0;
            }
            Rectangle clientRect = parent.getClient();
            TComponent comp = parent.getTComponent(curTComponent);
            Rectangle bounds = new Rectangle(clientRect.x + hGap, 
                                             clientRect.y + vGap, 
                                             clientRect.width - 2 * hGap, 
                                             clientRect.height - 2 * vGap);

            comp.setBounds(bounds);
            comp.setVisible(true);

        } finally {
            toolkit.unlockAWT();
        }
    }

    public void first(TContainer parent) {
        toolkit.lockAWT();
        try {
            check(parent);
            int size = parent.getTComponentCount(); 
            if (size == 0) {
                return;
            }

            hideCurrent(parent);
            curTComponent = 0;

            showCurrent(parent);
        } finally {
            toolkit.unlockAWT();
        }
    }

    private void hideCurrent(TContainer parent) {
        if ((curTComponent >= 0) && (curTComponent < parent.getTComponentCount())) {
            parent.getTComponent(curTComponent).setVisible(false);
        }
    }

    private void check(TContainer parent) {
        if (parent.getLayout() != this) {
            // awt.132=wrong parent for CardLayout
            throw new IllegalArgumentException(Messages.getString("awt.132")); //$NON-NLS-1$
        }
    }

    public void last(TContainer parent) {
        toolkit.lockAWT();
        try {
            check(parent);
            int size = parent.getTComponentCount(); 
            if ( size == 0) {
                return;
            }

            hideCurrent(parent);
            curTComponent = size - 1;

            showCurrent(parent);
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void next(TContainer parent) {
        toolkit.lockAWT();
        try {
            check(parent);
            int size = parent.getTComponentCount(); 
            if ( size == 0) {
                return;
            }

            hideCurrent(parent);
            curTComponent++;
            if (curTComponent >= size) {
                curTComponent = 0;
            }

            showCurrent(parent);
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void previous(TContainer parent) {
        toolkit.lockAWT();
        try {
            check(parent);
            int size = parent.getTComponentCount(); 
            if ( size == 0) {
                return;
            }

            hideCurrent(parent);
            curTComponent --;
            if (curTComponent < 0) {
                curTComponent = size - 1;
            }

            showCurrent(parent);
        } finally {
            toolkit.unlockAWT();
        }
    }

    public void show(TContainer parent, String name) {
        toolkit.lockAWT();
        try {
            check(parent);
            int size = parent.getTComponentCount();
            if (size == 0) {
                return;
            }

            TComponent comp = nameTable.get(name);

            if (comp == null) {
                return;
            }

            hideCurrent(parent);
            curTComponent = parent.getTComponentZOrder(comp);
            showCurrent(parent);
        } finally {
            toolkit.unlockAWT();
        }
    }

    public TDimension maximumLayoutSize(TContainer target) {
        toolkit.lockAWT();
        try {
            return new TDimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        } finally {
            toolkit.unlockAWT();
        }
    }

    public TDimension minimumLayoutSize(TContainer parent) {
        toolkit.lockAWT();
        try {
            if (parent.getTComponentCount() == 0) {
                return parent.addInsets(new TDimension(0, 0));
            }

            return parent.addInsets(layoutSize(parent, false));
        } finally {
            toolkit.unlockAWT();
        }
    }

    public TDimension preferredLayoutSize(TContainer parent) {
        toolkit.lockAWT();
        try {
            if (parent.getTComponentCount() == 0) {
                return parent.addInsets(new TDimension(0, 0));
            }

            return parent.addInsets(layoutSize(parent, true));
        } finally {
            toolkit.unlockAWT();
        }
    }

    private TDimension layoutSize(TContainer parent, boolean preferred) {
        int maxWidth = 0;
        int maxHeight = 0;

        for (int i = 0; i < parent.getTComponentCount(); i++) {
            TComponent comp = parent.getTComponent(i); 
            TDimension compSize = (preferred ? comp.getPreferredSize() :
                                              comp.getMinimumSize());

            maxWidth = Math.max(maxWidth, compSize.width);
            maxHeight = Math.max(maxHeight, compSize.height);
        }

        return new TDimension(maxWidth + 2 * hGap, maxHeight + 2 * vGap);
    }

}
