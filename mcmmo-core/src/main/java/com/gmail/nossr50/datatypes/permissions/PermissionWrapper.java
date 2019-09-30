package com.gmail.nossr50.datatypes.permissions;

import java.util.Objects;

public class PermissionWrapper {
    private String permissionAddress;
    private boolean playerDefault;
    private boolean operatorDefault;

    public PermissionWrapper(String permissionAddress) {
        this.permissionAddress = permissionAddress;
        this.playerDefault = true;
        this.operatorDefault = true;
    }

    public PermissionWrapper(String permissionAddress, boolean playerDefault, boolean operatorDefault) {
        this.permissionAddress = permissionAddress;
        this.playerDefault = playerDefault;
        this.operatorDefault = operatorDefault;
    }

    public boolean isPlayerDefault() {
        return playerDefault;
    }

    public void setPlayerDefault(boolean playerDefault) {
        this.playerDefault = playerDefault;
    }

    public boolean isOperatorDefault() {
        return operatorDefault;
    }

    public void setOperatorDefault(boolean operatorDefault) {
        this.operatorDefault = operatorDefault;
    }

    public String getPermissionAddress() {
        return permissionAddress;
    }

    public void setPermissionAddress(String permissionAddress) {
        this.permissionAddress = permissionAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionWrapper)) return false;
        PermissionWrapper that = (PermissionWrapper) o;
        return getPermissionAddress().equals(that.getPermissionAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPermissionAddress());
    }
}
