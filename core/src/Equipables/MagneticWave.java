package Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import Entities.EngineBrakeEffect;
import Entities.EngineIntegrityCompromisedEffect;
import Entities.FreezeShip;
import Entities.Projectile;
import Entities.Ship;
import Entities.ViewedCollidable;
import Entities.Projectile.Characters;
import Entities.ViewedCollidable.DamageType;

public class MagneticWave extends CounterMeasure implements QueryCallback
{	
	float m_potency = -32f;
	float m_engagedMultiplier = 50f;
	boolean m_engaged = false;
	float m_beliceSpecialMultiplier = 1f;
	ParticleEffect m_waveEffect = new ParticleEffect();
    ParticleEffectPool m_waveEffectPool;
    PooledEffect m_pooledWaveeEffect;
    
    ParticleEffect m_activatedWaveEffect = new ParticleEffect();
    ParticleEffectPool m_activatedWaveEffectPool;
    PooledEffect m_pooledActivatedWaveeEffect;
    
    ParticleEffect m_sandEffect = new ParticleEffect();
    ParticleEffectPool m_sandEffectPool;
    PooledEffect m_pooledSandEffect;
	
	public MagneticWave(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/magnet.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = 1f;
		m_rangeEnablersAndMultipliers[2] = -1f;
		
		m_waveEffect.load(Gdx.files.internal("data/magemit.p"), Gdx.files.internal("data/"));
		m_waveEffectPool = new ParticleEffectPool(m_waveEffect, 1, 2);
		m_pooledWaveeEffect = m_waveEffectPool.obtain();
		
		m_activatedWaveEffect.load(Gdx.files.internal("data/magwaveactivated.p"), Gdx.files.internal("data/"));
		m_activatedWaveEffectPool = new ParticleEffectPool(m_activatedWaveEffect, 1, 2);
		m_pooledActivatedWaveeEffect = m_activatedWaveEffectPool.obtain();
		
		m_sandEffect.load(Gdx.files.internal("data/sandpelt.p"), Gdx.files.internal("data/"));
		m_sandEffectPool = new ParticleEffectPool(m_sandEffect, 1, 2);
		m_pooledSandEffect = m_sandEffectPool.obtain();
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/magnet.png") ) );
	}

	


	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_potency *= m_engagedMultiplier;	
		m_engaged = true;
		
		if( m_specialAbilitiesActivated.get(Characters.Belice ) )
		{
			m_beliceSpecialMultiplier = -.01f;
		}
		m_pooledActivatedWaveeEffect.reset();
		
			
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
		m_engaged = false;
		
		if( m_specialAbilitiesActivated.get(Characters.Belice ) )
		{
			m_beliceSpecialMultiplier = 1f;
		}
		
		if( m_specialAbilitiesActivated.get(Characters.Gourt ) )
		{
			Vector2 gravity = new Vector2();
			gravity.x =  0;
			gravity.y =  0;
			m_ship.m_world.setGravity(gravity);
		}
		
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		Body potentialTarget = fixture.getBody();
		float distanceToPotential = potentialTarget.getPosition().dst(m_ship.m_body.getPosition() );
		ViewedCollidable vc = (ViewedCollidable) potentialTarget.getUserData();
				
		if( potentialTarget != m_ship.m_body && 
			distanceToPotential <= m_range &&
			m_ship.m_factionCode != vc.m_factionCode )
		{
			float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			float targetCenterX = vc.m_body.getPosition().x;
			float targetCenterY = vc.m_body.getPosition().y;
			double angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX);
			float xForce =  (float)( m_beliceSpecialMultiplier*m_potency * Math.cos(angleRadians) / distanceToPotential);
	        float yForce =  (float)( m_beliceSpecialMultiplier*m_potency * Math.sin(angleRadians) / distanceToPotential);
	        if( m_engaged )
	        {
	        	vc.m_body.applyLinearImpulse(xForce, yForce, vc.m_body.getPosition().x, vc.m_body.getPosition().y, true);
	        }
	        else
	        {
	        	vc.m_body.applyForceToCenter(xForce, yForce, true);
	        }
	        
	        YashpalMagWaveSpecial(vc);
	        
	        SSidMagWaveSpecial(vc);
	        
	        BobbiMagWaveSpecial(vc);
	        
	        ShavretMagWaveSpecial(vc);
	        
	        NoelsMagwaveSpecial(vc);
	        
	        SandyMagWaveSpecial(vc);	        
		}
		
		return true;
	}

	private void SandyMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.Sandy ) )
		{
			vc.damageIntegrity( m_ship, .5f, DamageType.Collision );
		}
	}

	private void NoelsMagwaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.Noel ) &&
			m_engaged &&
			Ship.class.isInstance(vc) )
		{
			Ship ship = (Ship) vc;
			if(ship.AttemptHack(.1f))
			{
				ship.AddOverTimeEffect( new FreezeShip(300, ship));
			}
		}
	}

	private void ShavretMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.Shavret ) &&
			m_engaged )
		{
			double damage = Math.sqrt( Math.sqrt( vc.m_body.getLinearVelocity().x * vc.m_body.getLinearVelocity().x + vc.m_body.getLinearVelocity().y * vc.m_body.getLinearVelocity().y ) );
			vc.damageIntegrity( m_ship, (float) damage, DamageType.Penetration );
			vc.m_body.setLinearVelocity(0, 0);
		}
	}

	private void BobbiMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.Bobbi ) &&
			m_engaged &&
			Ship.class.isInstance(vc) )
		{
			Ship ship = (Ship) vc;
			ship.AddOverTimeEffect( new EngineIntegrityCompromisedEffect(60, ship ));
		}
	}

	private void SSidMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.SSid ) &&
			Ship.class.isInstance(vc) )
		{
			Ship ship = (Ship) vc;
			
			if( ship.AttemptHack( .1f ) )
			{
				ship.AddOverTimeEffect( new EngineBrakeEffect(420f, ship ));
			}
		}
	}

	private void YashpalMagWaveSpecial(ViewedCollidable vc)
	{
		if( m_engaged &&
			m_specialAbilitiesActivated.get(Characters.Yashpal ) &&
			Ship.class.isInstance(vc) )
		{
			Ship ship = (Ship) vc;
			if (ship.me.m_boostJuice > 0 &&
				m_ship.me.m_boostJuice < 100 )
			{
				m_ship.me.m_boostJuice++;
				ship.me.m_boostJuice--;
			}
		}
	}

	@Override
	public void AcquireAndFire(SpriteBatch renderer)
	{
		float centerX = m_ship.m_body.getPosition().x;
		float centerY = m_ship.m_body.getPosition().y;
		
		
		m_pooledWaveeEffect.setPosition( m_ship.m_objectXPosition, m_ship.m_objectYPosition );
		m_pooledWaveeEffect.draw(renderer, 1f/60f );
		
		if( m_specialAbilitiesActivated.get(Characters.Sandy ) )
		{
			m_pooledSandEffect.setPosition( m_ship.m_objectXPosition, m_ship.m_objectYPosition );
			m_pooledSandEffect.draw(renderer, 1f/60f );
		}
		
		if(m_engaged)
		{
			m_pooledActivatedWaveeEffect.setPosition( m_ship.m_objectXPosition, m_ship.m_objectYPosition );
			m_pooledActivatedWaveeEffect.draw(renderer, 1f/60f);
		}
		
		m_world.QueryAABB(this, centerX - m_range / 2,
								centerY - m_range / 2,
								centerX + m_range / 2,
								centerY + m_range / 2 );

		GourtsMagWaveSpecial();
		
		if( m_potency < -12 )
		{
			m_potency+= 2;
		}
		else if( m_engaged )
		{
			DisengageCM();
			m_potency = -12;
		}
	}

	private void GourtsMagWaveSpecial()
	{
		if( m_specialAbilitiesActivated.get(Characters.Gourt ) &&
			m_engaged )
		{
			Vector2 gravity = new Vector2();
			gravity.x =  (float)( -m_potency * Math.cos(m_ship.m_angleRadians));
			gravity.y =  (float)( -m_potency * Math.sin(m_ship.m_angleRadians) );
			m_ship.m_world.setGravity(gravity);
		}
	}

}
