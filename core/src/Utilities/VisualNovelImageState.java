package Utilities;

public class VisualNovelImageState
{
	public enum ImageJustification
	{
		Left,
		Right,
		Center
	}
	
	boolean m_flipped = false;
	String m_characterImage = "None";
	ImageJustification m_positioning;
	int m_positionOffset = 0;
	
	public VisualNovelImageState( boolean flipped, String image, ImageJustification position, int offset )
	{
		m_flipped = flipped;
		if( image != null)
		{
			m_characterImage = image;
		}
		m_positioning = position;
		m_positionOffset = offset;
	}

}
