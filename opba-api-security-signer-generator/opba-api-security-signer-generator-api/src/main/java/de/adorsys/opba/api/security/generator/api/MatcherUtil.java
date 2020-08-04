package de.adorsys.opba.api.security.generator.api;

// Can't safely use Lombok in self-generated classes
public final class MatcherUtil {

    private MatcherUtil() {
    }

    public static boolean matches(String definedPath, String path) {
        String[] definedSegments = definedPath.split("/", -1);
        String[] pathSegments = path.split("/", -1);

        if (definedSegments.length != pathSegments.length) {
            return false;
        }

        for (int segment = 0; segment < definedSegments.length; ++segment) {
            if (definedSegments[segment].startsWith("{")) {
                // Any match here
                continue;
            }

            if (!definedSegments[segment].equals(pathSegments[segment])) {
                return false;
            }
        }

        return true;
    }
}
