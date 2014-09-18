package Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
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
import Entities.Projectile.Characters;

public class LongRangeSensors extends CounterMeasure implements QueryCallback
{
	int m_stealthCounter = 0;
	int m_stealthDuration = 300;
	
	ParticleEffect m_stealthEffect = new ParticleEffect();
    ParticleEffectPool m_stealthEffectPool;
    PooledEffect m_pooledstealthEffect;
    boolean m_stealthed = false;
    
	public LongRangeSensors(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings,  new Image( new Texture(Gdx.files.internal("data/sensors.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = -1f;
		m_rangeEnablersAndMultipliers[1] = -1f;
		m_rangeEnablersAndMultipliers[2] = 1f;
		m_fireFrequency = 30;
		
		m_stealthEffect.load(Gdx.files.internal("data/stealth.p"), Gdx.files.internal("data/"));
		m_stealthEffectPool = new ParticleEffectPool(m_stealthEffect, 1, 2);
		m_pooledstealthEffect = m_stealthEffectPool.obtain();
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/sensors.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		if( m_fireCounter > m_fireFrequency )
		{
			float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			m_world.QueryAABB(this, centerX - m_range,
									centerY - m_range,
									centerX + m_range,
									centerY + m_range );
			m_fireCounter = 0;
		}
		else
		{
			m_fireCounter++;
		}
		
		if( m_stealthed )
		{
			if( !m_pooledstealthEffect.isComplete())
			{
				m_pooledstealthEffect.setPosition( m_ship.m_objectXPosition, m_ship.m_objectYPosition );
				m_pooledstealthEffect.draw(renderer, 1/60f );
			}
			else
			{
				DisengageCM();
			}
		}
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_ship.m_detectionRangeReset = 5f;
		m_stealthed = true;
		m_pooledstealthEffect.reset();
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
		m_stealthed = false;
		m_ship.m_detectionRangeReset = 50f;
	}
	
	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable vc = (ViewedCollidable) fixture.getBody().getUserData();
		if( vc.m_factionCode != 0 && 
			vc.m_factionCode != m_ship.m_factionCode )
		{
			m_ship.m_trackedHostileTargets.remove( vc );
			m_ship.m_trackedHostileTargets.add( vc );
			
			if( m_specialAbilitiesActivated.get(Characters.Noel ) && Ship.class.isInstance(vc) )
			{
				Ship s = (Ship)vc;
				if(s.AttemptHack(1f))
				{
					s.m_shieldIntegrityRechargeFactor/=2;
				}
			}
		}
		
		return true;
	}

}
