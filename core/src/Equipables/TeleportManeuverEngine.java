package Equipables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import Entities.Ship;

public class TeleportManeuverEngine extends ManeuverEngine
{
	boolean m_dodging = false;
	boolean m_cooling = false;
	float m_xdelta = 0f;
    float m_ydelta = 0f;
	
	public TeleportManeuverEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		m_boostMagnitude = m_maxVelocity;
		
		m_engineEffect.load(Gdx.files.internal("data/teleporstartt.p"), Gdx.files.internal("data/"));
		m_engineEffectPool = new ParticleEffectPool(m_engineEffect, 1, 2);
		m_pooledEngineEffect = m_engineEffectPool.obtain();
		m_pooledEngineEffect.allowCompletion();
		
		m_engineAfterEffect.load(Gdx.files.internal("data/teleportend.p"), Gdx.files.internal("data/"));
		m_engineAfterEffectPool = new ParticleEffectPool(m_engineAfterEffect, 1, 2);
		m_pooledEngineAfterEffect = m_engineAfterEffectPool.obtain();
		m_pooledEngineAfterEffect.allowCompletion();
	}
	
	public void ApplyTeleport()
	{
		m_dodging = true;
		m_pooledEngineEffect.allowCompletion();
		m_ship.me.m_boostJuice-=35;
	}

	@Override
	public void ManeuverForward()
	{
		if( !m_dodging && !m_cooling )
		{			
	        
			ApplyTeleport();	
	        m_pooledEngineEffect.reset();
	        

	        float radius = 25;
	        m_xdelta =  (float)(radius * Math.cos(m_ship.m_angleRadians));
	        m_ydelta =  (float)(radius * Math.sin(m_ship.m_angleRadians));
		}
	}

	@Override
	public void ManeuverBackward()
	{
		if( !m_dodging && !m_cooling )
		{				
	        	        
			ApplyTeleport();
	        m_pooledEngineEffect.reset();

	        float radius = 25;
	        m_xdelta = -(float)(radius * Math.cos(m_ship.m_angleRadians));
	        m_ydelta = -(float)(radius * Math.sin(m_ship.m_angleRadians));
		}
	}

	@Override
	public void ManeuverPort()
	{
		if( !m_dodging && !m_cooling )
		{	        	        
			ApplyTeleport();	
	        m_pooledEngineEffect.reset();
	        
	        float radius = 25;
	        m_xdelta = (float)(radius * Math.cos(m_ship.m_angleRadians+Math.PI/2));
	        m_ydelta = (float)(radius * Math.sin(m_ship.m_angleRadians+Math.PI/2));
		}
	}

	@Override
	public void ManeuverStarboard()
	{
		if( !m_dodging && !m_cooling )
		{	        
			ApplyTeleport();
	        m_pooledEngineEffect.reset();
	        
	       
	        float radius = 25;
	        m_xdelta = -(float)(radius * Math.cos(m_ship.m_angleRadians+Math.PI/2));
	        m_ydelta = -(float)(radius * Math.sin(m_ship.m_angleRadians+Math.PI/2));
		}
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		
		if( m_dodging )
		{

			m_pooledEngineEffect.setPosition(m_ship.m_objectXPosition, m_ship.m_objectYPosition);
			m_pooledEngineEffect.draw(renderer, 1f/60f);
			
			if( m_pooledEngineEffect.isComplete() )
			{
				m_ship.m_body.setTransform(m_ship.m_body.getPosition().x + m_xdelta, m_ship.m_body.getPosition().y + m_ydelta, m_ship.m_body.getAngle() );
				m_dodging = false;
				m_cooling = true;
				m_pooledEngineAfterEffect.reset();
			}
		}
		
		if( m_cooling )
		{
			m_ship.m_untargetable = true;
			m_pooledEngineAfterEffect.setPosition(m_ship.m_objectXPosition, m_ship.m_objectYPosition);
			m_pooledEngineAfterEffect.draw(renderer, 1f/60f);
			
			if( m_pooledEngineAfterEffect.isComplete() )
			{
				m_cooling = false;
				m_ship.m_untargetable = false;
			}
		}	
		
	}

	@Override
	public void RegisterCollision()
	{	
	}

}
