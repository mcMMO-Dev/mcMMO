package com.gmail.nossr50.config;

import java.util.Collection;

public interface ConfigCollection<T> {
    Collection<T> getLoadedCollection();
}
