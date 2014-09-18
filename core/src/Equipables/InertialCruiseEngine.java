package Equipables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Entities.Ship;

public class InertialCruiseEngine extends CruiseEngine
{
	

	public InertialCruiseEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		// TODO Auto-generated constructor stub
		m_engineEffect.load(Gdx.files.internal("data/engine.p"), Gdx.files.internal("data/"));
		m_engineEffectPool = new ParticleEffectPool(m_engineEffect, 1, 2);
		m_pooledEngineEffect = m_engineEffectPool.obtain();
		
		m_engineTrailEffect.load(Gdx.files.internal("data/enginetrail.p"), Gdx.files.internal("data/"));
				
		m_engineTrailEffectPool = new ParticleEffectPool(m_engineTrailEffect, 1, 2);
		m_pooledEngineTrailEffect = m_engineTrailEffectPool.obtain();
		
		m_airJetEffect.load(Gdx.files.internal("data/airstream.p"), Gdx.files.internal("data/"));
		
		m_airJetEffect.getEmitters().get(0).setAttached(true);
		m_airJetEffect.getEmitters().get(0).getAngle().setHigh(5f, -5f);
		m_airJetEffectPool = new ParticleEffectPool(m_airJetEffect, 1, 2);
		m_pooledAirJetEffect = m_airJetEffectPool.obtain();
		
		m_enginePotency = 3f;
		m_brakePotency = 1f;
	}
	
	public void ApplyThrust( float forceX, float forceY )
	{
		ApplyThrust(forceX, forceY, false );
	}
	
	public void ApplyThrust( float forceX, float forceY, boolean brakingOrForwardMotion )
	{
		float vel = m_ship.m_body.getLinearVelocity().dst(0, 0);
		
		if( Math.abs(vel) >= m_maxVelocity )
		{
			  if( m_ship.m_body.getLinearVelocity().x > 0 && forceX > 0 ||
					  m_ship.m_body.getLinearVelocity().x < 0 && forceX < 0 )
			  {
				  forceX = 0;
			  }
			  
			  if( m_ship.m_body.getLinearVelocity().y > 0 && forceY > 0 ||
					  m_ship.m_body.getLinearVelocity().y < 0 && forceY < 0 )
			  {
				  forceY = 0;
			  }
			  
			  m_ship.m_body.setLinearDamping( 1f );
		}   
		else
		{
			m_ship.m_body.setLinearDamping(0f);
		}
		
		if( !brakingOrForwardMotion )
		{
			m_lastXForce = forceX;
			m_lastYForce = forceY;
		}
		
		m_ship.m_body.applyLinearImpulse(forceX, forceY, 0f, 0f, true);
	}

	@Override
	public void ThrottleForward()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(m_enginePotency * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(m_enginePotency * Math.sin(m_ship.m_angleRadians));
        
        ApplyThrust( xForce, yForce, true );
	}

	@Override
	public void ThrottleBackward()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(-m_enginePotency * Math.cos(m_ship.m_angleRadians));
        yForce = (float)(-m_enginePotency * Math.sin(m_ship.m_angleRadians));
        
        float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
        m_airJetAttachX = m_ship.m_body.getPosition().x*29f +(float)(radius * Math.cos(m_ship.m_angleRadians));
        m_airJetAttachY = m_ship.m_body.getPosition().y*29f +(float)(radius * Math.sin(m_ship.m_angleRadians));
        
	    ApplyThrust( xForce, yForce );
	}

	@Override
	public void ThrottlePort()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce =  (float)(-m_enginePotency * Math.sin(m_ship.m_angleRadians));
        yForce =  (float)(m_enginePotency * Math.cos(m_ship.m_angleRadians));
	    ApplyThrust( xForce, yForce );
	    
	    float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
	    m_airJetAttachX = m_ship.m_body.getPosition().x*29f +(float)(radius * Math.sin(m_ship.m_angleRadians));
	    m_airJetAttachY = m_ship.m_body.getPosition().y*29f -(float)(radius * Math.cos(m_ship.m_angleRadians));
	}

	@Override
	public void ThrottleStarboard()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(m_enginePotency * Math.sin(m_ship.m_angleRadians));
        yForce = (float)(-m_enginePotency * Math.cos(m_ship.m_angleRadians));
	    ApplyThrust( xForce, yForce );
	    
	    float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
        m_airJetAttachX = m_ship.m_body.getPosition().x*29f -(float)(radius * Math.sin(m_ship.m_angleRadians));
        m_airJetAttachY = m_ship.m_body.getPosition().y*29f +(float)(radius * Math.cos(m_ship.m_angleRadians));
	}

	@Override
	public void DisengageEngine()
	{
		// TODO Auto-generated method stub
		m_enginesEngaged = false;
		m_pooledEngineEffect.reset();
	}

	@Override
	public void EngageEngine()
	{
		// TODO Auto-generated method stub
		m_enginesEngaged = true;
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		// TODO Auto-generated method stub
		float radius = Math.max(m_ship.m_objectAppearance.getWidth() / 2, m_ship.m_objectAppearance.getHeight() ) / 2;
		float xdelta = (float) (Math.cos(m_ship.m_angleRadians) * radius);
		float ydelta = (float) (Math.sin(m_ship.m_angleRadians) * radius);
		

		if( m_enginesEngaged && m_hasEngines )
		{						
			m_pooledEngineEffect.setPosition( m_ship.m_objectXPosition - xdelta, m_ship.m_objectYPosition - ydelta );
			m_pooledEngineEffect.draw(renderer, 1f/60f);
			m_ship.IncreaseDetectionRange( 1f );
		}
		
		if( m_ship.m_integrity > 0 && m_hasEngines  )
		{
			m_pooledEngineTrailEffect.setPosition( m_ship.m_objectXPosition - xdelta, m_ship.m_objectYPosition - ydelta );
			m_pooledEngineTrailEffect.draw(renderer, 1f/60f);
		}
		
		if( m_jetsEngaged  )
		{
			m_ship.IncreaseDetectionRange( 1f );
			
			m_pooledAirJetEffect.setPosition( m_airJetAttachX, m_airJetAttachY );
			m_pooledAirJetEffect.getEmitters().get(0).getAngle().setHigh(m_ship.m_angleDegrees-5+ m_jetAngle, m_ship.m_angleDegrees+5+ m_jetAngle);			
			m_pooledAirJetEffect.draw(renderer, 1f/60f);
		}
		
		if( m_pooledAirJetEffect.isComplete() )
		{
			m_jetsEngaged = false;
		}
		
		if( m_brakesEngaged )
		{
			
		}
		
		
	}

	@Override
	public void EngineBrake()
	{
		// TODO Auto-generated method stub
		float xForce = 0;
	    float yForce = 0;
	    xForce = (float)(-m_brakePotency * m_ship.m_body.getLinearVelocity().x);
        yForce = (float)(-m_brakePotency * m_ship.m_body.getLinearVelocity().y);
	    ApplyThrust( xForce, yForce, true );
	    m_brakesEngaged = true;
	}

	@Override
	public void ProcessVelocity()
	{
		// TODO Auto-generated method stub
		float vel = m_ship.m_body.getLinearVelocity().dst(0, 0);
		
		if( vel < m_maxVelocity )
		{
			m_ship.m_body.setLinearDamping(0f);
		}
	}

	@Override
	public void EngageAirJets()
	{
		// TODO Auto-generated method stub
		m_jetsEngaged = true;
		m_pooledAirJetEffect.reset();
				
	}

	@Override
	public void DisengageAirJets()
	{
		// TODO Auto-generated method stub
		
		m_pooledAirJetEffect.allowCompletion();
	}

	@Override
	public void DisengageBrake()
	{
		// TODO Auto-generated method stub
		m_brakesEngaged = false;
	}
}
