package Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import Entities.Ship;
import Entities.ViewedCollidable;

public class NoWeapon extends CounterMeasure
{

	public NoWeapon(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings, new Image( new Texture(Gdx.files.internal("data/unselected.png") ) ));
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = 1f;
		m_rangeEnablersAndMultipliers[1] = 1f;
		m_rangeEnablersAndMultipliers[2] = 1f;
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void EngageCM( Button b )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void DisengageCM()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Image GetImageCopy()
	{
		// TODO Auto-generated method stub
		return new Image( new Texture(Gdx.files.internal("data/unselected.png") ) );
	}

}
