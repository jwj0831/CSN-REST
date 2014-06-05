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
		retJsonMap.put("category","Sensor");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorMetadata(SeedTransObj input ) {
		logger.info( input.toString());
		
////		if(AuthCheck.checkAuth(input.getKey())) {
			String id = operator.getSensorNetworkManager().createSensorMetadata(input.getSeed().getName(), input.getSeed().getMeasure());
			retJsonMap.put("action","POST");
			retJsonMap.put("scope","one");
			retJsonMap.put("id", id);
			String retJsonString = null;
			if(id != null)
				retJsonMap.put("result", "OK");
			else
				retJsonMap.put("result", "Fail");
			
			try {
				retJsonString = mapper.writeValueAsString(retJsonMap);
			} catch (JsonProcessingException e) {
				logger.error(e.toString());
			}
			return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSensorMetadata(@QueryParam("key") String key) {
		logger.info("Input Auth key: {}", key);
//		if(AuthCheck.checkAuth(key)) {
			List<SensorMetadata> metaList = operator.getSensorNetworkManager().getAllSensorMetadata();
			if (metaList != null)
				return Response.ok(metaList, MediaType.APPLICATION_JSON).build();
			else
				return Response.ok("{'category':'Sensor', 'action':'GET', 'id':'null', 'result':'Fail' }", MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAllSensorMetadata(@QueryParam("key") String key) {
		logger.info("Input Auth key: {}", key);
//		if(AuthCheck.checkAuth(key)) {
			DAOReturnType retType = operator.getSensorNetworkManager().deleteAllSensorMetadata();
			if(retType == DAOReturnType.RETURN_OK)
				return Response.ok("{'category':'Sensor', 'action':'DELETE', 'id':'null', 'result':'OK' }", MediaType.APPLICATION_JSON).build();
			else 
				return Response.ok("{'category':'Sensor', 'action':'DELETE', 'id':'null', 'result':'Fail' }", MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@GET
	@Path("/ids")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllId(@QueryParam("key") String key) {
		logger.info("Input Auth key: {}", key);
//		if(AuthCheck.checkAuth(key)) {
			Set<String> idSet = operator.getSensorNetworkManager().getAllSensorID();
			if(idSet != null)
				return Response.ok(idSet, MediaType.APPLICATION_JSON).build();
			else 
				return Response.ok("{'category':'Sensor', 'action':'GET', 'id':'null', 'result':'Fail' }", MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@GET
	@Path("/{name}-{epoch}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
//		if(AuthCheck.checkAuth(key)) {
			SensorMetadata metadata = operator.getSensorNetworkManager().getSensorMetadata(id);
			if(metadata != null)
				return Response.ok(metadata, MediaType.APPLICATION_JSON).build();
			else
				throw new NotFoundException("Sensor Network ID: " + id + " is not found");
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@DELETE
	@Path("/{name}-{epoch}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
//		if(AuthCheck.checkAuth(key)) {
			DAOReturnType retType = operator.getSensorNetworkManager().removeSensorMetadata(id);
			if(retType == DAOReturnType.RETURN_OK)
				return Response.ok("{'category':'Sensor', 'action':'DELETE', 'id':'"+ id +"', 'result':'OK' }", MediaType.APPLICATION_JSON).build();
			else
				return Response.serverError().build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@POST
	@Path("/{name}-{epoch}/opts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorOptionalMetadata(OptionalMetaDataTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
//		if(AuthCheck.checkAuth(input.getKey())) {
			DAOReturnType retType = operator.getSensorNetworkManager().addOptionalSensorMetadata(id, input.getOpt_meta().getElmts());
			
			retJsonMap.put("action","POST");
			retJsonMap.put("scope","one");
			retJsonMap.put("id", id );
			String retJsonString = null;
			if(retType != DAOReturnType.RETURN_ERROR)
				retJsonMap.put("result", "OK");
			else
				retJsonMap.put("result", "Fail");

			try {
				retJsonString = mapper.writeValueAsString(retJsonMap);
			} catch (JsonProcessingException e) {
				logger.error(e.toString());
			}
			return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@GET
	@Path("/{name}-{epoch}/opts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorOptaionalMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
//		if(AuthCheck.checkAuth(key)) {
			Map<String, String> optMap = operator.getSensorNetworkManager().getAllOptionalSensorMetadata(id);
			if(optMap != null)
				return Response.ok(optMap, MediaType.APPLICATION_JSON).build();
			else
				throw new NotFoundException("Optional Metadata of Sensor Network ID: " + id + " is not found");
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@PUT
	@Path("/{name}-{epoch}/opts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSensorOptionalMetadata(OptionalMetaDataTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
//		if(AuthCheck.checkAuth(input.getKey())) {
			DAOReturnType retType = operator.getSensorNetworkManager().updateOptionalSensorMetadata(id, input.getOpt_meta().getElmts());
			retJsonMap.put("action","POST");
			retJsonMap.put("scope","one");
			retJsonMap.put("id", id );
			String retJsonString = null;
			if(retType != DAOReturnType.RETURN_ERROR)
				retJsonMap.put("result", "OK");
			else
				retJsonMap.put("result", "Fail");

			try {
				retJsonString = mapper.writeValueAsString(retJsonMap);
			} catch (JsonProcessingException e) {
				logger.error(e.toString());
			}
			return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@DELETE
	@Path("/{name}-{epoch}/opts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorAllOptionalMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
//		if(AuthCheck.checkAuth(key)) {
			DAOReturnType retType = operator.getSensorNetworkManager().removeOptionalSensorMetadata(id);
			if(retType == DAOReturnType.RETURN_OK)
				return Response.ok("{'category':'Sensor', 'action':'DELETE', 'id':'"+ id +"', 'result':'OK' }", MediaType.APPLICATION_JSON).build();
			else
				return Response.serverError().build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	

//	@POST
//	@Path("/{name}-{epoch}/opts/{opt_name}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response setSensorOptionalMetadataValue(OptionalMetaDataTransObj input, @PathParam("name") String name, 
//											@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
//		logger.info(input.toString());
//		String id = name+"-"+epoch ;
//		logger.info("Input Sensor ID: {}", id );
//		
////		if(AuthCheck.checkAuth(input.getKey())) {
//			DAOReturnType retType = operator.getSensorNetworkManager().addOptionalSensorMetadata(id, input.getOpt_meta().getElmts());
//			
//			retJsonMap.put("action","POST");
//			retJsonMap.put("scope","one");
//			retJsonMap.put("id", id );
//			String retJsonString = null;
//			if(retType != DAOReturnType.RETURN_ERROR)
//				retJsonMap.put("result", "OK");
//			else
//				retJsonMap.put("result", "Fail");
//
//			try {
//				retJsonString = mapper.writeValueAsString(retJsonMap);
//			} catch (JsonProcessingException e) {
//				logger.error(e.toString());
//			}
//			return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
//	}
	
	@GET
	@Path("/{name}-{epoch}/opts/{opt_name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorOptaionalMetadataValue(@QueryParam("key") String key, @PathParam("name") String name, 
											@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
//		if(AuthCheck.checkAuth(key)) {
			String opt_val = operator.getSensorNetworkManager().getOptionalSensorMetadataValue(id, opt_name);
			if(opt_val != null) {
				Map<String, String> optMap = new HashMap<String, String>();
				optMap.put(opt_name, opt_val);
				return Response.ok(optMap, MediaType.APPLICATION_JSON).build();
			}
			else
				throw new NotFoundException("Option Vaule of Option Name: " + opt_name + " in SN ID: " + id + " is not found");
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
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
//		if(AuthCheck.checkAuth(input.getKey())) {
			DAOReturnType retType = operator.getSensorNetworkManager().updateOptionalSensorMetadata(id, input.getOpt_meta().getElmts());
			retJsonMap.put("action","PUT");
			retJsonMap.put("scope","one");
			retJsonMap.put("id", id );
			String retJsonString = null;
			if(retType != DAOReturnType.RETURN_ERROR)
				retJsonMap.put("result", "OK");
			else
				retJsonMap.put("result", "Fail");

			try {
				retJsonString = mapper.writeValueAsString(retJsonMap);
			} catch (JsonProcessingException e) {
				logger.error(e.toString());
			}
			return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@DELETE
	@Path("/{name}-{epoch}/opts/{opt_name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorAllOptionalMetadataValue(@QueryParam("key") String key, @PathParam("name") String name, 
													@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
//		if(AuthCheck.checkAuth(key)) {
			DAOReturnType retType = operator.getSensorNetworkManager().removeOptionalSensorMetadataValue(id, opt_name);
			if(retType == DAOReturnType.RETURN_OK)
				return Response.ok("{'category':'Sensor', 'action':'DELETE', 'id':'"+ id +"', 'result':'OK' }", MediaType.APPLICATION_JSON).build();
			else
				return Response.serverError().build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@POST
	@Path("/{name}-{epoch}/tags")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorTags(TagTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
//		if(AuthCheck.checkAuth(input.getKey())) {
			DAOReturnType retType = operator.getSensorNetworkManager().addTagSetToSensor(id, input.getTag_meta().getTags());
			retJsonMap.put("action","POST");
			retJsonMap.put("scope","one");
			retJsonMap.put("id", id );
			String retJsonString = null;
			if(retType != DAOReturnType.RETURN_ERROR)
				retJsonMap.put("result", "OK");
			else
				retJsonMap.put("result", "Fail");

			try {
				retJsonString = mapper.writeValueAsString(retJsonMap);
			} catch (JsonProcessingException e) {
				logger.error(e.toString());
			}
			return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@GET
	@Path("/{name}-{epoch}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorTags(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
//		if(AuthCheck.checkAuth(key)) {
			Set<String> tagSet = operator.getSensorNetworkManager().getAllTagBySensorID(id);
			if(tagSet != null)
				return Response.ok(tagSet, MediaType.APPLICATION_JSON).build();
			else
				throw new NotFoundException("Tags of Sensor Network ID: " + id + " is not found");
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
//	@PUT
//	@Path("/{name}-{epoch}/tags")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response updatetSensorTags(OptionalMetaDataTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
//		logger.info(input.toString());
//		String id = name+"-"+epoch ;
//		logger.info("Input Sensor ID: {}", id );
////		if(AuthCheck.checkAuth(input.getKey())) {
//			DAOReturnType retType = operator.getSensorNetworkManager().updateOptionalSensorMetadata(id, input.getOpt_meta().getElmts());
//			
//			retJsonMap.put("action","POST");
//			retJsonMap.put("scope","one");
//			retJsonMap.put("id", id );
//			String retJsonString = null;
//			if(retType != DAOReturnType.RETURN_ERROR)
//				retJsonMap.put("result", "OK");
//			else
//				retJsonMap.put("result", "Fail");
//
//			try {
//				retJsonString = mapper.writeValueAsString(retJsonMap);
//			} catch (JsonProcessingException e) {
//				logger.error(e.toString());
//			}
//			return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
//	}
	
	@DELETE
	@Path("/{name}-{epoch}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorAllTag(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
//		if(AuthCheck.checkAuth(key)) {
			DAOReturnType retType = operator.getSensorNetworkManager().removeAllTagInSensor(id);
			if(retType == DAOReturnType.RETURN_OK)
				return Response.ok("{'category':'Sensor', 'action':'DELETE', 'id':'"+ id +"', 'result':'OK' }", MediaType.APPLICATION_JSON).build();
			else
				return Response.serverError().build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
//	@GET
//	@Path("/{name}-{epoch}/opts/{tag}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response getSensorTag(@QueryParam("key") String key, @PathParam("name") String name, 
//											@PathParam("epoch") String epoch, @PathParam("tag") String tag) {
//		logger.info("Input Auth key: {}", key);
//		String id = name+"-"+epoch ;
//		logger.info("Input Sensor ID: {}", id );
//		logger.info("Input Tag: {}", tag);
////		if(AuthCheck.checkAuth(key)) {
//			String opt_val = operator.getSensorNetworkManager().getOptionalSensorMetadataValue(id, opt_name);
//			if(opt_val != null) {
//				Map<String, String> optMap = new HashMap<String, String>();
//				optMap.put(opt_name, opt_val);
//				return Response.ok(optMap, MediaType.APPLICATION_JSON).build();
//			}
//			else
//				throw new NotFoundException("Option Vaule of Option Name: " + opt_name + " in SN ID: " + id + " is not found");
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
//	}
	
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
//		if(AuthCheck.checkAuth(input.getKey())) {
			retJsonMap.put("action","PUT");
			retJsonMap.put("scope","one");
			retJsonMap.put("id", id );
			String retJsonString = null;
			
			if( input.getTag_meta().getTags().size() != 1) {
				 retJsonMap.put("result", "Fail");
				 return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
			}
			else {
				String newTag = input.getTag_meta().getTags().iterator().next();
				DAOReturnType retType = operator.getSensorNetworkManager().updateTagInSensor(id, tag, newTag);
				
				if(retType != DAOReturnType.RETURN_ERROR)
					retJsonMap.put("result", "OK");
				else
					retJsonMap.put("result", "Fail");

				try {
					retJsonString = mapper.writeValueAsString(retJsonMap);
				} catch (JsonProcessingException e) {
					logger.error(e.toString());
				}
				return Response.ok(retJsonString, MediaType.APPLICATION_JSON).build();
			}
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
	
	@DELETE
	@Path("/{name}-{epoch}/tags/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorTag(@QueryParam("key") String key, @PathParam("name") String name, 
													@PathParam("epoch") String epoch, @PathParam("tag") String tag) {
		logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		logger.info("Input Tag: {}", tag);
//		if(AuthCheck.checkAuth(key)) {
			DAOReturnType retType = operator.getSensorNetworkManager().removeTagInSensor(id, tag);
			if(retType == DAOReturnType.RETURN_OK)
				return Response.ok("{'category':'Sensor', 'action':'DELETE', 'id':'"+ id +"', 'result':'OK' }", MediaType.APPLICATION_JSON).build();
			else
				return Response.serverError().build();
//		}
//		else
//			throw new UnauthorizedException("Authorization Required");
	}
}
