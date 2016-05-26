package spacegame.gameplay;

public class VelocityVector {
	public float xCoord = 0;
	public float yCoord = 0; 
	public float xVelocity;
	public float yVelocity;
	public float vehicleDrag = 0.15f;
	public float rotation = 0;
	public float angularVelocity;
	public float angularDrag = 0.4f;
	public float velocityLength;
	
	public VelocityVector(float x, float y) {
		this.xVelocity = x;
		this.yVelocity = y;
	}
	
	public void accelerate(int power, float delta) {
		xVelocity += power*Math.sin(Math.toRadians(rotation))*delta;
		yVelocity -= power*Math.cos(Math.toRadians(rotation))*delta;
		velocityLength = calculateVelocityLength();
	}
	
	//TODO get velocity rotation and decelerate from that, probs using atan(y, x) - pi/2 + pi (atan(y, x) + pi/2)
	public void decelerate(float power, float delta) {
		xVelocity += power*Math.sin(Math.toRadians(rotation+180))*delta;
		yVelocity -= power*Math.cos(Math.toRadians(rotation+180))*delta;
		velocityLength = calculateVelocityLength();
	}
	
	public void accelerateInDirection(int power, float rotation, float delta) {
		xVelocity += power*Math.sin(Math.toRadians(rotation))*delta;
		yVelocity -= power*Math.cos(Math.toRadians(rotation))*delta;
		velocityLength = calculateVelocityLength();
	}
	
	public void steerInDirection(boolean direction, int power, float delta) {
		angularVelocity = angularVelocity + (direction ? power*delta : -power*delta);
	}
	
	public void setVelocity(int velocity) {
		float scaler = velocity/velocityLength;
		xVelocity = xVelocity*scaler;
		yVelocity = yVelocity*scaler;
		velocityLength = calculateVelocityLength();
	}
	
	public void setVelocityWithDirection(int velocity, float direction) {
		xVelocity = (float) (velocity*Math.sin(Math.toRadians(direction)));
		yVelocity = (float) (-velocity*Math.cos(Math.toRadians(direction)));
		velocityLength = calculateVelocityLength();
	}
	
	public void removeVelocity() {
		xVelocity = 0;
		yVelocity = 0;
		velocityLength = 0;
		angularVelocity = 0;
	}
	
	public void onUpdate(int delta) {
		float deltaSec = delta/1000f;
		xCoord += xVelocity*deltaSec;
		yCoord += yVelocity*deltaSec;
		rotation += angularVelocity*deltaSec;
		
		xVelocity = xVelocity - deltaSec*vehicleDrag*xVelocity;
		yVelocity = yVelocity - deltaSec*vehicleDrag*yVelocity;
		angularVelocity = angularVelocity- deltaSec*angularDrag*angularVelocity*10;
		
		velocityLength = calculateVelocityLength();
	}
	
	public void setCoords(float x, float y) {
		this.xCoord = x;
		this.yCoord = y;
	}
	
	private float calculateVelocityLength() {
		return (float) Math.sqrt(Math.pow(xVelocity, 2)+Math.pow(yVelocity, 2));
	}
}
