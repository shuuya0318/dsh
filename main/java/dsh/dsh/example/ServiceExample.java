package dsh.dsh.example;

/**
 * An example service interface
 * @author h.indzhov
 */
public interface ServiceExample {
 
	// concatenate the arguments
	public String concat(String... args);

	public String hello(String... args);
}
