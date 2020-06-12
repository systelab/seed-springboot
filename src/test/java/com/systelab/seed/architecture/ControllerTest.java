package com.systelab.seed.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.systelab.architecture.rules.ClassesTransformers.methods;
import static com.systelab.architecture.rules.methods.MethodConditionUtilities.beAnnotated;
import static com.systelab.architecture.rules.methods.MethodConditionUtilities.returnType;
import static com.systelab.architecture.rules.methods.MethodPredicateUtilities.arePublic;
import static com.systelab.architecture.rules.methods.MethodPredicateUtilities.inClassesAnnotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "com.systelab.seed", importOptions = ImportOption.DontIncludeTests.class)
public class ControllerTest {

    @ArchTest
    static ArchRule allFilesInPackageControllerShouldBeControllersOrDtos = classes()
            .that().resideInAPackage("..controller..")
            .should().haveSimpleNameEndingWith("Controller").orShould().haveSimpleNameEndingWith("Dto")
            .because("we want to have only controllers and dtos in the controllers package");

    @ArchTest
    static ArchRule allPublicMethodsInRestControllersShouldReturnResponseEntity =
            all(methods())
                    .that(inClassesAnnotatedWith(RestController.class)).and(arePublic())
                    .should(returnType(ResponseEntity.class))
                    .because("we want our Rest operations to always return a ResponseEntity");

    @ArchTest
    static ArchRule allPublicMethodsInRestControllersShouldHaveApiOperationAnnotation =
            all(methods())
                    .that(inClassesAnnotatedWith(RestController.class)).and(arePublic())
                    .should(beAnnotated(Operation.class))
                    .because("we want our Rest operations to be always documented with OpenApi");

    @ArchTest
    static ArchRule allNamedControllerShouldBeAnnotatedWithSTIController = classes()
            .that().resideOutsideOfPackage("..shared..")
            .and().resideInAPackage("..controller..").and().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(RestController.class)
            .because("we want all the controllers to be annotated with @STIController to have common properties");


    @ArchTest
    static ArchRule controllersShouldNotAccessRepositories = noClasses()
            .that().resideInAPackage("..controller..")
            .should().accessClassesThat().resideInAPackage("..repository..")
            .because("we want our controller to do not access our repositories directly");
}