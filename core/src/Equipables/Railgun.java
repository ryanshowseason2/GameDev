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
import Entities.Projectile;
import Entities.Ship;
import Entities.ViewedCollidable;

public class Railgun extends CounterMeasure
{	
	boolean m_activated = false;
	int m_activatedFireCountdown;
	int m_gunSoundIndex = 0;
	
	public Railgun(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/railgun.png") ) ) );
		m_gunSoundIndex = AudioManager.AddToLibrary("data/railgun.ogg");
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[2] = 1f;
		m_fireFrequency = 60;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/railgun.png") ) );
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
				float centerX = m_ship.m_body.getPosition().x;
				float centerY = m_ship.m_body.getPosition().y;
				Projectile p = new Projectile("railgunbullet", 0, m_world, centerX, centerY, m_aliveThings, m_ship.m_factionCode );
				p.m_stopAtFirstShields = m_activated;
				p.m_projectileVelocity = -150;
				p.m_additionalDamage = 100;
				p.Fire(m_ship, m_target, 0);
				m_fireCounter = m_fireFrequency;
				AudioManager.PlaySound(m_gunSoundIndex, false, m_ship );
				m_ship.IncreaseDetectionRange( 5f );
				
				if(m_activated)
				{
					m_activatedFireCountdown--;
					if(m_activatedFireCountdown <=0)
					{
						DisengageCM();
					}
				}
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
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);		
		m_activated = true;
		m_activatedFireCountdown = 10;
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
		m_activated = false;
	}

}
