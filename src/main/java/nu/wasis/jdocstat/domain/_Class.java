package nu.wasis.jdocstat.domain;

import java.util.HashSet;
import java.util.Set;

public class _Class {

    private final String className;
    private final String _package;
    private final Set<_Method> methods = new HashSet<>();
    private final boolean deprecated;

    public _Class(final String className, final String _package, final boolean deprecated) {
        this.className = className;
        this._package = _package;
        this.deprecated = deprecated;
    }

    public void addMethod(final _Method _method) {
        methods.add(_method);
    }

    public String getClassName() {
        return className;
    }

    public String getPackage() {
        return _package;
    }

    public String getName() {
        return _package + "." + className;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public Set<_Method> getMethods() {
        return methods;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_package == null) ? 0 : _package.hashCode());
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((methods == null) ? 0 : methods.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final _Class other = (_Class) obj;
        if (_package == null) {
            if (other._package != null)
                return false;
        } else if (!_package.equals(other._package))
            return false;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (methods == null) {
            if (other.methods != null)
                return false;
        } else if (!methods.equals(other.methods))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "_Class [fullName=" + getName() + ", methods=" + methods + ", deprecated=" + deprecated + "]";
    }

}
