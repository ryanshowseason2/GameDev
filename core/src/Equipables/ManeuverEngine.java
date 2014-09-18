package Equipables;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import Entities.Ship;

public abstract class ManeuverEngine 
{
	Ship m_ship;
	public float m_maxVelocity;
	public float m_boostJuice = 100;
	public float m_boostMagnitude = 15000f;
	ParticleEffect m_engineEffect = new ParticleEffect();
    ParticleEffectPool m_engineEffectPool;
    PooledEffect m_pooledEngineEffect;
    
    ParticleEffect m_engineAfterEffect = new ParticleEffect();
    ParticleEffectPool m_engineAfterEffectPool;
    PooledEffect m_pooledEngineAfterEffect;
	public float m_boostJuiceMax = 100;
    
	public ManeuverEngine( Ship s, float maxVelocity )
	{
		m_ship = s;
		m_maxVelocity = maxVelocity;
	}
	
	public abstract void ManeuverForward();
	public abstract void ManeuverBackward();
	public abstract void ManeuverPort();
	public abstract void ManeuverStarboard();
	public abstract void Draw( SpriteBatch renderer );
	public abstract void RegisterCollision();
}
