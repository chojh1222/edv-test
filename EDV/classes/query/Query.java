package query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import db.Dbcon;

public class Query 
{
	
	// ArrayList ���� []�� �����ϴ� ���� ���� �� ����.
	public static ArrayList executeQuery(String sql, ArrayList sparams) throws Exception
	{
		ArrayList result = new ArrayList();
		String columnName;
		String columnValue;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection connection = null;
		
		try
		{
			if (Query.countQuestionMark(sql) != sparams.size()) throw new Exception("SQL���� ? ������ �Ķ���� ������ ���� �ʴ�.");
			connection = Dbcon.getConnection();
			pstmt = connection.prepareStatement(sql);
			for (int i = 0; i < sparams.size(); i++)
				pstmt.setString(i + 1, (String) sparams.get(i));

			rs = pstmt.executeQuery();
			String[] columnNames = Query.getColumnNames(rs);
			Hashtable temp_hash;
			while (rs.next())
			{
				temp_hash = new Hashtable();
				for (int i = 0; i < columnNames.length; i++)
				{
					columnName = columnNames[i];
					columnValue = rs.getString(columnName);
					temp_hash.put(columnName, (columnValue == null) ? "" : columnValue);
				}
				result.add(temp_hash);
			}
			rs.close();
			pstmt.close();
			connection.close();
		}
		catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			if(rs != null) { rs.close(); rs = null; }
			if(pstmt != null) { pstmt.close(); pstmt = null; }
			if(connection != null) { connection.close(); connection = null; }
		}
		return result;
	}
	// sql�� ? �� sql parameter�� ����
	private static int countQuestionMark(String sql)
	{
		char[] chars = sql.toCharArray();
		int count = 0;
		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] == '?') count++;
		}
		return count;
	}
	// �÷��� ����.
	private static String[] getColumnNames(ResultSet rs) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		String[] columns = new String[rsmd.getColumnCount()];

		for (int i = 0; i < columns.length; i++)
			columns[i] = rsmd.getColumnName(i + 1);
		return columns;
	}
}
