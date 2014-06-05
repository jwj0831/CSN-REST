package cir.lab.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import cir.lab.csn.client.CSNOperator;

import com.fasterxml.jackson.databind.ObjectMapper;
public class SearchAPI {
	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ObjectMapper mapper = new ObjectMapper();
	Map<String, String> retJsonMap = new HashMap<String, String>();
	
	private CSNOperator operator;

	public SearchAPI(CSNOperator operator) {
		super();
		this.operator = operator;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response ping() {
		operator.getSensorNetworkManager().isItSensorMetadata(null);
		String msg = "<div style=\"text-align:center;\"><h1>Here is for CSN RESTful API!</h1></div>";
		return Response.ok(msg, MediaType.TEXT_HTML).build();
	}
	
	
}
