package com.example.lab4;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ProjArchitectureTests {

    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example.lab4");
    }

    @Test
    void shouldFollowLayerArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(applicationClasses);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("Controllers should not depend on other controllers")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .because("Repositories should not depend on services")
                .check(applicationClasses);
    }

    @Test
    void controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    @Test
    void controllerClassesShouldBeAnnotatedByRestController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(RestController.class)
                .check(applicationClasses);
    }

    @Test
    void repositoryClassesShouldBeInterfaces() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .beInterfaces()
                .check(applicationClasses);
    }

    @Test
    void controllerFieldsShouldNotBeAutowired() {
        noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith(Autowired.class)
                .because("Controllers should not use field injection")
                .check(applicationClasses);
    }

    @Test
    void modelFieldsShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should().bePrivate()
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldBeAnnotatedWithServiceOrComponent() {
        classes()
                .that().resideInAPackage("..service..")
                .should()
                .beAnnotatedWith(org.springframework.stereotype.Service.class)
                .orShould()
                .beAnnotatedWith(org.springframework.stereotype.Component.class)
                .check(applicationClasses);
    }

    @Test
    void noCyclicDependenciesBetweenLayers() {
        slices().matching("com.example.lab4.(*)..")
                .should().beFreeOfCycles()
                .because("There should be no cyclic dependencies between layers")
                .check(applicationClasses);
    }


    @Test
    void noClassesShouldUseFieldInjection() {
        noFields()
                .should()
                .beAnnotatedWith(Autowired.class)
                .because("Field injection should be avoided")
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldNotAccessControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .accessClassesThat().resideInAPackage("..controller..")
                .because("Services should not depend on controllers")
                .check(applicationClasses);
    }

    @Test
    void controllersShouldOnlyCallServiceLayer() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .onlyAccessClassesThat().resideInAPackage("..service..")
                .orShould().accessClassesThat().resideInAPackage("java..") // Дозволяємо доступ до класів з java.lang
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldDependOnRepositories() {
        classes()
                .that().resideInAPackage("..service..")
                .should()
                .dependOnClassesThat().resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    @Test
    void noServiceClassShouldBeAnnotatedWithController() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .beAnnotatedWith(Controller.class)
                .check(applicationClasses);
    }

    @Test
    void noControllerClassShouldBeAnnotatedWithService() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(org.springframework.stereotype.Service.class)
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotAccessOtherRepositories() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .accessClassesThat().resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    @Test
    void modelClassesShouldNotAccessControllers() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should()
                .accessClassesThat().resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    @Test
    void restControllerClassesShouldBePublic() {
        classes()
                .that().areAnnotatedWith(RestController.class)
                .should().bePublic()
                .check(applicationClasses);
    }

    @Test
    void servicesShouldNotDependOnOtherServicesDirectly() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .dependOnClassesThat().resideInAPackage("..service..")
                .check(applicationClasses);
    }
}