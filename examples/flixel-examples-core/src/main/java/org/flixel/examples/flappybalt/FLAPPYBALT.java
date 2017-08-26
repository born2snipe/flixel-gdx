package org.flixel.examples.flappybalt;

import org.flixel.FlxG;
import org.flixel.FlxGame;

/**
 * Canabalt-themed flappy-like for http://itch.io/jam/flappyjam
 *
 * @author Adam Atomic
 * @author Thomas Weston
 */
public class FLAPPYBALT extends FlxGame {
    public FLAPPYBALT() {
        super(160, 240, PlayState.class, 3, 50, 50);

        FlxG.debug = true;
    }
}
