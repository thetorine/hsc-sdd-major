package spacegame.core;

import java.io.*;
import java.util.*;

import spacegame.*;

public class DataHandler {
	public ArrayList<ISavable> interfaces;
	public String baseDir = System.getProperty("java.class.path").split(":")[0];
	
	public DataHandler() {
		interfaces = new ArrayList<>();
		CoreGame.getInstance().firstLoad = !(new File(new File(baseDir).getParentFile(), "save").exists());
	}
	
	public void saveInterfaceData() throws IOException {
		for(ISavable is : interfaces) {
			File dir = new File(baseDir).getParentFile();
			File saveDir = new File(dir, "save/" + is.getSaveDir());
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
	
	public static interface ISavable {
		public void addSavableData(String savable, HashMap<String, Object> savableMap);
		public void loadData(String savable, HashMap<String, String> rawData);
		public ArrayList<String> getSavableList();
		public String getSaveDir();
	}
}

