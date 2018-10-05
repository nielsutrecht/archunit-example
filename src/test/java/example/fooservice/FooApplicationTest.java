package example.fooservice;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import example.servicebase.ServiceBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class FooApplicationTest {
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
            .should().onlyBeAccessed().byAnyPackage("example.*service.service")
            .check(classes);
    }

    @Test
    public void baseDirectoryShouldHaveAnApplicationClass() {
        classes()
            .that().resideInAPackage("example.*service")
            .should().haveSimpleNameEndingWith("Application")
            .andShould().implement(ServiceBase.class)
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
    public void ex2() {
        noClasses().that().resideInAPackage("..service").should().dependOnClassesThat().resideInAPackage("..repostory");
    }
}
