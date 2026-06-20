package br.edu.utfpr.dv.siacoes.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class ConnectionDAO {
	
	private String SERVER = "localhost:5432";
	private String DATABASE = "siacoes";
	private String USER = "postgres";
	private String PASSWORD = "postgres";
	
	private DataSource datasource = null;
	private static ConnectionDAO instance = null;
	
	private ConnectionDAO(){}
	
	public static synchronized ConnectionDAO getInstance() throws SQLException{
		if((ConnectionDAO.instance == null) || (ConnectionDAO.instance.datasource == null)){
			ConnectionDAO.instance = new ConnectionDAO();
			ConnectionDAO.instance.createDataSource();
		}
		
		return ConnectionDAO.instance;
	}
	
	public Connection getConnection() throws SQLException{
		return this.datasource.getConnection();
	}
	
	private void createDataSource() throws SQLException{
		Properties props = new Properties();

		try{
			InputStream fis = this.getClass().getClassLoader().getResourceAsStream("/dblocal.properties");
			props.load(fis);
		}catch(Exception e){
			// usa valores padrão definidos nos campos da classe
		}

		String server = getConfig(props, "DB_SERVER", SERVER);
		String database = getConfig(props, "DB_NAME", DATABASE);
		String user = getConfig(props, "DB_USERNAME", USER);
		String password = getConfig(props, "DB_PASSWORD", PASSWORD);
		String driver = getConfig(props, "DB_DRIVER_CLASS", "org.postgresql.Driver");
		String type = getConfig(props, "DB_TYPE", "postgresql");
		
		PoolProperties p = new PoolProperties();
		p.setUrl("jdbc:" + type + "://" + server + "/" + database);
		p.setDriverClassName(driver);
		p.setUsername(user);
		p.setPassword(password);
		p.setJmxEnabled(true);
		p.setTestWhileIdle(false);
		p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		p.setTestOnReturn(false);
		p.setValidationInterval(30000);
		p.setTimeBetweenEvictionRunsMillis(30000);
		p.setMaxActive(1000);
		p.setInitialSize(10);
		p.setMaxWait(10000);
		p.setRemoveAbandonedTimeout(30);
		p.setMinEvictableIdleTimeMillis(30000);
		p.setMinIdle(10);
		p.setLogAbandoned(true);
		p.setRemoveAbandoned(true);
		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
		
		datasource = new DataSource();
		datasource.setPoolProperties(p);
        
		if(type.equals("mysql")){
			Statement stmt = this.datasource.getConnection().createStatement();
			stmt.execute("SET GLOBAL max_allowed_packet=1024*1024*14;");	
		}
	}

	private String getConfig(Properties props, String key, String defaultValue) {
		String envValue = System.getenv(key);
		if (envValue != null && !envValue.trim().isEmpty()) {
			return envValue.trim();
		}

		String propValue = props.getProperty(key);
		if (propValue != null && !propValue.trim().isEmpty()) {
			return propValue.trim();
		}

		return defaultValue;
	}
	
}
