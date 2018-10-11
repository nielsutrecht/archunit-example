package example;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.*;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import example.servicebase.ServiceBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.tngtech.archunit.core.domain.JavaModifier.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.all;
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
    public void serviceClassesShouldBeNamedXServiceOrXComponent() {
        classes()
                .that().resideInAPackage("example.*service.service")
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("Component")
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
    public void utilClassesShouldBePackagePrivate() {
        classes()
                .that().haveSimpleNameEndingWith("Util")
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

    @Test
    public void utilityClassesShouldHavePrivateConstructor() {
        ClassesTransformer<JavaConstructor> utilityConstructors = new AbstractClassesTransformer<JavaConstructor>("utility constructors") {
            @Override
            public Iterable<JavaConstructor> doTransform(JavaClasses classes) {
                Set<JavaConstructor> result = new HashSet<>();
                for (JavaClass javaClass : classes) {
                    if(javaClass.getSimpleName().endsWith("Util")) {
                        result.addAll(javaClass.getConstructors());
                    }
                }
                return result;
            }
        };

        ArchCondition<JavaConstructor> havePrivateConstructors = new ArchCondition<JavaConstructor>("be private") {
            @Override
            public void check(JavaConstructor constructor, ConditionEvents events) {
                boolean privateAccess = constructor.getModifiers().contains(PRIVATE);
                String message = String.format("%s is not private",  constructor.getFullName());
                events.add(new SimpleConditionEvent(constructor, privateAccess, message));
            }
        };

        all(utilityConstructors).should(havePrivateConstructors).check(classes);
    }

    @Test
    public void utilityClassesShouldHavePublicMethods() {
        ClassesTransformer<JavaMethod> utilityMethods = new AbstractClassesTransformer<JavaMethod>("utility methods") {
            @Override
            public Iterable<JavaMethod> doTransform(JavaClasses classes) {
                Set<JavaMethod> result = new HashSet<>();
                for (JavaClass javaClass : classes) {
                    if(javaClass.getSimpleName().endsWith("Util")) {
                        result.addAll(javaClass.getMethods());
                    }
                }
                return result;
            }
        };

        ArchCondition<JavaMethod> havePublicStaticFunctions = new ArchCondition<JavaMethod>("be public and static ") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                boolean publicStaticFunctions = method.getModifiers().contains(PUBLIC) && method.getModifiers().contains(STATIC);
                String message = String.format("%s is not public and static",  method.getFullName());
                events.add(new SimpleConditionEvent(method, publicStaticFunctions, message));
            }
        };

        all(utilityMethods).should(havePublicStaticFunctions).check(classes);
    }
}
