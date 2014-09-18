package Equipables;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import Entities.Ship;

public abstract class CruiseEngine 
{
	Ship m_ship;
	float m_jetAngle = 0;
	float m_maxVelocity;
	boolean m_enginesEngaged = false;
	boolean m_jetsEngaged = false;
	boolean m_brakesEngaged = false;
	float m_airJetAttachX;
	float m_airJetAttachY;
	float m_lastXForce;
	float m_lastYForce;
	public float m_enginePotency;
	public float m_brakePotency;
	public boolean m_hasEngines = true;
	
	ParticleEffect m_engineEffect = new ParticleEffect();
    ParticleEffectPool m_engineEffectPool;
    public PooledEffect m_pooledEngineEffect;
    
    ParticleEffect m_engineTrailEffect = new ParticleEffect();
    ParticleEffectPool m_engineTrailEffectPool;
    public PooledEffect m_pooledEngineTrailEffect;
    
    ParticleEffect m_airJetEffect = new ParticleEffect();
    ParticleEffectPool m_airJetEffectPool;
    PooledEffect m_pooledAirJetEffect;
    
	public CruiseEngine( Ship s, float maxVelocity )
	{
		m_ship = s;
		m_maxVelocity = maxVelocity;
	}
	
	public boolean EnginesEngaged(){ return m_enginesEngaged; }
	
	public abstract void ThrottleForward();
	public abstract void ThrottleBackward();
	public abstract void ThrottlePort();
	public abstract void ThrottleStarboard();
	public abstract void EngineBrake();
	public abstract void DisengageEngine();
	public abstract void EngageEngine();
	public abstract void ProcessVelocity();
	public abstract void Draw( SpriteBatch renderer );
	public abstract void EngageAirJets();
	public abstract void DisengageAirJets();

	public abstract void DisengageBrake();
	
}
