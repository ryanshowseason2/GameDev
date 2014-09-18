package Entities;

public abstract class OverTimeEffect
{
	public enum EffectCode
	{
		EngineBrake,
		FreezeShip,
		EngineIntegrity,
		RunawayStarSling,
		EngineBrakeFailure
	}
	
	float m_counter = 60;
	EffectCode m_effectCode;
	public OverTimeEffect( float counter, EffectCode effectCode )
	{
		m_counter = counter;
		m_effectCode = effectCode;
	}
	
	public abstract boolean Action();

}
