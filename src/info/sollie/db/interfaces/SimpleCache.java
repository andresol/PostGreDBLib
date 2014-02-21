package info.sollie.db.interfaces;

public interface SimpleCache<T> {
	
	public T get(String key);
	
	public boolean put(String key, T t);
	
	public boolean purge(String key);
	
	public void clear();

}
