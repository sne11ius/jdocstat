package nu.wasis.jdocstat.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

import nu.wasis.jdocstat.domain.ApiDescriptor;
import nu.wasis.jdocstat.domain.JavaVersion;
import nu.wasis.jdocstat.domain._Method;
import nu.wasis.jdocstat.exception.ArgumentParsingException;
import nu.wasis.jdocstat.parser.Java102DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java118DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java12DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java17DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java18DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.JavaDocTreeHtmlParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class JDocStat {

    private static final Logger LOG = LogManager.getLogger();
    private static final PrintStream ERR = System.err;
    private static final PrintStream OUT = System.out;

    public static void main(final String... args) throws IOException {
        try {
            final JDocStatConfig config = parseArgs(args);
            ApiDescriptor apiDescriptor = null;
            final JavaDocTreeHtmlParser parser = createParser(config);
            apiDescriptor = parser.parseHtml();
            // OUT.println(apiDescriptor);
            if (null != apiDescriptor) {
                final List<String> deprecatedClasses = apiDescriptor.getClasses().stream().filter(c -> c.isDeprecated()).map(c -> c.getClassName()).collect(Collectors.toList());
                OUT.println("# Deprecated classes: " + deprecatedClasses.size());
                final List<_Method> deprecatedMethods = apiDescriptor.getClasses().stream().flatMap(c -> c.getMethods().stream()).filter(m -> m.isDeprecated()).collect(Collectors.toList());
                OUT.println("# Deprecated methods: " + deprecatedMethods.size());
                // OUT.println("All classes: " +
                // apiDescriptor.getClasses().stream().map(c ->
                // c.getClassName()).collect(Collectors.toList()));
                final int numClasses = apiDescriptor.getClasses().size();
                final int numMethods = apiDescriptor.getClasses().stream().mapToInt(c -> c.getMethods().size()).sum();
                OUT.println(apiDescriptor.getJavaVersion().toString().replaceAll("V_", "").replaceAll("_", ".") + ", " + numClasses + ", " + numMethods + ", " + deprecatedClasses.size() + ", "
                        + deprecatedMethods.size());
                // OUT.println("Api version: " +
                // apiDescriptor.getJavaVersion());
                // OUT.println("# Classes: " +
                // apiDescriptor.getClasses().size());
                // OUT.println("# Methods: " +
                // apiDescriptor.getClasses().stream().mapToInt(c ->
                // c.getMethods().size()).sum());
            }
        } catch (final IllegalArgumentException e) {
            if (!(e instanceof ArgumentParsingException) && null != e.getMessage()) {
                ERR.println(e.getMessage());
                printHelp(new CmdLineParser(new Options()));
            }
            return;
        }
    }

    private static JavaDocTreeHtmlParser createParser(final JDocStatConfig config) {
        switch (config.getJavaVersion()) {
            case V_1_0_2: {
                return new Java102DocTreeHtmlParser(config);
            }
            case V_1_1_8: {
                return new Java118DocTreeHtmlParser(config);
            }
            case V_1_2:
            case V_1_3_1:
            case V_1_4_2:
            case V_1_5:
            case V_1_6:
                return new Java12DocTreeHtmlParser(config);
            case V_1_7:
                return new Java17DocTreeHtmlParser(config);
            case V_1_8:
                return new Java18DocTreeHtmlParser(config);
            default:
                throw new RuntimeException("Unimplemented Java version: " + config.getJavaVersion() + ".");
        }
    }

    private static JDocStatConfig parseArgs(final String... args) throws IOException {
        final Options options = new Options();
        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            LOG.error(e);
            throw new ArgumentParsingException();
        }

        if (null == options.getJavaVersion()) {
            ERR.println("No Java version specified.");
            printHelp(parser);
            throw new ArgumentParsingException();
        }

        if (null == options.getTreeFile()) {
            ERR.println("No tree file specified.");
            printHelp(parser);
            throw new ArgumentParsingException();
        }

        final JavaVersion javaVersion = JavaVersion.valueOf(options.getJavaVersion());
        final File treeFile = options.getTreeFile();
        if (!treeFile.exists()) {
            ERR.println("Tree file not found.");
            printHelp(parser);
            throw new ArgumentParsingException();
        }
        if (!treeFile.isFile()) {
            ERR.println("Tree file not a regular file.");
            printHelp(parser);
            throw new ArgumentParsingException();
        }
        if (!treeFile.canRead()) {
            ERR.println("Tree file not readable.");
            printHelp(parser);
            throw new ArgumentParsingException();
        }

        return new JDocStatConfig(javaVersion, treeFile);
    }

    private static void printHelp(final CmdLineParser parser) throws IOException {
        final Writer stringWriter = new StringWriter();
        stringWriter.append("Usage: rcmp [options]\n");
        parser.printUsage(stringWriter, null);
        OUT.println(stringWriter.toString());
    }

}
