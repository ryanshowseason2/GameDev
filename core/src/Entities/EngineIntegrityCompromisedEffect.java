package Entities;

import Entities.ViewedCollidable.DamageType;

public class EngineIntegrityCompromisedEffect extends OverTimeEffect
{
	Ship m_ship;
	public EngineIntegrityCompromisedEffect(float counter, Ship ship)
	{
		super(counter, EffectCode.EngineIntegrity);
		m_ship = ship;
	}

	@Override
	public boolean Action()
	{
		if( m_ship.ce.EnginesEngaged() )
		{
			m_ship.damageIntegrity( null, 1, DamageType.Explosion, true, true, true );
		}
		m_counter--;
		return m_counter > 0;
	}

}
