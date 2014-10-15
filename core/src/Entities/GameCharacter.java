package Entities;

import java.util.HashMap;
import java.util.Map;

import Utilities.AudioManager;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class GameCharacter
{

	String m_characterName;
	Map  m_characterImages = new HashMap();
	Map  m_characterTextures = new HashMap();
	public int m_soundIndex;
	public int m_talkSpeed = 5;
	
	
	public GameCharacter( String name )
	{
		m_characterName = name;
		LoadImageFiles( "data/"+name );
		LoadAudioFile();
	}
	
	private void LoadAudioFile()
	{
		m_soundIndex = AudioManager.AddToLibrary("data/"+m_characterName+"/"+m_characterName+".ogg");
	}
	
	private void LoadImageFiles(String directory) 
	{
		FileHandle dirHandle;
		
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			  dirHandle = Gdx.files.internal(directory);			
		} 
		else 
		{
			  // ApplicationType.Desktop ..
			  dirHandle = Gdx.files.internal("./bin/"+ directory);			
		}
		
		for (FileHandle entry: dirHandle.list(".png")) 
		{
			Texture t = new Texture( entry );
			Sprite s =  new Sprite( t );
			Image i = new Image( t );
			m_characterImages.put( entry.nameWithoutExtension(), i );
			m_characterTextures.put( entry.nameWithoutExtension(), s);
		}
	}

	public Image GetImage( String charImageString )
	{
	    //try to find it
		Image i = (Image) m_characterImages.values().toArray()[0];
		// if you can't use the default image
		if(m_characterImages.containsKey( charImageString ) )
		{
			i = (Image) m_characterImages.get( charImageString );
		}
		
		return i;
	}
	
	public Sprite GetSprite( String charImageString )
	{
	    //try to find it
		Sprite t = (Sprite) m_characterTextures.values().toArray()[0];
		// if you can't use the default image
		if(m_characterTextures.containsKey( charImageString ) )
		{
			t = (Sprite) m_characterTextures.get( charImageString );
		}
		
		return t;
	}

}
