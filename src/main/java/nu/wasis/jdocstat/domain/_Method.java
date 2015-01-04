package nu.wasis.jdocstat.domain;

import java.util.ArrayList;
import java.util.List;

public class _Method {

    private final String name;
    private List<String> argTypes = new ArrayList<>();
    private final boolean deprecated;

    public _Method(final String name, final List<String> argTypes, final boolean deprecated) {
        this.name = name;
        this.argTypes = argTypes;
        this.deprecated = deprecated;
    }

    public String getName() {
        return name;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((argTypes == null) ? 0 : argTypes.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final _Method other = (_Method) obj;
        if (argTypes == null) {
            if (other.argTypes != null)
                return false;
        } else if (!argTypes.equals(other.argTypes))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "_Method [name=" + getName() + ", argTypes=" + argTypes + ", deprecated=" + deprecated + "]";
    }

}
