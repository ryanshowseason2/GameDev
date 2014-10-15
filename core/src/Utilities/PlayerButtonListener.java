package Utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import Entities.PlayerEntity;
import Entities.ViewedCollidable;
import Equipables.CounterMeasure;

public class PlayerButtonListener extends ChangeListener
{
	PlayerEntity m_player;
	Stage m_stage;
	
	public PlayerButtonListener(PlayerEntity p, Stage stage)
	{
		m_player = p;
		m_stage = stage;
	}

	@Override
	public void changed(ChangeEvent event, Actor actor)
	{
		boolean changeEquipment = false;
		if( actor == m_player.m_longRange )
		{
			if( m_player.m_longRangeCMS.size() == 0 )
			{
				changeEquipment = true;
			}
			else
			{
				 m_player.m_longRangeCMS.get(0).EngageCM( (Button) actor );
			}
		}
		
		if( actor == m_player.m_mediumRange )
		{
			if( m_player.m_mediumRangeCMS.size() == 0 )
			{
				changeEquipment = true;
			}
			else
			{
				 m_player.m_mediumRangeCMS.get(0).EngageCM( (Button) actor );
			}
		}
		
		if( actor == m_player.m_shortRange )
		{
			if( m_player.m_shortRangeCMS.size() == 0 )
			{
				changeEquipment = true;
			}
			else
			{
				 m_player.m_shortRangeCMS.get(0).EngageCM( (Button) actor );
			}
		}
		
		if( actor == m_player.m_changeEquipment || changeEquipment )
		{
			// display the equipment screen...
			ViewedCollidable.EnterMenu();
			
			Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
			Dialog window = new Dialog("", skin);
			m_player.m_equipChangeListener.m_window = window;
	        window.setMovable(false);
	        window.setPosition(m_player.m_window.getWidth(), m_player.m_window.getY() );
	        window.row();
	        for(int i = 0; i< m_player.m_availableCMS.size(); i++)
	        {
	        	CounterMeasure c = m_player.m_availableCMS.get(i);
	        	
	        	if(c.m_rangeEnablersAndMultipliers[2] > 0)
	        	{
	        		Button choice = new Button(c.GetImageCopy(), skin);
	        		choice.setUserObject( new CounterMeasureAndRangePair(c , 2));
	        		choice.pad(10);
	        		choice.addListener( m_player.m_equipChangeListener );
	                window.add(choice);
	        	}
	        }
	        window.row();
	        for(int i = 0; i< m_player.m_availableCMS.size(); i++)
	        {
	        	CounterMeasure c = m_player.m_availableCMS.get(i);
	        	
	        	if(c.m_rangeEnablersAndMultipliers[1] > 0)
	        	{
	        		Button choice = new Button(c.GetImageCopy(), skin);
	        		choice.setUserObject( new CounterMeasureAndRangePair(c , 1));
	        		choice.pad(10);
	        		choice.addListener( m_player.m_equipChangeListener );
	                window.add(choice);
	        	}
	        }
	        window.row();
	        for(int i = 0; i< m_player.m_availableCMS.size(); i++)
	        {
	        	CounterMeasure c = m_player.m_availableCMS.get(i);
	        	
	        	if(c.m_rangeEnablersAndMultipliers[0] > 0)
	        	{
	        		Button choice = new Button(c.GetImageCopy(), skin);
	        		choice.setUserObject( new CounterMeasureAndRangePair(c , 0));
	        		choice.pad(10);
	        		choice.addListener( m_player.m_equipChangeListener );
	                window.add(choice);
	        	}
	        }
	        	        
	        window.row();
	        Texture cancel = new Texture(Gdx.files.internal("data/cancel.png"));
	        TextureRegion cancelRgn = new TextureRegion(cancel);
	        Button choice = new Button( new Image(cancelRgn), skin);
	        choice.addListener( m_player.m_equipChangeListener );
    		choice.pad(10);
            window.add(choice);
	        window.row();
	        window.pack();
	        m_stage.addActor(window);
		}
	}

}
