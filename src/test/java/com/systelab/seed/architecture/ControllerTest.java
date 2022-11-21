package com.systelab.seed.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "com.systelab.seed", importOptions = ImportOption.DoNotIncludeTests.class)
public class ControllerTest {

    @ArchTest
    static ArchRule allFilesInPackageControllerShouldBeControllersOrDtos = classes()
            .that().resideInAPackage("..controller..")
            .should().haveSimpleNameEndingWith("Controller")
            .orShould().haveSimpleNameEndingWith("Mapper")
            .orShould().haveSimpleNameEndingWith("MapperImpl")
            .orShould().haveSimpleNameEndingWith("DTO")
            .because("we want to have only controllers, mappers and dtos in the controllers package");

    @ArchTest
    static ArchRule allPublicMethodsInRestControllersShouldReturnResponseEntity = ArchRuleDefinition.methods()
            .that().arePublic()
            .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
            .and().areNotAnnotatedWith(EventListener.class)
            .should().haveRawReturnType(ResponseEntity.class)
            .orShould().haveRawReturnType(SseEmitter.class)
            .because("we want our Rest operations to always return a ResponseEntity or a SseEmitter");


    @ArchTest
    static ArchRule allPublicMethodsInRestControllersShouldHaveApiOperationAnnotation =
            ArchRuleDefinition.methods()
                    .that().arePublic()
                    .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                    .and().areNotAnnotatedWith(EventListener.class)
                    .should().beAnnotatedWith(Operation.class)
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