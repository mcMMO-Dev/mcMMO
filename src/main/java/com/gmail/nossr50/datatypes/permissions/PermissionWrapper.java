package com.gmail.nossr50.datatypes.permissions;

import java.util.Objects;

public class PermissionWrapper {
    private String permissionAddress;

    public PermissionWrapper(String permissionAddress) {
        this.permissionAddress = permissionAddress;
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
