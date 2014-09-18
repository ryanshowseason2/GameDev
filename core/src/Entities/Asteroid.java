package Entities;

import java.util.ArrayList;

import Utilities.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import Entities.ViewedCollidable.DamageType;

public class Asteroid extends ViewedCollidable 
{
	public enum AsteroidSizeClass
	{
		PlainSize,
		Chunk,
		Full,
		Round,
		Shard
	}
	
	
	public enum AsteroidTypes
	{
		PlainType,
		Explosive,
		Rock,
		Rock2,
		Hive,
		Hive2,
		Lined,
		Lined2,
		Lesion,
		Lesion2,
		Comet,
		Comet2
	}
	
	
	
	public Asteroid(String appearanceLocation, float collisionScale, float drawScale, World world, float startX, float startY, ArrayList<ViewedCollidable> aliveThings ) {
		super(appearanceLocation, collisionScale, world, startX, startY, aliveThings, 0);
		// TODO Auto-generated constructor stub
		
		
		m_body.setUserData(this);
		//m_body.setLinearDamping(0.2f);
		m_body.setAngularDamping(0.1f);
		m_body.setAngularVelocity((float) (4*Math.random()-2));
		m_deathEffect.load(Gdx.files.internal("data/explosionwhite.p"), Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();
		
		m_damageResistances[0] = .5f;
		m_damageResistances[1] = .5f;
		
		m_damageReductions[1] = 30;
		m_damageReductions[0] = 10;
		
		m_objectSprite.scale(drawScale);
		
		m_deathSoundIndex = AudioManager.AddToLibrary("data/sounds/asteroid death/explosion2.ogg");
	}
	
	public void SetAsteroidTypeAndSize( AsteroidTypes t, AsteroidSizeClass s )
	{
		MassData data = new MassData();
		
		switch( t )
		{
		case PlainType:
			data.mass = 200;
			break;
			default:
				switch( s )
				{
					case Chunk:
						data.mass = 20;
						break;
					case Full:
						data.mass = 100;
						break;
					case Round:
						data.mass = 10;
						break;
					case Shard:
						data.mass = 3;
						break;
				}
		}
		
		data.I = .2f;
		m_body.setMassData(data);
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		// TODO Auto-generated method stub
		if( crashVelocity > 3 )
		{
			object2.damageIntegrity( this, crashVelocity + m_body.getMass() / 17f, DamageType.Collision );
		}
	}
	
	public void Draw( SpriteBatch renderer )
    {
		m_objectSprite.setRotation((float) Math.toDegrees( m_body.getAngle() ) ); 
		super.Draw(renderer);
    }

}
