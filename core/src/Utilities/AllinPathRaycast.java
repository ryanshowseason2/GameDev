package Utilities;

import java.util.ArrayList;

import Entities.ViewedCollidable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class AllinPathRaycast extends LaserRayCastBase implements RayCastCallback
{
	Body m_self;
	public float m_range;
	Vector2 m_contactPoint;
	ArrayList<ViewedCollidable> m_hitEntities = new ArrayList<ViewedCollidable>();
	ArrayList<Vector2> m_hitPoints = new ArrayList<Vector2>();
	public AllinPathRaycast(  Body self, float range )
	{
		m_self = self;
		m_range = range;
		Reset();
	}
	
	public void Reset()
	{
		m_hitEntities.clear();
		m_hitPoints.clear();
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		ViewedCollidable navigator = (ViewedCollidable) m_self.getUserData();
		ViewedCollidable inTheWay = (ViewedCollidable) fixture.getBody().getUserData();
		float distance = navigator.m_body.getPosition().dst(point);
		if( fixture.getBody() != m_self &&
			!inTheWay.m_ignoreForPathing &&
			inTheWay.m_factionCode != navigator.m_factionCode )
		{
			m_hitEntities.add( inTheWay );
			m_hitPoints.add( point );
		}
		return 1;
	}

	@Override
	public float GetDistanceTraveled()
	{
		// TODO Auto-generated method stub
		return m_range;
	}

	@Override
	public ArrayList<ViewedCollidable> GetEntitiesHit()
	{
		return m_hitEntities;
	}

}
