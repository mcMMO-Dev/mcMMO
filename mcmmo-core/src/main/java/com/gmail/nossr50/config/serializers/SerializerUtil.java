package com.gmail.nossr50.config.serializers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class SerializerUtil {

    /**
     * Adds a comment to a Configuration node if its possible
     * @param configurationNode target configuration node
     * @param comment desired comment
     */
    public static void addCommentIfCompatible(ConfigurationNode configurationNode, String comment) {
        if(configurationNode instanceof CommentedConfigurationNode) {
            CommentedConfigurationNode node = (CommentedConfigurationNode) configurationNode;
            node.setComment(comment);
        }
    }
}
