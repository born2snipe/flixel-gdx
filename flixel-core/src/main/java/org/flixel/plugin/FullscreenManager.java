package org.flixel.plugin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import org.flixel.FlxBasic;
import org.flixel.FlxG;

/**
 * A simple plugin for switching between fullscreen and windowed mode
 *
 * @author Noah Greenberg
 */
public class FullscreenManager extends FlxBasic
{
	public String hotkey;

	private boolean inFullscreen = false;
	private boolean useDesktopDisplayMode = false;
	private int fullscreenWidth, fullscreenHeight, windowedWidth, windowedHeight;

	public FullscreenManager(int FullscreenWidth, int FullscreenHeight, int WindowedWidth, int WindowedHeight, String Hotkey)
	{
		fullscreenWidth = FullscreenWidth;
		fullscreenHeight = FullscreenHeight;
		windowedWidth = WindowedWidth;
		windowedHeight = WindowedHeight;
		hotkey = Hotkey;
	}

	public FullscreenManager(int FullscreenWidth, int FullscreenHeight, int WindowedWidth, int WindowedHeight)
	{
		this(FullscreenWidth, FullscreenHeight, WindowedWidth, WindowedHeight, null);
	}

	public FullscreenManager(int WindowedWidth, int WindowedHeight, String Hotkey)
	{
		windowedWidth = WindowedWidth;
		windowedHeight = WindowedHeight;
		hotkey = Hotkey;
		useDesktopDisplayMode = true;
	}

	public FullscreenManager(int WindowedWidth, int WindowedHeight)
	{
		this(WindowedWidth, WindowedHeight, null);
	}

	public void resize(int WindowedWidth, int WindowedHeight)
	{
		windowedWidth = WindowedWidth;
		windowedHeight = WindowedHeight;
	}

	/**
	 * If in fullscreen mode, the game will enter windowed mode.
	 * If in windowed mode, the game will enter fullscreen mode.
	 */
	public void toggle()
	{
		if(inFullscreen)
			Gdx.graphics.setWindowedMode(windowedWidth, windowedHeight);
		else if(useDesktopDisplayMode)
		{
			DisplayMode desktopDisplayMode = Gdx.graphics.getDisplayMode();
			Gdx.graphics.setFullscreenMode(desktopDisplayMode);
		}
		else
			Gdx.graphics.setFullscreenMode(findDisplayMode());

		inFullscreen = !inFullscreen;
	}

	private DisplayMode findDisplayMode() {
		DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();
		for (DisplayMode displayMode : displayModes) {
		    if (displayMode.width == fullscreenWidth && displayMode.height == fullscreenHeight) {
		        return displayMode;
            }
		}

        throw new IllegalArgumentException("No display mode found for: " + fullscreenWidth + "x" + fullscreenHeight);
	}

	@Override
	public void update()
	{
		if(hotkey != null && FlxG.keys.justReleased(hotkey))
			toggle();
	}
}