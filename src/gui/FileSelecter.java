package gui;

import java.io.File;
import java.util.concurrent.Semaphore;

import javax.swing.JFileChooser;

public class FileSelecter {

	private boolean fileChooserPending; 
	private Semaphore loadSema;
	private File fileToLoad;
	
	public FileSelecter(){
		loadSema = new Semaphore(1);
		final JFileChooser jfc = new JFileChooser("user");
		new Thread(){
			public void run() {
				try {
					loadSema.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				fileChooserPending = true;
				int i = jfc.showDialog(null, "Select PCB");
				if(i == JFileChooser.APPROVE_OPTION)
					fileToLoad = jfc.getSelectedFile();
				if(i == JFileChooser.CANCEL_OPTION)
					fileChooserPending = false;
				loadSema.release();
			};
		}.start();
	}
}
