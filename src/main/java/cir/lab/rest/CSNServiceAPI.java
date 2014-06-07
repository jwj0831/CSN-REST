package cir.lab.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cir.lab.csn.client.CSNOperator;
import cir.lab.csn.client.ServiceOperatorProvider;
import cir.lab.csn.metadata.csn.CSNConfigMetadata;
import cir.lab.csn.util.TimeGeneratorUtil;
import cir.lab.exception.NotFoundException;
//import cir.lab.exception.UnauthorizedException;
import cir.lab.transfer.csn.ConfigMetadataTransObj;
//import cir.lab.util.AuthCheck;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Path("/")
public class CSNServiceAPI {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ObjectMapper mapper = new ObjectMapper();
	Map<String, String> retJsonMap = new HashMap<String, String>();
	private CSNOperator operator = null;
	private CSNConfigMetadata config = null;

	public CSNServiceAPI() {
		logger.info("CSN Service API!!!");
		retJsonMap.put("cat","CSN");
	}

	@Path("/sensor-networks")
	public SensorNetworkAPI getSesnorNetworkAPI() {
		return new SensorNetworkAPI(operator);
	}

	@Path("/sensors")
	public SensorAPI getSesnorAPI() {
		return new SensorAPI(operator);
	}

	@Path("/search")
	public SearchAPI getSearchAPI() {
		return new SearchAPI(operator);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response ping() {
		String msg = "<div style=\"text-align:center;\"><h1>Here is for CSN RESTful API!</h1></div>";
		return Response.ok(msg, MediaType.TEXT_HTML).build();
	}

	@POST
	@Path("/csn")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setCSNConfigMetadata(ConfigMetadataTransObj input) {
		logger.info(input.toString());
		if(config == null)
			config = new CSNConfigMetadata();

		config.setName(input.getConfig().getName());
		config.setCreationTime(TimeGeneratorUtil.getCurrentTimestamp());
		config.setAdminName(input.getConfig().getAdminName());
		config.setAdminEmail(input.getConfig().getAdminEmail());

		URI createdUri = null;
		try {
			createdUri = new URI("http://117.16.146.55/CSN-REST/csn");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return Response.created(createdUri).entity(config).type("application/json").build();
	}

	@GET
	@Path("/csn")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCSNConfigMetadata(@QueryParam("key") String key) {
		if(operator != null)
			config = operator.getCsnConfigMetadata();

		if(config == null)
			throw new NotFoundException("No CSN Configuration File");
		else
			return Response.ok(config, MediaType.APPLICATION_JSON).build(); 
	}

	@DELETE
	@Path("/csn")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeCSNConfigMetadata(@QueryParam("key") String key) {
		if(operator != null) {
			operator.stopSystem();
			operator = null;
		}
		config = null;
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","all");
		retJsonMap.put("result","Ok");
		String retJsonString = null;
		try {
			retJsonString = mapper.writeValueAsString(retJsonMap);
		} catch (JsonProcessingException e) {
			logger.error(e.toString());
		}

		return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build(); 
	}

	@POST
	@Path("/csn/start")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response startCSN(ConfigMetadataTransObj input) {
		logger.info(input.toString());
		if(config == null)
			config = new CSNConfigMetadata();
		config.setCreationTime(TimeGeneratorUtil.getCurrentTimestamp());
		config.setAdminName(input.getConfig().getAdminName());
		config.setAdminEmail(input.getConfig().getAdminEmail());
		operator = ServiceOperatorProvider.getCSNCoreService(input.getConfig().getName(), config);
		operator.initCSN();
		operator.startSystem();
		return Response.ok(config, MediaType.APPLICATION_JSON).build(); 
	}

	@POST
	@Path("/csn/restart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response restartCSNPOST(ConfigMetadataTransObj input) {
		logger.info(input.toString());
		operator.stopSystem();

		if(config == null)
			config = new CSNConfigMetadata();

		config.setCreationTime(TimeGeneratorUtil.getCurrentTimestamp());
		config.setAdminName(input.getConfig().getAdminName());
		config.setAdminEmail(input.getConfig().getAdminEmail());
		operator = ServiceOperatorProvider.getCSNCoreService(input.getConfig().getName(), config);

		operator.initCSN();
		operator.startSystem();

		return Response.ok(config, MediaType.APPLICATION_JSON).build(); 
	}

	@POST
	@Path("/csn/stop")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopCSNPOST() {
		operator.stopSystem();
		operator = null;
		config = null;
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","all");
		retJsonMap.put("result","Ok");
		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build(); 
	}
}