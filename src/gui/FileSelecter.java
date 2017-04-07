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
		final JFileChooser jfc = new JFileChooser();
		try {
			loadSema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fileChooserPending = true;
		
		new Thread("FileChooser"){
			public void run() {
				int i = jfc.showDialog(null, "Select");
				if(i == JFileChooser.APPROVE_OPTION)
					fileToLoad = jfc.getSelectedFile();
				//if(i == JFileChooser.CANCEL_OPTION)
					//fileChooserPending = false;
				loadSema.release();
			};
		}.start();
	}
	
	public boolean isPending(){
		return loadSema.availablePermits()<=0;
	}
	
	public boolean hasSelected(){
		return fileToLoad != null;
	}
	
	public boolean FileProcessed(){
		return !fileChooserPending;
	}
	
	public File getFile(){
		if(isPending())return null;
		fileChooserPending = false;
		return fileToLoad;
	}
}
