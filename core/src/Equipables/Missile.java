package Equipables;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import Entities.MissileEntity;
import Entities.Projectile;
import Entities.Ship;
import Entities.ViewedCollidable;
import Entities.Projectile.Characters;

public class Missile extends CounterMeasure
{
	int m_missileCounter = 0;
	boolean m_missileSpecialActivated = false;
	int m_rememberedFrequency;	
	
	public Missile(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/missileicon.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[1] = 1f;
		m_fireFrequency = 60;
		m_fireCounter = 0;				
	}
	
	
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/missileicon.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		if( ( m_target != null && m_target.m_integrity <= 0 ) )
		{
			m_target = null;
		}
		
		if( m_target == null )
		{
			// Pull the closest tracked target from the ship computer! 
			float leastDistance = Float.MAX_VALUE;
			for( int i = 0; i < m_ship.m_trackedHostileTargets.size(); i++ )
			{
				ViewedCollidable vc = m_ship.m_trackedHostileTargets.get(i);
				float distance = vc.m_body.getPosition().dst(m_ship.m_body.getPosition());
				if( distance <= m_range && distance < leastDistance )
				{
					leastDistance = distance;
					m_target = vc;
				}
			}
		}
		
		if( m_target != null && m_fireCounter <= 0 )
		{
			float distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );

			if( distanceToCurrentTarget <= m_range )
			{
				MissileEntity m = new MissileEntity( m_target, m_world, m_ship.m_body.getPosition().x, m_ship.m_body.getPosition().y, 0,
							50f, m_ship.m_factionCode, m_aliveThings, m_ship );
				m.SetSpecials(m_specialAbilitiesActivated);
				 m_fireCounter = m_fireFrequency;
				 m_missileCounter++;
			}
			else
			{
				m_target = null;
			}
		}
		else
		{
			m_fireCounter-= 1;
		}
		
		if( m_missileSpecialActivated && m_missileCounter > 10 )
		{
			DisengageCM();
		}
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_missileCounter = 0;
		m_missileSpecialActivated = true;
		m_rememberedFrequency = m_fireFrequency;
		m_fireFrequency = 10;
		m_fireCounter = 0;
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
		m_fireFrequency = m_rememberedFrequency;
		m_missileSpecialActivated = false;
	}

}
