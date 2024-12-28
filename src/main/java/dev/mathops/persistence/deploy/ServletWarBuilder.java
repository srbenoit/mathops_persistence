package dev.mathops.persistence.deploy;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.builder.HtmlBuilder;
import dev.mathops.commons.file.FileLoader;
import dev.mathops.commons.installation.Installation;
import dev.mathops.commons.installation.Installations;
import dev.mathops.commons.log.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Constructs the servlet WAR file.
 *
 * <p>
 * This program assumes a single IDEA project structure of this form:
 *
 * <pre>
 * {user.home}/dev/IDEA/mathops_persistence
 *    /src
 *    /lib
 *    /jars
 *    /build/classes/java/main/[packages and class files]
 * </pre>
 */
final class ServletWarBuilder {

    /** The directory in which to store the generated jar file. */
    private static final String LIB_DIR = "lib";

    /** Directory where project is stored. */
    private final File projectDir;

    /**
     * Constructs a new {@code ServletWarBuilder}.
     */
    private ServletWarBuilder() {

        final File userDir = new File(System.getProperty("user.home"));
        final File dev = new File(userDir, "dev");
        final File idea = new File(dev, "IDEA");
        this.projectDir = new File(idea, "mathops_persistence");
    }

    /**
     * Builds the WAR file.
     */
    private void build() {

        if (buildRootJar()) {
            buildRootWar();
        }
    }

    /**
     * Builds the ROOT.jar file.
     *
     * @return {@code true} if successful
     */
    private boolean buildRootJar() {
        final File root = new File(this.projectDir, "build/classes/java/main");
        final File persistenceClasses = new File(root, "dev/mathops/persistence");
        final File schemaClasses = new File(root, "dev/mathops/schema");

        final File lib = new File(this.projectDir, "lib");

        boolean success = checkDirectoriesExist(persistenceClasses, schemaClasses, lib);

        if (success) {
            try (final FileOutputStream out = new FileOutputStream(new File(lib, "ROOT.jar"));
                 final BufferedOutputStream bos = new BufferedOutputStream(out, 128 << 10);
                 final JarOutputStream jar = new JarOutputStream(bos)) {

                addManifest(jar);

                Log.finest(Res.fmt(Res.ADDING_FILES, this.projectDir), CoreConstants.CRLF);
                addFiles(root, persistenceClasses, jar);
                addFiles(root, schemaClasses, jar);
                jar.finish();
                Log.finest(Res.fmt(Res.JAR_DONE, "ROOT"), CoreConstants.CRLF);
            } catch (final IOException ex) {
                Log.warning(Res.get(Res.JAR_WRITE_FAILED), ex);
                success = false;
            }
        }

        return success;
    }

    /**
     * Builds the ROOT.war file.
     */
    private void buildRootWar() {

        final File webRoot = new File(this.projectDir, "build/classes/java/main");
        final File deployDir = new File(this.projectDir, LIB_DIR);

        try (final FileOutputStream out = new FileOutputStream(new File(deployDir, "ROOT.war"));
             final BufferedOutputStream bos = new BufferedOutputStream(out, 128 << 10);
             final JarOutputStream war = new JarOutputStream(bos)) {

            addManifest(war);

            war.putNextEntry(new ZipEntry("WEB-INF/"));
            war.closeEntry();

            war.putNextEntry(new ZipEntry("WEB-INF/classes/"));
            war.closeEntry();

            war.putNextEntry(new ZipEntry("WEB-INF/lib/"));
            war.closeEntry();

            final File jarFile = new File(deployDir, "ROOT.jar");
            war.putNextEntry(new ZipEntry("WEB-INF/lib/ROOT.jar"));
            war.write(FileLoader.loadFileAsBytes(jarFile, true));
            war.closeEntry();

            final File commonsFile = new File(deployDir, "mathops_commons.jar");
            war.putNextEntry(new ZipEntry("WEB-INF/lib/mathops_commons.jar"));
            war.write(FileLoader.loadFileAsBytes(commonsFile, true));
            war.closeEntry();

            final File webFile = new File(deployDir, "web.xml");
            war.putNextEntry(new ZipEntry("WEB-INF/web.xml"));
            war.write(FileLoader.loadFileAsBytes(webFile, true));
            war.closeEntry();

            final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss 'on' EEEE, MMM d, yyyy", Locale.US);
            final String now = fmt.format(new Date());
            war.putNextEntry(new ZipEntry("WEB-INF/classes/date.txt"));
            war.write(now.getBytes(StandardCharsets.UTF_8));
            war.closeEntry();

            final String deployPath = deployDir.getAbsolutePath();
            Log.finest(Res.fmt(Res.WAR_DONE, "ROOT"), " in ", deployPath, CoreConstants.CRLF);

        } catch (final IOException ex) {
            Log.warning(Res.get(Res.WAR_WRITE_FAILED), ex);
        }
    }

    /**
     * Recursively adds all files in a directory to a jar output stream. All "module_info.class" and
     * "package-info.class" files will be skipped, and any directory that contains a "war_ignore.txt" file (and its
     * descendants) will be ignored.
     *
     * @param rootDir the root directory of the file tree
     * @param dir     the directory
     * @param jar     the jar output stream to which to add entries
     * @throws IOException if an exception occurs while writing
     */
    private void addFiles(final File rootDir, final File dir, final JarOutputStream jar)
            throws IOException {

        if (!new File(dir, "war_ignore.txt").exists()) {
            final int count = copyFiles(rootDir, dir, jar);

            if (count > 0) {
                // Build a log message with package name and number of files added
                String pkgName = dir.getName();

                File temp = dir.getParentFile();
                final StringBuilder builder = new StringBuilder(100);

                if (temp == null) {
                    Log.warning("Null parent: dir=", dir.getAbsolutePath(), " root=", rootDir.getAbsolutePath());
                } else {
                    while (!temp.equals(rootDir)) {
                        builder.append(temp.getName()).append('.').append(pkgName);
                        pkgName = builder.toString();
                        builder.setLength(0);
                        temp = temp.getParentFile();
                        if (temp == null) {
                            Log.warning("Null parent: dir=", dir.getAbsolutePath(), " root=",
                                    rootDir.getAbsolutePath());
                            break;
                        }
                    }
                }

                final HtmlBuilder msg = new HtmlBuilder(80);
                msg.add(' ').add(pkgName).padToLength(55);
                msg.addln(": ", Integer.toString(count), CoreConstants.SPC, Res.get(Res.FILES_COPIED));
                Log.finest(msg.toString());
            }
        }
    }

    /**
     * Recursively copies files from a directory into the Jar stream.
     *
     * @param rootDir the root directory of the file tree
     * @param dir     the directory
     * @param jar     the jar output stream to which to add entries
     * @return the number of files copied
     * @throws IOException if an exception occurs while writing
     */
    private int copyFiles(final File rootDir, final File dir, final JarOutputStream jar) throws IOException {

        final File[] files = dir.listFiles();

        int count = 0;

        if (files != null) {
            for (final File file : files) {

                String name = file.getName();
                if ("package-info.class".equals(name) || "module-info.class".equals(name)) {
                    continue;
                }

                // Prepend relative path to the name
                File parent = file.getParentFile();
                final HtmlBuilder builder = new HtmlBuilder(100);
                while (!parent.equals(rootDir)) {
                    builder.add(parent.getName(), CoreConstants.SLASH, name);
                    name = builder.toString();
                    builder.reset();
                    parent = parent.getParentFile();
                }

                if (file.isDirectory()) {
                    addFiles(rootDir, file, jar);
                } else {
                    jar.putNextEntry(new ZipEntry(name));
                    final byte[] bytes = FileLoader.loadFileAsBytes(file, true);
                    if (bytes == null) {
                        throw new IOException(Res.fmt(Res.READ_FAILED, file.getAbsolutePath()));
                    }
                    jar.write(bytes);
                    ++count;
                }
                jar.closeEntry();
            }
        }

        return count;
    }

    /**
     * Given a list of {@code File} objects, tests that each exists and is a directory.
     *
     * @param dirs the list of {@code File} objects
     * @return {@code true} if all {@code File} represent existing directories
     */
    private static boolean checkDirectoriesExist(final File... dirs) {

        boolean good = true;

        for (final File test : dirs) {
            if (!test.exists() || !test.isDirectory()) {
                Log.warning(Res.fmt(Res.DIR_NOT_FOUND, test.getAbsolutePath()));
                good = false;
                break;
            }
        }

        return good;
    }

    /**
     * Adds the manifest file to a jar output stream.
     *
     * @param jar the {@code JarOutputStream} to which to add the manifest
     * @throws IOException if an exception occurs while writing
     */
    private static void addManifest(final JarOutputStream jar) throws IOException {

        jar.putNextEntry(new ZipEntry("META-INF/"));
        jar.closeEntry();

        final HtmlBuilder htm = new HtmlBuilder(500);
        htm.addln("Manifest-Version: 1.0");
        htm.addln("Application-Name: MathOps Persistence Layer");
        htm.addln("Permissions: all-permissions");
        htm.addln("Codebase: *");
        htm.addln("Application-Library-Allowable-Codebase: *");
        htm.addln("Caller-Allowable-Codebase: *");
        htm.addln("Created-By: ServletWarBuilder 2.00");

        jar.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        final byte[] bytes = htm.toString().getBytes(StandardCharsets.UTF_8);
        jar.write(bytes);
        jar.closeEntry();
    }

    /**
     * Main method to execute the builder.
     *
     * @param args command-line arguments
     */
    public static void main(final String... args) {

        // Use the default installation
        final Installation installation = Installations.get().getInstallation(null, null);
        Installations.setMyInstallation(installation);

        new ServletWarBuilder().build();
        Log.finest(Res.get(Res.FINISHED), CoreConstants.CRLF);
    }
}
