package com.systelab.seed.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.systelab.seed", importOptions = ImportOption.DontIncludeTests.class)
public class ServiceTest {

    @ArchTest
    static ArchRule servicesShouldNotDependOnControllers = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .because("we do not want services depending on controllers");

    @ArchTest
    static ArchRule servicesCouldOnlyBeAccessedByOtherServicesOrControllers = classes()
            .that().resideInAPackage("..service..")
            .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..")
            .because("we want services to be accessed only by controllers or other services");
}
