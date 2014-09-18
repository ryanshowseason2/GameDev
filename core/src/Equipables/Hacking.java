package Equipables;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import Entities.Projectile;
import Entities.Ship;
import Entities.ViewedCollidable;

public class Hacking extends CounterMeasure
{
	ViewedCollidable m_secondaryTarget = null;
	int m_activateSecondaryMode = 0;
	
	public Hacking(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings,  new Image( new Texture(Gdx.files.internal("data/hacking.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[0] = 1f;
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/hacking.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_activateSecondaryMode = 120;
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
	}



}
