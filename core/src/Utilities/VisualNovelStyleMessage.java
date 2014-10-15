package Utilities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import Entities.GameCharacter;
import Screens.CombatScreen;
import Utilities.VisualNovelImageState.ImageJustification;

public class VisualNovelStyleMessage extends ChangeListener
{
	GameCharacter m_char;
	String m_message;
	VisualNovelImageState m_state;
	boolean m_buttonPressed = false;
	int m_charactersToDisplay = 0;
	boolean m_started = false;
	
	public static Dialog m_messageDialog;
	public static Label m_textArea;
	public static Button m_button;
	//public static Image m_image;
	public static Map< GameCharacter, VisualNovelImageState>  m_characterImages = new HashMap<GameCharacter, VisualNovelImageState >();
	
	public VisualNovelStyleMessage( GameCharacter character, VisualNovelImageState state, String message )
	{
		m_char = character;
		m_message = message;
		m_state = state;
	}
	
	
	public boolean Display( SpriteBatch renderer )
	{
		if( !m_started )
		{
			m_started = true;
			m_button.addListener(this);
			
			// Do the image thingy
			if( m_state.m_characterImage.equals("Remove") )
			{
				m_characterImages.remove(m_char);
			}
			else if( !m_state.m_characterImage.equals("None"))
			{
				m_characterImages.put( m_char, m_state);
			}								
		}
		
		renderer.begin();
		for (Map.Entry<GameCharacter, VisualNovelImageState> entry : m_characterImages.entrySet())
		{
		    // Draw the damn things
			VisualNovelImageState vnis = entry.getValue();
			GameCharacter gc = entry.getKey();
			Sprite image = gc.GetSprite(vnis.m_characterImage );
			
			if( vnis.m_flipped )
			{
				image.flip(true, false);
			}
			
			float x = 0;
			float y = m_messageDialog.getHeight();
			
			if(vnis.m_positioning == ImageJustification.Right )
			{
				x = CombatScreen.WIDTH - image.getWidth();
			}
			else if( vnis.m_positioning == ImageJustification.Center )
			{
				x = CombatScreen.WIDTH/2 - image.getWidth()/2;
			}
			
			x += vnis.m_positionOffset;
			
			image.setPosition(x, y);
			image.draw( renderer );		
			
			if( vnis.m_flipped )
			{
				image.flip(true, false);
			}
		}
		renderer.end();
		
		if(m_message != null)
		{
			//set the static members to stuff decrement the time to display. 
			m_textArea.setText(m_message.subSequence(0, m_charactersToDisplay));
			
			if( m_charactersToDisplay < (m_message.length() ) )
			{
				m_charactersToDisplay++;
				if((m_charactersToDisplay % m_char.m_talkSpeed) == 0)
					AudioManager.PlaySound(m_char.m_soundIndex, false, CombatScreen.player );
			}
		}
		else
		{
			m_buttonPressed = true;
		}
		

		return m_buttonPressed;
	}
	
	public void Reset()
	{
		m_buttonPressed = false;
		m_charactersToDisplay = 0;
		m_started = false;
	}


	@Override
	public void changed(ChangeEvent event, Actor actor)
	{
		if( m_charactersToDisplay < (m_message.length() ) )
		{
			m_charactersToDisplay = m_message.length();
		}
		else
		{
			m_buttonPressed = true;
			m_button.removeListener(this);
		}
		
	}

}
