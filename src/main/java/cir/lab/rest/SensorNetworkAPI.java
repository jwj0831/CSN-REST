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
import cir.lab.csn.metadata.network.SensorNetworkMetadata;
import cir.lab.exception.NotFoundException;
//import cir.lab.exception.UnauthorizedException;
import cir.lab.transfer.common.OptionalMetaDataTransObj;
import cir.lab.transfer.common.TagTransObj;
import cir.lab.transfer.network.SeedTransObj;
//import cir.lab.util.AuthCheck;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SensorNetworkAPI {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ObjectMapper mapper = new ObjectMapper();
	Map<String, String> retJsonMap = new HashMap<String, String>();
	private CSNOperator operator;

	public SensorNetworkAPI(CSNOperator operator) {
		this.operator = operator;
		retJsonMap.put("cat","Network");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorNetworkMetadata(SeedTransObj input ) {
		logger.info( input.toString());

		String id = operator.getSensorNetworkManager().createSensorNetworkMetadata(input.getSeed().getName(), input.getSeed().getMembers());
		retJsonMap.put("action","POST");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id);
		if(id != null)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSensorNetworkMetadata(@QueryParam("key") String key) {
		List<SensorNetworkMetadata> metaList = operator.getSensorNetworkManager().getAllSensorNetworkMetadata();
		if (metaList != null)
			return Response.ok(metaList, MediaType.APPLICATION_JSON).build();
		else {
			retJsonMap.put("action","GET");
			retJsonMap.put("scope","all");
			retJsonMap.put("id", null);
			retJsonMap.put("result", "Fail");
			return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		}
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAllSensorNetworkMetadata(@QueryParam("key") String key) {
		DAOReturnType retType = operator.getSensorNetworkManager().deleteAllSensorNetworkMetadata();
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
		Set<String> idSet = operator.getSensorNetworkManager().getAllSensorNetworkID();
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
	@Path("/topics")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTopicName(@QueryParam("key") String key) {
		Set<String> topicNameSet = operator.getSensorNetworkManager().getAllSensorNetworkTopicName();
		if(topicNameSet != null)
			return Response.ok(topicNameSet, MediaType.APPLICATION_JSON).build();
		else{
			retJsonMap.put("action","GET");
			retJsonMap.put("scope","all");
			retJsonMap.put("id", null);
			retJsonMap.put("result", "Fail");
			return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		}  
	}

	@GET
	@Path("/members")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTopicAndMemberIDs(@QueryParam("key") String key) {
		Map<String, Set<String>> membersMap = operator.getSensorNetworkManager().getAllSNTopicNameAndTheirMemberIDs();
		if(membersMap != null)
			return Response.ok(membersMap, MediaType.APPLICATION_JSON).build();
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
	public Response getSensorNetworkMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		SensorNetworkMetadata metadata = operator.getSensorNetworkManager().getSensorNetworkMetadata(id);
		if(metadata != null)
			return Response.ok(metadata, MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Sensor Network ID: " + id + " is not found");
	}

	@DELETE
	@Path("/{name}-{epoch}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorNetworkMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().removeSensorNetworkMetadata(id);
		retJsonMap.put("action","NETWORK");
		retJsonMap.put("scope","all");
		retJsonMap.put("id", id);

		if(retType == DAOReturnType.RETURN_OK)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");
		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/{name}-{epoch}/members")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorNetworkMember(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		Set<String> memberSet = operator.getSensorNetworkManager().getSensorMembersInSensorNetwork(id);
		if(memberSet != null)
			return Response.ok(memberSet, MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Members of Sensor Network ID: " + id + " is not found");
	}

	@GET
	@Path("/{name}-{epoch}/topic")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorNetworTopicName(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		String topicName = operator.getSensorNetworkManager().getSensorNetworkTopicName(id);
		if(topicName != null)
			return Response.ok("{\"topic_name\":\"" + topicName + "\"}", MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Topic Name of Sensor Network ID: " + id + " is not found");
	}

	@POST
	@Path("/{name}-{epoch}/opts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSensorNetworkOptionalMetadata(OptionalMetaDataTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().addOptionalSensorNetworkMetadata(id, input.getOpt_meta().getElmts());

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
	public Response getSensorNetworkOptaionalMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		Map<String, String> optMap = operator.getSensorNetworkManager().getAllOptionalSensorNetworkMetadata(id);
		if(optMap != null)
			return Response.ok(optMap, MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Optional Metadata of Sensor Network ID: " + id + " is not found");
	}

	@PUT
	@Path("/{name}-{epoch}/opts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSensorNetworkOptionalMetadata(OptionalMetaDataTransObj input, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().updateOptionalSensorNetworkMetadata(id, input.getOpt_meta().getElmts());

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
	public Response removeSensorNetworkAllOptionalMetadata(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().removeOptionalSensorNetworkMetadata(id);
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");

		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		
	}


	@GET
	@Path("/{name}-{epoch}/opts/{opt_name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSensorNetworkOptaionalMetadataValue(@QueryParam("key") String key, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
		String opt_val = operator.getSensorNetworkManager().getOptionalSensorNetworkMetadataValue(id, opt_name);
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
	public Response updateSensorNetworkOptionalMetadataValue(OptionalMetaDataTransObj input, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
		DAOReturnType retType = operator.getSensorNetworkManager().updateOptionalSensorNetworkMetadata(id, input.getOpt_meta().getElmts());
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
	public Response removeSensorNetworkOptionalMetadataValue(@QueryParam("key") String key, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("opt_name") String opt_name) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		logger.info("Input Option Name: {}", opt_name);
		DAOReturnType retType = operator.getSensorNetworkManager().removeOptionalSensorNetworkMetadataValue(id, opt_name);
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
	public Response setSensorNetworkTags(TagTransObj input,  @PathParam("name") String name, @PathParam("epoch") String epoch) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		DAOReturnType retType = operator.getSensorNetworkManager().addTagSetToSensorNetwork(id, input.getTag_meta().getTags());
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
	public Response getSensorNetworkTags(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		Set<String> tagSet = operator.getSensorNetworkManager().getAllTagBySensorNetworkID(id);
		if(tagSet != null)
			return Response.ok(tagSet, MediaType.APPLICATION_JSON).build();
		else
			throw new NotFoundException("Tags of Sensor Network ID: " + id + " is not found");
	}


	@DELETE
	@Path("/{name}-{epoch}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSensorNetworkAllTag(@QueryParam("key") String key, @PathParam("name") String name, @PathParam("epoch") String epoch) {
		//logger.info("Input Auth key: {}", key);
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		//		if(AuthCheck.checkAuth(key)) {
		DAOReturnType retType = operator.getSensorNetworkManager().removeAllTagInSensorNetwork(id);
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
	public Response updateSensorNetworkTag(TagTransObj input, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("tag") String tag) {
		logger.info(input.toString());
		String id = name+"-"+epoch ;
		logger.info("Input Sensor ID: {}", id );
		logger.info("Input Tag: {}", tag);
		//		if(AuthCheck.checkAuth(input.getKey())) {
		retJsonMap.put("action","PUT");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );

		if( input.getTag_meta().getTags().size() != 1) {
			retJsonMap.put("result", "Fail");
			return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
		}
		else {
			String newTag = input.getTag_meta().getTags().iterator().next();
			DAOReturnType retType = operator.getSensorNetworkManager().updateTagInSensorNetwork(id, tag, newTag);

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
	public Response removeSensorNetworkTag(@QueryParam("key") String key, @PathParam("name") String name, 
			@PathParam("epoch") String epoch, @PathParam("tag") String tag) {
		String id = name+"-"+epoch ;
		logger.info("Input Sensor Network ID: {}", id );
		logger.info("Input Tag: {}", tag);
		DAOReturnType retType = operator.getSensorNetworkManager().removeTagInSensorNetwork(id, tag);
		retJsonMap.put("action","DELETE");
		retJsonMap.put("scope","one");
		retJsonMap.put("id", id );
		if(retType != DAOReturnType.RETURN_ERROR)
			retJsonMap.put("result", "OK");
		else
			retJsonMap.put("result", "Fail");
		return Response.ok(retJsonMap, MediaType.APPLICATION_JSON).build();
	}
}
