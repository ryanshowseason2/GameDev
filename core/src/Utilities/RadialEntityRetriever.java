package Utilities;

import java.util.ArrayList;

import Entities.ViewedCollidable;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

public class RadialEntityRetriever implements QueryCallback
{
	public ArrayList<ViewedCollidable> m_detectedEntities = new ArrayList<ViewedCollidable>();
	public RadialEntityRetriever( World m_world, float radius, float centerX, float centerY )
	{
		m_world.QueryAABB(this, centerX - radius,
								centerY - radius,
								centerX + radius,
								centerY + radius );	
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		m_detectedEntities.remove((ViewedCollidable) fixture.getBody().getUserData());
		m_detectedEntities.add( (ViewedCollidable) fixture.getBody().getUserData());
		return true;
	}

}
