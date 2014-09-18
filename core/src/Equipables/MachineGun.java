package Equipables;

import java.util.ArrayList;

import Utilities.AudioManager;

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
import Entities.PlayerEntity;
import Entities.Projectile;
import Entities.Ship;
import Entities.ViewedCollidable;

public class MachineGun extends CounterMeasure
{
	ViewedCollidable m_secondaryTarget = null;
	int m_activateSecondaryMode = 0;
	int m_secondaryFireFrequency = 6;
	int m_secondaryFireCounter = 0;
	int m_gunSoundIndex = 0;
	long m_gunInstanceID = -1;
	
	public MachineGun(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/machinegun.png") ) ) );
		m_gunSoundIndex = AudioManager.AddToLibrary("data/machinegun.ogg");
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = 1f;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/machinegun.png") ) );
	}
	
	@Override
	public void StopSound()
	{
		AudioManager.StopSound(m_gunSoundIndex, m_gunInstanceID);
		m_gunInstanceID = -1;
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
				if( vc != null )
				{
					float distance = vc.m_body.getPosition().dst(m_ship.m_body.getPosition());
					if( distance <= m_range && distance < leastDistance )
					{
						leastDistance = distance;
						m_target = vc;
					}
				}
			}
		}
		
		if( m_target != null && m_fireCounter <= 0 )
		{
			float distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );

			if( distanceToCurrentTarget <= m_range )
			{
				float centerX = m_ship.m_body.getPosition().x;
				float centerY = m_ship.m_body.getPosition().y;
				Projectile p = new Projectile("bullet", 0, m_world, centerX, centerY, m_aliveThings, m_ship.m_factionCode );				
				p.Fire(m_ship, m_target, (float) Math.random()/2 - .25f);
				m_fireCounter = m_fireFrequency;
				m_ship.IncreaseDetectionRange( 5f );
				
				if (m_gunInstanceID == -1 )
				{
					m_gunInstanceID = AudioManager.PlaySound(m_gunSoundIndex, true, m_ship );
				}
				else
				{
					AudioManager.AdjustSound(m_gunSoundIndex, m_gunInstanceID, m_ship);
				}
			}
			else
			{
				m_target = null;
				if (m_gunInstanceID != -1 )
				{
					AudioManager.StopSound(m_gunSoundIndex, m_gunInstanceID);
					m_gunInstanceID = -1;
				}
			}
		}
		else
		{
			m_fireCounter-= 1;
		}
		
		if( m_activateSecondaryMode > 0 )
		{
			if( m_activateSecondaryMode > 40 )
			{
				if( ( m_secondaryTarget != null && m_secondaryTarget.m_integrity <= 0 ) )
				{
					m_secondaryTarget = null;
				}
				
				if( m_secondaryTarget == null )
				{
					if( m_target != null )
					{
						m_secondaryTarget = m_target;
						m_target = null;
					}
				}
				
				if( m_secondaryTarget != null && m_secondaryFireCounter <= 0 )
				{
					float distanceToCurrentTarget = m_secondaryTarget.m_body.getPosition().dst(m_ship.m_body.getPosition() );
		
					if( distanceToCurrentTarget <= m_range )
					{
						float centerX = m_ship.m_body.getPosition().x;
						float centerY = m_ship.m_body.getPosition().y;
						Projectile p = new Projectile("bullet", 0, m_world, centerX, centerY, m_aliveThings, m_ship.m_factionCode );				
						p.Fire(m_ship, m_secondaryTarget, (float) Math.random()/2 - .25f);
						m_secondaryFireCounter = m_secondaryFireFrequency;
						m_ship.IncreaseDetectionRange( 5f );					
					}
					else
					{
						m_secondaryTarget = null;
					}
				}
				else
				{
					m_secondaryFireCounter-= 1;
				}
			}
			
			m_activateSecondaryMode--;
			
			if(m_activateSecondaryMode <= 0)
			{
				DisengageCM();
			}
		}
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_activateSecondaryMode = 280;
		m_secondaryFireFrequency = m_fireFrequency;
		m_secondaryFireCounter = m_fireCounter = 0;				
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();		
	}

}
