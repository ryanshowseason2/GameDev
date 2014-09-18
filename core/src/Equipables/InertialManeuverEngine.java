package Equipables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import Entities.Ship;

public class InertialManeuverEngine extends ManeuverEngine
{
	boolean m_dodging = false;
	float m_origxVelocity = 0;
	float m_origyVelocity = 0;
	float m_xVelocity = 0;
	float m_yVelocity = 0;
	int m_dodgeCounter = 30;
	float m_xdelta = 0f;
	float m_ydelta = 0f;
	double m_angleToEmitAt = 0f;
	
	public InertialManeuverEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		m_boostMagnitude = m_maxVelocity;
		
		m_engineEffect.load(Gdx.files.internal("data/starslingwave.p"), Gdx.files.internal("data/"));
		m_engineEffectPool = new ParticleEffectPool(m_engineEffect, 1, 2);
		m_pooledEngineEffect = m_engineEffectPool.obtain();
		m_pooledEngineEffect.getEmitters().get(0).getTransparency().setHigh(0);
		//m_pooledEngineEffect.allowCompletion();
	}
	
	public void ApplyVelocity()
	{
		m_dodging = true;
		m_origxVelocity = m_ship.m_body.getLinearVelocity().x;
		m_origyVelocity = m_ship.m_body.getLinearVelocity().y;
		
		m_ship.m_body.setLinearVelocity(m_xVelocity, m_yVelocity);
		m_dodgeCounter = 30;
	}

	@Override
	public void ManeuverForward()
	{
		if( !m_dodging )
		{			
			m_xVelocity = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
			m_yVelocity = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        
	        ApplyVelocity();	
	        //m_pooledEngineEffect.reset();
	        m_pooledEngineEffect.getEmitters().get(0).getTransparency().setHigh(1);
	        m_pooledEngineEffect.getEmitters().get(0).getAngle().setHigh(m_ship.m_angleDegrees);
	        m_pooledEngineEffect.getEmitters().get(0).getRotation().setHigh(m_ship.m_angleDegrees);
	        m_angleToEmitAt = m_ship.m_angleDegrees;
	        float radius = 150;
	        m_xdelta =  (float)(radius * Math.cos(m_ship.m_angleRadians));
	        m_ydelta =  (float)(radius * Math.sin(m_ship.m_angleRadians));
		}
	}

	@Override
	public void ManeuverBackward()
	{
		if( !m_dodging )
		{				
			m_xVelocity = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
			m_yVelocity = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        	        
	        ApplyVelocity();
	        m_pooledEngineEffect.getEmitters().get(0).getTransparency().setHigh(1);
	        //m_pooledEngineEffect.reset();
	        m_pooledEngineEffect.getEmitters().get(0).getAngle().setHigh(m_ship.m_angleDegrees - 180);
	        m_pooledEngineEffect.getEmitters().get(0).getRotation().setHigh(m_ship.m_angleDegrees - 180);

	        float radius = 150;
	        m_xdelta = -(float)(radius * Math.cos(m_ship.m_angleRadians));
	        m_ydelta = -(float)(radius * Math.sin(m_ship.m_angleRadians));
		}
	}

	@Override
	public void ManeuverPort()
	{
		if( !m_dodging )
		{
			m_xVelocity = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
			m_yVelocity = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        
	        
	        ApplyVelocity();	
	        //m_pooledEngineEffect.reset();
	        m_pooledEngineEffect.getEmitters().get(0).getTransparency().setHigh(1);
	        m_pooledEngineEffect.getEmitters().get(0).getAngle().setHigh(m_ship.m_angleDegrees + 90);
	        m_pooledEngineEffect.getEmitters().get(0).getRotation().setHigh(m_ship.m_angleDegrees + 90);
	        float radius = 150;
	        m_xdelta = (float)(radius * Math.cos(m_ship.m_angleRadians+Math.PI/2));
	        m_ydelta = (float)(radius * Math.sin(m_ship.m_angleRadians+Math.PI/2));
		}
	}

	@Override
	public void ManeuverStarboard()
	{
		if( !m_dodging )
		{
			m_xVelocity = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
			m_yVelocity = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        
	        ApplyVelocity();
	        //m_pooledEngineEffect.reset();
	        m_pooledEngineEffect.getEmitters().get(0).getTransparency().setHigh(1);
	        m_pooledEngineEffect.getEmitters().get(0).getAngle().setHigh(m_ship.m_angleDegrees - 90);
	        m_pooledEngineEffect.getEmitters().get(0).getRotation().setHigh(m_ship.m_angleDegrees - 90);
	        float radius = 150;
	        m_xdelta = -(float)(radius * Math.cos(m_ship.m_angleRadians+Math.PI/2));
	        m_ydelta = -(float)(radius * Math.sin(m_ship.m_angleRadians+Math.PI/2));
		}
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		
		if( m_dodging )
		{
			m_dodgeCounter--;
			m_ship.m_body.setLinearVelocity(m_xVelocity, m_yVelocity );
			m_boostJuice -= 1;	
			if(m_ship.ce.m_brakesEngaged)
			{
				m_dodgeCounter = 0;
			}
			
			
		}
		
		if( m_dodging && m_dodgeCounter <= 0 )
		{
			m_ship.m_body.setLinearVelocity(m_origxVelocity, m_origyVelocity );
			m_dodging = false;
			//m_pooledEngineEffect.allowCompletion();
			m_pooledEngineEffect.getEmitters().get(0).getTransparency().setHigh(0);
		}		

		{
			m_pooledEngineEffect.setPosition(m_ship.m_objectXPosition, m_ship.m_objectYPosition);
			m_pooledEngineEffect.draw(renderer, 1f/60f);
		}		
		
	}

	@Override
	public void RegisterCollision()
	{
		m_dodgeCounter = 0;		
	}

}
