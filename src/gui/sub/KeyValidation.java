package gui.sub;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import cryptoUtility.NetEncryptionFrame;
import main.Fonts;
import main.Language;
import main.PicLoader;

public class KeyValidation extends menu.MoveMenu{

	private final NetEncryptionFrame nef;
	
	private final String yourFP;
	private final String friendsFP;
	
	private BufferedImage[] imas;
	
	private final boolean server;
	
	public KeyValidation(int x, int y, String t, NetEncryptionFrame n, boolean s) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m500x300.png"), Language.lang.text(10100)+t);
		nef = n;
		
		yourFP = cryptoUtility.RSAkeyFingerprint.getFingerprint(nef.getMySuperKey());
		friendsFP = cryptoUtility.RSAkeyFingerprint.getFingerprint(nef.getOtherSuperKey());
		
		imas = new BufferedImage[1];
		imas[0] = PicLoader.pic.getImage("res/ima/men/LC07.png");
		
		server = s;
	}

	@Override
	protected void paintSecond(Graphics g) {
		g.drawImage(imas[0], 30, 50, null);
		
		g.setColor(Color.white);
		g.setFont(Fonts.font16);
		
		g.drawString(yourFP.substring(0,32), 30, 180);
		g.drawString(yourFP.substring(33), 30, 194);
		g.drawString(friendsFP.substring(0,32), 30, 230);
		g.drawString(friendsFP.substring(33), 30, 244);
		
		g.setColor(Color.lightGray);
		g.drawString(Language.lang.text(10103), 30, 165);
		if(server)
			g.drawString(Language.lang.text(10102), 30, 215);
		else
			g.drawString(Language.lang.text(10101), 30, 215);
	}

	@Override
	protected boolean close() {
		return true;
	}

	@Override
	protected void uppdateIntern() {}

	
}
