package Entities;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.physics.box2d.World;
import Entities.Projectile.Characters;
import Entities.ViewedCollidable.DamageType;
import Equipables.ConventionalCruiseEngine;
import Equipables.ConventionalManeuverEngine;
import Equipables.CounterMeasure;
import Equipables.CruiseEngine;
import Equipables.InertialManeuverEngine;
import Equipables.ManeuverEngine;
import Equipables.TeleportManeuverEngine;

public class Ship extends ViewedCollidable
{

	public CruiseEngine ce;
	public ManeuverEngine me;
	public ArrayList<CounterMeasure> m_shortRangeCMS = new ArrayList<CounterMeasure>();
	public ArrayList<CounterMeasure> m_mediumRangeCMS = new ArrayList<CounterMeasure>();
	public ArrayList<CounterMeasure> m_longRangeCMS = new ArrayList<CounterMeasure>();
	float m_maxVelocity;
	ArrayList<ViewedCollidable> m_aliveThings;

	ParticleEffect m_shieldEffect = new ParticleEffect();
	ParticleEffectPool m_shieldEffectPool;
	PooledEffect m_pooledShieldEffect;

	float[] m_shieldDamageResistances = { 1, 1, 1, 1 };
	float[] m_shieldDamageReductions = { 0, 0, 0, 0 };
	public float m_shieldIntegrity = 1000f;
	int m_shieldRechargeDelay = 120;
	int m_shieldRechargeCounter = 0;
	public float m_shieldIntegrityRechargeFactor = 1;
	float m_sensorRange = 30;
	public ArrayList<ViewedCollidable> m_trackedHostileTargets = new ArrayList<ViewedCollidable>();
	public float m_softwareIntegrity = 1000f;
	public float m_softwareIntegrityMax = 1000f;
	int m_hackedDrawCounter = 0;
	BitmapFont m_font;
	EnumMap<Characters, Boolean> m_specialAbilitiesActivated = new EnumMap<Characters, Boolean>(
			Characters.class);
	public boolean m_isEthereal = false;
	boolean m_enteringFromSidelines = false;

	ArrayList<OverTimeEffect> m_overTimeEffects = new ArrayList<OverTimeEffect>();
	boolean m_freezeShip = false;
	protected int m_weaponsFree = 180;
	
	ParticleEffect m_starSlingEnterEffect = new ParticleEffect();
    ParticleEffectPool m_starSlingEnterEffectPool;
    PooledEffect m_pooledStarSlingEnterEffect;
    
    ParticleEffect m_starSlingExitEffect = new ParticleEffect();
    ParticleEffectPool m_starSlingExitEffectPool;
    PooledEffect m_pooledStarSlingExitEffect;

	public Ship(String appearanceLocation, float collisionScale, World world,
			float startX, float startY, float maxV,
			ArrayList<ViewedCollidable> aliveThings, int factionCode)
	{
		super(appearanceLocation, collisionScale, world, startX, startY,
				aliveThings, factionCode);
		// TODO Auto-generated constructor stub
		m_maxVelocity = maxV;
		me = new InertialManeuverEngine(this, maxV);
		ce = new ConventionalCruiseEngine(this, maxV);
		m_aliveThings = aliveThings;

		m_shieldEffect.load(Gdx.files.internal("data/shield.p"),
				Gdx.files.internal("data/" + appearanceLocation + "/"));
		m_shieldEffectPool = new ParticleEffectPool(m_shieldEffect, 1, 2);
		m_pooledShieldEffect = m_shieldEffectPool.obtain();
		
		m_starSlingEnterEffect.load(Gdx.files.internal("data/starsling.p"),
				Gdx.files.internal("data/"));
		m_starSlingEnterEffectPool = new ParticleEffectPool(m_starSlingEnterEffect, 1, 2);
		m_pooledStarSlingEnterEffect = m_starSlingEnterEffectPool.obtain();
		
		m_starSlingExitEffect.load(Gdx.files.internal("data/starslingexit.p"),
				Gdx.files.internal("data/"));
		m_starSlingExitEffectPool = new ParticleEffectPool(m_starSlingExitEffect, 1, 2);
		m_pooledStarSlingExitEffect = m_starSlingExitEffectPool.obtain();
		
		float radius = Math.max(m_objectAppearance.getWidth(), m_objectAppearance.getHeight() ) ;
		m_pooledStarSlingEnterEffect.getEmitters().get(0).getSpawnHeight().setHigh(radius);
		m_pooledStarSlingEnterEffect.getEmitters().get(0).getSpawnWidth().setHigh(radius);
		m_pooledStarSlingEnterEffect.getEmitters().get(1).getScale().setHigh(radius*2);
		m_pooledStarSlingEnterEffect.getEmitters().get(1).getScale().setLow(radius);
		m_pooledStarSlingEnterEffect.reset();
		
		m_pooledStarSlingExitEffect.getEmitters().get(0).getSpawnHeight().setHigh(radius);
		m_pooledStarSlingExitEffect.getEmitters().get(0).getSpawnWidth().setHigh(radius);
		m_pooledStarSlingExitEffect.getEmitters().get(1).getScale().setHigh(radius*2);
		m_pooledStarSlingExitEffect.getEmitters().get(1).getScale().setLow(radius);
		m_pooledStarSlingExitEffect.reset();
		
		m_detectionRange = 50f;
		m_font = new BitmapFont(Gdx.files.internal("data/font16.fnt"), false);
		m_shieldDamageReductions[DamageType.Energy.value] = 1f;
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		if( crashVelocity > 3 )
		{
			object2.damageIntegrity( this, crashVelocity * m_body.getMass()/ 30, DamageType.Collision );
			me.RegisterCollision();
		}
	}

	@Override
	public void damageIntegrity(ViewedCollidable damageOrigin, float damage, DamageType type)
	{
		damageIntegrity( damageOrigin, damage, type, false, false, false);
	}

	@Override
	public void damageIntegrity(ViewedCollidable damageOrigin, float damage, DamageType type,
			boolean bypassShieldResistances, boolean bypassShields,
			boolean bypassResistances)
	{
		float damageToIntegrity = damage;

		if (m_shieldIntegrity > 0 && !bypassShields)
		{
			if (!bypassShieldResistances)
			{
				damage = damage * m_shieldDamageResistances[type.value];
				damage = damage > m_shieldDamageReductions[type.value] ? damage
						- m_damageReductions[type.value] : 0;
			}
			damageToIntegrity = 0;
			m_shieldIntegrity = damage > m_shieldIntegrity ? 0
					: m_shieldIntegrity - damage;
		}

		if (damage > 0)
		{
			m_shieldRechargeCounter = m_shieldRechargeDelay;
		}

		 super.damageIntegrity(damageOrigin, damageToIntegrity, type,
		 bypassShieldResistances, bypassShields, bypassResistances );
	}

	public void AddShortRangeCounterMeasure(CounterMeasure c)
	{
		m_shortRangeCMS.add(c);
		m_shortRangeCMS.get(0).Equip(0);
	}

	public void AddMidRangeCounterMeasure(CounterMeasure c)
	{
		m_mediumRangeCMS.add(c);
		m_mediumRangeCMS.get(0).Equip(1);
	}

	public void AddLongRangeCounterMeasure(CounterMeasure c)
	{
		m_longRangeCMS.add(c);
		m_longRangeCMS.get(0).Equip(2);
	}

	public void ProcessCounterMeasures(SpriteBatch renderer)
	{
		for (int i = 0; i < m_shortRangeCMS.size(); i++)
		{
			m_shortRangeCMS.get(i).AcquireAndFire(renderer);
		}

		for (int i = 0; i < m_mediumRangeCMS.size(); i++)
		{
			m_mediumRangeCMS.get(i).AcquireAndFire(renderer);
		}

		for (int i = 0; i < m_longRangeCMS.size(); i++)
		{
			m_longRangeCMS.get(i).AcquireAndFire(renderer);
		}
	}
	
	public void StopCounterMeasureSound()
	{
		for (int i = 0; i < m_shortRangeCMS.size(); i++)
		{
			m_shortRangeCMS.get(i).StopSound();
		}

		for (int i = 0; i < m_mediumRangeCMS.size(); i++)
		{
			m_mediumRangeCMS.get(i).StopSound();
		}

		for (int i = 0; i < m_longRangeCMS.size(); i++)
		{
			m_longRangeCMS.get(i).StopSound();
		}
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		super.Draw(renderer);
		if (!m_inMenu)
		{

			m_detectionRange = m_detectionRangeReset;
			SetShieldColor();
			m_pooledShieldEffect.setPosition(m_objectXPosition,
					m_objectYPosition);
			m_pooledShieldEffect.getEmitters().get(0).getRotation()
					.setHigh((float) m_angleDegrees);
			m_pooledShieldEffect.getEmitters().get(0).getRotation()
					.setLow((float) m_angleDegrees);
			m_pooledShieldEffect.draw(renderer, 1f / 60f);
			
			
			
			DrawHackedIndicator(renderer);
			for (int i = 0; i < m_overTimeEffects.size(); i++)
			{
				OverTimeEffect e = m_overTimeEffects.get(i);
				if (!e.Action())
				{
					m_overTimeEffects.remove(e);
				}
			}

			if (!m_freezeShip)
			{
				if( m_weaponsFree > 0 )
				{
					ProcessCounterMeasures(renderer);
				}
				else
				{
					StopCounterMeasureSound();
				}
				ce.Draw(renderer);
				me.Draw(renderer);
				HandleShieldRecharging();				
		    	HandleBoostRecharge();			      
			}
		}

		super.Draw(renderer);
	}

	private void HandleBoostRecharge()
	{
		if( me.m_boostJuice < me.m_boostJuiceMax )
		{
		    me.m_boostJuice += me.m_boostJuiceMax * .002;
		}
	}

	private void DrawHackedIndicator(SpriteBatch renderer)
	{
		if (m_hackedDrawCounter > 0)
		{
			m_font.draw(renderer, "HACKED!", m_body.getPosition().x * 29f,
					m_body.getPosition().y * 29f + m_hackedDrawCounter % 60);
			m_hackedDrawCounter--;
		}
	}

	private void HandleShieldRecharging()
	{
		if (m_shieldIntegrity < 1000f && m_shieldRechargeCounter <= 0)
		{
			m_shieldIntegrity += m_shieldIntegrityRechargeFactor;
		} else
		{
			m_shieldRechargeCounter--;
		}

		if (m_shieldIntegrity <= 0 && m_shieldIntegrityRechargeFactor < 0)
		{
			m_shieldIntegrityRechargeFactor = 1;
		}
	}

	private void SetShieldColor()
	{
		float[] r = { 1, 1, 1, 1 };
		float shieldPercent = m_shieldIntegrity / 1000f;

		if (m_isEthereal)
		{
			// blue
			r[0] = .1f;
			r[1] = .75f;
			r[2] = 1f;
		} else if (shieldPercent > .9f)
		{
			// white
			r[0] = 1f;
			r[1] = 1f;
			r[2] = 1f;
		} else if (shieldPercent > .75f)
		{
			// blue
			r[0] = .1f;
			r[1] = .95f;
			r[2] = .1f;
		} else if (shieldPercent > .5f)
		{
			// yellow
			r[0] = 1f;
			r[1] = 1f;
			r[2] = .25f;
		} else if (shieldPercent > .3f)
		{
			// orange
			r[0] = 1f;
			r[1] = .25f;
			r[2] = .1f;
		} else if (shieldPercent > 0f)
		{
			// red
			r[0] = 1f;
			r[1] = 0f;
			r[2] = 0f;
		} else
		{
			// black no shields
			r[0] = 0f;
			r[1] = 0f;
			r[2] = 0f;
		}

		m_pooledShieldEffect.getEmitters().get(0).getTint().setColors(r);
	}

	public void IncreaseDetectionRange(float f)
	{
		m_detectionRange += f;
	}

	public boolean AttemptHack(float d)
	{
		m_softwareIntegrity -= d;
		float attempt = (float) (Math.random() * 1000);
		boolean b = attempt > m_softwareIntegrity ? true : false;
		m_hackedDrawCounter = (b ? m_hackedDrawCounter == 0 ? 60
				: m_hackedDrawCounter : m_hackedDrawCounter);
		if(b)
		{
			m_softwareIntegrity = m_softwareIntegrityMax;
		}
		return b;
	}
	
	@Override
	public boolean deathThroesDone() 
	{
		boolean b = super.deathThroesDone();
		if( b )
		{
			StopCounterMeasureSound();
		}
		
		return b;
	}

	@Override
	public void destroy()
	{
	}

	public void AddOverTimeEffect(OverTimeEffect e)
	{
		boolean found = false;
		for (int i = 0; i < m_overTimeEffects.size() && !found; i++)
		{
			OverTimeEffect tmp = m_overTimeEffects.get(i);
			if (tmp.m_effectCode == e.m_effectCode)
			{
				tmp.m_counter = e.m_counter;
				found = true;
			}
		}

		if (!found)
		{
			m_overTimeEffects.add(e);
		}
	}
}
