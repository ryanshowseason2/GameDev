package Entities;

public class EngineBrakeEffect extends OverTimeEffect
{
	Ship m_ship;
	public EngineBrakeEffect(float counter, Ship ship )
	{
		super(counter, EffectCode.EngineBrake);
		m_ship = ship;
	}

	@Override
	public boolean Action()
	{
		m_ship.ce.EngineBrake();
		m_counter--;
		return m_counter > 0;
	}

}
