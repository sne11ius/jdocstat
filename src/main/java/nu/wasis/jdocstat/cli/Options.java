package nu.wasis.jdocstat.cli;

import java.io.File;

import org.kohsuke.args4j.Option;

public class Options {

    @Option(name = "-v", usage = "Java version (one of V_1_0_2, V_1_1_8, V_1_2, V_1_3_1, V_1_4_2, V_1_5, V_1_6, V_1_7, V_1_8)")
    private String javaVersion;

    @Option(name = "-f", usage = "tree.html file to scan")
    private File treeFile;

    public String getJavaVersion() {
        return javaVersion;
    }

    public File getTreeFile() {
        return treeFile;
    }

}
