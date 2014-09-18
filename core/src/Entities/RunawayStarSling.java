package Entities;

public class RunawayStarSling extends OverTimeEffect
{

	Ship m_ship;
	public RunawayStarSling(float counter, Ship ship)
	{
		super(counter, EffectCode.RunawayStarSling);
		m_ship = ship;
	}

	@Override
	public boolean Action()
	{
		m_counter--;
		m_ship.m_body.applyForceToCenter((float)(Math.random() - .5) * m_counter*50, (float)(Math.random() - .5) * m_counter*50, true);
		
		return m_counter > 0;
	}

}
