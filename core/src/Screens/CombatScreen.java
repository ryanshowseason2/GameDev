package Screens;

import java.util.ArrayList;

import Entities.Asteroid;
import Entities.CivilianShuttle;
import Entities.CivilianShuttle.CivilianBehavior;
import Entities.CivilianShuttle.WaypointUpdateType;
import Entities.CrazedRammer;
import Entities.EnemyShip;
import Entities.GameCharacter;
import Entities.PlayerEntity;
import Entities.PoorStation;
import Entities.ViewedCollidable;
import Entities.WingBlade;
import Equipables.Hacking;
import Equipables.Laser;
import Equipables.LongRangeSensors;
import Equipables.MachineGun;
import Equipables.MagneticWave;
import Equipables.Missile;
import Equipables.NoWeapon;
import Equipables.Railgun;
import Equipables.WingBlades;
import Utilities.ClutterSpawner;
import Utilities.MydebugRenderer;
import Utilities.TimedMessage;
import Utilities.VisualNovelImageState;
import Utilities.VisualNovelImageState.ImageJustification;
import Utilities.VisualNovelStyleMessage;
import box2dlight.ConeLight;
import box2dlight.Light;
import box2dlight.PointLight;
import box2dlight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class CombatScreen extends OrionScreen implements ContactListener
{
	public class ParallaxCamera extends OrthographicCamera {
		Matrix4 parallaxView = new Matrix4();
		Matrix4 parallaxCombined = new Matrix4();
		Vector3 tmp = new Vector3();
		Vector3 tmp2 = new Vector3();		

		public ParallaxCamera (float viewportWidth, float viewportHeight) {
			super(viewportWidth, viewportHeight);
		}

		public Matrix4 calculateParallaxMatrix (float parallaxX, float parallaxY) {
			update();
			tmp.set(position);
			tmp.x *= parallaxX;
			tmp.y *= parallaxY;

			parallaxView.setToLookAt(tmp, tmp2.set(tmp).add(direction), up);
			parallaxCombined.set(projection);
			Matrix4.mul(parallaxCombined.val, parallaxView.val);
			return parallaxCombined;
		}
	}
	
	private ParallaxCamera cam;
	private Texture background;
	private Texture parralax1;
	private Texture parralax2;
	private SpriteBatch spriteBatch;
	public static int WIDTH  = 1024;
    public static int HEIGHT = 768;
    private Rectangle glViewport;
    BitmapFont font;
    public PlayerEntity player;
    World w;
    MydebugRenderer debugRenderer = new MydebugRenderer();
    Asteroid asty;
    ArrayList<ViewedCollidable> m_deadThings = new ArrayList<ViewedCollidable>();
	ArrayList<ViewedCollidable> m_aliveThings = new ArrayList<ViewedCollidable>();
	RayHandler rayHandler;
	EnemyShip shippy;
	CivilianShuttle cvs;
	
	Skin skin;
	Stage m_stage;
	Texture m_defaultButtonTexture;
	Texture m_changeEquipmentTexture;
	Button m_longRange;
	Button m_mediumRange;
	Button m_shortRange;
	Button m_changeEquipment;
	InputMultiplexer m_inputSplitter = new InputMultiplexer();
	ArrayList<EnemyIndicatorButton> m_enemyButtons = new ArrayList<EnemyIndicatorButton>();
	Dialog m_nonBlockMessages;	
	Label m_timedMessageText;
	Image m_timedImage;
	ArrayList< TimedMessage > m_timedMessages = new ArrayList< TimedMessage >();
	
	Dialog m_visualNovelStyleMessage;
	Label m_visualNovelStyleMessageText;
	Button m_VisualNovelStyleMessageButton;
	ArrayList< VisualNovelStyleMessage > m_visualNovelStyleMessageList = new ArrayList< VisualNovelStyleMessage >();
	
	ParticleEffect m_energyIndicator = new ParticleEffect();
	ParticleEffectPool m_energyIndicatorPool;
	PooledEffect m_pooledEnergyIndicator;
	
	ParticleEffect m_integrityIndicator = new ParticleEffect();
	ParticleEffectPool m_integrityIndicatorPool;
	PooledEffect m_pooledIntegrityIndicator;
	
	public CombatScreen()
	{
		background = new Texture(Gdx.files.internal("data/background1.jpg"));
		background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		parralax1 = new Texture(Gdx.files.internal("data/slide.png"));
		parralax1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		parralax2 = new Texture(Gdx.files.internal("data/slide.png"));
		parralax2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		spriteBatch = new SpriteBatch();
		cam = new ParallaxCamera(WIDTH, HEIGHT);        
        cam.position.set(WIDTH / 2, HEIGHT / 2, 0);
        font = new BitmapFont(Gdx.files.internal("data/font16.fnt"), false);
        w = new World(new Vector2(0,0), true );
        m_stage = new Stage();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        Dialog window = new Dialog("", skin);
        m_nonBlockMessages = new Dialog("", skin);
        m_visualNovelStyleMessage = new Dialog("", skin);
        player = new PlayerEntity("playership", w, 0, -205, -90, 40f, m_aliveThings, cam, m_stage);
        m_aliveThings.remove( player );
        asty = new Asteroid("asteroid", 4.5f, .1f, w, 0, 70, m_aliveThings );
        //asty = new Asteroid("asteroid", 4.5f, .1f, w, 5, 40, m_aliveThings );
        //asty = new Asteroid("asteroid", 4.5f, .1f, w, 10, 40, m_aliveThings );
        //asty = new Asteroid("asteroid", 4.5f, .1f, w, 15, 40, m_aliveThings );
        
        //asty = new Asteroid("asteroid", 4.5f, .1f, w, 7, 60, m_aliveThings );
        
        Asteroid a = new Asteroid( "shardcomet", 1.4f, .4f, w, 0f, 0f, m_aliveThings );
        
        ClutterSpawner c = new ClutterSpawner( w, m_aliveThings);
        c.SpawnAsteroidsFromImage("data/asteroidspawnmap.png", 20, 15, 5);
        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
        w.setContactListener(this);
        //shippy = new EnemyShip( "crazedrammer", 3.5f,w, 0, 50, 0, 50, 2, m_aliveThings );
        shippy = new CrazedRammer( w, 0, 50, 2, m_aliveThings );
        PoorStation p = new PoorStation(w, 0, -50, 1, m_aliveThings );
        PoorStation p1 = new PoorStation(w, -50, 0, 1, m_aliveThings );
        PoorStation p2 = new PoorStation(w, 50, -50, 1, m_aliveThings );
        cvs = new CivilianShuttle(w, 0, -2, 1, m_aliveThings, p );
        cvs.AddMidRangeCounterMeasure( new Laser( w, cvs, m_aliveThings ) );
        //cvs.m_shippingTargets.add( p1 );
        //cvs.m_shippingTargets.add( p2 );
        cvs.SetBehavior( CivilianBehavior.PatrolWaypoints );
        cvs.SetWaypoints(WaypointUpdateType.RadialCoordinates, cvs.GenerateRadialWaypoints(10, 5), player, null);
        cvs.EnterFromSidelines(0, -200);
       // shippy.AddToFighterGroup( new EnemyShip( "stateczek", 0, w, 0, 90, -90, 40, 2, m_aliveThings ) );
        shippy.AddShortRangeCounterMeasure( new MachineGun( w, shippy, m_aliveThings ) );
        
        /** BOX2D LIGHT STUFF BEGIN */
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(w);
        rayHandler.setAmbientLight(0.15f, 0.15f, 0.15f, 0.1f);
        rayHandler.setCulling(true);
        // rayHandler.setBlur(false);
        rayHandler.setBlurNum(1);
        //rayHandler.setShadows(true);
        cam.update(true);


        Light light = new PointLight(rayHandler, 128);
        Color color = new Color(1f,1f,1f,1f);
        Light light2 = new ConeLight(rayHandler, 64, color,
					    			300, 0, 0, 0,
					    			15);
        light.setDistance(300f);
        light2.setDistance(800);
        light.attachToBody(player.m_body, 0f, 0f);
        light2.attachToBody(player.m_body, 0f, 0f);
       
        light.setColor( 1, 1, 1, 1f);

        /** BOX2D LIGHT STUFF END */
        
        
        SetupWeaponSwitcherDialog(window);
        
        m_visualNovelStyleMessage.setModal(true);
        m_visualNovelStyleMessage.setMovable(false);
        m_visualNovelStyleMessage.setPosition(0 + WIDTH/40, 5);
        m_visualNovelStyleMessage.setVisible(false);
        m_visualNovelStyleMessage.setWidth(19*WIDTH/20);
        m_visualNovelStyleMessage.setHeight(HEIGHT/3);
        m_visualNovelStyleMessage.debug();  
        m_visualNovelStyleMessage.clearChildren();
        
        m_visualNovelStyleMessageText = new Label("I'm a message from somebody and I'm really longI'm a message from somebody and I'm really longI'm a message from somebody and I'm really longI'm a message from somebody and I'm really longI'm a message from somebody and I'm really long", skin);
        m_visualNovelStyleMessageText.setWrap(true);
        m_visualNovelStyleMessageText.setAlignment(Align.top | Align.left);
        m_VisualNovelStyleMessageButton = new Button( skin );
        m_VisualNovelStyleMessageButton.add(m_visualNovelStyleMessageText).expand().fill();
       // b.setFillParent(true);
        m_VisualNovelStyleMessageButton.top();
        m_VisualNovelStyleMessageButton.left();
        m_visualNovelStyleMessage.add( m_VisualNovelStyleMessageButton ).expand().fill();
        //m_visualNovelStyleMessages.bottom();
       // m_visualNovelStyleMessages.left();
        
        m_stage.addActor(m_visualNovelStyleMessage);
        

        m_nonBlockMessages.setModal(false);
        m_nonBlockMessages.setMovable(false);
        m_nonBlockMessages.setPosition(0, 0);
        m_nonBlockMessages.setVisible(true);
        m_nonBlockMessages.setWidth(WIDTH/2);
        m_nonBlockMessages.debug();        
        
        
    	
    	m_timedImage = new Image(m_defaultButtonTexture);
        Container imageContainer = new Container( m_timedImage );
        m_timedMessageText = new Label("I'm a message from somebody and I'm really longI'm a message from somebody and I'm really longI'm a message from somebody and I'm really longI'm a message from somebody and I'm really longI'm a message from somebody and I'm really long", skin);
        m_timedMessageText.setWrap(true);
        m_timedMessageText.setAlignment(Align.top | Align.left);

        m_nonBlockMessages.align(Align.left);
        m_nonBlockMessages.clearChildren();
        m_nonBlockMessages.add(imageContainer).size(100, 100);
        m_nonBlockMessages.add( m_timedMessageText ).expand().fill();
        m_nonBlockMessages.setHeight( (float) (m_nonBlockMessages.getPrefHeight()*1.0) );
        m_nonBlockMessages.left().top();
        
      
        m_nonBlockMessages.setPosition(0, HEIGHT - m_nonBlockMessages.getHeight() );
        m_stage.addActor(m_nonBlockMessages);
        m_nonBlockMessages.setVisible(false);
        
        
        
        GameCharacter MC = new GameCharacter("MC");
        
        TimedMessage m_timedMassage = new TimedMessage(MC, "lol", "Would somebody like a body BODY MASSAGE! Because I'm a body massage machine!!! GO! .expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill().expand().fill()", 600);
        m_timedMessages.add( m_timedMassage );
        
        VisualNovelImageState state = new VisualNovelImageState( false, "maincbefore", ImageJustification.Left, 0 );
        VisualNovelStyleMessage vnMassage = new VisualNovelStyleMessage(MC, state, "Would somebody like a body BODY MASSAGE! Because I'm a body massage machine!!! GO! Would somebody like a body BODY MASSAGE! Because I'm a body massage machine!!! GO!Would somebody like a body BODY MASSAGE! Because I'm a body massage machine!!! GO!Would somebody like a body BODY MASSAGE! Because I'm a body massage machine!!! GO!Would somebody like a body BODY MASSAGE! Because I'm a body massage machine!!! GO!Would somebody like a body BODY MASSAGE! Because I'm a body massage machine!!! GO!");
        m_visualNovelStyleMessageList.add( vnMassage );
        
        TimedMessage.m_image = m_timedImage;
        TimedMessage.m_messageDialog = m_nonBlockMessages;
        TimedMessage.m_textArea = m_timedMessageText;
        
        VisualNovelStyleMessage.m_messageDialog = m_visualNovelStyleMessage;
        VisualNovelStyleMessage.m_textArea = m_visualNovelStyleMessageText;
        VisualNovelStyleMessage.m_button = m_VisualNovelStyleMessageButton;

        player.AddLongRangeCounterMeasure( new Railgun( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new Missile( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new Laser( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new MagneticWave( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new Hacking( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new WingBlades( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new MachineGun( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new LongRangeSensors( w, player, m_aliveThings ) );
        player.AddLongRangeCounterMeasure( new NoWeapon( w, player, m_aliveThings ) );
        
        m_energyIndicator.load(Gdx.files.internal("data/energyhud.p"), Gdx.files.internal("data/"));
        m_energyIndicatorPool = new ParticleEffectPool(m_energyIndicator, 1, 2);
        m_pooledEnergyIndicator = m_energyIndicatorPool.obtain();
        
        m_integrityIndicator.load(Gdx.files.internal("data/integrityhud.p"), Gdx.files.internal("data/"));
        m_integrityIndicatorPool = new ParticleEffectPool(m_integrityIndicator, 1, 2);
        m_pooledIntegrityIndicator = m_integrityIndicatorPool.obtain();
	}

	private void SetupWeaponSwitcherDialog(Dialog window)
	{
		m_defaultButtonTexture = new Texture(Gdx.files.internal("data/unselected.png"));
        m_changeEquipmentTexture = new Texture(Gdx.files.internal("data/changeweapon.png"));
        TextureRegion defaultImg = new TextureRegion(m_defaultButtonTexture);
        TextureRegion changeImg = new TextureRegion(m_changeEquipmentTexture);

        
        
        m_inputSplitter.addProcessor(player);
        m_inputSplitter.addProcessor(m_stage);
        Gdx.input.setInputProcessor(m_inputSplitter);

        
        m_longRange = new Button(new Image(defaultImg), skin);
        m_longRange.pad(10);
    	m_mediumRange = new Button(new Image(defaultImg), skin);
    	m_mediumRange.pad(10);
    	m_shortRange = new Button(new Image(defaultImg), skin);
    	m_shortRange.pad(10);
    	m_changeEquipment = new Button(new Image(changeImg), skin);
    	m_changeEquipment.pad(10);
    	
    	
    	player.m_shortRange = m_shortRange;
        player.m_longRange = m_longRange;
        player.m_mediumRange = m_mediumRange;
        player.m_changeEquipment = m_changeEquipment;


        window.setMovable(false);
        window.setPosition(0, HEIGHT/2);
        window.row();
        window.add(m_longRange);
        window.row();
        window.add(m_mediumRange);
        window.row();
        window.add(m_shortRange);
        window.row();
        window.add(m_changeEquipment);
        window.pack();
        
        player.m_window = window;

        // stage.addActor(new Button("Behind Window", skin));
        m_stage.addActor(window);


        m_longRange.addListener( player.m_buttonListener );
        m_mediumRange.addListener( player.m_buttonListener );
        m_shortRange.addListener( player.m_buttonListener );
        m_changeEquipment.addListener( player.m_buttonListener );
	}
		
	@Override
	public void update(float delta) 
	{
		// TODO Auto-generated method stub
		/*if (Gdx.input.justTouched()) {
			m_isDone = true;
		}*/
		
	}
	
	private void handleInput() 
	{
		player.HandleMovement( cam );
    }

	@Override
	public void draw(float delta) 
	{
		if( !ViewedCollidable.GetInMenu() )
		{
			w.step(1/60f, 60, 20);
			handleInput();
		}
		
        GL20 gl = Gdx.graphics.getGL20();

        // Camera --------------------- /
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);

        cam.position.set( player.m_body.getPosition().x*29f, player.m_body.getPosition().y*29f, 0);
        cam.update();

        
     // background layer, no parallax, centered around origin
        spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(0, 0));
        spriteBatch.disableBlending();
     		spriteBatch.begin();
     		spriteBatch.draw(background, -(int)(background.getWidth() / 2), -(int)(background.getHeight() / 2));
     		spriteBatch.end();
     		spriteBatch.enableBlending();

     		// midground layer, 0.5 parallax (move at half speed on x, full speed on y)
     		/*/ layer is 2048x2048
     		spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(0.5f, .5f));
     		spriteBatch.begin();
     		int x = (int) (cam.position.x / 4096);
     		int y = (int) cam.position.y;
     		spriteBatch.draw(parralax1, x*2048, 0);
     		spriteBatch.end();
     		*/
     		DrawParralaxLayer( .25f, parralax1 );
     		DrawParralaxLayer( .5f, parralax2 );
     		
     		//spriteBatch.setProjectionMatrix();
     		
     		// foreground layer, 1.0 parallax (move at full speed)
     		// layer is 2048x320
     		spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(1f, 1f));
     		
     		spriteBatch.begin();
    		//player.Draw( spriteBatch );
    		for(int i = 0; i < m_aliveThings.size(); i++)
    		{
    			ViewedCollidable tmp = (ViewedCollidable) m_aliveThings.get(i);
    			
    			if(tmp.m_integrity > 0 || !tmp.deathThroesDone() )
    			{
    				tmp.Draw(spriteBatch);
    			}
    			else
    			{
    				m_aliveThings.remove( i );
    				i--;
    				m_deadThings.add( tmp );
    			}
    		}
    		
    		if(player.m_integrity > 0 || !player.deathThroesDone() )
			{
    			player.Draw(spriteBatch);
			}
			else
			{				
				m_deadThings.add( player );
			}
    		
    		spriteBatch.end();
    		/** BOX2D LIGHT STUFF BEGIN */

    		
    		rayHandler.setCombinedMatrix(cam.combined, cam.position.x,
    		cam.position.y, cam.viewportWidth * cam.zoom,
    		cam.viewportHeight * cam.zoom);

    		// rayHandler.setCombinedMatrix(camera.combined);
    		//if ( !player.m_inMenu )
    		{
	    		rayHandler.update();
	    		rayHandler.render();
    		}

    		/** BOX2D LIGHT STUFF END */
    		
     		
    		//debugRenderer.render(w, cam.combined);
    		
    		m_stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
    		m_stage.draw();
    		
    		
    		RemoveButtonsForDeadEnemies();
    		
    		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    		spriteBatch.begin();
    		HandleEnemyButtons( spriteBatch );
    		spriteBatch.end();
    		
    		DrawAndHandleCombatMessages();
    		DrawAndHandleVisualNovelMessages( spriteBatch );
    		
    		spriteBatch.begin();
    		m_pooledEnergyIndicator.setPosition( 19f/20f* WIDTH, 19f/20f * HEIGHT );
    		float percentage = player.me.m_boostJuice / player.me.m_boostJuiceMax * 68;
    		m_pooledEnergyIndicator.getEmitters().get(0).getVelocity().setHigh( percentage > 0 ? percentage : 0 );
    		m_pooledEnergyIndicator.draw(spriteBatch, 1f/60f);
    		    
    		m_pooledIntegrityIndicator.setPosition( 18f/20f* WIDTH, 19f/20f * HEIGHT );
    		percentage = player.m_integrity / 1000f;
    		float[] r = { 0, 0, 0, 1 };
    		r[0] = 1 - percentage;
    		r[1] = percentage;
    		m_pooledIntegrityIndicator.getEmitters().get(0).getTint().setColors(r);
    		m_pooledIntegrityIndicator.draw(spriteBatch, 1f/60f);
    		spriteBatch.end();
    	    DebugDraw();
    		
    		for(int i = 0; i < m_deadThings.size(); i++)
    		{
    			ViewedCollidable tmp = (ViewedCollidable) m_deadThings.get(i);
    			w.destroyBody(tmp.m_body);
    			tmp.destroy();
    		}
    		m_deadThings.clear();    		
	}

	private void DrawAndHandleVisualNovelMessages( SpriteBatch renderer )
	{
		// in the main loop
		if( m_visualNovelStyleMessageList.size() > 0 )
		{
			// set the dialog to be visible
			m_visualNovelStyleMessage.setVisible(true);
			ViewedCollidable.EnterMenu();
			VisualNovelStyleMessage vnm = m_visualNovelStyleMessageList.get(0);
			if( vnm.Display( renderer ) )
			{
				m_visualNovelStyleMessageList.remove(0);
				
				if( m_visualNovelStyleMessageList.size() <= 0 )
				{
					//set the dialog as invisible
					m_visualNovelStyleMessage.setVisible(false);
					ViewedCollidable.ExitMenu();
				}
			}
		}
	}
	
	private void DrawAndHandleCombatMessages()
	{
		// in the main loop
		if( m_timedMessages.size() > 0 )
		{
			// set the dialog to be visible
			m_nonBlockMessages.setVisible(true);
			TimedMessage tm = m_timedMessages.get(0);
			if( !tm.Display() )
			{
				m_timedMessages.remove(0);
			}
		}
		else
		{
			//set the dialog as invisible
			m_nonBlockMessages.setVisible(false);
		}
	}

	private void DebugDraw()
	{
		Vector2 pos = player.m_body.getPosition();
		
   	// draw fps
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();
		//font.draw(spriteBatch, " shippy X: " + cvs.m_body.getPosition().x + " Y: " + cvs.m_body.getPosition().y , 0, 90);
		font.draw(spriteBatch, "body x:  " + pos.x + " y: " + pos.y , 0, 60);

		font.draw(spriteBatch, "vel: " + player.m_body.getLinearVelocity().dst(0, 0), 0, 30);
		spriteBatch.end();
	}

	private void HandleEnemyButtons(SpriteBatch spriteBatch)
	{
		for( int i = 0; i< player.m_trackedHostileTargets.size(); i++)
		{
			ViewedCollidable vc = player.m_trackedHostileTargets.get(i);
			if( vc.m_body.getPosition().dst(player.m_body.getPosition()) > 768f/29f &&
				vc.m_isTargetable )
			{
				boolean found = false;
				for( int j = 0; j < m_enemyButtons.size() && !found; j++)
				{
					EnemyIndicatorButton eib = m_enemyButtons.get(j);
					if(eib.m_trackedEntity == vc && vc.m_integrity > 0 )
					{
						found = UpdateEnemyButton(vc, eib, spriteBatch );
					}
				}
				
				if( !found && vc.m_integrity > 0 )
				{
					AddNewEnemyButton(vc);
				}
			}
			else
			{
				RemoveAnEnemyButton(vc);
			}
		}
	}

	private void RemoveAnEnemyButton(ViewedCollidable vc)
	{
		boolean found = false;
		for( int j = 0; j < m_enemyButtons.size() && !found; j++)
		{
			EnemyIndicatorButton eib = m_enemyButtons.get(j);
			if(eib.m_trackedEntity == vc )
			{
				eib.remove();
				found = true;
				m_enemyButtons.remove(eib);
			}
		}
	}

	private boolean UpdateEnemyButton(ViewedCollidable vc,
			EnemyIndicatorButton eib, SpriteBatch spriteBatch)
	{
		boolean found;
		found = true;
		float xPosition = vc.m_body.getPosition().x - player.m_body.getPosition().x;
		xPosition*=29f;
		xPosition+=512;
		xPosition = Math.max(0, xPosition);
		xPosition = Math.min(1024 - eib.getWidth(), xPosition );
		
		float yPosition = vc.m_body.getPosition().y - player.m_body.getPosition().y;
		yPosition*=29f;
		yPosition+=512;
		yPosition = Math.max(0, yPosition);
		yPosition = Math.min(768 - eib.getHeight(), yPosition );
		
		eib.setPosition(xPosition, yPosition);
		
		float dst = vc.m_body.getPosition().dst(player.m_body.getPosition());
		int meters = (int) (dst * 29f);
		font.draw(spriteBatch, meters + "m" , xPosition, yPosition + 20 );
		
		return found;
	}

	private void AddNewEnemyButton(ViewedCollidable vc)
	{
		EnemyIndicatorButton eib = new EnemyIndicatorButton(vc, player);
		float xPosition = vc.m_body.getPosition().x - player.m_body.getPosition().x;
		xPosition*=29f;
		xPosition+=512;
		xPosition = Math.max(0, xPosition);
		xPosition = Math.min(1024 - eib.getWidth(), xPosition );
		
		float yPosition = vc.m_body.getPosition().y - player.m_body.getPosition().y;
		yPosition*=29f;
		yPosition+=512;
		yPosition = Math.max(0, yPosition);
		yPosition = Math.min(768 - eib.getHeight(), yPosition );
		
		eib.setPosition(xPosition, yPosition);
		m_stage.addActor( eib );
		m_enemyButtons.add(eib);
	}

	private void RemoveButtonsForDeadEnemies()
	{
		for( int j = 0; j < m_enemyButtons.size(); j++)
		{
			EnemyIndicatorButton eib = m_enemyButtons.get(j);
			if( eib.m_trackedEntity.m_integrity <= 0)
			{
				eib.remove();
			}
		}
	}
	
	private void DrawParralaxLayer( float parallaxFactor, Texture texture ) 
	{
		spriteBatch.setProjectionMatrix(cam.calculateParallaxMatrix(parallaxFactor, parallaxFactor));
		spriteBatch.begin();
		int x = (int) (cam.position.x / (texture.getWidth() / parallaxFactor));
		int y = (int) (cam.position.y / (texture.getHeight() / parallaxFactor));
		
		//constant y series
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y)*texture.getHeight());
		
		//constant x series
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y-2)*texture.getHeight());
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x)*texture.getWidth(), (y+2)*texture.getHeight());
		
		//negative var series
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y-2)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y-2)*texture.getHeight());
		
		//positive var series
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y+2)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y+2)*texture.getHeight());
		
		// opposites series
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x-2)*texture.getWidth(), (y+1)*texture.getHeight());
		
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x+2)*texture.getWidth(), (y-1)*texture.getHeight());
		
		//spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y+1)*texture.getHeight());
		spriteBatch.draw(texture, (x-1)*texture.getWidth(), (y+2)*texture.getHeight());
		
		//spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y-1)*texture.getHeight());
		spriteBatch.draw(texture, (x+1)*texture.getWidth(), (y-2)*texture.getHeight());
		spriteBatch.end();
	}

	@Override
	public boolean isDone() 
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void resize (int width, int height) 
	{
	//stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose()
	{
		background.dispose();
		parralax1.dispose();
		parralax2.dispose();
		spriteBatch.dispose();
		font.dispose();
		
		m_stage.dispose();
		skin.dispose();
		m_changeEquipmentTexture.dispose();
		m_defaultButtonTexture.dispose();
	}

	@Override
	public void beginContact(Contact contact) 
	{
		// TODO Auto-generated method stub
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		ViewedCollidable object1 = (ViewedCollidable) body1.getUserData();
		ViewedCollidable object2 = (ViewedCollidable) body2.getUserData();
		float crashVelocity = Math.abs( body1.getLinearVelocity().dst(0, 0) - body2.getLinearVelocity().dst(0, 0) );
		
		if( object1 != null && object2 != null )
		{
			object1.damageCalc( object2, crashVelocity );
			object2.damageCalc( object1, crashVelocity );
		}
	}

	@Override
	public void endContact(Contact contact) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		
		HandleWingBladeContact(contact);
	}

	private void HandleWingBladeContact(Contact contact)
	{
		if( WingBlade.class.isInstance( contact.getFixtureA().getBody().getUserData() ) )
		{
			contact.setEnabled(false);
			WingBlade wb = (WingBlade) contact.getFixtureA().getBody().getUserData();
			wb.HandleContact(contact);
		}
		
		if( WingBlade.class.isInstance( contact.getFixtureB().getBody().getUserData() ) )
		{
			contact.setEnabled(false);
			WingBlade wb = (WingBlade) contact.getFixtureB().getBody().getUserData();
			wb.HandleContact(contact);
		}
		
		if( PlayerEntity.class.isInstance( contact.getFixtureA().getBody().getUserData() ) )
		{
			PlayerEntity p = (PlayerEntity) contact.getFixtureA().getBody().getUserData();
			if( p.m_isEthereal )
			{
				contact.setEnabled(false);
				p.m_leftWing.HandleContact(contact);
			}
		}
		
		if( PlayerEntity.class.isInstance( contact.getFixtureB().getBody().getUserData() ) )
		{
			PlayerEntity p = (PlayerEntity) contact.getFixtureB().getBody().getUserData();
			if( p.m_isEthereal )
			{
				contact.setEnabled(false);
				p.m_leftWing.HandleContact(contact);
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		// TODO Auto-generated method stub
		
	}
	
	

}
