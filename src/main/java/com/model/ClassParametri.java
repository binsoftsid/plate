package com.model;

import java.sql.*;

public class ClassParametri {
	public Connection connection = null;
	
	public ClassParametri() {
		
	}
	
    public void kopirajParametre(Connection conn,String _pred,String _juzer) {
		String sql="";
		sql = "DROP TABLE IF EXISTS aaa";
		izvrsi(conn,sql);

		sql = "CREATE TEMPORARY TABLE aaa LIKE parametriobracuna";
		izvrsi(conn,sql);
		
		sql = "INSERT INTO aaa SELECT * FROM parametriobracuna WHERE parametriobracuna.pre=1" + 
				" AND parametriobracuna.juzer='admin'";
		izvrsi(conn,sql);

		sql = "UPDATE aaa SET pre=" + _pred + ",juzer='" + _juzer + "'";
		izvrsi(conn,sql);

		sql = "INSERT INTO parametriobracuna SELECT * FROM aaa";
		izvrsi(conn,sql);

		sql = "DROP TABLE IF EXISTS aaa";
		izvrsi(conn,sql);
		
	}
	//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(Connection _conn,String sql) {
	      Statement statement = null;
		  try {
				statement = _conn.createStatement();
				int result = statement.executeUpdate( sql );
	      }
	      catch ( SQLException sqlex ) {
	      }
		  finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
				}
			}
		  }
	 }
}
