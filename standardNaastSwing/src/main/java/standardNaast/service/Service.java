/**
 * 
 */
package standardNaast.service;

/**
 * @author stessy
 * 
 */
public interface Service {

	<T> void add(T entity);


	<T> T update(T entity);


	<T> void remove(T entity);

}
