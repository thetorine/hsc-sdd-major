package spacegame.core;

import java.util.*;

import org.newdawn.slick.particles.*;

import spacegame.other.*;
import spacegame.other.GameUtilities.*;

//a simple class using slick2ds particle system to manage particles
public class ParticleFX {
	public ParticleSystem effectSystem;
	public ConfigurableEmitter emitter;
	public HashMap<Integer, Point> loadedEmitters = new HashMap<>();
	
	public ParticleFX(String resource) {
		try {
			effectSystem = ParticleIO.loadConfiguredSystem(resource);
			effectSystem.getEmitter(0).setEnabled(false);
			effectSystem.setRemoveCompletedEmitters(true); 
			emitter = (ConfigurableEmitter) effectSystem.getEmitter(0);
			emitter.setEnabled(false);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
	
	public void updateCoordianates() {
		HashMap<Integer, Point> updatedEmitters = new HashMap<>();
		
		for(int i = 0; i < effectSystem.getEmitterCount(); i++) {
			ConfigurableEmitter e = (ConfigurableEmitter) effectSystem.getEmitter(i);
			Point p = loadedEmitters.get(e.hashCode());
			if(p != null) {
				Point p1 = GameUtilities.translateMapToGame(p);
				effectSystem.moveAll(e, p1.x-e.getX(), p1.y-e.getY());
				e.setPosition(p1.x, p1.y, false);
				
				if(loadedEmitters.keySet().contains(e.hashCode())) {
					updatedEmitters.put(e.hashCode(), p);
				}
			}
		}
		loadedEmitters = updatedEmitters;
	}
	
	public void addEmitterAt(Point p) {
		ConfigurableEmitter e = (ConfigurableEmitter) emitter.duplicate();
		e.setEnabled(true);
		Point p1 = GameUtilities.translateMapToGame(p);
		e.setPosition(p1.x, p1.y, false);
		loadedEmitters.put(e.hashCode(), p);
		effectSystem.addEmitter(e); 
	}
}
