package io.github.bakedlibs.dough.versions;

import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.bukkit.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestMinecraftVersion {

    @ParameterizedTest
    @MethodSource("getValidVersions")
    void testParser(@Nonnull String serverVersion, int major, int minor, int patch) throws UnknownServerVersionException {
        Server server = Mockito.mock(Server.class);
        // getMinecraftVersion() returns null on a plain mock, causing the primary path to
        // fall through to the legacy getBukkitVersion() path — which is what we test here.
        Mockito.when(server.getBukkitVersion()).thenReturn(serverVersion);

        MinecraftVersion version = MinecraftVersion.of(server);
        SemanticVersion expected = new SemanticVersion(major, minor, patch);

        assertEquals(expected, version);
        Assertions.assertTrue(version.getAsString().startsWith("Minecraft"));
    }

    private static @Nonnull Stream<Arguments> getValidVersions() {
        /*
         * These samples were taken directly from the historical
         * data of CraftBukkit's pom.xml file:
         * https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/pom.xml
         * 
         * We should be safe to assume that forks follow these conventions and do not mess
         * with this version number (Spigot, Paper and Tuinity do at least).
         */
        // @formatter:off
        return Stream.of(
            Arguments.of("1.13.2-R0.1-SNAPSHOT",    1, 13, 2),
            Arguments.of("1.13-R0.2-SNAPSHOT",      1, 13, 0),
            Arguments.of("1.13.2-R0.1-SNAPSHOT",    1, 13, 2),
            Arguments.of("1.13-pre7-R0.1-SNAPSHOT", 1, 13, 0),
            Arguments.of("1.14-pre5-SNAPSHOT",      1, 14, 0),
            Arguments.of("1.15-R0.1-SNAPSHOT",      1, 15, 0),
            Arguments.of("1.16.5-R0.1-SNAPSHOT",    1, 16, 5),
            Arguments.of("1.17-R0.1-SNAPSHOT",      1, 17, 0)
        );
        // @formatter:on
    }

    /**
     * Tests the primary {@code getMinecraftVersion()} path, which is used for year-based
     * versioning (Minecraft 26.1+) where {@code getBukkitVersion()} may contain extra
     * build metadata that cannot be parsed as a semantic version.
     */
    @ParameterizedTest
    @MethodSource("getValidMinecraftVersions")
    void testParserViaGetMinecraftVersion(@Nonnull String minecraftVersion, int major, int minor, int patch) throws UnknownServerVersionException {
        Server server = Mockito.mock(Server.class);
        Mockito.when(server.getMinecraftVersion()).thenReturn(minecraftVersion);

        MinecraftVersion version = MinecraftVersion.of(server);
        SemanticVersion expected = new SemanticVersion(major, minor, patch);

        assertEquals(expected, version);
        Assertions.assertTrue(version.getAsString().startsWith("Minecraft"));
    }

    private static @Nonnull Stream<Arguments> getValidMinecraftVersions() {
        // @formatter:off
        return Stream.of(
            // Year-based versioning introduced in Minecraft 26.1
            Arguments.of("26.1",   26, 1, 0),
            Arguments.of("26.1.1", 26, 1, 1),
            // Traditional versioning also works via this path
            Arguments.of("1.21.4", 1, 21, 4),
            Arguments.of("1.21",   1, 21, 0)
        );
        // @formatter:on
    }

    @ParameterizedTest
    @MethodSource("getInvalidVersions")
    void testParserInvalidVersion(String serverVersion) {
        Server server = Mockito.mock(Server.class);
        Mockito.when(server.getBukkitVersion()).thenReturn(serverVersion);

        assertThrows(UnknownServerVersionException.class, () -> MinecraftVersion.of(server));
    }

    private static @Nonnull Stream<Arguments> getInvalidVersions() {
        // @formatter:off
        return Stream.of(
            Arguments.of("R0.1-SNAPSHOT"),
            Arguments.of("15SNAPSHOT"),
            Arguments.of("Can I has Bukkit?"),
            Arguments.of("pre7-R0.1-SNAPSHOT"),
            Arguments.of("1152-SNAPSHOT"),
            Arguments.of("1R1SNAPSHOT"),
            Arguments.of("R2-D2-SNAPSHOT"),
            Arguments.of("C3PO")
        );
        // @formatter:on
    }

}
