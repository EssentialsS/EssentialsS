package org.essentialss.config.loaders;

import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TypeLoaders {

    public static final DataViewLoader DATA_VIEW = new DataViewLoader();
    public static final ItemStackLoader ITEM_STACK = new ItemStackLoader();

    private TypeLoaders() {
        throw new RuntimeException("should not create");
    }

    private static <T> void apply(TypeSerializerCollection.Builder builder, TypeLoader<T> loader) {
        builder.register(loader.ofType(), loader);
    }

    public static <L extends AbstractConfigurationLoader.Builder<?, ?>> L applyAll(L loaderBuilder) {
        Collection<TypeLoader<?>> serializers = serializers();
        loaderBuilder.defaultOptions(objs -> objs.serializers(builder -> {
            for (TypeLoader<?> serializer : serializers) {
                apply(builder, serializer);
            }
        }));
        return loaderBuilder;
    }

    public static Collection<TypeLoader<?>> serializers() {
        return Arrays
                .stream(TypeLoaders.class.getDeclaredFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()))
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> TypeLoader.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (TypeLoader<?>) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        //noinspection ReturnOfNull
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
