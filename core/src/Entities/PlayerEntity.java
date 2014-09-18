package Entities;

import java.util.ArrayList;
import java.util.Date;

import Utilities.AudioManager;
import Utilities.EquipChangeListener;
import Utilities.PlayerButtonListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import Entities.ViewedCollidable.DamageType;
import Entities.WingBlade.Placement;
import Equipables.CounterMeasure;
import Screens.CombatScreen.ParallaxCamera;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class PlayerEntity extends Ship implements InputProcessor, RayCastCallback, QueryCallback
{

	int m_lastKey = -1;
	long m_keyPressedMilliseconds = 0;
	ParallaxCamera m_cam;
	public PlayerButtonListener m_buttonListener;
	public EquipChangeListener m_equipChangeListener;
	public Button m_longRange;
	public Button m_mediumRange;
	public Button m_shortRange;
	public Button m_changeEquipment;
	public Window m_window;	
	public WingBlade m_leftWing;
	public WingBlade m_rightWing;
	public WingBlade m_chainSaw;
	int m_damage5SoundIndex = 0;
	int m_damage15SoundIndex = 0;
	int m_damage30SoundIndex = 0;
	int m_damage100SoundIndex = 0;
	
	public ArrayList<CounterMeasure> m_availableCMS = new ArrayList< CounterMeasure >();
	
	
	public PlayerEntity(String appearanceLocation, World world, float startX,
			float startY, float initialAngleAdjust, float maxV, ArrayList<ViewedCollidable> aliveThings, ParallaxCamera cam, Stage stage ) 
	{
		super(appearanceLocation, 5f, world, startX, startY, maxV, aliveThings, 1 );
		// TODO Auto-generated constructor stub
		
		//m_objectSprite.rotate((float) initialAngleAdjust);
		m_body.setFixedRotation(true);
		m_body.setTransform(m_body.getPosition(), (float) Math.toRadians(0 ) );
		MassData data = new MassData();
		data.mass = 10;
		m_body.setMassData(data);
		m_body.setUserData(this);		
		m_deathEffect.load(Gdx.files.internal("data/explosionred.p"), Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();	
		m_cam = cam;
		m_buttonListener = new PlayerButtonListener(this, stage );
		m_equipChangeListener = new EquipChangeListener( this );
		
		m_leftWing = new WingBlade("laserblade", m_world, m_body.getPosition().x + 4f, m_body.getPosition().y - 0f, aliveThings, 1, this );
		m_chainSaw = new WingBlade("chainblade", m_world, m_body.getPosition().x + 4f, m_body.getPosition().y - 0f, aliveThings, 1, this );
		m_chainSaw.m_placement = Placement.Front;
		m_rightWing = new WingBlade("laserblade", m_world, m_body.getPosition().x + 4f, m_body.getPosition().y - 0f, aliveThings, 1, this );
		m_rightWing.m_placement = Placement.Right;
		AudioManager.m_player = this;
		m_damage5SoundIndex = AudioManager.AddToLibrary("data/sounds/armor hit/0_5percenthit.ogg");
		m_damage15SoundIndex = AudioManager.AddToLibrary("data/sounds/armor hit/5_15percenthit.ogg");
		m_damage30SoundIndex = AudioManager.AddToLibrary("data/sounds/armor hit/15_30percenthit.ogg");
		m_damage100SoundIndex = AudioManager.AddToLibrary("data/sounds/armor hit/30percenthit.ogg");
	}
	
	
	   
   public void HandleMovement(ParallaxCamera cam)
   {
      float vel = m_body.getLinearVelocity().dst(0, 0);
      Vector2 pos = m_body.getPosition();
      Vector3 vec = new Vector3( Gdx.input.getX(0), Gdx.input.getY(0) ,0 );
      cam.unproject( vec );
      m_angleRadians = Math.atan2(vec.y - pos.y*29f, vec.x - pos.x*29f);
      
      m_angleDegrees = (float) Math.toDegrees(m_angleRadians);
      m_objectSprite.setRotation(m_angleDegrees );//rotate( (float) (difference) );
      
      //Normalize angle so that joints don't go fuckin nuts
      float bodyTransformAngle = (float) (m_angleRadians);
      m_body.setTransform(m_body.getPosition(), bodyTransformAngle );
      
      
      
      // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.A)) 
      {          
    	  ce.ThrottlePort();
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.D) ) 
      {        
    	  ce.ThrottleStarboard();
           
      }
      
            // apply left impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.S) ) 
      {          
    	  ce.ThrottleBackward();
      }

      // apply right impulse, but only if max velocity is not reached yet
      if (Gdx.input.isKeyPressed(Keys.W) ) 
      {
    	  ce.ThrottleForward();
      }
      
      // apply stopping impulse
      if (Gdx.input.isKeyPressed(Keys.X) ) 
      {
    	  ce.EngineBrake();
      }
      
      
      
      ce.ProcessVelocity();
      //m_body.applyForce( xForce, yForce, pos.x, pos.y, true);

   }

	@Override
	public boolean keyDown(int keycode) 
	{
		Date d = new Date();
		if( keycode == m_lastKey &&
			( d.getTime() - m_keyPressedMilliseconds ) < 200 &&
			me.m_boostJuice > 0 )
		{			
	      if (keycode == Keys.A ) 
	      {          
	    	  me.ManeuverPort();
	      }
	
	      // apply right impulse, but only if max velocity is not reached yet
	      if ( keycode == Keys.D ) 
	      {
	          me.ManeuverStarboard();
	      }
		      
	      // apply left impulse, but only if max velocity is not reached yet
	      if (keycode == Keys.S ) 
	      {          
	    	  me.ManeuverBackward();
	      }
	
	      // apply right impulse, but only if max velocity is not reached yet
	      if (keycode == Keys.W ) 
	      {
	    	  me.ManeuverForward();
	      }	      
		}
		
		if( keycode == Keys.W )
		{
			ce.EngageEngine();
		}
		
		if( keycode == Keys.A ||
			keycode == Keys.S ||
			keycode == Keys.D )
		{
			ce.EngageAirJets();
		}
		
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode) 
	{
		m_lastKey = keycode;
		Date d = new Date();
		m_keyPressedMilliseconds = d.getTime();
		
		if( keycode == Keys.W )
		{
			ce.DisengageEngine();			
		}
		
		if( keycode == Keys.X )
		{
			ce.DisengageBrake();			
		}
		
		if( keycode == Keys.A ||
			keycode == Keys.S ||
			keycode == Keys.D )
		{
			ce.DisengageAirJets();
		}
			
		return true;
	}
	
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		Vector3 vec = new Vector3( screenX, screenY ,0 );
		m_cam.calculateParallaxMatrix(1f, 1f);
		m_cam.unproject( vec );
		float screenXf = vec.x / 29;
		float screenYf = vec.y / 29;
		Vector2 point = new Vector2();
		point.x = screenXf;
		point.y = screenYf;
		
		m_world.rayCast(this, m_body.getPosition(), point);
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity) 
	{
		if( crashVelocity > 1 )
		{
			object2.damageIntegrity( this, crashVelocity * m_body.getMass()/ 30, DamageType.Collision );
			me.RegisterCollision();
		}
	}
	
	@Override
	public void Draw( SpriteBatch renderer )
    {
		super.Draw(renderer);	
		
		if(!m_inMenu)
		{
			float centerX = m_body.getPosition().x;
			float centerY = m_body.getPosition().y;
			m_world.QueryAABB(this, centerX - m_sensorRange / 2, centerY
					- m_sensorRange / 2, centerX + m_sensorRange / 2,
					centerY + m_sensorRange / 2);
			
			UpdateTrackedTargetsList();
		}
    }
	
	@Override
	public void damageIntegrity( ViewedCollidable damageOrigin, float damage , DamageType type)
	{
		float integrityBefore = m_integrity;		
		
		super.damageIntegrity(damageOrigin, damage, type);
		
		if( m_shieldIntegrity > 0)
		{
			
		}
		else
		{
			float damageTaken = integrityBefore - m_integrity;
			float percentageDamage = damageTaken / m_maxIntegrity * 100;
			if( percentageDamage > 0 )
			{
				if(percentageDamage < 5)
				{
					AudioManager.PlaySound(m_damage5SoundIndex, false, this );
				}
				else if( percentageDamage < 15)
				{
					AudioManager.PlaySound(m_damage15SoundIndex, false, this );
				}
				else if( percentageDamage < 30)
				{				
					AudioManager.PlaySound(m_damage30SoundIndex, false, this );
				}
				else
				{
					AudioManager.PlaySound(m_damage100SoundIndex, false, this );
				}
			}
		}
		m_integrity = m_integrity > 0 ? m_integrity : 1000;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		ViewedCollidable target = (ViewedCollidable) fixture.getBody().getUserData();
		if( target != this &&
			target.m_isTargetable &&
			target.m_factionCode != m_factionCode )
		{
			SetPlayerTarget(target);
		}
		return 1;
	}



	public void SetPlayerTarget(ViewedCollidable target)
	{
		for( int i = 0; i < m_shortRangeCMS.size(); i++ )
		{
			m_shortRangeCMS.get(i).SetTarget( target );
		}
		
		for( int i = 0; i < m_mediumRangeCMS.size(); i++ )
		{
			m_mediumRangeCMS.get(i).SetTarget( target );
		}
		
		for( int i = 0; i < m_longRangeCMS.size(); i++ )
		{
			m_longRangeCMS.get(i).SetTarget( target );
		}
		
		m_trackedHostileTargets.remove( target );
		m_trackedHostileTargets.add( target );
	}
	
	@Override
	public void AddShortRangeCounterMeasure( CounterMeasure c)
	{
		//super.AddShortRangeCounterMeasure(c);
		m_availableCMS.add(c);
		//m_shortRange.clearChildren();
		//m_shortRange.add(m_shortRangeCMS.get(0).m_icon);
		//m_window.pack();		
	}
	
	public void EquipCounterMeasure( CounterMeasure c, int rangeIndex)
	{
		if( rangeIndex == 0 )
		{
			for(int i = 0; i< m_shortRangeCMS.size(); i++ )
			{
				CounterMeasure tmp = m_shortRangeCMS.get(i);
				tmp.Unequip();
			}
			m_shortRangeCMS.clear();
			super.AddShortRangeCounterMeasure(c);
			m_shortRange.clearChildren();
			m_shortRange.add(c.GetImageCopy());
			m_window.pack();
		}
		
		if( rangeIndex == 1 )
		{
			for(int i = 0; i< m_mediumRangeCMS.size(); i++ )
			{
				CounterMeasure tmp = m_mediumRangeCMS.get(i);
				tmp.Unequip();
			}
			m_mediumRangeCMS.clear();
			super.AddMidRangeCounterMeasure(c);
			m_mediumRange.clearChildren();
			m_mediumRange.add(c.GetImageCopy());
			m_window.pack();
		}
		
		if( rangeIndex == 2 )
		{
			for(int i = 0; i< m_longRangeCMS.size(); i++ )
			{
				CounterMeasure tmp = m_longRangeCMS.get(i);
				tmp.Unequip();
			}
			m_longRangeCMS.clear();
			super.AddLongRangeCounterMeasure(c);
			m_longRange.clearChildren();
			m_longRange.add(c.GetImageCopy());
			m_window.pack();
		}
	}
	
	@Override
	public void AddMidRangeCounterMeasure( CounterMeasure c)
	{
		//super.AddMidRangeCounterMeasure(c);
		m_availableCMS.add(c);
		//m_mediumRange.clearChildren();
		//m_mediumRange.add(m_mediumRangeCMS.get(0).m_icon);
		//m_window.pack();		
	}
	
	
	@Override
	public void AddLongRangeCounterMeasure( CounterMeasure c)
	{
		//super.AddLongRangeCounterMeasure(c);
		m_availableCMS.add(c);
		//m_longRange.clearChildren();
		//m_longRange.add(m_longRangeCMS.get(0).m_icon);
		//m_window.pack();		
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable p = (ViewedCollidable) fixture.getBody().getUserData();
		
		if (p != null && 
			p.m_factionCode != m_factionCode &&
			p.m_isTargetable &&
			p.m_factionCode != 0 )
		{			
			Ship s = (Ship) fixture.getBody().getUserData();
			
			if( s != null )
			{
				if( s.m_body.getPosition().dst(m_body.getPosition()) <= s.m_detectionRange )
				{

					m_trackedHostileTargets.remove(p);
					m_trackedHostileTargets.add(p);
				}
			}
			else
			{
				m_trackedHostileTargets.remove(p);
				m_trackedHostileTargets.add(p);
			}
		}
		return true;
	}
	
	private void UpdateTrackedTargetsList()
	{
		//update tracked targets
		ArrayList<ViewedCollidable> targetsToRemove = new ArrayList<ViewedCollidable>();
		
		for( int i = 0; i< m_trackedHostileTargets.size(); i++ )
		{
			ViewedCollidable vc = m_trackedHostileTargets.get(i);
			if(	vc.m_integrity <=0 )
			{
				targetsToRemove.add(vc);
			}				
		}
		
		for( int i = 0; i< targetsToRemove.size(); i++ )
		{
			m_trackedHostileTargets.remove(targetsToRemove.get(i) );
		}
	}

}
