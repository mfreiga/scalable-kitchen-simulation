package de.tum.in.ase.eist;

import java.util.ArrayList;
import java.util.List;

// IMPORTANT: Please don't change anything in this class.
public class LoggingReadWriteLock extends ReadWriteLock {

    // List to store logged function calls
    private List<String> functionCallLog;

    public LoggingReadWriteLock() {
        // Initialize the log list
        functionCallLog = new ArrayList<>();
    }

    @Override
    public synchronized void lockRead() {
        // Log the function call
        functionCallLog.add("lr");
        super.lockRead();
    }

    @Override
    public synchronized void unlockRead() {
        // Log the function call
        functionCallLog.add("ur");
        super.unlockRead();
    }

    @Override
    public synchronized void lockWrite() {
        // Log the function call
        functionCallLog.add("lw");
        super.lockWrite();
    }

    @Override
    public synchronized void unlockWrite() {
        // Log the function call
        functionCallLog.add("uw");
        super.unlockWrite();
    }


    // Additional method to retrieve the function call log
    public List<String> getFunctionCallLog() {
        return functionCallLog;
    }
}
