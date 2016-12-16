package spacegame.core;

import spacegame.gamestates.IngameState;
import spacegame.gamestates.StateManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DataHandler {
	public ArrayList<ISavable> interfaces;
	public String baseDir = StateManager.getHomeDirectory().getAbsolutePath();
	
	public DataHandler() {
		interfaces = new ArrayList<>();
		IngameState.getInstance().firstLoad = !(new File(new File(baseDir).getParentFile(), "save").exists());
	}

	//saves the data by querying every class that wants to save data for any data that is required to be saved.
	public void saveInterfaceData() throws IOException {
		for(ISavable is : interfaces) {
			File dir = new File(baseDir).getParentFile();
			File saveDir = new File(dir, "save/" + is.getSaveDir());
			//deletes the previously saved data and creates a new folder for the save folder
			if(saveDir.exists()) {
				File[] containedFiles = saveDir.listFiles();
				if(containedFiles.length > 0) {
					for(File f : containedFiles) {
						f.delete(); //BE CAREFUL WITH THIS U CAN DELETE THE WORLD
					}
				}
			} else {
				saveDir.mkdirs();
			}
			//query's each class for savable data and writes it to a file
			for(String savableData : is.getSavableList()) {
				HashMap<String, Object> savableMap = new HashMap<>();
				is.addSavableData(savableData, savableMap);
				File saveFile = new File(saveDir, savableData + ".txt");
				saveFile.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
				for(String object : savableMap.keySet()) {
					writer.write(object + ":" + savableMap.get(object));
					writer.newLine();
				}
				writer.close();
			}
		}
	}

	//loads all the data and provides it to the classes that need it
	public void loadInterfaceData() {
		File dir = new File(baseDir).getParentFile();
		File fileDir = new File(dir, "save");
		if(fileDir.exists()) {
			for(File saveDir : fileDir.listFiles()) {
				if(saveDir.isDirectory()) {
					ISavable saveInstance = null;
					for(ISavable is : interfaces) {
						if(is.getSaveDir().equals(saveDir.getName())) {
							saveInstance = is;
						}
					}
					FilenameFilter filter = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(".txt");
						}
					};
					for(File detailFile : saveDir.listFiles(filter)) {
						HashMap<String, String> rawData = new HashMap<>();
						try {
							BufferedReader br = new BufferedReader(new FileReader(detailFile));
							while(br.ready()) {
								String[] line = br.readLine().split(":");
								rawData.put(line[0], line.length == 2 ? line[1] : "");
							}
							br.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						saveInstance.loadData(detailFile.getName().replace(".txt", ""), rawData);
					}
				}
			}
		}
	}
	
	public void registerInterface(ISavable savable) {
		interfaces.add(savable);
	}

	//an implementation that allows classes to save and load data.
	public static interface ISavable {
		public void addSavableData(String savable, HashMap<String, Object> savableMap);
		public void loadData(String savable, HashMap<String, String> rawData);
		public ArrayList<String> getSavableList();
		public String getSaveDir();
	}
}

