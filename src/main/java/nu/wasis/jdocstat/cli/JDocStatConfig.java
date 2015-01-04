package nu.wasis.jdocstat.cli;

import java.io.File;

import nu.wasis.jdocstat.domain.JavaVersion;

public class JDocStatConfig {

    private final JavaVersion javaVersion;
    private final File treeFile;

    public JDocStatConfig(final JavaVersion javaVersion, final File treeFile) {
        this.javaVersion = javaVersion;
        this.treeFile = treeFile;
    }

    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    public File getTreeFile() {
        return treeFile;
    }

}
