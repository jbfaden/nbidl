/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * PairMap.java
 *
 * Created on May 12, 2004, 10:11 PM
 */

package com.cottagesystems.nbidl.idloutput2;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * A synchronized LIFO map that can contain duplicate keys, and can be 
 * toggled between using weak and strong references for values.  Used for
 * mapping instances of NbIO to the names used in NbIOProvider.getIO().
 *
 * @author  Tim Boudreau
 */
class PairMap {
    //XXX weak referencing of objects no longer used - delete?

    private String [] keys = new String[10];
    /** package private for unit tests */
    Object [] vals = new Object[10];
    int last = -1;
    private boolean weak = false;
    
    /** Creates a new instance of PairMap */
    PairMap() {
    }
    
    public synchronized void clear() {
        keys = new String[10];
        vals = new Object[10];
        last = -1;
    }
    
    public synchronized int size() {
        if (weak) {
            return prune();
        } else {
            return last+1;
        }
    }
    
    public synchronized boolean isEmpty() {
        return size() == 0;
    }
    
    public synchronized void setWeak(boolean val) {
        if (weak != val) {
            weak = val;
            for (int i=0; i <= last; i++) {
                if (weak) {
                    vals[i] = new WeakReference(vals[i]);
                } else {
                    vals[i] = ((WeakReference) vals[i]).get();
                }
            }
            if (!weak) {
                prune();
            }
        }
    }
    
    public synchronized void add (String key, PvwaveIO value) {
        if (last == keys.length -1) {
            growArrays();
        }
        last++;
        keys[last] = key;
        vals[last] = value;
    }
    
    public synchronized PvwaveIO get (String key, boolean mustBeClosed) {
        if (last < 0) {
            return null;
        }
        boolean foundNull = false;
        for (int i=last; i >= 0; i--) {
            if (keys[i].equals(key)) {
                PvwaveIO io = getValue(i);
                foundNull |= io == null;
                if (io != null && (!mustBeClosed || (mustBeClosed && io.isStreamClosed()))) {
                    return io;
                }
            }
        }
        if (foundNull) {
            prune();
        }
        return null;
    }
    
    public boolean containsValue(PvwaveIO io) {
        if (last < 0) {
            return false;
        }
        for (int i=last; i >= 0; i--) {
            if (getValue(i) == io) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized PvwaveIO get (String key) {
        return get(key, false);
    }
    
    public synchronized String remove (PvwaveIO io) {
        int idx = indexOfVal (io);
        if (idx == -1) {
            return null;
        }
        String result = keys[idx];
        removeIndex(idx);
        return result;
    }
    
    public synchronized PvwaveIO remove (String key) {
        int idx = indexOfKey(key);
        if (idx == -1) {
            return null;
        }
        PvwaveIO result = getValue(idx);
        removeIndex(idx);
        return result;
    }
    
    private PvwaveIO getValue(int idx) {
        if (idx > last) {
            throw new ArrayIndexOutOfBoundsException ("Tried to fetch item " +  //NOI18N
                idx + " but map only contains " + (last + 1) + " elements");  //NOI18N
        }
        PvwaveIO result;
        Object o = vals[idx];
        if (weak) {
            result = (PvwaveIO) ((WeakReference) vals[idx]).get();
            if (result == null && !pruning) {
                removeIndex(idx);
            }

        } else {
            result = (PvwaveIO) o;
        }
        return result;
    }
    
    public void setValue (int idx, PvwaveIO value) {
        if (weak) {
            vals[idx] = new WeakReference(value);
        } else {
            vals[idx] = value;
        }
    }
    
    private void removeIndex (int idx) {
        if (idx < 0 || idx > last) {
            throw new ArrayIndexOutOfBoundsException ("Trying to remove " + //NOI18N
                "element " + idx + " but map only contains " + (last+1) +  //NOI18N
                " elements"); //NOI18N
        }
        if (idx == last) {
            keys[idx] = null;
            vals[idx] = null;
        } else {
            keys[idx] = keys[last];
            vals[idx] = vals[last];
            vals[last] = null;
            keys[last] = null;
        } 
        last--;
    }
    
    private int indexOfKey (String key) {
        for (int i=last; i >= 0; i--) {
            if (keys[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }
    
    private int indexOfVal (PvwaveIO val) {
        for (int i=last; i >= 0; i--) {
            if (vals[i] == val) {
                return i;
            }
        }
        return -1;
    }
    
    private void growArrays() {
        String[] newKeys = new String[keys.length * 2];
        PvwaveIO[] newVals = new PvwaveIO[vals.length * 2];
        System.arraycopy (keys, 0, newKeys, 0, last+1);
        System.arraycopy (vals, 0, newVals, 0, last+1);
        keys = newKeys;
        vals = newVals;
    }
    
    private boolean pruning = false;
    private int prune() {
        pruning = true;
        int oldSize = last + 1;
        int result = oldSize;
        int[] removes = new int[oldSize];
        Arrays.fill (removes, -1);
        for (int i=last; i >= 0; i--) {
            if (getValue(i) == null) {
                removes[i] = i;
                result--;
            }
        }

        if (result != oldSize) {
            for (int i=removes.length-1; i >= 0; i--) {
                if (removes[i] != -1) {
                    removeIndex(removes[i]);
                }
            }
        }
        pruning = false;
        return result;
    }
    
}
