package main;

import java.awt.Font;
import java.io.File;

public class Fonts {
	
	public static Font font10;
	public static Font font12;
	public static Font font14;
	public static Font font16;
	public static Font font18;
	public static Font font22;
	public static Font fontBold18;
	public static Font fontBold14;

	public static void createAllFonts(){
		font10 = createFont(10);
		font12 = createFont(12);
		font14 = createFont(14);
		font16 = createFont(16);
		font18 = createFont(18);
		font22 = createFont(22);
		
		fontBold14 = createFontBold(14);
		fontBold18 = createFontBold(18);
	}
	
	public static Font createFont(float size){
		Font font = null;
		try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeMono.ttf"));
            font = font.deriveFont(size);
        }
        catch(Exception e)
        {
    		font = new Font(Font.MONOSPACED, Font.BOLD, (int)size);
            e.printStackTrace();
        }
		return font;
	}
	public static Font createFontBold(float size){
		Font font = null;
		try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSansBold.ttf"));
            font = font.deriveFont(size);
        }
        catch(Exception e)
        {
    		font = new Font(Font.MONOSPACED, Font.BOLD, (int)size);
            e.printStackTrace();
        }
		return font;
	}
}
