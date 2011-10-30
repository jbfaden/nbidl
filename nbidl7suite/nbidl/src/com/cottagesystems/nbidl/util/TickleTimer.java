/*
 * TickleTimer.java
 *
 * Created on July 28, 2006, 9:23 PM
 *
 */

package com.cottagesystems.nbidl.util;

import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TickleTimer is a timer that fires once it's been left alone for 
 * a while.  The idea is the keyboard can be pecked away and 
 * the change event will not be fired until the keyboard is idle.
 *
 * @author Jeremy Faden
 */
public class TickleTimer {
    long tickleTime;
    long delay;
    ChangeListener listener;
    boolean running;
    
    static final Logger log= Logger.getLogger("pvwave");
    
    /**
     * @param delay time in milliseconds to wait until firing off the change.
     */
    public TickleTimer( long delay, ChangeListener listener ) {
        this.tickleTime= System.currentTimeMillis();
        this.delay= delay;
        this.listener= listener;
        this.running= false;
    }
    private void startTimer() {
        running= true;
        new Thread( newRunnable() ).start();
    }
    
    private Runnable newRunnable() {
        return new Runnable() {
            public void run() {
                long d=  System.currentTimeMillis() - tickleTime;
                while ( d < delay ) {
                    try {
                        log.finer("tickleTimer sleep "+(delay-d));
                        Thread.sleep( delay-d );
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    d= System.currentTimeMillis() - tickleTime;
                }
                log.finer("tickleTimer fire after "+(d));
                listener.stateChanged( new ChangeEvent(this) );
                running= false;
            }
        };
    }
    
    public synchronized void tickle(){
        tickleTime= System.currentTimeMillis();
        if ( !running ) startTimer();
    }
}
