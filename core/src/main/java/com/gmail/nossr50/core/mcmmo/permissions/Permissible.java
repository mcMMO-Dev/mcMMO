package com.gmail.nossr50.core.mcmmo.permissions;

/**
 * A thing that can have Permissions is a Permissible
 */
public interface Permissible {
    /**
     * Returns whether or not this Permissible has this permission
     * @param path the permission nodes full path
     * @return true if the permissible has this permission
     */
    boolean hasPermission(String path);
}
