package Utilities;

import Entities.GameCharacter;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class TimedMessage
{
	GameCharacter m_char;
	String m_message;
	int m_timeToDisplay;
	String m_state;
	boolean m_started = false;
	
	public static Dialog m_messageDialog;
	public static Label m_textArea;
	public static Image m_image;
	
	public TimedMessage( GameCharacter character, String state, String message, int time)
	{
		m_char = character;
		m_message = message;
		m_timeToDisplay = time;
		m_state = state;
	}
	
	
	public boolean Display()
	{
		if( !m_started )
		{
			//set the static members to stuff decrement the time to display. 
			m_textArea.setText(m_message);
			m_image.setDrawable(m_char.GetImage(m_state).getDrawable());
		}
		
		m_timeToDisplay--;
		return m_timeToDisplay > 0;
	}
	
	public void Reset()
	{
		m_started = false;
	}

}
