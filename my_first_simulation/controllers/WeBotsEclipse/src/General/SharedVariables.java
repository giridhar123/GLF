package General;

public class SharedVariables {
	
	private int TIME_STEP;
	private int SERVER_TCP_PORT;
	private String projectPath;
	private String webotsPath;
	
	private static SharedVariables instance = null;
	
	private SharedVariables(String projectPath, String webotsPath, int TIME_STEP, int SERVER_TCP_PORT)
	{
		this.projectPath = projectPath;
		this.webotsPath = webotsPath;
		this.TIME_STEP = TIME_STEP;
		this.SERVER_TCP_PORT = SERVER_TCP_PORT;
	}
	
	public static void init(String projectPath, String webotsPath, int TIME_STEP, int SERVER_TCP_PORT)
	{
		if (instance == null)
			instance = new SharedVariables(projectPath, webotsPath, TIME_STEP, SERVER_TCP_PORT);
	}
	
	public static int getTimeStep()
	{
		return instance.TIME_STEP;
	}
	
	public static int getTcpServerPort()
	{
		return instance.SERVER_TCP_PORT;
	}
	
	public static String getWebotsPath()
	{
		return instance.webotsPath;
	}
	
	public static String getProjectPath()
	{
		return instance.projectPath;
	}
}
