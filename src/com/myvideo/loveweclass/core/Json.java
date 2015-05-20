package com.myvideo.loveweclass.core;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Json 
{
	public static <T> String Serialize(T entity) throws JsonGenerationException, JsonMappingException, IOException
    {
		if (entity == null)
			return null;
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(entity);
    }

    public static <T> T Deserialize(String data, Class<T> type) throws JsonParseException, IOException
    {
    	if (data == null)
    		return null;

    	ObjectMapper mapper = new ObjectMapper();
    	T entity = mapper.readValue(data, type);
    	return entity;
    }

    public static <T> T Deserialize(InputStream data, Class<T> type) throws JsonParseException, JsonMappingException, IOException
    {
    	if (data == null)
    		return null;

		ObjectMapper mapper = new ObjectMapper();
		T entity = mapper.readValue(data, type);
		return entity;
    }

// GSON Library
//    public static <T> String Serialize(Class<T> type, T entity)
//    {
//    	Gson gson = new GsonBuilder().create();
//        return gson.toJson(entity, type);
//    }
//
//    public static <T> T Deserialize(Class<T> type, String data)
//    {
//		Gson gson = new GsonBuilder().create();
//    	T entity = gson.fromJson(data, type);
//		return entity;
//    }
}
