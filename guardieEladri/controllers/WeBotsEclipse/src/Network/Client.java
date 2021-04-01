package Network;

import Map.Mappa;
import Map.Point;

public interface Client
{
	public abstract void connectToServer();
	public abstract void onStcSendMapReceived(Mappa mappa);
	public abstract void onCtsObstacleInMapReceived(Point point);
	public abstract void onCtsGoingToReceived(Point point);
	public abstract void onCtsNewGuardiaPosReceived(Point before, Point after);
	public abstract void onCtsGoalChangedReceived(Point old, Point New);
	public abstract void onCtsLadroFound(Point punto);
	public abstract void onStcStartGuardie();
}
