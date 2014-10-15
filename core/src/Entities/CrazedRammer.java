package Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import Entities.EnemyShip.SeekType;
import Entities.ViewedCollidable.DamageType;
import Equipables.ConventionalManeuverEngine;
import Equipables.TeleportManeuverEngine;

public class CrazedRammer extends EnemyShip
{

	public CrazedRammer( World world, float startX, float startY, int factionCode, ArrayList<ViewedCollidable> aliveThings)
	{
		super("crazedrammer", 3.5f, world, startX, startY, 0, 40, factionCode, aliveThings);
		me = new ConventionalManeuverEngine(this, 40);
		m_onDeckSeekType = m_seekType = SeekType.EnterFiringRange;
		me.m_boostJuiceMax = 100;
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		super.Draw(renderer);
		if(!GetInMenu() && ! m_freezeShip )
		{
			
			if(m_weaponsFree > 0)
			{
				m_onDeckSeekType = m_seekType = SeekType.RamTarget;
				
				if( m_body.getLinearVelocity().len() < 50)
				{
					me.ManeuverForward();
				}
			}
			else
			{
				m_onDeckSeekType = m_seekType = SeekType.EnterFiringRange;
			}
		}
	}
	
	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		if( m_weaponsFree > 0 )
		{
			object2.damageIntegrity( this, (crashVelocity * m_body.getMass()/ 20), DamageType.Collision );
			me.RegisterCollision();
		}
		else if( crashVelocity > 3 )
		{
			object2.damageIntegrity( this, crashVelocity * m_body.getMass()/ 30, DamageType.Collision );
			me.RegisterCollision();
		}
	}
}
