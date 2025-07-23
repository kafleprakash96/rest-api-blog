package org.prkguides.blog.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtils {

    private SlugUtils() {
        // Utility class - prevent instantiation
    }

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");
    private static final Pattern EDGE_DASHES = Pattern.compile("^-|-$");

    /**
     * Generates a URL-friendly slug from the given input string.
     *
     * @param input The input string to convert to a slug
     * @return A URL-friendly slug
     */
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String slug = input.toLowerCase().trim();

        // Normalize unicode characters (remove accents, etc.)
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);

        // Replace whitespace with dashes
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Remove non-latin characters (keep letters, numbers, dashes)
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // Replace multiple consecutive dashes with single dash
        slug = MULTIPLE_DASHES.matcher(slug).replaceAll("-");

        // Remove dashes from beginning and end
        slug = EDGE_DASHES.matcher(slug).replaceAll("");

        return slug;
    }

    /**
     * Generates a unique slug by appending a number if the slug already exists.
     *
     * @param baseSlug The base slug
     * @param existingSlugChecker Function to check if slug exists
     * @return A unique slug
     */
    public static String generateUniqueSlug(String baseSlug, java.util.function.Predicate<String> existingSlugChecker) {
        String slug = generateSlug(baseSlug);

        if (!existingSlugChecker.test(slug)) {
            return slug;
        }

        int counter = 1;
        String uniqueSlug;

        do {
            uniqueSlug = slug + "-" + counter;
            counter++;
        } while (existingSlugChecker.test(uniqueSlug));

        return uniqueSlug;
    }

    /**
     * Validates if a string is a valid slug format.
     *
     * @param slug The slug to validate
     * @return true if valid slug format
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        // Valid slug: lowercase letters, numbers, hyphens, no spaces
        return slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }
}
