package dev.mathops.persistence.site;

import dev.mathops.commons.file.FileLoader;
import dev.mathops.commons.log.Log;

import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that loads a list of authorized administrators, and then can test whether a request from a client under a
 * specified login is authorized to perform administration (and if so, under which role).
 */
public final class Administrators {

    /** The list of authorized administrator records. */
    private final List<AdminRecord> records;

    /**
     * Constructs a new {@code Administrators}, reading configuration data from a specified directory.
     *
     * <p>
     * Administrators are configured in a file named "administrators.cfg" in the supplied directory.  If that file is
     * present, its contents are used to define authorized administrators.  If it is absent or not valid, no
     * administrators are authorized.
     *
     * <p>
     * The file's contents must be a set of lines, where each line is an IP address, a space, and a username, a space,
     * and a role name.  For example, the line "100.2.3.4 jsmith admin" would authorize the user whose login has
     * username "jsmith" to use "admin" role functions from a client with IP address 100.2.3.4.
     *
     * <p>
     * The IP address may end with a "*" wildcard, which will match any address with the same leading numbers, such as
     * "100.2.3.*", "100.2.*", "100.*", or "*".
     *
     * <p>
     * An IPv6 address can be used in the format "2001:db8:85a3:8d3:1319:8a2e:370:7348 jsmith admin", with
     *
     * @param configDir the configuration directory
     */
    public Administrators(final File configDir) {

        this.records = new ArrayList<>(20);

        final File admininstratorsFile = new File(configDir, "administrators.cfg");
        final String[] administratorsLines = FileLoader.loadFileAsLines(admininstratorsFile, false);

        if (administratorsLines == null) {
            Log.warning("Unable to read 'administrators.cfg' - remote administration locked out");
        } else {
            for (final String line : administratorsLines) {
                final AdminRecord record = parse(line);
                if (record != null) {
                    this.records.add(record);
                }
            }
        }
    }

    /**
     * Attempts to parse an {@code AdminRecord} from a single line in the configuration file.
     *
     * @param line the line
     * @return the parsed {@code AdminRecord} if successful; {@code null} if not
     */
    private AdminRecord parse(final String line) {

        // TODO:
        return null;
    }

    /**
     * Tests whether a user is authorized to perform administration from a source address.
     *
     * @param address  the source address
     * @param username the login username
     * @return the role under which the user is authorized to administer the system; null if not authorized
     */
    public String getAuthorizedRole(final InetAddress address, final String username) {

        String role = null;

        if (address instanceof final Inet4Address address4) {
            role = getAuthorizedRole4(address4, username);
        } else if (address instanceof final Inet6Address address6) {
            role = getAuthorizedRole6(address6, username);
        }

        return role;
    }

    /**
     * Tests whether a user is authorized to perform administration from a source IPv4 address.
     *
     * @param address4 the source IPv4 address
     * @param username the login username
     * @return the role under which the user is authorized to administer the system; null if not authorized
     */
    private String getAuthorizedRole4(final Inet4Address address4, final String username) {

        String role = null;

        final byte[] bytes = address4.getAddress();

        if (bytes.length == 4) {
            for (final AdminRecord test : this.records) {
                if (test.username().equals(username)) {
                    final int[] testParts = test.parts();
                    if (testParts.length == 4) {
                        if (testParts[0] == -1) {
                            // Configured address is "*"
                            role = test.role();
                            break;
                        }
                        final int part1 = (int) bytes[0] & 0xFF;
                        if (part1 == testParts[0]) {
                            if (testParts[1] == -1) {
                                // Configured address is "###.*"
                                role = test.role();
                                break;
                            }
                            final int part2 = (int) bytes[1] & 0xFF;
                            if (part2 == testParts[1]) {
                                if (testParts[2] == -1) {
                                    // Configured address is "###.###.*"
                                    role = test.role();
                                    break;
                                }

                                final int part3 = (int) bytes[2] & 0xFF;
                                if (part3 == testParts[2]) {
                                    if (testParts[3] == -1) {
                                        // Configured address is "###.###.###.*"
                                        role = test.role();
                                        break;
                                    }

                                    final int part4 = (int) bytes[3] & 0xFF;
                                    if (part4 == testParts[3]) {
                                        // Configured address is "###.###.###.###"
                                        role = test.role();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return role;
    }

    /**
     * Tests whether a user is authorized to perform administration from a source IPv6 address.
     *
     * @param address6 the source IPv6 address
     * @param username the login username
     * @return the role under which the user is authorized to administer the system; null if not authorized
     */
    private String getAuthorizedRole6(final Inet6Address address6, final String username) {

        String role = null;

        final byte[] bytes = address6.getAddress();

        if (bytes.length == 16) {
            for (final AdminRecord test : this.records) {
                if (test.username().equals(username)) {
                    final int[] testParts = test.parts();
                    if (testParts.length == 8) {
                        final int part1 = (((int) bytes[0] & 0xFF) << 8) + (int) bytes[1] & 0xFF;
                        if (part1 == testParts[0]) {
                            final int part2 = (((int) bytes[2] & 0xFF) << 8) + (int) bytes[3] & 0xFF;
                            if (part2 == testParts[1]) {
                                final int part3 = (((int) bytes[4] & 0xFF) << 8) + (int) bytes[5] & 0xFF;
                                if (part3 == testParts[2]) {
                                    final int part4 = (((int) bytes[6] & 0xFF) << 8) + (int) bytes[7] & 0xFF;
                                    if (part4 == testParts[3]) {
                                        final int part5 = (((int) bytes[8] & 0xFF) << 8) + (int) bytes[9] & 0xFF;
                                        if (part5 == testParts[4]) {
                                            final int part6 = (((int) bytes[10] & 0xFF) << 8) + (int) bytes[11] & 0xFF;
                                            if (part6 == testParts[5]) {
                                                final int part7 = (((int) bytes[12] & 0xFF) << 8)
                                                                  + (int) bytes[13] & 0xFF;
                                                if (part7 == testParts[6]) {
                                                    final int part8 = (((int) bytes[14] & 0xFF) << 8)
                                                                      + (int) bytes[15] & 0xFF;
                                                    if (part8 == testParts[7]) {
                                                        role = test.role();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return role;
    }

    /**
     * A record of an authorized administrator read from the configuration file.
     *
     * @param parts    the parts of the address (4 for an IPV4 address), with -1 in each wildcard location
     * @param username the username
     * @param role     the authorized role
     */
    private record AdminRecord(int[] parts, String username, String role) {
    }
}
