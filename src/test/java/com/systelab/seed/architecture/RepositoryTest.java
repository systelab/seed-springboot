package com.systelab.seed.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.systelab.seed", importOptions = ImportOption.DoNotIncludeTests.class)
public class RepositoryTest {

    @ArchTest
    static ArchRule allRepositoriesShouldBeAnnotatedWithRepository = classes()
            .that().resideOutsideOfPackage("..shared..").and().resideInAPackage("..repository..")
            .and().haveSimpleNameEndingWith("Repository").and().haveSimpleNameNotEndingWith("MemoryRepository")
            .should().beAnnotatedWith(Repository.class)
            .because("in Spring all repositories should be annotated with @Repository");

    @ArchTest
    static ArchRule allFilesInPackageRepositoryShouldBeRepositories = classes()
            .that().resideInAPackage("..repository..")
            .should().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameEndingWith("Exception")
            .orShould().haveNameMatching(".*Specs.*")
            .because("we want to have only repositories in the repositories package");

    @ArchTest
    static ArchRule repositoriesCouldOnlyBeAccessedByServices = classes()
            .that().resideInAPackage("..repository..")
            .should().onlyBeAccessed().byAnyPackage("..service..","..repository..")
            .because("we want repositories be accessed only by services or by same repository");

}