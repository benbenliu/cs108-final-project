package edu.stanford.cs108.bunnyworld;

/**
 * Created by vinyao on 17/11/23.
 */

class mySingleton {
    private static final mySingleton ourInstance = new mySingleton();
    public Document savedDocument;
    public PageView currentPageView;

    static mySingleton getInstance() {
        return ourInstance;
    }

    private mySingleton() {
    }
}
