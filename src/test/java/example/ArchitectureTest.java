package example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import example.servicebase.ServiceBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.Serializable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class ArchitectureTest {
    private JavaClasses classes;

    @BeforeEach
    public void setup() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DONT_INCLUDE_TESTS)
            .importPackages("example");
    }

    @Test
    public void serviceClassesShouldOnlyBeAccessedByApplication() {
        classes()
            .that().resideInAPackage("example.*service.service")
            .should().onlyBeAccessed().byAnyPackage("example.*service")
            .check(classes);
    }

    @Test
    public void repositoryClassesClassesShouldOnlyBeAccessedByServiceClasses() {
        classes()
            .that().resideInAPackage("example.*service.repository")
            .should().onlyBeAccessed().byAnyPackage("example.*service.service", "example.*service")
            .check(classes);
    }

    @Test
    public void serviceClassesShouldBeNamedXService() {
        classes()
                .that().resideInAPackage("example.*service.service")
                .should().haveSimpleNameEndingWith("Service")
                .check(classes);
    }

    @Test
    public void repositoryClassesShouldBeNamedXRepository() {
        classes()
                .that().resideInAPackage("example.*service.repository")
                .should().haveSimpleNameEndingWith("Repository")
                .check(classes);
    }

    @Test
    public void baseDirectoryShouldHaveAnApplicationClass() {
        classes()
            .that().resideInAPackage("example.*service")
            .should().haveSimpleNameEndingWith("Application")
            .check(classes);
    }

    @Test
    public void applicationClassShouldImplementServiceBase() {
        classes()
            .that().resideInAPackage("example.*service")
            .should()
            .beAssignableTo(ServiceBase.class)
            .check(classes);
    }

    @Test
    public void domainClassesShouldBeSerializable() {
        classes()
                .that().resideInAPackage("example.*service.domain")
                .should()
                .beAssignableTo(Serializable.class)
                .check(classes);
    }

    @Test
    public void domainClassesShouldBePublic() {
        classes()
                .that().resideInAPackage("example.*service.domain")
                .should()
                .bePublic()
                .check(classes);
    }

    @Test
    public void serviceClassesShouldHaveSpringServiceAnnotation() {
        classes()
                .that().resideInAPackage("example.*service.service")
                .should().beAnnotatedWith(Service.class)
                .check(classes);
    }

    @Test
    public void repositoryClassesShouldHaveSpringRepositoryAnnotation() {
        classes()
                .that().resideInAPackage("example.*service.repository")
                .should().beAnnotatedWith(Repository.class)
                .check(classes);
    }

    @Test
    public void domainClassesShouldNotDependOnEachOther() {
        SlicesRuleDefinition.slices()
                .matching("example.(*service).domain")
                .should().notDependOnEachOther()
                .check(classes);
    }
}
