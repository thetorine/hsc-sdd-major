package spacegame.core;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.w3c.dom.*;

import spacegame.other.*;

//entire class to manage textures required for the game
public class AssetManager {
	
	public String baseResource = GameConstants.RESOURCE + "textures/spritesheet/base.png";
	
	public static Image baseSheet;
	
	public static HashMap<Image, ArrayList<SpriteImage>> spriteSheets = new HashMap<>();
	public static HashMap<LoadedImage, Polygon> polygonsLoaded = new HashMap<>();
	
	public static HashMap<String, Image> uiImages = new HashMap<>();
	
	public AssetManager() {
		baseSheet = registerSpriteSheet(baseSheet, baseResource);
		
		AssetManager.registerCustomImage("missile", "weapons/missile.png", 1f, true);
		AssetManager.registerCustomImage("bg", "environment/bg/bg.png", 1f, false);
		AssetManager.registerCustomImage("star", "environment/planets/star.png", 0.6f, true);
		AssetManager.registerCustomImage("moon", "environment/planets/moon.png", 0.7f, true);
		
		loadUIImages();
	}

	//retrieves a specific image from a spritesheet
	private static Image getImageFromSheet(Image i, String name, float scale) {
		SpriteImage sprite = getSpriteFromName(i, name);
		Image subImage = i.getSubImage(sprite.x, sprite.y, sprite.width, sprite.height);
		Image scaledCopy = subImage.getScaledCopy(scale);
		Polygon p = CollisionDetector.createPolygonFromImage(subImage);
		p = (Polygon) p.transform(Transform.createScaleTransform(scale, scale));
		polygonsLoaded.put(new LoadedImage(scaledCopy, name), p);
		return scaledCopy;
	}

	//loads a saved image from a specific spritesheet
	public static Image getImageByName(Image i, String name, float scale) {
		for(LoadedImage img : polygonsLoaded.keySet()) {
			if(img.name.equals(name)) {
				return img.img.copy();
			}
		}
		return getImageFromSheet(i, name, scale);
	}

	//deprecated/not used
	private static SpriteImage getSpriteFromName(Image i, String name) {
		for(Image img : spriteSheets.keySet()) {
			if(img.getResourceReference().equals(i.getResourceReference())) {
				for(SpriteImage sprite : spriteSheets.get(img)) {
					if(sprite.fileName.equals(name)) {
						return sprite;
					}
				}
			}
		}
		return null;
	}

	//gets a saved polygon from a name
	public static Polygon getPolygonForImage(String name) {
		for(LoadedImage i: polygonsLoaded.keySet()) {
			if(i.name.equals(name)) {
				return polygonsLoaded.get(i);
			}
		}
		return null;
	}

	//loads a custom image
	public static void registerCustomImage(String name, String resource, float scale, boolean requiresPolygon) {
		try {
			Image i = new Image(GameConstants.RESOURCE + "textures/" + resource);
			if(requiresPolygon) {
				Polygon p = CollisionDetector.createPolygonFromImage(i);
				p = (Polygon) p.transform(Transform.createScaleTransform(scale, scale));
				polygonsLoaded.put(new LoadedImage(i.getScaledCopy(scale), name), p);
			} else {
				polygonsLoaded.put(new LoadedImage(i.getScaledCopy(scale), name), null);
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	//gets a custom image
	public static Image getCustomImageByName(String name) {
		for(LoadedImage i : polygonsLoaded.keySet()) {
			if(i.name.equals(name)) {
				return i.img.copy();
			}
		}
		return null;
	}

	//loads every ui image saved in the resources folder
	public void loadUIImages() {
		File uiDir = new File(GameConstants.RESOURCE + "textures/ui/");
		FilenameFilter pngFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".png");
			}
		};
		
		for(File imgFile : uiDir.listFiles(pngFilter)) {
			try {
				Image img = new Image(imgFile.getPath());
				uiImages.put(imgFile.getName(), img);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}

	//registers a spritesheet that can have smaller images extracted from it.
	//uses a textureatlas to load the separate images from the spritesheet
	public Image registerSpriteSheet(Image img, String resource) {
		try {
			img = new Image(resource);
			ArrayList<SpriteImage> loadedSprites = new ArrayList<>();
			File xmlFile = new File(img.getResourceReference().replace(".png", ".xml"));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("SubTexture");
			for(int i = 0; i < nList.getLength(); i++) {
				Element e = (Element) nList.item(i);
				String name = e.getAttribute("name");
				int x = Integer.parseInt(e.getAttribute("x"));
				int y = Integer.parseInt(e.getAttribute("y"));
				int width = Integer.parseInt(e.getAttribute("width"));
				int height = Integer.parseInt(e.getAttribute("height"));
				loadedSprites.add(new SpriteImage(name, x, y, width, height));
			}
			spriteSheets.put(img, loadedSprites);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static class SpriteImage {
		public int x;
		public int y;
		public int width;
		public int height;
		public String fileName;
		
		public SpriteImage(String n, int x, int y, int width, int height) {
			this.fileName = n;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
	
	public static class LoadedImage {
		public Image img;
		public String name;
		
		public LoadedImage(Image i, String name) {
			this.img = i;
			this.name = name;
		}
	}
}