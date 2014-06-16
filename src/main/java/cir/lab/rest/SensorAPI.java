package cir.lab.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cir.lab.csn.client.CSNOperator;
import cir.lab.csn.data.DAOReturnType;
import cir.lab.csn.metadata.sensor.SensorMetadata;
import cir.lab.exception.NotFoundException;
//import cir.lab.exception.UnauthorizedException;
import cir.lab.transfer.common.OptionalMetaDataTransObj;
import cir.lab.transfer.common.TagTransObj;
import cir.lab.transfer.sensor.SeedTransObj;
//import cir.lab.util.AuthCheck;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SensorAPI {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());;
	ObjectMapper mapper = new ObjectMapper();
	Map<String, String> retJsonMap = new HashMap<String, String>();
	private CSNOperator operator;

	public SensorAPI(CSNOperator operator) {
		this.operator = operator;
		retJsonMap.put("cat","Sensor");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorMetadata(SeedTransObj input ) {
		logger.info( input.toString());

		String id = operator.getSensorNetworkManager().createSensorMetadata(input.getSeed().getName(), input.getSeed().getMeasure());
		retJsonMap.put("action","POST");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id);
		if(id != null)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		//		}
		//		else
		//			throw new UnauthorizedException("Authorization Required");
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSensorMetadata(@QueryParam("key") String key) {
		List<SensorMetadata> metaList = operator.getSensorNetworkManager().getAllSensorMetadata();
		if (metaList != null)
			return Response.ok(metaList, MediaType.APPLICATION_JSON).build();
		else{
			retJsonMap.put("action","GET");
			retJsonMap.put("scope","all");
			retJsonMap.put("id", null);
			retJsonMap.put("result", "Fail");
			return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		}
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAllSensorMetadata(@QueryParam("key") String key) {
		DAOReturnType retType = operator.getSensorNetworkManager().deleteAllSensorMetadata();
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","all");
		retJsonMap.put("id", null);
		if(retType == DAOReturnType.RETURN_OK)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/ids")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllId(@QueryParam("key") String key) {
		Set<String> idSet = operator.getSensorNetworkManager().getAllSensorID();
		if(idSet != null)
			return Response.ok(idSet, MediaType.APPLICATION_JSON).build();
		else{
			retJsonMap.put("action","GET");
			retJsonMap.put("scope","all");
			retJsonMap.put("id", null);
			retJsonMap.put("result", "Fail");
			return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		} 
	}

	@GET
	@Path("/{name}-{epoch}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		SensorMetadata metadata = operator.getSensorNetworkManager().getSensorMetadata(id);
		if(metadata != null)
			return Response.ok(metadata, MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Sensor Network ID: " + id + " is not found");
	}

	@DELETE
	@Path("/{name}-{epoch}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().removeSensorMetadata(id);
		retJsonMap.put("action", "DELETE");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id);
		if(retType == DAOReturnType.RETURN_OK)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("/{name}-{epoch}/opts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorOptionalMetadata(OptionalMetaDataTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().addOptionalSensorMetadata(id, input.getOpt_meta().getElmts());

		retJsonMap.put("action","POST");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/{name}-{epoch}/opts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorOptaionalMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		Map<String, String> optMap = operator.getSensorNetworkManager().getAllOptionalSensorMetadata(id);
		if(optMap != null)
			return Response.ok(optMap, MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Optional Metadata of Sensor Network ID: " + id + " is not found");
	}

	@PUT
	@Path("/{name}-{epoch}/opts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSensorOptionalMetadata(OptionalMetaDataTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().updateOptionalSensorMetadata(id, input.getOpt_meta().getElmts());
		retJsonMap.put("action","PUT");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("/{name}-{epoch}/opts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorAllOptionalMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().removeOptionalSensorMetadata(id);
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","all");
		retJsonMap.put("id", null );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}


	@GET
	@Path("/{name}-{epoch}/opts/{opt_name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorOptaionalMetadataValue(@QueryParam("key") String key, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
		String opt_val = operator.getSensorNetworkManager().getOptionalSensorMetadataValue(id, opt_name);
		if(opt_val != null) {
			Map<String, String> optMap = new HashMap<String, String>();
			optMap.put(opt_name, opt_val);
			return Response.ok(optMap, MediaType.APPLICATION_JSON).build();
		}
		else
			throw new NotFoundException("Option Vaule of Option Name: " + opt_name + " in SN ID: " + id + " is not found");
	}

	@PUT
	@Path("/{name}-{epoch}/opts/{opt_name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSensorOptionalMetadataValue(OptionalMetaDataTransObj input, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
		DAOReturnType retType = operator.getSensorNetworkManager().updateOptionalSensorMetadata(id, input.getOpt_meta().getElmts());
		retJsonMap.put("action","PUT");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("/{name}-{epoch}/opts/{opt_name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorAllOptionalMetadataValue(@QueryParam("key") String key, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
		DAOReturnType retType = operator.getSensorNetworkManager().removeOptionalSensorMetadataValue(id, opt_name);
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");
		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("/{name}-{epoch}/tags")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorAllTags(TagTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().addTagSetToSensor(id, input.getTag_meta().getTags());
		retJsonMap.put("action","POST");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/{name}-{epoch}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorAllTags(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		Set<String> tagSet = operator.getSensorNetworkManager().getAllTagBySensorID(id);
		if(tagSet != null)
			return Response.ok(tagSet, MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Tags of Sensor Network ID: " + id + " is not found");
	}


	@DELETE
	@Path("/{name}-{epoch}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorAllTag(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().removeAllTagInSensor(id);
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");
		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	
	@PUT
	@Path("/{name}-{epoch}/tags/{tag}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSensorTag(TagTransObj input, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("tag") String tag) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		logger.info("Input Tag: {}", tag);
		retJsonMap.put("action","PUT");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );

		if( input.getTag_meta().getTags().size() != 1) {
			logger.error("Can't get the tag from the DB");
			retJsonMap.put("result", "Fail");
			return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		}
		else {
			String newTag = input.getTag_meta().getTags().iterator().next();
			DAOReturnType retType = operator.getSensorNetworkManager().updateTagInSensor(id, tag, newTag);

			if(retType != DAOReturnType.RETURN_ERROR)
				retJsonMap.put("result", "OK");
			else
				retJsonMap.put("result", "Fail");

			return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		}
	}

	@DELETE
	@Path("/{name}-{epoch}/tags/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorTag(@QueryParam("key") String key, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("tag") String tag) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		logger.info("Input Tag: {}", tag);
		DAOReturnType retType = operator.getSensorNetworkManager().removeTagInSensor(id, tag);
		retJsonMap.put("action","DELET");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");
		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}
}
