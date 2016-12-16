package spacegame.other;

import java.util.*;

import org.lwjgl.input.*;

public class KeyboardListener {
	private static ArrayList<IKeyboard> keyListeners = new ArrayList<>();
	private static ArrayList<KeyState> keyStates = new ArrayList<>();
	
	private static ArrayList<IKeyboard> listenersToAdd = new ArrayList<>();
	
	public void onUpdate(int delta) {
		for(IKeyboard ik : keyListeners) {
			for(int key : ik.getKeysToListen().keySet()) {
				KeyState ks = findKeyState(key);
				boolean singleEvent = ik.getKeysToListen().get(key);
				if(Keyboard.isKeyDown(key)) {
					if(singleEvent) {
						if(!ks.state) {
							ik.onKeyPress(key, delta);
							ks.state = true;
						}
					} else {
						ik.onKeyPress(key, delta);
						ks.state = true;
					}
				} else {
					ks.state = false;
				}
			}
		}
		if(listenersToAdd.size() > 0) {
			keyListeners.addAll(listenersToAdd);
			for(IKeyboard k : listenersToAdd) {
				for(int i = 0; i < k.getKeysToListen().size(); i++) {
					int key = (int) k.getKeysToListen().keySet().toArray()[i];
					keyStates.add(new KeyState(key));
				}
			}
			listenersToAdd.clear();
		}
	}
	
	public static void registerListener(IKeyboard k) {
		listenersToAdd.add(k);
	}
	
	public static void deregisterListener(IKeyboard k) {
		keyListeners.remove(k);
	}
	
	private KeyState findKeyState(int key) {
		for(KeyState ks : keyStates) {
			if(ks.key == key) {
				return ks;
			}
		}
		return null;
	}
	
	public static interface IKeyboard {
		public HashMap<Integer, Boolean> getKeysToListen();
		public void onKeyPress(int key, int delta);
		
	}
	
	public static class KeyState {
		public int key;
		public boolean state; 
		
		public KeyState(int key) {
			this.key = key;
		}
	}
}
