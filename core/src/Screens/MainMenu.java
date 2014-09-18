package Screens;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class MainMenu extends OrionScreen 
{
	/** the SpriteBatch used to draw the background, logo and text **/
	private final SpriteBatch spriteBatch;
	/** the background texture **/
	private final Texture background;
	/** the logo texture **/
	//private final Texture logo;
	/** the font **/
	//private final BitmapFont font;
	private boolean m_isDone = false;
	private Matrix4 viewMatrix = new Matrix4();
	private Matrix4 transformMatrix = new Matrix4();
	
	public MainMenu () 
	{
		spriteBatch = new SpriteBatch();
		background = new Texture(Gdx.files.internal("data/main.jpg"));
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		//logo = new Texture(Gdx.files.internal("data/title.png"));
		//logo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		//font = new BitmapFont(Gdx.files.internal("data/font16.fnt"), Gdx.files.internal("data/font16.png"), false);
		
		// check for attached controllers and if we are on
		// Ouya.
//		if(Controllers.getControllers().size > 0) {
//			Controller controller = Controllers.getControllers().get(0);
//			if(Ouya.ID.equals(controller.getName())) {
//				controller.addListener(new ControllerAdapter() {
//					@Override
//					public boolean buttonUp (Controller controller, int buttonIndex) {
//						isDone = true;
//						return false;
//					}
//				});
//			}
//		}
	}
	
	/** Called when the screen should update itself, e.g. continue a simulation etc. */
	public void update (float delta) 
	{
		if (Gdx.input.justTouched()) {
			m_isDone = true;
		}
	}

	/** Called when a screen should render itself */
	public void draw (float delta) 
	{
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewMatrix.setToOrtho2D(0, 0, 480, 320);
		spriteBatch.setProjectionMatrix(viewMatrix);
		spriteBatch.setTransformMatrix(transformMatrix);
		spriteBatch.begin();
		spriteBatch.disableBlending();
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(background, 0, 0, 480, 320, 0, 0, 512, 512, false, false);
		spriteBatch.enableBlending();
		//spriteBatch.draw(logo, 0, 320 - 128, 480, 128, 0, 0, 512, 256, false, false);
		spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		//String text = "Touch screen to start!";
		/*float width = font.getBounds(text).width;
		font.draw(spriteBatch, text, 240 - width / 2, 128);
		if (Gdx.app.getType() == ApplicationType.WebGL) {
			text = "Press Enter for Fullscreen Mode";
			width = font.getBounds(text).width;
			font.draw(spriteBatch, "Press Enter for Fullscreen Mode", 240 - width / 2, 128 - font.getLineHeight());
		}*/
		spriteBatch.end();
	}

	/** Called by GdxInvaders to check whether the screen is done.
	 * @return true when the screen is done, false otherwise */
	public boolean isDone () 
	{
		return m_isDone;
	}

	@Override
	public void render (float delta) 
	{
		update(delta);
		draw(delta);
	}

	@Override
	public void resize (int width, int height) 
	{
	}

	@Override
	public void show () 
	{
	}

	@Override
	public void hide () 
	{
	}

	@Override
	public void pause ()
	{
	}

	@Override
	public void resume () 
	{
	}

	@Override
	public void dispose ()
	{
		//spriteBatch.dispose();
		background.dispose();
		//logo.dispose();
		//font.dispose();
	}
}
