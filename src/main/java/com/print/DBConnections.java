package com.print;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBConnections {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	Connection conn;
	DataSource dataSource;

	public DBConnections(){
		try{
			dataSource  = jdbcTemplate.getDataSource();
			conn = dataSource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
	}

	public Connection getConnection()
	{
		return this.conn;
	}
}
