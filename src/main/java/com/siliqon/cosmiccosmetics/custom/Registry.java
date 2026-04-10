package com.siliqon.cosmiccosmetics.custom;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Registry<T, U> {
    protected CosmeticsPlugin getPlugin() {
        return CosmeticsPlugin.getInstance();
    }

    protected Map<T, U> registryMap = new HashMap<>();

    public Map<T, U> getRegistry() {
        return Collections.unmodifiableMap(registryMap);
    }

    public void register(T key, U value) {
        registryMap.put(key, value);
    }

    public U get(T key) {
        return registryMap.get(key);
    }
}
