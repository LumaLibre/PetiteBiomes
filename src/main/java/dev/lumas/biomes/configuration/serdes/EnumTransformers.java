package dev.lumas.biomes.configuration.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import lombok.NonNull;

import java.util.Locale;

public final class EnumTransformers {

    private EnumTransformers() {}

    public static <E extends Enum<E>> BidirectionalTransformer<String, E> lowercase(Class<E> enumClass) {
        return new BidirectionalTransformer<>() {
            @Override
            public GenericsPair<String, E> getPair() {
                return this.genericsPair(String.class, enumClass);
            }

            @Override
            public E leftToRight(@NonNull String data, @NonNull SerdesContext ctx) {
                return Enum.valueOf(enumClass, data.toUpperCase(Locale.ROOT));
            }

            @Override
            public String rightToLeft(@NonNull E data, @NonNull SerdesContext ctx) {
                return data.name().toLowerCase(Locale.ROOT);
            }
        };
    }
}