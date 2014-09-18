package Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import Entities.PlayerEntity;
import Entities.ViewedCollidable;

public class AudioManager
{
	static ArrayList<Sound> m_soundEffects = new ArrayList<Sound>();
	static ArrayList<String> m_soundEffectLocations = new ArrayList<String>();
	public static PlayerEntity m_player;
	public AudioManager()
	{
		
	}
	

	
	public static int AddToLibrary( String fileHandle )
	{
		int index = -1;
		
		if( !m_soundEffectLocations.contains( fileHandle ) )
		{
			Sound sound = Gdx.audio.newSound(Gdx.files.internal(fileHandle));
			m_soundEffectLocations.add(fileHandle);
			m_soundEffects.add( sound );
			index = m_soundEffects.size() - 1;
		}
		else
		{
			index = m_soundEffectLocations.indexOf(fileHandle);
		}
				
		return index;
	}
	
	public static long PlaySound( int index, boolean looping, ViewedCollidable source )
	{
		Sound sound = (Sound)m_soundEffects.get(index);
		long instanceID = 0;
		if( looping )
		{			
			instanceID = sound.loop();			
		}
		else
		{
			instanceID = sound.play();
		}		
		
		float adjustedVolume = 35f / m_player.m_body.getPosition().dst(source.m_body.getPosition());
		float adjustedPan = ( -1 * ( m_player.m_body.getPosition().x - source.m_body.getPosition().x) );
		if( Math.abs(adjustedPan) < 35 )
		{
			adjustedPan = 0;
		}
		else
		{
			adjustedPan = adjustedPan / 70; 			
		}
		adjustedPan = adjustedPan > 1 ? 1: adjustedPan;
		adjustedPan = adjustedPan < -1 ? -1: adjustedPan;
		sound.setPan( instanceID, adjustedPan, adjustedVolume > 1 ? 1 : adjustedVolume );
		
		return instanceID;
	}
	
	public static void AdjustSound( int index, long instanceID, ViewedCollidable source )
	{
		Sound sound = (Sound)m_soundEffects.get(index);
		float adjustedVolume = 35f / m_player.m_body.getPosition().dst(source.m_body.getPosition());
		
		float adjustedPan = ( -1 * ( m_player.m_body.getPosition().x - source.m_body.getPosition().x) );
		if( Math.abs(adjustedPan) < 35 )
		{
			adjustedPan = 0;
		}
		else
		{
			adjustedPan = adjustedPan / 70; 			
		}
		adjustedPan = adjustedPan > 1 ? 1: adjustedPan;
		adjustedPan = adjustedPan < -1 ? -1: adjustedPan;
		sound.setPan( instanceID, adjustedPan, adjustedVolume > 1 ? 1 : adjustedVolume );
	}
	
	public static void StopSound( int index, long instanceID )
	{		
		Sound sound = (Sound)m_soundEffects.get(index);
		sound.stop( instanceID );
	}
	
	public static void StopAllSound()
	{		
		for( int i = 0; i < m_soundEffects.size(); i++ )
		{
			Sound s = (Sound)m_soundEffects.get( i );
			s.stop();
		}
	}
	
	public static void PauseAll()
	{
		for( int i = 0; i < m_soundEffects.size(); i++ )
		{
			Sound s = (Sound)m_soundEffects.get( i );
			s.pause();
		}
	}
	
	public static void ResumeAll()
	{
		for( int i = 0; i < m_soundEffects.size(); i++ )
		{
			Sound s = (Sound)m_soundEffects.get( i );
			s.resume();
		}
	}
	
	public static void ReleaseAllResources()
	{
		for( int i = 0; i < m_soundEffects.size(); i++ )
		{
			Sound s = (Sound)m_soundEffects.get( i );
			s.dispose();
		}
		m_soundEffects.clear();
	}

}
