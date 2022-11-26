package com.systelab.seed;

import com.systelab.seed.core.audit.AuditRevisionListener;
import org.hibernate.envers.boot.internal.LegacyModifiedColumnNamingStrategy;
import org.hibernate.envers.configuration.internal.ClassesAuditingData;
import org.hibernate.envers.internal.EnversMessageLogger;
import org.hibernate.envers.strategy.DefaultAuditStrategy;
import org.hibernate.tuple.CreationTimestampGeneration;
import org.hibernate.tuple.UpdateTimestampGeneration;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ReflectionUtils;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection()
                .registerConstructor(
                        CreationTimestampGeneration.class.getConstructors()[0], ExecutableMode.INVOKE);

        hints.resources().registerPattern("keystore.p12");
    }
}