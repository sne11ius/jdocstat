package nu.wasis.jdocstat.domain;

import java.util.HashSet;
import java.util.Set;

public class ApiDescriptor {

    private final JavaVersion javaVersion;

    private final Set<_Class> classes = new HashSet<>();

    public ApiDescriptor(final JavaVersion javaVersion) {
        this.javaVersion = javaVersion;
    }

    public void addClass(final _Class _class) {
        classes.add(_class);
    }

    public Set<_Class> getClasses() {
        return classes;
    }

    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    @Override
    public String toString() {
        return "ApiDescriptor [javaVersion=" + javaVersion + ", classes=" + classes + "]";
    }

}
