package com.gmail.nossr50.database.queuemanager;

public interface Queueable {      
        public void run();
        public String getPlayer();
}
