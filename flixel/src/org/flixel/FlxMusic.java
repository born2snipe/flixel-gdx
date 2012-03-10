package org.flixel;

import com.badlogic.gdx.audio.Music;

public class FlxMusic extends FlxBasic
{
	/**
	 * Whether or not this sound should be automatically destroyed when you switch states.
	 */
	public boolean survive;
	/**
	 * Whether to call destroy() when the sound has finished.
	 */
	public boolean autoDestroy;

	/**
	 * Internal tracker for a Flash sound object.
	 */
	protected Music _sound;
	/**
	 * Internal tracker for the position in runtime of the music playback.
	 */
	protected float _position;
	/**
	 * Internal tracker for how loud the sound is.
	 */
	protected float _volume;
	/**
	 * Internal tracker for total volume adjustment.
	 */
	protected float _volumeAdjust;
	/**
	 * Internal tracker for whether the sound is looping or not.
	 */
	protected boolean _looped;
	/**
	 * Internal tracker for the sound's "target" (for proximity and panning).
	 */
	protected FlxObject _target;
	/**
	 * Internal tracker for the maximum effective radius of this sound (for proximity and panning).
	 */
	protected float _radius;
	/**
	 * Internal tracker for whether to pan the sound left and right.  Default is false.
	 */
	protected boolean _pan;
	/**
	 * Internal timer used to keep track of requests to fade out the sound playback.
	 */
	protected float _fadeOutTimer;
	/**
	 * Internal helper for fading out sounds.
	 */
	protected float _fadeOutTotal;
	/**
	 * Internal flag for whether to pause or stop the sound when it's done fading out.
	 */
	protected boolean _pauseOnFadeOut;
	/**
	 * Internal timer for fading in the sound playback.
	 */
	protected float _fadeInTimer;
	/**
	 * Internal helper for fading in sounds.
	 */
	protected float _fadeInTotal;
	
	
	public FlxMusic()
	{
		super();
		createSound();
	}
	
	/**
	 * An internal function for clearing all the variables used by sounds.
	 */
	protected void createSound()
	{
		destroy();
//		_sound.setPan(soundID, 0, 1.0f);
		_sound = null;
		_volume = 1.0f;
		_volumeAdjust = 1.0f;
		_looped = false;
		_target = null;
		_radius = 0;
		_pan = false;
		_fadeOutTimer = 0;
		_fadeOutTotal = 0;
		_pauseOnFadeOut = false;
		_fadeInTimer = 0;
		_fadeInTotal = 0;
		exists = false;
		active = false;
		visible = false;
		autoDestroy = false;
	}
	
	
	/**
	 * Clean up memory.
	 */
	@Override
	public void destroy()
	{
		kill();
		_sound = null;
		_target = null;		
		super.destroy();
	}
	
	

	public void dispose()
	{
		_sound.dispose();
	}
	
	
	/**
	 * Handles fade out, fade in, panning, proximity, and amplitude operations each frame.
	 */
	@Override
	public void update()
	{
		float radial = 1.0f;
		float fade = 1.0f;
		
		//Cross-fading volume control
		if(_fadeOutTimer > 0)
		{
			_fadeOutTimer -= FlxG.elapsed;
			if(_fadeOutTimer <= 0)
			{
				if(_pauseOnFadeOut)
					pause();
				else
					stop();
			}
			fade = _fadeOutTimer/_fadeOutTotal;
			if(fade < 0) fade = 0;
		}
		else if(_fadeInTimer > 0)
		{
			_fadeInTimer -= FlxG.elapsed;
			fade = _fadeInTimer/_fadeInTotal;
			if(fade < 0) fade = 0;
			fade = 1 - fade;
		}		
		_volumeAdjust = radial*fade;
		updateTransform();
	}
	
	
	@Override
	public void kill()
	{
		super.kill();
		if(_sound != null)
			stop();
	}

	
	/**
	 * One of two main setup functions for sounds, this function loads a sound from an embedded MP3.
	 * 
	 * @param	EmbeddedSound	An embedded Class object representing an MP3 file.
	 * @param	Looped			Whether or not this sound should loop endlessly.
	 * @param	AutoDestroy		Whether or not this <code>FlxSound</code> instance should be destroyed when the sound finishes playing.  Default value is false, but FlxG.play() and FlxG.stream() will set it to true by default.
	 * 
	 * @return	This <code>FlxSound</code> instance (nice for chaining stuff together, if you're into that).
	 */
	public FlxMusic loadEmbedded(Music EmbeddedSound, boolean Looped, boolean AutoDestroy)
	{
		stop();
		createSound();
		_sound = EmbeddedSound;
		autoDestroy = AutoDestroy;
		_looped = Looped;
		_sound.setLooping(_looped);
		updateTransform();
		exists = true;
		return this;
	}
	

	/**
	 * One of two main setup functions for sounds, this function loads a sound from an embedded MP3.
	 * 
	 * @param	EmbeddedSound	An embedded Class object representing an MP3 file.
	 * @param	Looped			Whether or not this sound should loop endlessly.
	 * 
	 * @return	This <code>FlxSound</code> instance (nice for chaining stuff together, if you're into that).
	 */
	public FlxMusic loadEmbedded(Music EmbeddedSound, boolean Looped)
	{
		return loadEmbedded(EmbeddedSound, Looped, false);
	}
	
	/**
	 * One of two main setup functions for sounds, this function loads a sound from an embedded MP3.
	 * 
	 * @param	EmbeddedSound	An embedded Class object representing an MP3 file.
	 * 
	 * @return	This <code>FlxSound</code> instance (nice for chaining stuff together, if you're into that).
	 */
	public FlxMusic loadEmbedded(Music EmbeddedSound)
	{
		return loadEmbedded(EmbeddedSound, false, false);
	}

	
	/**
	 * Call this function to play the sound - also works on paused sounds.
	 * 
	 * @param	ForceRestart	Whether to start the sound over or not.  Default value is false, meaning if the sound is already playing or was paused when you call <code>play()</code>, it will continue playing from its current position, NOT start again from the beginning.
	 */
	public void play(boolean ForceRestart)
	{
		if(ForceRestart)
		{
			boolean oldAutoDestroy = autoDestroy;
			autoDestroy = false;
			stop();
			autoDestroy = oldAutoDestroy;
		}
		_sound.play();
		active = true;//(_sound != null);
		_position = 0;
	}
	
	/**
	 * Call this function to play the sound - also works on paused sounds.
	 */
	public void play()
	{
		play(false);
	}
	
	
	/**
	 * Unpause a sound.  Only works on sounds that have been paused.
	 */
	public void resume()
	{
		if(_position <= 0)
			return;
		if(_looped)
		{
			_sound.play();
			if(_sound == null)
				exists = false;
		}
		else
		{
			_sound.play();
		}
		active = (_sound != null);
	}
	
	public void pause()
	{
		_sound.pause();
		_position = _sound.getPosition();
		active = false;
	}
	
	public void stop()
	{
		if(_sound != null)
		{
			_sound.stop();
			active = false;
			if(autoDestroy)
				destroy();
		}
	}
	
	
	public boolean isPlaying()
	{
		return _sound.isPlaying();
	}
	
	
	/**
	 * Call this function to make this sound fade out over a certain time interval.
	 * 
	 * @param	Seconds			The amount of time the fade out operation should take.
	 * @param	PauseInstead	Tells the sound to pause on fadeout, instead of stopping.
	 */
	public void fadeOut(float Seconds, boolean PauseInstead)
	{
		_pauseOnFadeOut = PauseInstead;
		_fadeInTimer = 0;
		_fadeOutTimer = Seconds;
		_fadeOutTotal = _fadeOutTimer;
	}
	
	/**
	 * Call this function to make this sound fade out over a certain time interval.
	 * 
	 * @param	Seconds			The amount of time the fade out operation should take.
	 */ 
	public void fadeOut(float Seconds)
	{
		fadeOut(Seconds, false);
	}

	/**
	 * Call this function to make a sound fade in over a certain
	 * time interval (calls <code>play()</code> automatically).
	 * 
	 * @param	Seconds		The amount of time the fade-in operation should take.
	 */
	public void fadeIn(float Seconds)
	{
		_fadeOutTimer = 0;
		_fadeInTimer = Seconds;
		_fadeInTotal = _fadeInTimer;
		play();
	}
	
	/**
	 * Set <code>volume</code> to a value between 0 and 1 to change how this sound is.
	 */
	public float getVolume()
	{
		return _volume;
	}
	
	
	/**
	 * @private
	 * @param Volume
	 */
	public void setVolume(float Volume)
	{
		_volume = Volume;	
		if(_volume < 0)
			_volume = 0;
		else if(_volume > 1)
			_volume = 1;
		_sound.setVolume(Volume);
	}
	
	
	/**
	 * Call after adjusting the volume to update the sound channel's settings.
	 */
	private void updateTransform()
	{
		if(_sound != null)
			_sound.setVolume((FlxG.mute?0:1) * FlxG.getVolume()* _volume * _volumeAdjust);
	}
}