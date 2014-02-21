package info.sollie.db.implementation;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class PerformanceQueryRunner extends QueryRunner {
	
	/**
	 * @see org.apache.commons.dbutils.QueryRunner#batch(java.sql.Connection, java.lang.String, java.lang.Object[][])
	 */
	@Override
	public int[] batch(Connection conn, String sql, Object[][] params) throws SQLException {
		final long start = System.currentTimeMillis();
		int[] result = super.batch(conn, sql, params);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	private void updateTime(long time) {
//		Context context = Contexts.get();
//		if (context != null && context instanceof PerformanceContext) {
//			PerformanceContext performanceContext = (PerformanceContext) context;
//			performanceContext.increaseDBCall();
//			performanceContext.increaseDBTime(time);
//		}
	}

	/** 
	 * @see org.apache.commons.dbutils.QueryRunner#batch(java.lang.String, java.lang.Object[][])
	 */
	@Override
	public int[] batch(String sql, Object[][] params) throws SQLException {
		final long start = System.currentTimeMillis();
		int[] result = super.batch(sql, params);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.sql.Connection, java.lang.String, java.lang.Object, org.apache.commons.dbutils.ResultSetHandler)
	 */
	@Override
	@Deprecated
	public <T> T query(Connection conn, String sql, Object param, ResultSetHandler<T> rsh) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(conn, sql, param, rsh);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return t;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.sql.Connection, java.lang.String, java.lang.Object[], org.apache.commons.dbutils.ResultSetHandler)
	 */
	@Override
	@Deprecated
	public <T> T query(Connection conn, String sql, Object[] params, ResultSetHandler<T> rsh) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(conn, sql, params, rsh);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return t;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.sql.Connection, java.lang.String, org.apache.commons.dbutils.ResultSetHandler, java.lang.Object[])
	 */
	@Override
	public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(conn, sql, rsh, params);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return t;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.sql.Connection, java.lang.String, org.apache.commons.dbutils.ResultSetHandler)
	 */
	@Override
	public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(conn, sql, rsh);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return t;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.lang.String, java.lang.Object, org.apache.commons.dbutils.ResultSetHandler)
	 */
	@Override
	@Deprecated
	public <T> T query(String sql, Object param, ResultSetHandler<T> rsh) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(sql, param, rsh);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return t;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.lang.String, java.lang.Object[], org.apache.commons.dbutils.ResultSetHandler)
	 */
	@Override
	@Deprecated
	public <T> T query(String sql, Object[] params, ResultSetHandler<T> rsh) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(sql, params, rsh);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return t;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.lang.String, org.apache.commons.dbutils.ResultSetHandler, java.lang.Object[])
	 */
	@Override
	public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(sql, rsh, params);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop - start);
		return t;
	}

	/*
	 * @see org.apache.commons.dbutils.QueryRunner#query(java.lang.String, org.apache.commons.dbutils.ResultSetHandler)
	 */
	@Override
	public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException {
		final long start = System.currentTimeMillis();
		T t = super.query(sql, rsh);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return t;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#update(java.sql.Connection, java.lang.String)
	 */
	@Override
	public int update(Connection conn, String sql) throws SQLException {
		final long start = System.currentTimeMillis();
		int result = super.update(conn, sql);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#update(java.sql.Connection, java.lang.String, java.lang.Object)
	 */
	@Override
	public int update(Connection conn, String sql, Object param) throws SQLException {
		final long start = System.currentTimeMillis();
		int result = super.update(conn, sql, param);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#update(java.sql.Connection, java.lang.String, java.lang.Object[])
	 */
	@Override
	public int update(Connection conn, String sql, Object... params) throws SQLException {
		final long start = System.currentTimeMillis();
		int result = super.update(conn, sql, params);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#update(java.lang.String)
	 */
	@Override
	public int update(String sql) throws SQLException {
		final long start = System.currentTimeMillis();
		int result = super.update(sql);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public int update(String sql, Object param) throws SQLException {
		final long start = System.currentTimeMillis();
		int result = super.update(sql, param);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	/**
	 * @see org.apache.commons.dbutils.QueryRunner#update(java.lang.String, java.lang.Object[])
	 */
	@Override
	public int update(String sql, Object... params) throws SQLException {
		final long start = System.currentTimeMillis();
		int result =  super.update(sql, params);
		final long stop = System.currentTimeMillis();
		this.updateTime(stop-start);
		return result;
	}

	/**
	 * 
	 */
	public PerformanceQueryRunner() {
		super();
	}

	/**
	 * @param pmdKnownBroken
	 */
	public PerformanceQueryRunner(boolean pmdKnownBroken) {
		super(pmdKnownBroken);
	}

	/**
	 * @param ds
	 * @param pmdKnownBroken
	 */
	public PerformanceQueryRunner(DataSource ds, boolean pmdKnownBroken) {
		super(ds, pmdKnownBroken);
	}

	/**
	 * @param ds
	 */
	public PerformanceQueryRunner(DataSource ds) {
		super(ds);
	}
}
