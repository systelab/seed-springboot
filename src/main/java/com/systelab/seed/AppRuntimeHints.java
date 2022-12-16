package com.systelab.seed;

import org.hibernate.tuple.CreationTimestampGeneration;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection()
                .registerConstructor(
                        CreationTimestampGeneration.class.getConstructors()[0], ExecutableMode.INVOKE);

        hints.resources().registerPattern("keystore.p12");
    }
}