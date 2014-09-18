package Entities;

import Entities.OverTimeEffect.EffectCode;

public class EngineBrakeFailure extends OverTimeEffect
{

	Ship m_ship;
	float originalBrakeLevel = 0;
	public EngineBrakeFailure(float counter, Ship ship)
	{
		super(counter, EffectCode.EngineBrakeFailure);
		m_ship = ship;
		originalBrakeLevel = m_ship.ce.m_brakePotency;
	}

	@Override
	public boolean Action()
	{
		m_counter--;
		if( m_counter > 0)
		{
			m_ship.ce.m_brakePotency = 0;
		}
		else
		{
			m_ship.ce.m_brakePotency = originalBrakeLevel;
		}
		
		return m_counter > 0;
	}

}
