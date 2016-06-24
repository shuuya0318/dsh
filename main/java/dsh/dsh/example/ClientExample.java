package dsh.dsh.example;

import dsh.dsh.RpcClient;

public class ClientExample {
 
	public static void main(String[] args) {
		// lookup the service with name "example", interface ServiceExample located at host localhost and port 6789
		ServiceExample example = RpcClient.lookupService("localhost", 4885, "example", ServiceExample.class);
		// call the method concat and display the result
		System.out.println(example.concat("foo", " ", "bar", " ", "baz"));
	}
}
