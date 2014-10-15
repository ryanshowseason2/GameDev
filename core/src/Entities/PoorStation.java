package Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import Entities.EnemyShip.SeekType;
import Entities.ViewedCollidable.DamageType;
import Equipables.ConventionalManeuverEngine;
import Equipables.TeleportManeuverEngine;

public class PoorStation extends EnemyShip
{

	public PoorStation( World world, float startX, float startY, int factionCode, ArrayList<ViewedCollidable> aliveThings)
	{
		super("poorstation", 17.5f, world, startX, startY, 0, 10, factionCode, aliveThings);
		me = new ConventionalManeuverEngine(this, 10);
		m_onDeckSeekType = m_seekType = SeekType.Stationary;
		me.m_boostJuiceMax = 10000;
		MassData data = new MassData();
		data.mass = 1000;
		m_body.setMassData(data);
		ce.m_hasEngines = false;
		m_body.setAngularVelocity((float) (.5));
		m_pooledShieldEffect.getEmitters().get(0).getScale().setHigh(530);
		m_pooledShieldEffect.getEmitters().get(0).getScale().setLow(510);
	}
	
	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{		
		object2.damageIntegrity( this, (crashVelocity * 1), DamageType.Collision );
		me.RegisterCollision();		
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		m_objectSprite.setRotation((float) Math.toDegrees( m_body.getAngle() ) ); 
		m_angleRadians = m_body.getAngle();
		m_angleDegrees = (float) (m_angleRadians * 180 / Math.PI);
		super.Draw(renderer);
		if(!GetInMenu() && ! m_freezeShip )
		{			
			ce.EngineBrake();
		}
	}
	
}
