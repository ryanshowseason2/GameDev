package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import Screens.*;

public class OrionTrail extends Game {
	private OrthographicCamera cam;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	static int WIDTH  = 1024;
    static int HEIGHT = 768;
	
	@Override
	public void create() {		
		Gdx.graphics.setDisplayMode(WIDTH, HEIGHT, false);
		
		cam = new OrthographicCamera(WIDTH/2, HEIGHT/2);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		
		CombatScreen screen = new CombatScreen();
		setScreen(screen);
		//music = Gdx.audio.newMusic(Gdx.files.getFileHandle("data/8.12.mp3", FileType.Internal));
		//music.setLooping(true);
		//music.play();
		//Gdx.input.setInputProcessor( screen.player );
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}
	
	

	@Override
	public void render() 
	{		
		OrionScreen currentScreen = getScreen();

		// update the screen
		if (!currentScreen.isDone())
		{
			
			currentScreen.render(Gdx.graphics.getDeltaTime());
		}
		else
		{
			Gdx.gl20.glClearColor(1, 1, 1, 1);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			
			
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			sprite.draw(batch);
			batch.end();
		}

		// When the screen is done we change to the
		// next screen. Ideally the screen transitions are handled
		// in the screen itself or in a proper state machine.
		if (currentScreen.isDone()) 
		{
			// dispose the resources of the current screen
			currentScreen.dispose();

			// if the current screen is a main menu screen we switch to
			// the game loop
			/*if (currentScreen instanceof MainMenu) 
			{
				setScreen(new GameLoop());
			} 
			else 
			{
				// if the current screen is a game loop screen we switch to the
				// game over screen
				if (currentScreen instanceof GameLoop) 
				{
					setScreen(new GameOver());
				} 
				else if (currentScreen instanceof GameOver) 
				{
					// if the current screen is a game over screen we switch to the
					// main menu screen
					setScreen(new MainMenu());
				}
			}*/
		}
		else
		{
			
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	/** For this game each of our screens is an instance of InvadersScreen.
	 * @return the currently active {@link InvadersScreen}. */
	@Override
	public OrionScreen getScreen () {
		return (OrionScreen)super.getScreen();
	}
}
