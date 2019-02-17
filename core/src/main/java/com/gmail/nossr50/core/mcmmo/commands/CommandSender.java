package com.gmail.nossr50.core.mcmmo.commands;

/**
 * Command senders are either Players or the CLI
 */
public interface CommandSender {

    /**
     * Send a message to the CommandSender
     * @param msg the message to send
     */
    void sendMessage(String msg);
}
