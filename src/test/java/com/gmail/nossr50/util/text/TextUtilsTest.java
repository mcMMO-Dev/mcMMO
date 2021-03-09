package com.gmail.nossr50.util.text;

import org.junit.Assert;
import org.junit.Test;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * This Unit Test checks if Adventure was set up correctly and works as expected.
 * Normally we can rely on this to be the case. However sometimes our dependencies
 * lack so far behind that things stop working correctly.
 * This test ensures that basic functionality is guaranteed to work as we would expect.
 * 
 * See https://github.com/mcMMO-Dev/mcMMO/pull/4446
 *
 */
public class TextUtilsTest {

    @Test
    public void testColorizeText() {
        String inputText = "&4This text should be red.";

        /*
         * If this method raises an exception, we know Adventure is not set up correctly.
         * This will also make the test fail and warn us about it.
         */
        TextComponent component = TextUtils.colorizeText(inputText);

        Assert.assertEquals("Looks like Adventure is not working correctly.",
                NamedTextColor.DARK_RED, component.color());
    }
}
