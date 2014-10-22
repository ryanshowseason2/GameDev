package Equipables;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import Entities.Ship;
import Entities.ViewedCollidable;
import Entities.Projectile.Characters;

public abstract class CounterMeasure
{
	float m_range = 20;
	World m_world;
	Ship m_ship;
	ViewedCollidable m_target = null;
	ArrayList<ViewedCollidable> m_aliveThings;
	Button m_buttonActivatedOn;
	public Image m_icon;
	public float[] m_rangeEnablersAndMultipliers = {0,0,0};
	int m_fireFrequency = 6;
	int m_fireCounter = 0;
	EnumMap<Characters, Boolean> m_specialAbilitiesActivated = new EnumMap<Characters, Boolean>(Characters.class);
	
	public CounterMeasure( World w, Ship s, ArrayList<ViewedCollidable> aliveThings, Image icon )
	{
		m_world = w;
		m_ship = s;
		m_aliveThings = aliveThings;
		m_icon = icon;
		PopulateSpecials();
	}
	
	private void PopulateSpecials()
	{
		m_specialAbilitiesActivated.put(Characters.Sandy, false);
		m_specialAbilitiesActivated.put(Characters.Gourt, false);
		m_specialAbilitiesActivated.put(Characters.Noel, false);
		m_specialAbilitiesActivated.put(Characters.Shavret, false);
		m_specialAbilitiesActivated.put(Characters.Bobbi, false);
		m_specialAbilitiesActivated.put(Characters.SSid, false);
		m_specialAbilitiesActivated.put(Characters.Belice, false);
		m_specialAbilitiesActivated.put(Characters.Yashpal, false);
	}
	
	public void SetSpecials( EnumMap<Characters, Boolean> specialAbilitiesActivated )
	{
		m_specialAbilitiesActivated = specialAbilitiesActivated;		
	}
	
	public void Equip( int rangeIndex )
	{
		m_range = 20 * m_rangeEnablersAndMultipliers[rangeIndex] * (rangeIndex + 1);
	}
	
	public abstract void AcquireAndFire(SpriteBatch renderer);
	
	public void EngageCM( Button b )
	{
		m_buttonActivatedOn = b;
		b.setDisabled(true);
		b.clearChildren();
		b.add( new Image( new Texture(Gdx.files.internal("data/time.png") ) ) );
	}
	public void DisengageCM()
	{
		m_buttonActivatedOn.setDisabled(false);
		m_buttonActivatedOn.clearChildren();
		m_buttonActivatedOn.add( GetImageCopy() );
	}
	
	public abstract Image GetImageCopy();

	public void SetTarget( ViewedCollidable target )
	{
		m_target = target;
	}

	public void Unequip()
	{
		StopSound();
	}
	
	public void StopSound()
	{
		// Nothing to do in base class
	}

	
}
