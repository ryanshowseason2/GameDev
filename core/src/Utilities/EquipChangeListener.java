package Utilities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import Entities.PlayerEntity;
import Equipables.CounterMeasure;

public class EquipChangeListener extends ChangeListener
{
	public Window m_window;
	PlayerEntity m_player;
	
	public EquipChangeListener( PlayerEntity p)
	{
		m_player = p;
	}

	@Override
	public void changed(ChangeEvent event, Actor actor)
	{
		
		if(actor.getUserObject() == null )
		{
			m_player.m_inMenu = false;
			m_window.remove();
		}
		else
		{
			CounterMeasureAndRangePair cnr =  (CounterMeasureAndRangePair) actor.getUserObject();
			m_player.EquipCounterMeasure( cnr.m_counterMeasure, cnr.m_rangeIndex );
		}
	}

}
