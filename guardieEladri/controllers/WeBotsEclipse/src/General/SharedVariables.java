package General;

public class SharedVariables {
	
	private int timeStep;
	private int serverTcpPort;
	private String projectPath;
	private String webotsPath;
	private int numeroGuardie, numeroLadri;
	
	// mappa
	private int dimMappaInternaX, dimMappaInternaY, dimSpawnX, dimSpawnGate;	
	private String difficolta;
	
	private static SharedVariables instance = null;
	
	private SharedVariables(String projectPath,
			String webotsPath, 
			int timeStep,
			int serverTcpPort,
			int numeroGuardie,
			int numeroLadri,
			int dimMappaInternaX,
			int dimMappaInternaY,
			int dimSpawnX,
			int dimSpawnGate,
			String difficolta)
	{
		
		this.projectPath = projectPath;
		this.webotsPath = webotsPath;
		this.timeStep = timeStep;
		this.serverTcpPort = serverTcpPort;
		setNumeroGuardie(numeroGuardie);
		setNumeroLadri(numeroLadri);
		setDimMappaInternaX(dimMappaInternaX);
		setDimMappaInternaY(dimMappaInternaY);
		setXDimSpawn(dimSpawnX);
		setDimSpawnGate(dimSpawnGate);
		setDifficolta(difficolta);
	}
	
	private SharedVariables(String projectPath,
			String webotsPath, 
			int timeStep,
			int serverTcpPort,
			int numeroGuardie,
			int numeroLadri)
	{		
		this.projectPath = projectPath;
		this.webotsPath = webotsPath;
		this.timeStep = timeStep;
		this.serverTcpPort = serverTcpPort;		
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
			String difficolta)
	{
		if (instance == null)
			instance = new SharedVariables(projectPath, webotsPath, TIME_STEP, SERVER_TCP_PORT, numeroGuardie, numeroLadri, DimMapX, DimMapY, xDimSpawn, SpawnPort, difficolta);
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
		return instance.timeStep;
	}
	
	public static int getTcpServerPort()
	{
		return instance.serverTcpPort;
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

	public static int getDimMappaInternaX() {
		return instance.dimMappaInternaX;
	}

	public static int getDimMappaInternaY() {
		return instance.dimMappaInternaY;
	}

	public static int getDimSpawnX() {
		return instance.dimSpawnX;
	}

	public static int getDimSpawnGate() {
		return instance.dimSpawnGate;
	}

	public static String getDifficolta() {
		return instance.difficolta;
	}

	private void setNumeroGuardie(int numeroGuardie)
	{
		this.numeroGuardie = numeroGuardie > 5 ? 5 : numeroGuardie;
	}
	
	private void setNumeroLadri(int numeroLadri)
	{
		this.numeroLadri = numeroLadri > 5 ? 5 : numeroLadri;
	}

	private void setDimMappaInternaX(int dimMappaInternaX)
	{
		if (dimMappaInternaX < 10)
			dimMappaInternaX = 10;
		if (dimMappaInternaX > 20)
			dimMappaInternaX = 20;
		
		this.dimMappaInternaX = dimMappaInternaX;
	}
	
	private void setDimMappaInternaY(int dimMappaInternaY)
	{
		if (dimMappaInternaY < 10)
			dimMappaInternaY = 10;
		if (dimMappaInternaY > 20)
			dimMappaInternaY = 20;
		
		this.dimMappaInternaY = dimMappaInternaY;
	}
	
	private void setXDimSpawn(int xDimSpawn)
	{
		if (xDimSpawn < 3)
			xDimSpawn = 3;
		if (xDimSpawn > 5)
			xDimSpawn = 5;
		
		this.dimSpawnX = xDimSpawn;
	}
	
	private void setDimSpawnGate(int dimSpawnGate)
	{
		if (dimSpawnGate < 3)
			dimSpawnGate = 3;
		
		this.dimSpawnGate = dimSpawnGate;
	}
	
	private void setDifficolta(String difficolta)
	{
		this.difficolta = difficolta;
	}
}
