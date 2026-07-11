package com.gmail.nossr50.commands.levelup;

/**
 * Where a level up registration came from. Config reloads clear {@link #CONFIG} registrations
 * and leave {@link #API} registrations untouched.
 */
public enum RegistrationSource {
    CONFIG,
    API
}
