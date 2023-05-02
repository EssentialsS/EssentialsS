package org.essentialss.implementation.config.loaders;

import org.spongepowered.configurate.serialize.TypeSerializer;

public interface TypeLoader<T> extends TypeSerializer<T> {

    Class<T> ofType();
}
