package cl.exe.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/servicio")
public class Servicio {

	@Path("/test")
	@GET
	@Produces("text/html")
	public String test() {
		return "Hola Mundo";
	}

	@Path("/p")
	@POST
	@Consumes("application/json")
	@Produces("text/html")
	public String p(String json) {
		System.out.println(json);
		return "Hola Mundo 2";
	}

	@Path("/echo")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public String echo(String json) {
		return json;
	}

	@Path("/prueba/{parametroruta}/{conversa}")
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response prueba(@Context HttpServletRequest request, @QueryParam("parametro") String parametro,
			@PathParam("parametroruta") Long id, String json) {
		System.out.println(request.getCharacterEncoding());
		System.out.println(parametro);
		System.out.println(id);

		return Response.ok("{\"estado\":\"ok\"}").status(200).build();
	}

	@Path("/formulario")
	@POST
	@Produces("application/json")
	public Response formulario(@FormParam("valor") String test) {

		return Response.ok("{\"test\":" + test + "\"}").status(200).build();
	}

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response uploadFile(MultipartFormDataInput input) throws IOException {

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		// Get file data to save
		List<InputPart> inputParts = uploadForm.get("archivo");

		for (InputPart inputPart : inputParts) {
			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				String fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);
				// constructs upload file path
				fileName = "/Users/utaladriz/Downloads/" + fileName;
				writeFile(bytes, fileName);

				return Response.status(200).entity("Archivo subido : " + fileName).build();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	// Utility method
	private void writeFile(byte[] content, String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			System.out.println("not exist> " + file.getAbsolutePath());
			file.createNewFile();
		}
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(content);
		fop.flush();
		fop.close();
	}

}
