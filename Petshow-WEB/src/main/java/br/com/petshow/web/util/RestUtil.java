package br.com.petshow.web.util;



import javax.ws.rs.client.Entity;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import br.com.petshow.enums.EnumErrosSistema;
import br.com.petshow.exceptions.ExceptionErroCallRest;
import br.com.petshow.exceptions.ExceptionValidation;

import br.com.petshow.model.Entidade;
import br.com.petshow.util.FileApplicationUtil;
import br.com.petshow.util.JsonUtil;
import br.com.petshow.util.MapErroRetornoRest;
import br.com.petshow.util.WriteConsoleUtil;


public class RestUtil {
	
	public static final String URL_BASE = FileApplicationUtil.getUrlBaseREST();
	
	public ResteasyClient client;
	
	public ResteasyWebTarget target;
	
	
	public static <T> T postEntity(Entidade entidade, String url,Class<T> type) throws ExceptionErroCallRest,ExceptionValidation{

		ResteasyClient client = new ResteasyClientBuilder().build();
		
		ResteasyWebTarget target= client.target(URL_BASE+url);
		
		WriteConsoleUtil.write("Enviado:"+JsonUtil.getJSON(entidade));
		
		Response response = target.request().post(Entity.entity(entidade, MediaType.APPLICATION_JSON));
		
		if(response.getStatus() != 200){
			MapErroRetornoRest erro=null;
			try{
				erro=response.readEntity(MapErroRetornoRest.class); // caso der problema de conversao é por que nao foi um erro previsto pelo proprio REST
			}catch(Throwable th){
				throw new ExceptionErroCallRest("Failed: HTTP error code:" +response.getStatus());
			}
			if(erro.getType()==EnumErrosSistema.ERRO_VALIDACAO){
				throw new ExceptionValidation(erro.getMessage());
			}
		}
		
		
		WriteConsoleUtil.write("Retornado:"+JsonUtil.getJSON(entidade));
		return  type.cast(response.readEntity(type));
	}


	public static void delete(String url) throws ExceptionErroCallRest, ExceptionValidation{

		ResteasyClient client = new ResteasyClientBuilder().build();
		
		ResteasyWebTarget target= client.target(URL_BASE+url);
		
		Response response =target.request().delete();
		
		if(response.getStatus() != 200){
			MapErroRetornoRest erro=null;
			try{
				erro=response.readEntity(MapErroRetornoRest.class); // caso der problema de conversao é por que nao foi um erro previsto pelo proprio REST
			}catch(Throwable th){
				throw new ExceptionErroCallRest("Failed: HTTP error code:" +response.getStatus());
			}
			
			if(erro.getType()==EnumErrosSistema.ERRO_VALIDACAO){
				throw new ExceptionValidation(erro.getMessage());
			}
		}
		
		
	
	}
	
	
	public static <T>  T getEntity(String url,Class<T> type) throws ExceptionErroCallRest, ExceptionValidation{

		ResteasyClient client = new ResteasyClientBuilder().build();
		
		ResteasyWebTarget target= client.target(URL_BASE+url);
		
		
		Object entidade=null;
		try{
			Response response = target.request().get();
			entidade = response.readEntity(type);
		}catch(Exception ex){
			throw new ExceptionErroCallRest("Failed: HTTP error code:"+ex.getMessage());
		
		}

		if(entidade instanceof MapErroRetornoRest){// caso seja um objeto do tipo MapErroRetornoRest ocorreu um erro/validacao previsto no REST
			MapErroRetornoRest erro=(MapErroRetornoRest) entidade;
			if(erro.getType()==EnumErrosSistema.ERRO_VALIDACAO){
				throw new ExceptionValidation(erro.getMessage());
			}
		}
		
		
		WriteConsoleUtil.write("Retornado:"+JsonUtil.getJSON(entidade));
		return type.cast(entidade);
	}
	
	
	
	
	/*
	public static <T> T getList(String url,Class<T> type) throws ExceptionErroCallRest{

		ResteasyClient client = new ResteasyClientBuilder().build();
		
		ResteasyWebTarget target= client.target(URL_BASE+url);
		
		
		Object entidades = null;
		try{
			entidades =  target.request().get(new javax.ws.rs.core.GenericType<T>() {});
			WriteConsoleUtil.write("Enviado:"+JsonUtil.getJSON(entidades));
		}catch(Exception ex){
			throw new ExceptionErroCallRest("Failed: HTTP error code:"+ex.getMessage());
			
		}
		return type.cast(entidades);
	
	}
	*/
		
	
}
