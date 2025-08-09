package de.tum.in.ase.eist;

// TODO Implement this class. Check out the LoggingReadWriteLock which extends this class while doing so.
// Tip regarding interruptedexceptions: You can just ignore them and continue waiting
/**
 * A simple ReadWriteLock implementation.
 * NOTE: this implementation does not need to be "fair" (fair == lock requests
 * are guaranteed to be handled in the order of their arrival), as requiring
 * fairness would make this task too hard.
 */
public class ReadWriteLock {
    private int activeReaders = 0;
    private boolean writerActive = false;


    public synchronized void lockRead()  {
        while (writerActive){
            try{
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
            activeReaders++;

    }

    public synchronized void unlockRead() {
        if(activeReaders > 0){
            activeReaders --;

            if(activeReaders == 0){
                notifyAll();
            }
        }

        if(activeReaders < 0){
            throw new IllegalStateException("no active readers");
        }

    }

    public synchronized void lockWrite() {
        while (writerActive || activeReaders > 0){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        writerActive = true;
    }

    public synchronized void unlockWrite() {
        if(!writerActive) {
            return;
        }
        writerActive = false;
        notifyAll();

    }
}