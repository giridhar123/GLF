package General;

public class SharedVariables {
	
	private int TIME_STEP;
	private int SERVER_TCP_PORT;
	private String projectPath;
	private String webotsPath;
	private int numeroGuardie, numeroLadri;
	
	boolean initialized;
	
	// mappa
	private int DimMapX, DimMapY, xDimSpawn, SpawnPort;
	private double WeBotsXYMap,WeBotsTile;	
	private String difficolta;
	
	private static SharedVariables instance = null;
	
	private SharedVariables(String projectPath,
			String webotsPath, 
			int TIME_STEP,
			int SERVER_TCP_PORT,
			int numeroGuardie,
			int numeroLadri,
			int DimMapX,
			int DimMapY,
			int xDimSpawn,
			int SpawnPort,
			double WeBotsXYMap,
			double WeBotsTile,
			String difficolta)
	{
		initialized = true;
		
		this.projectPath = projectPath;
		this.webotsPath = webotsPath;
		this.TIME_STEP = TIME_STEP;
		this.SERVER_TCP_PORT = SERVER_TCP_PORT;
		setNumeroGuardie(numeroGuardie);
		setNumeroLadri(numeroLadri);
		setDimMapX(DimMapX);
		setDimMapY(DimMapY);
		setXDimSpawn(xDimSpawn);
		setSpawnPort(SpawnPort);
		setWeBotsXYMap(WeBotsXYMap);
		setWeBotsTile(WeBotsTile);
		setDifficolta(difficolta);
	}
	
	private SharedVariables(String projectPath,
			String webotsPath, 
			int TIME_STEP,
			int SERVER_TCP_PORT,
			int numeroGuardie,
			int numeroLadri)
	{
		initialized = true;
		
		this.projectPath = projectPath;
		this.webotsPath = webotsPath;
		this.TIME_STEP = TIME_STEP;
		this.SERVER_TCP_PORT = SERVER_TCP_PORT;		
		setNumeroGuardie(numeroGuardie);
		setNumeroLadri(numeroLadri);
	}
	
	public static void init(String projectPath,
			String webotsPath,
			int TIME_STEP,
			int SERVER_TCP_PORT,
			int numeroGuardie,
			int numeroLadri,
			int DimMapX,
			int DimMapY,
			int xDimSpawn,
			int SpawnPort,
			double WeBotsXYMap,
			double WeBotsTile,
			String difficolta)
	{
		if (instance == null)
			instance = new SharedVariables(projectPath, webotsPath, TIME_STEP, SERVER_TCP_PORT, numeroGuardie, numeroLadri, DimMapX, DimMapY, xDimSpawn, SpawnPort,WeBotsXYMap,WeBotsTile,difficolta);
	}

	public static void init(String projectPath,
			String webotsPath,
			int TIME_STEP,
			int SERVER_TCP_PORT,
			int numeroGuardie,
			int numeroLadri)
	{
		if (instance == null)
			instance = new SharedVariables(projectPath, webotsPath, TIME_STEP, SERVER_TCP_PORT, numeroGuardie, numeroLadri);
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
	
	public static int getNumeroGuardie()
	{
		return instance.numeroGuardie;
	}
	
	public static int getNumeroLadri()
	{
		return instance.numeroLadri;
	}

	public static int getDimMapX() {
		return instance.DimMapX;
	}

	public static int getDimMapY() {
		return instance.DimMapY;
	}

	public static int getxDimSpawn() {
		return instance.xDimSpawn;
	}

	public static int getSpawnPort() {
		return instance.SpawnPort;
	}

	public static double getWeBotsXYMap() {
		return instance.WeBotsXYMap;
	}

	public static double getWeBotsTile() {
		return instance.WeBotsTile;
	}

	public static String getDifficolta() {
		return instance.difficolta;
	}

	public static boolean isInitialized()
	{
		return instance.initialized;
	}

	private void setNumeroGuardie(int numeroGuardie)
	{
		this.numeroGuardie = numeroGuardie;
	}
	
	private void setNumeroLadri(int numeroLadri)
	{
		this.numeroLadri = numeroLadri;
	}

	private void setDimMapX(int DimMapX)
	{
		this.DimMapX = DimMapX;
	}
	
	private void setDimMapY(int DimMapY)
	{
		this.DimMapY = DimMapY;
	}
	
	private void setXDimSpawn(int xDimSpawn)
	{
		this.xDimSpawn = xDimSpawn;
	}
	
	private void setSpawnPort(int spawnPort)
	{
		this.SpawnPort = spawnPort;
	}
	
	private void setWeBotsXYMap(double WeBotsXYMap)
	{
		this.WeBotsXYMap = WeBotsXYMap;
	}
	
	private void setWeBotsTile(double WeBotsTile)
	{
		this.WeBotsTile = WeBotsTile;
	}
	
	private void setDifficolta(String difficolta)
	{
		this.difficolta = difficolta;
	}
}
